package dev.gr1.db.dao;

import dev.gr1.Main;
import dev.gr1.db.bind.*;
import dev.gr1.proj.GeldType;
import dev.gr1.proj.GeldUtils;
import dev.gr1.proj.ProjectType;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class ProjektDao extends Dao<Projekt> {
    public ProjektDao(Connection connection, Class<Projekt> clazz) throws SQLException {
        super(connection, clazz);
    }

    public ProjektVersion selectLatest(int projId) throws SQLException {
        Projekt proj = select(projId);
        Dao<ProjektVersion> pvDao = Main.DB.dao();
        return pvDao.select(proj.latestID);
    }

    public ProjektVersion createNewVersion(int projId, int userId, JSONObject extra) throws SQLException {
        ProjektVersion newVersion = new ProjektVersion();
        newVersion.userID = userId;

        newVersion.erstellt = System.currentTimeMillis();
        newVersion.ProjektID = projId;

        Finanzierung finanzierung = new Finanzierung();
        Dao<Finanzierung> finanzierungDao = Main.DB.dao();
        finanzierungDao.insert(finanzierung);
        newVersion.finanzierungID = finanzierung.ID;

        Kosten kosten = new Kosten();
        Dao<Kosten> kostenDao = Main.DB.dao();
        kostenDao.insert(kosten);
        newVersion.kostenID = kosten.ID;

        Privat privat = new Privat();
        Dao<Privat> privatDao = Main.DB.dao();
        privatDao.insert(privat);
        newVersion.privatID = privat.ID;

        Ertrag ertrag = new Ertrag();
        Dao<Ertrag> ertragDao = Main.DB.dao();
        ertragDao.insert(ertrag);
        newVersion.ertragID = ertrag.ID;

        Projekt proj = select(projId);
        Dao<ProjektVersion> pvDao = Main.DB.dao();
        pvDao.insert(newVersion);
        if (proj.firstID == null) {
            proj.firstID = newVersion.ID;
        }
        update(proj);
        proj.latestID = newVersion.ID;

        if (proj.type.equals(ProjectType.Freemium.name())) {
            Dao<FreemiumProjektVersion> freemiumDao = Main.DB.dao();
            FreemiumProjektVersion newFreemium = new FreemiumProjektVersion();
            newFreemium.BasisNutzer = extra.getInt("basisNutzer");
            newFreemium.PremiumNutzer = extra.getInt("premiumNutzer");
            newFreemium.PreisPremium = extra.getDouble("preisPremium");
            newFreemium.Wachstumsrate = extra.getDouble("wachstumsrate");
            newFreemium.AboZeit = extra.getInt("aboZeit");
            newFreemium.ProjektVersionID = newVersion.ID;
            freemiumDao.insert(newFreemium);

            Geld[] gelder = GeldUtils.fromJson(extra.getJSONArray("varKosten"), GeldType.Freemium_VarKosten, newFreemium.ID);
            GeldUtils.insertAll(gelder);
        }

        return newVersion;
    }

    public Projekt createNewProject(int userId, ProjectType type, String name) throws SQLException {
        Projekt newProjekt = new Projekt();
        newProjekt.userID = userId;
        newProjekt.type = type.name();
        newProjekt.Name = name;

        StartKosten startKosten = new StartKosten();
        Dao<StartKosten> startKostenDao = Main.DB.dao();
        startKostenDao.insert(startKosten);
        newProjekt.startKostenID = startKosten.ID;

        Kapital kapital = new Kapital();
        Dao<Kapital> kapitalDao = Main.DB.dao();
        kapitalDao.insert(kapital);
        newProjekt.kapitalID = kapital.ID;

        return newProjekt;
    }

    public List<ProjektVersion> allVersions(int projId) throws SQLException {
        ProjektVersionDao versionDao = Main.DB.dao();
        return versionDao.selectAll()
                .stream()
                .filter(v -> v.ProjektID == projId)
                .sorted(Comparator.comparingLong(v -> v.erstellt))
                .toList();
    }

    public List<Integer> allVersionsIDs(int projId) throws SQLException {
        ProjektVersionDao versionDao = Main.DB.dao();
        return versionDao.selectAll()
                .stream()
                .filter(v -> v.ProjektID == projId)
                .sorted(Comparator.comparingLong(v -> v.erstellt))
                .map(v -> v.ID)
                .toList();
    }

    public void deleteProject(Projekt projekt) throws SQLException {
        ProjektVersionDao versionDao = Main.DB.dao();
        for(int versionId : allVersionsIDs(projekt.ID)) {
            versionDao.deleteVersion(versionId);
        }
        GeldDao geldDao = Main.DB.dao();
        List<Geld> startGelder = GeldUtils.allGeld(projekt.startKostenID, GeldType.StartKosten);
        for (Geld startGeld : startGelder) {
            geldDao.delete(startGeld);
        }
        Dao<StartKosten> startKostenDao = Main.DB.dao();
        startKostenDao.delete(projekt.startKostenID);

        List<Geld> kapitalGeld = GeldUtils.allGeld(projekt.kapitalID, GeldType.Kapital);
        GeldUtils.deleteAll(kapitalGeld);
        Dao<Kapital> kapitalDao = Main.DB.dao();
        kapitalDao.delete(projekt.kapitalID);

        ProjektDao projektDao = Main.DB.dao();
        projektDao.delete(projekt);


    }
}
