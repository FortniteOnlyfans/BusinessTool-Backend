package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.proj.GeldType;
import dev.gr1.proj.GeldUtils;
import dev.gr1.routes.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class ProjInfoRouter implements Route {

    @Override
    public Object handle(Request request, Response response) {
        try {
            if (Utils.isExpired(request)) return Utils.expired();

            int id = Utils.intParam(request.params(":id"));
            if (id < 0) {
                return Utils.fail("Invalid id");
            }
            ProjektDao dao = Main.DB.dao();
            Projekt projekt = dao.select(id);
            if (projekt == null) return Utils.fail("No such project");
            JSONObject payload = new JSONObject();
            payload.put("name", projekt.Name);
            payload.put("type", projekt.type);
            if (projekt.latestID != null) {
                payload.put("latest", projekt.latestID);
            }
            List<Integer> versions = dao.allVersionsIDs(id);
            payload.put("versions", versions);
            payload.put("created", projekt.creationDate);
            List<Geld> startKosten = GeldUtils.allGeld(projekt.startKostenID, GeldType.StartKosten);
            JSONArray geldArray = GeldUtils.toJson(startKosten);
            payload.put("startKosten", geldArray);
            return Utils.success(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.fail(e.getMessage());
        }
    }
}
