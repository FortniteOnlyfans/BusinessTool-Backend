package dev.gr1.db.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ORMSystem {
    public static TableDesc createDescFor(Class<?> clazz) {
        if(!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class " + clazz + " must be annotated with @Table!");
        }
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        Field[] fields = clazz.getDeclaredFields();
        String PKIDFieldName = null;
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                PKIDFieldName = field.getName();
            }
            fieldNames.add(field.getName());
        }

        if (PKIDFieldName == null) {
            throw new IllegalArgumentException("One field of " + clazz + " must be annotated with @Id!");
        }

        return new TableDesc(clazz, tableName, fieldNames, PKIDFieldName);
    }

    private static Object createValue(Object o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object o, String fieldName, Object value) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(o, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TableContext createContext(Connection connection, TableDesc desc) throws SQLException {
        //skip the id field here
        int amtFields = desc.fieldNames().size() - 1;

        //select
        String selectSql = "SELECT * FROM " + desc.tableName() + " WHERE " + desc.PKIDFieldName() + " = ?";
        PreparedStatement select = connection.prepareStatement(selectSql);

        //insert
        String fields = String.join(",", desc.fieldNames()
                .stream()
                .filter(f -> !f.equals(desc.PKIDFieldName()))
                .toList());

        String placeholders = "?,".repeat(desc.fieldNames().size());
        placeholders = placeholders.substring(0, amtFields * 2 - 1);

        String insertSql = String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                desc.tableName(), fields, placeholders
        );
        PreparedStatement insert = connection.prepareStatement(insertSql);

        //update
        String setClause = String.join(", ",
                desc.fieldNames().stream()
                        .filter(f -> !f.equals(desc.PKIDFieldName()))
                        .map(f -> f + " = ?")
                        .toList()
        );

        String updateSql = String.format(
                "UPDATE %s SET %s WHERE %s = ?",
                desc.tableName(),
                setClause,
                desc.PKIDFieldName()
        );
        PreparedStatement update = connection.prepareStatement(updateSql);

        //delete
        String deleteSql = String.format(
                "DELETE FROM %s WHERE %s = ?",
                desc.tableName(),
                desc.PKIDFieldName()
        );
        PreparedStatement delete = connection.prepareStatement(deleteSql);

        String selectAllSql = "SELECT * FROM " + desc.tableName();
        PreparedStatement selectAll = connection.prepareStatement(selectAllSql);

        return new TableContext(desc, select, insert, update, delete, selectAll);
    }

    public static Object select(TableContext context, int id) throws SQLException {
        try {
            PreparedStatement stmt = context.selectById;
            stmt.setInt(0, id);
            ResultSet set = stmt.executeQuery();

            if (!set.next()) return null;

            Class<?> clazz = context.desc.clazz();
            Object o = clazz.getDeclaredConstructor().newInstance();

            for (String fieldName : context.desc.fieldNames()) {
                Object value = set.getObject(fieldName);
                setField(o, fieldName, value);
            }
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insert(TableContext context, Object o) throws SQLException {
        PreparedStatement stmt = context.insert;
        String pkName = context.desc.PKIDFieldName();

        int i = 1;
        for (String fieldName : context.desc.fieldNames()) {
            if (fieldName.equals(pkName)) {
                continue;
            }
            stmt.setObject(i++, createValue(o, fieldName));
        }

        stmt.executeUpdate();

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                Object newId = generatedKeys.getObject(1);
                setField(o, pkName, newId);
            }
        }
    }

    public static void update(TableContext context, Object o) throws SQLException {
        PreparedStatement stmt = context.update;
        List<String> fields = context.desc.fieldNames();
        String pkName = context.desc.PKIDFieldName();

        int i = 1;
        for (String fieldName : fields) {
            if (fieldName.equals(pkName)) continue;
            stmt.setObject(i++, createValue(o, fieldName));
        }

        stmt.setObject(i, createValue(o, pkName));
        stmt.executeUpdate();
    }

    public static void delete(TableContext context, Object id) throws SQLException {
        PreparedStatement stmt = context.delete;
        Object idValue;

        if (id.getClass().equals(context.desc.clazz())) {
            idValue = createValue(id, context.desc.PKIDFieldName());
        } else {
            idValue = id;
        }

        if (idValue == null) {
            throw new IllegalArgumentException("Cannot delete: ID value is null.");
        }

        stmt.setObject(1, idValue);
        stmt.executeUpdate();
    }

    public static List<Object> queryAll(TableContext context) throws SQLException {
        List<Object> results = new ArrayList<>();

        try (ResultSet rs = context.selectAll.executeQuery()) {
            Class<?> clazz = context.desc.clazz();

            while (rs.next()) {
                try {
                    Object o = clazz.getDeclaredConstructor().newInstance();
                    for (String fieldName : context.desc.fieldNames()) {
                        Object value = rs.getObject(fieldName);
                        setField(o, fieldName, value);
                    }
                    results.add(o);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
                }
            }
        }
        return results;
    }
}
