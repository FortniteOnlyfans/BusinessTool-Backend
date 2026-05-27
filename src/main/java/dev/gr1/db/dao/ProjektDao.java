package dev.gr1.db.dao;

import dev.gr1.Main;
import dev.gr1.db.bind.*;
import dev.gr1.proj.ProjectType;

import java.sql.Connection;
import java.sql.SQLException;

public class ProjektDao extends Dao<Projekt> {
    public ProjektDao(Connection connection, Class<Projekt> clazz) throws SQLException {
        super(connection, clazz);
    }

    public ProjektVersion selectLatest(int projId) throws SQLException {
        Projekt proj = select(projId);
        Dao<ProjektVersion> pvDao = Main.DB.dao();
        return pvDao.select(proj.latestID);
    }

    public ProjektVersion createNewVersion(int projId, int userId, int zeitspanne) throws SQLException {
        ProjektVersion newVersion = new ProjektVersion();
        newVersion.userID = userId;
        newVersion.zeitspanne = zeitspanne;

        newVersion.erstellt = System.currentTimeMillis();
        newVersion.ProjektID = projId;

        Finanzierung finanzierung = new Finanzierung();
        Dao<Finanzierung> finanzierungDao = Main.DB.dao();
        finanzierungDao.insert(finanzierung);
        newVersion.finanzierungID = finanzierung.ID;

        Kapital kapital = new Kapital();
        Dao<Kapital> kapitalDao = Main.DB.dao();
        kapitalDao.insert(kapital);
        newVersion.kapitalID = kapital.ID;

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
        proj.latestID = newVersion.ID;
        update(proj);

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

        return newProjekt;
    }
}
