package dev.gr1.db;

import dev.gr1.db.dao.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class Database {
    private final Connection connection;
    private HashMap<Class<?>, Dao<?>> daos;
    private HashMap<Class<?>, DaoSupply<?>> customSupplies;

    public Database(String filename) {
        this.daos = new HashMap<>();
        this.customSupplies = new HashMap<>();

        String url = "jdbc:sqlite:" + filename;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.err.println("Error connecting to SQLite: " + e.getMessage());
        }

        this.connection = conn;
    }

    public <T> void registerCustomDao(Class<T> forClass, DaoSupply<T> supply) {
        this.customSupplies.put(forClass, supply);
    }

    @SuppressWarnings("unchecked")
    public synchronized  <D extends Dao<T>, T> D dao(T... ignore) {
        Class<T> clazz = (Class<T>) ignore.getClass().getComponentType();
        return (D) daos.computeIfAbsent(clazz, c -> {
            try {
                if (customSupplies.containsKey(c)) {
                    DaoSupply supply = customSupplies.get(c);
                    return supply.createDao(connection, clazz);
                }

                return new Dao<T>(connection, clazz);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void close() throws SQLException {
        connection.close();
    }

    public interface DaoSupply<T> {
        Dao<T> createDao(Connection connection, Class<T> cls) throws SQLException;
    }
}
