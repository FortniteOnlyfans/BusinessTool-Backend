package dev.gr1.routes.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.FreemiumProjektVersion;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.bind.User;
import dev.gr1.db.dao.Dao;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.db.dao.ProjektVersionDao;
import dev.gr1.proj.GeldType;
import dev.gr1.proj.GeldUtils;
import dev.gr1.routes.Utils;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class ProjVerInfoRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (Utils.isExpired(request)) return Utils.expired();

            int vid = Utils.intParam(request.params(":vid"));
            if (vid < 0) {
                return Utils.fail("Invalid version id");
            }

            ProjektVersionDao dao = Main.DB.dao();
            ProjektVersion version = dao.select(vid);
            if (version == null) return Utils.fail("No such version");

            ProjektDao projektDao = Main.DB.dao();
            Projekt projekt = projektDao.select(version.ProjektID);

            Dao<User> userDao = Main.DB.dao();
            User user = userDao.select(version.userID);

            JSONObject payload = new JSONObject();
            payload.put("erstellt", version.erstellt);
            payload.put("userName", user.Name);

            payload.put("kosten", GeldUtils.toJson(GeldUtils.allGeld(version.kostenID, GeldType.Kosten)));
            payload.put("finanzierung", GeldUtils.toJson(GeldUtils.allGeld(version.finanzierungID, GeldType.Finanzierung)));
            payload.put("kapital", GeldUtils.toJson(GeldUtils.allGeld(version.kapitalID, GeldType.Kapital)));
            payload.put("privat", GeldUtils.toJson(GeldUtils.allGeld(version.privatID, GeldType.Privat)));
            payload.put("ertrag", GeldUtils.toJson(GeldUtils.allGeld(version.ertragID, GeldType.Ertrag)));

            if (projekt.isFreemium()) {
                FreemiumProjektVersion freemium = dao.selectFreemium(vid);
                JSONObject freemiumObj = new JSONObject();
                freemiumObj.put("aboZeit", freemium.AboZeit);
                freemiumObj.put("basisNutzer", freemium.BasisNutzer);
                freemiumObj.put("premiumNutzer", freemium.PremiumNutzer);
                freemiumObj.put("preisPremium", freemium.PreisPremium);
                payload.put("extra", freemiumObj);
            }

            return Utils.success(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.fail(e.getMessage());
        }
    }
}
