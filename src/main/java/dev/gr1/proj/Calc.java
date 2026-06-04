package dev.gr1.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.FreemiumProjektVersion;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.dao.Dao;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.db.dao.ProjektVersionDao;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class Calc {
    public static Result calculateVersion(int verId) throws SQLException {
        return calculateVersion(verId, true);
    }

    public static Result calculateVersion(int verId, boolean recursion) throws SQLException {
        ProjektDao projektDao = Main.DB.dao();
        ProjektVersionDao versionDao = Main.DB.dao();
        ProjektVersion version = versionDao.select(verId);
        int projId = version.ProjektID;

        Projekt projekt = projektDao.select(projId);

        double kapitalBedarf = GeldUtils.sumGeld(projId, GeldType.StartKosten);

        double umsatz, kosten, deckungsbeitrag, gewinn, rentabilitat, liq;
        if (projekt.isFreemium()) {
            FreemiumProjektVersion freemiumVersion = versionDao.selectFreemium(verId);

            //1<=aboZeit<=12
            double ertrag = GeldUtils.sumGeld(version.ertragID, GeldType.Ertrag);

            umsatz = ertrag + freemiumVersion.PreisPremium * freemiumVersion.PremiumNutzer
                            * freemiumVersion.Wachstumsrate * (12.0 / freemiumVersion.AboZeit);

            double fixKosten = GeldUtils.sumGeld(verId, GeldType.Kosten);
            double varKosten = GeldUtils.sumGeld(freemiumVersion.ID, GeldType.Freemium_VarKosten);
            kosten = fixKosten + varKosten * (freemiumVersion.BasisNutzer + freemiumVersion.PremiumNutzer);
            deckungsbeitrag = umsatz - varKosten;
            gewinn = deckungsbeitrag - fixKosten;

            if (projekt.firstID != null && projekt.firstID == verId) {
                rentabilitat = gewinn - kapitalBedarf;
            } else {
                rentabilitat = gewinn;
            }

            double bestand = GeldUtils.sumGeld(verId, GeldType.Finanzierung) - kapitalBedarf;
            for (int otherVerId : projektDao.allVersionsIDs(projId)) {
                if (!recursion) break;
                Result result = calculateVersion(otherVerId, false);
                if (result == null) continue;
                bestand += result.gewinn;
                if (otherVerId == verId) {
                    break;
                }
            }

            liq = bestand;
        } else {
            return null;
        }

        return new Result(kapitalBedarf, umsatz, kosten, deckungsbeitrag, gewinn, rentabilitat, liq);
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
        o.put("rentabilitat", result.rentabilitat);
        o.put("liquiditat", result.liq);
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

    public record Result(double kapitalBedarf, double umsatz, double kosten, double deckungsbeitrag, double gewinn, double rentabilitat, double liq) {}
    public record DatedResult(long date, Result result) {}
}
