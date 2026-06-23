package dev.gr1.routes;

import dev.gr1.Main;
import dev.gr1.auth.Auth;
import dev.gr1.db.bind.User;
import dev.gr1.db.dao.Dao;
import org.json.JSONObject;
import spark.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Utils {
    public static String fail(String reason) {
        return new JSONObject()
                .put("status", "fail")
                .put("reason", reason)
                .toString();
    }

    public static String success() {
        return new JSONObject()
                .put("status", "success")
                .toString();
    }

    public static String success(JSONObject payload) {
        JSONObject object = new JSONObject()
                .put("status", "success")
                .put("payload", payload);
        return object.toString();
    }

    public static User findUser(List<User> users, String username) {
        for (User user : users) {
            boolean isEq = user.Name.equals(username);
            if (isEq) return user;
        }
        return null;
    }

    public static String expired() {
        return new JSONObject()
                .put("status", "expired")
                .toString();
    }

    public static int intParam(String param) {
        try {
            return Integer.parseInt(param);
        } catch (Throwable _) {
            return -1;
        }
    }

    ///true = expired
    public static boolean isExpired(Request request) {
        String token = request.headers("Authorization");
        System.out.println(token);
        if (token == null) return true;
        return !Auth.trustworthy(token);
    }

    public static int userID(Request request) throws SQLException {
        String token = request.headers("Authorization");
        String sub = Auth.getSub(token);
        Dao<User> userDao = Main.DB.dao();
        return Objects.requireNonNull(findUser(userDao.selectAll(), sub)).ID;
    }
}
