package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.routes.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class ProjListRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (Utils.isExpired(request)) return Utils.expired();
        int user = Utils.userID(request);

        ProjektDao dao = Main.DB.dao();
        List<Projekt> projects = dao.selectAll();

        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        projects.forEach(p -> {
            array.put(new JSONObject().put("id", p.ID).put("name", p.Name));
        });
        obj.put("projects", array);
        return Utils.success(obj);
    }
}
