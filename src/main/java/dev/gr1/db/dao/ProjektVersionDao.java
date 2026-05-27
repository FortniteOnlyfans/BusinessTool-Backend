package dev.gr1.db.dao;

import dev.gr1.Main;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.proj.GeldType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProjektVersionDao extends Dao<ProjektVersion> {
    public ProjektVersionDao(Connection connection, Class<ProjektVersion> clazz) throws SQLException {
        super(connection, clazz);
    }

    public List<Geld> allGeld(int projVerId, GeldType type) throws SQLException {
        ProjektVersion p = select(projVerId);

        GeldDao geldDao = Main.DB.dao();
        return geldDao.selectAll().stream()
                .filter(g -> g.isTarget(type) && g.targetID == p.kostenID)
                .toList();

    }
}
