package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.FreemiumProjektVersion;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.dao.Dao;
import dev.gr1.db.dao.FreemiumVersionDao;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.db.dao.ProjektVersionDao;
import dev.gr1.proj.GeldType;
import dev.gr1.proj.GeldUtils;
import dev.gr1.proj.ProjectType;
import dev.gr1.routes.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class ProjVerSaveRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (Utils.isExpired(request)) return Utils.expired();
            int user = Utils.userID(request);

            int vid = Utils.intParam(request.params(":vid"));
            if (vid < 0) {
                return Utils.fail("Invalid version id");
            }

            JSONObject body = new JSONObject(request.body());

            ProjektVersionDao dao = Main.DB.dao();
            ProjektDao projDao = Main.DB.dao();
            //ProjektVersion version = dao.createNewVersion(vid, user, body.getJSONObject("extra"));
            ProjektVersion version = dao.select(vid);
            Projekt proj = projDao.select(version.ProjektID);

            if (body.has("kosten")) {
                JSONArray kosten = body.getJSONArray("kosten");
                Geld[] geld = GeldUtils.fromJson(kosten, GeldType.Kosten, version.kostenID);
                GeldUtils.deleteAllGeldFor(version.kostenID, GeldType.Kosten);
                GeldUtils.insertAll(geld);
            }

            if (body.has("finanzierung")) {
                JSONArray finanzierung = body.getJSONArray("finanzierung");
                Geld[] geld = GeldUtils.fromJson(finanzierung, GeldType.Finanzierung, version.finanzierungID);
                GeldUtils.deleteAllGeldFor(version.finanzierungID, GeldType.Finanzierung);
                GeldUtils.insertAll(geld);
            }

            if (body.has("privat")) {
                JSONArray privat = body.getJSONArray("privat");
                Geld[] geld = GeldUtils.fromJson(privat, GeldType.Privat, version.privatID);
                GeldUtils.deleteAllGeldFor(version.privatID, GeldType.Privat);
                GeldUtils.insertAll(geld);
            }

            if (body.has("ertrag")) {
                JSONArray ertrag = body.getJSONArray("ertrag");
                Geld[] geld = GeldUtils.fromJson(ertrag, GeldType.Ertrag, version.ertragID);
                GeldUtils.deleteAllGeldFor(version.ertragID, GeldType.Ertrag);
                GeldUtils.insertAll(geld);
            }

            if (proj.type.equals(ProjectType.Freemium.name())) {
                JSONObject extra = body.getJSONObject("extra");
                FreemiumVersionDao freemiumDao = Main.DB.dao();
                FreemiumProjektVersion newFreemium = freemiumDao.selectForVersion(vid);
                newFreemium.BasisNutzer = extra.getInt("basisNutzer");
                newFreemium.PremiumNutzer = extra.getInt("premiumNutzer");
                newFreemium.PreisPremium = extra.getDouble("preisPremium");
                newFreemium.Wachstumsrate = extra.getDouble("wachstumsrate");
                newFreemium.AboZeit = extra.getInt("aboZeit");
                newFreemium.ProjektVersionID = version.ID;

                Geld[] gelder = GeldUtils.fromJson(extra.getJSONArray("varKosten"), GeldType.Freemium_VarKosten, newFreemium.ID);
                GeldUtils.deleteAllGeldFor(newFreemium.ID, GeldType.Freemium_VarKosten);
                GeldUtils.insertAll(gelder);
            }

            JSONObject payload = new JSONObject();
            payload.put("projVerId", version.ID);
            return Utils.success(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.fail(e.getMessage());
        }
    }
}
