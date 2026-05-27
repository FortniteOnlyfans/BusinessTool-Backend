package dev.gr1.routes;

import dev.gr1.db.bind.User;
import org.json.JSONObject;

import java.util.List;
import java.util.function.Consumer;

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

    public static String success(Consumer<JSONObject> objFunc) {
        JSONObject object = new JSONObject()
                .put("status", "success");
        objFunc.accept(object);
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
}
