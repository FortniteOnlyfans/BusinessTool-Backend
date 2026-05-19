package dev.gr1.db.dao;

import dev.gr1.db.orm.ORMSystem;
import dev.gr1.db.orm.TableContext;
import dev.gr1.db.orm.TableDesc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Dao<T> {
    protected final TableContext context;

    public Dao(Connection connection, Class<T> clazz) throws SQLException {
        TableDesc desc = ORMSystem.createDescFor(clazz);
        this.context = ORMSystem.createContext(connection, desc);
    }

    @SuppressWarnings("unchecked")
    public T select(int id) throws SQLException {
        return (T) ORMSystem.select(context, id);
    }

    @SuppressWarnings("unchecked")
    public List<T> selectAll() throws SQLException {
        return (List<T>) ORMSystem.queryAll(context);
    }

    public void insert(T obj) throws SQLException {
        ORMSystem.insert(context, obj);
    }

    public void update(T obj) throws SQLException {
        ORMSystem.update(context, obj);
    }

    public void delete(Object idOrObject) throws SQLException {
        ORMSystem.delete(context, idOrObject);
    }
}
