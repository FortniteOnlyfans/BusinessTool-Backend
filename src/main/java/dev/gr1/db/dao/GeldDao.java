package dev.gr1.db.dao;

import dev.gr1.db.bind.Geld;
import dev.gr1.proj.GeldType;

import java.sql.Connection;
import java.sql.SQLException;

public class GeldDao extends Dao<Geld> {
    public GeldDao(Connection connection, Class<Geld> clazz) throws SQLException {
        super(connection, clazz);
    }


}
