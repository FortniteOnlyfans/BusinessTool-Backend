package dev.gr1.proj;

import dev.gr1.Main;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.dao.GeldDao;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class GeldUtils {
    public static Geld[] fromJson(JSONArray json, GeldType type, int target) {
        Geld[] array = new Geld[json.length()];
        for (int i = 0; i < json.length(); i++) {
            JSONObject geldObj = json.getJSONObject(i);
            Geld geld = new Geld();
            geld.targetID = target;
            geld.target = type.name();
            geld.Name = geldObj.getString("name");
            geld.Wert = geldObj.getFloat("wert");
            if (geldObj.has("zinsen")) {
                geld.Zinsen = geldObj.getDouble("zinsen");
            }
            if (geldObj.has("laufzeit")) {
                geld.Laufzeit = (int) geldObj.getFloat("laufzeit");
            }
            array[i] = geld;
        }
        return array;
    }

    public static double sumGeld(int targetId, GeldType type) throws SQLException {
        List<Geld> gs = allGeld(targetId, type);
        double res = 0;
        for (Geld g : gs) {
            res += g.Wert;
        }
        return res;
    }

    public static void insertAll(Geld[] geld) throws SQLException {
        GeldDao dao = Main.DB.dao();

        for (Geld geld1 : geld) {
            dao.insert(geld1);
        }
    }

    public static List<Geld> allGeld(int targetID, GeldType type) throws SQLException {
        GeldDao geldDao = Main.DB.dao();
        return geldDao.selectAll().stream()
                .filter(g -> g.isTarget(type) && g.targetID == targetID)
                .toList();

    }

    public static void deleteAll(List<Geld> gelder) throws SQLException {
        GeldDao geldDao = Main.DB.dao();
        for (Geld geld : gelder) {
            geldDao.delete(geld);
        }
    }

    public static JSONArray toJson(List<Geld> gelder) {
        JSONArray array = new JSONArray();
        for (Geld geld : gelder) {
            JSONObject object = new JSONObject();
            object.put("name", geld.Name);
            object.put("wert", geld.Wert);
            if (geld.Zinsen != null) {
                object.put("zinsen", geld.Zinsen);
            }
            if (geld.Laufzeit != null) {
                object.put("laufzeit", geld.Laufzeit);
            }
            array.put(object);
        }
        return array;
    }
}
