package dev.gr1.db.dao;

import dev.gr1.Main;
import dev.gr1.db.bind.*;
import dev.gr1.proj.GeldType;
import dev.gr1.proj.GeldUtils;
import dev.gr1.proj.ProjectType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProjektVersionDao extends Dao<ProjektVersion> {
    public ProjektVersionDao(Connection connection, Class<ProjektVersion> clazz) throws SQLException {
        super(connection, clazz);
    }

    public void deleteVersion(int versionId) throws SQLException {
        ProjektVersion version = select(versionId);

        List<Geld> privatGeld = GeldUtils.allGeld(version.privatID, GeldType.Privat);
        GeldUtils.deleteAll(privatGeld);
        Dao<Privat> privatDao = Main.DB.dao();
        privatDao.delete(version.privatID);

        List<Geld> ertragGeld = GeldUtils.allGeld(version.ertragID, GeldType.Ertrag);
        GeldUtils.deleteAll(ertragGeld);
        Dao<Ertrag> ertragDao = Main.DB.dao();
        ertragDao.delete(version.ertragID);

        List<Geld> finanzierungGeld = GeldUtils.allGeld(version.finanzierungID, GeldType.Finanzierung);
        GeldUtils.deleteAll(finanzierungGeld);
        Dao<Finanzierung> finanzierungDao = Main.DB.dao();
        finanzierungDao.delete(version.finanzierungID);

        List<Geld> kostenGeld = GeldUtils.allGeld(version.kostenID, GeldType.Kosten);
        GeldUtils.deleteAll(kostenGeld);
        Dao<Privat> kostenDao = Main.DB.dao();
        kostenDao.delete(version.kostenID);

        ProjektDao projektDao = Main.DB.dao();
        Projekt proj = projektDao.select(version.ProjektID);
        if (proj.type.equals(ProjectType.Freemium.name())) {
            Dao<FreemiumProjektVersion> freemiumDao = Main.DB.dao();
            FreemiumProjektVersion freemiumId = freemiumDao.selectAll()
                    .stream()
                    .filter(f -> f.ProjektVersionID == versionId)
                    .findFirst()
                    .get();

            List<Geld> gelder = GeldUtils.allGeld(freemiumId.ID, GeldType.Freemium_VarKosten);
            GeldUtils.deleteAll(gelder);

            freemiumDao.delete(freemiumId);
        }

        delete(version);
    }

    public FreemiumProjektVersion selectFreemium(int versionId) throws SQLException {
        Dao<FreemiumProjektVersion> freemiumDao = Main.DB.dao();
        return freemiumDao.selectAll()
                .stream()
                .filter(f -> f.ProjektVersionID == versionId)
                .findFirst()
                .get();
    }
}
