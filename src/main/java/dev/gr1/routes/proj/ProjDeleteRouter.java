package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.routes.Utils;
import spark.Request;
import spark.Response;
import spark.Route;

public class ProjDeleteRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (Utils.isExpired(request)) return Utils.expired();

            int id = Utils.intParam(request.params(":id"));
            if (id < 0) {
                return Utils.fail("Invalid id");
            }

            ProjektDao dao = Main.DB.dao();
            Projekt projekt = dao.select(id);
            if (projekt == null) return Utils.fail("No such project");
            dao.deleteProject(projekt);
            return Utils.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.fail(e.getMessage());
        }
    }
}
