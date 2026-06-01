package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.proj.GeldType;
import dev.gr1.proj.GeldUtils;
import dev.gr1.proj.ProjectType;
import dev.gr1.routes.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class ProjCreateRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (Utils.isExpired(request)) return Utils.expired();
            int user = Utils.userID(request);

            JSONObject body = new JSONObject(request.body());
            String name = body.getString("name");
            String type = body.getString("type");
            JSONArray startKosten = body.getJSONArray("startKosten");
            ProjectType projectType = ProjectType.valueOf(type);

            ProjektDao dao = Main.DB.dao();
            Projekt projekt = dao.createNewProject(user, projectType, name);
            dao.insert(projekt);

            Geld[] geld = GeldUtils.fromJson(startKosten, GeldType.StartKosten, projekt.startKostenID);
            GeldUtils.insertAll(geld);

            JSONObject payload = new JSONObject();
            payload.put("projId", projekt.ID);
            return Utils.success(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.fail(e.getMessage());
        }
    }
}
