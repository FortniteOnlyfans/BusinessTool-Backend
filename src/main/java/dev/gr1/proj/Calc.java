package dev.gr1.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.FreemiumProjektVersion;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.db.dao.ProjektVersionDao;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class Calc {
    public static Result calculateVersion(int verId) throws SQLException {
        ProjektDao projektDao = Main.DB.dao();
        ProjektVersionDao versionDao = Main.DB.dao();
        ProjektVersion version = versionDao.select(verId);
        int projId = version.ProjektID;

        Projekt projekt = projektDao.select(projId);

        double kapitalBedarf = GeldUtils.sumGeld(projId, GeldType.StartKosten);

        double umsatz, kosten, deckungsbeitrag, gewinn;
        if (projekt.isFreemium()) {
            FreemiumProjektVersion freemiumVersion = versionDao.selectFreemium(verId);

            //TODO: hier ertrag einfügen
            //kapital auch zu projekt packen von version weg
            //rentabilität: im 1. version die startkosten abziehen
            //hier auch aboZeit mit einbeziehen!!!
            //bei projektversion zeitspanne wegnehmen und immer ein jahr lang machen
            umsatz = freemiumVersion.PreisPremium * freemiumVersion.PremiumNutzer
                            * freemiumVersion.Wachstumsrate;

            double fixKosten = GeldUtils.sumGeld(verId, GeldType.Kosten);
            double varKosten = GeldUtils.sumGeld(freemiumVersion.ID, GeldType.Freemium_VarKosten);
            kosten = fixKosten + varKosten * (freemiumVersion.BasisNutzer + freemiumVersion.PremiumNutzer);
            deckungsbeitrag = umsatz - varKosten;
            gewinn = deckungsbeitrag - fixKosten;
        } else {
            return null;
        }

        return new Result(kapitalBedarf, umsatz, kosten, deckungsbeitrag, gewinn);
    }

    public static Result calculateLatest(int projId) throws SQLException {
        ProjektDao projektDao = Main.DB.dao();
        Projekt projekt = projektDao.select(projId);
        return calculateVersion(projekt.latestID);
    }

    public static DatedResult[] calculateAllVersions(int projId) throws SQLException {
        ProjektDao projektDao = Main.DB.dao();
        List<ProjektVersion> versions = projektDao.allVersions(projId);
        DatedResult[] res = new DatedResult[versions.size()];
        int i = 0;
        for (ProjektVersion version : versions) {
            Result result = calculateVersion(version.ID);
            res[i++] = new DatedResult(version.erstellt, result);
        }
        return res;
    }

    public static JSONObject toJson(Result result) {
        JSONObject o = new JSONObject();
        o.put("kapitalbedarf", result.kapitalBedarf);
        o.put("umsatz", result.umsatz);
        o.put("kosten", result.kosten);
        o.put("deckungsbeitrag", result.deckungsbeitrag);
        o.put("gewinn", result.gewinn);
        return o;
    }

    public static JSONArray toJsonArray(DatedResult[] results) {
        JSONArray array = new JSONArray();
        for (DatedResult result : results) {
            JSONObject obj = new JSONObject();
            obj.put("date", result.date);
            obj.put("result", toJson(result.result));
            array.put(obj);
        }
        return array;
    }

    public record Result(double kapitalBedarf, double umsatz, double kosten, double deckungsbeitrag, double gewinn) {}
    public record DatedResult(long date, Result result) {}
}
