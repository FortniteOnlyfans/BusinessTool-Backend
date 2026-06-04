package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.proj.GeldType;
import dev.gr1.proj.GeldUtils;
import dev.gr1.routes.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class ProjVerCreateRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (Utils.isExpired(request)) return Utils.expired();
            int user = Utils.userID(request);

            int pid = Utils.intParam(request.params(":pid"));
            if (pid < 0) {
                return Utils.fail("Invalid project id");
            }

            JSONObject body = new JSONObject(request.body());

            ProjektDao dao = Main.DB.dao();
            ProjektVersion version = dao.createNewVersion(pid, user, body.getJSONObject("extra"));

            if (body.has("kosten")) {
                JSONArray kosten = body.getJSONArray("kosten");
                Geld[] geld = GeldUtils.fromJson(kosten, GeldType.Kosten, version.kostenID);
                GeldUtils.insertAll(geld);
            }

            if (body.has("finanzierung")) {
                JSONArray finanzierung = body.getJSONArray("finanzierung");
                Geld[] geld = GeldUtils.fromJson(finanzierung, GeldType.Finanzierung, version.finanzierungID);
                GeldUtils.insertAll(geld);
            }

            if (body.has("kapital")) {
                JSONArray kapital = body.getJSONArray("kapital");
                Geld[] geld = GeldUtils.fromJson(kapital, GeldType.Kapital, version.kapitalID);
                GeldUtils.insertAll(geld);
            }

            if (body.has("privat")) {
                JSONArray privat = body.getJSONArray("privat");
                Geld[] geld = GeldUtils.fromJson(privat, GeldType.Privat, version.privatID);
                GeldUtils.insertAll(geld);
            }

            if (body.has("ertrag")) {
                JSONArray ertrag = body.getJSONArray("ertrag");
                Geld[] geld = GeldUtils.fromJson(ertrag, GeldType.Ertrag, version.ertragID);
                GeldUtils.insertAll(geld);
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
