package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.FreemiumProjektVersion;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.dao.Dao;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.db.dao.ProjektVersionDao;
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
                return Utils.fail("Invalid version id");
            }

            ProjektDao projektDao = Main.DB.dao();
            Projekt project = projektDao.select(pid);
            if (project.isFreemium()) {
                FreemiumProjektVersion newFreemium = new FreemiumProjektVersion();
                Dao<FreemiumProjektVersion> freemiumDao = Main.DB.dao();
                freemiumDao.insert(newFreemium);
            }

            ProjektDao dao = Main.DB.dao();
            ProjektVersion version = dao.createNewVersion(pid, user);

            JSONObject payload = new JSONObject();
            payload.put("projVerId", version.ID);
            return Utils.success(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.fail(e.getMessage());
        }
    }
}
