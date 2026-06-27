package dev.gr1.db.dao;

import dev.gr1.db.bind.FreemiumProjektVersion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FreemiumVersionDao extends Dao<FreemiumProjektVersion> {
    public FreemiumVersionDao(Connection connection, Class<FreemiumProjektVersion> clazz) throws SQLException {
        super(connection, clazz);
    }

    public FreemiumProjektVersion selectForVersion(int vid) throws SQLException {
        List<FreemiumProjektVersion> all = selectAll();
        return all
                .stream()
                .filter(f -> f.ProjektVersionID == vid)
                .findFirst()
                .get();
    }
}
