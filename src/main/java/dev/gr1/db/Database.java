package dev.gr1.db;

import dev.gr1.db.dao.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class Database {
    private final Connection connection;
    private HashMap<Class<?>, Dao<?>> daos;

    public Database(String filename) {
        this.daos = new HashMap<>();

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

    @SuppressWarnings("unchecked")
    public <T> Dao<T> dao(T... ignore) {
        Class<T> clazz = (Class<T>) ignore.getClass().getComponentType();
        return (Dao<T>) daos.computeIfAbsent(clazz, c -> {
            try {
                return new Dao<T>(connection, clazz);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
