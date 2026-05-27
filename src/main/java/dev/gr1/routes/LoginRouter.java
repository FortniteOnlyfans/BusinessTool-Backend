package dev.gr1.routes;

import dev.gr1.Main;
import dev.gr1.auth.Auth;
import dev.gr1.db.Database;
import dev.gr1.db.bind.User;
import dev.gr1.db.dao.Dao;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class LoginRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String body = request.body();
        try {
            JSONObject object = new JSONObject(body);
            String username = object.getString("username");
            String password = object.getString("password");

            //check for existance
            Database db = Main.DB;
            Dao<User> usersDAO = db.dao();
            List<User> users = usersDAO.selectAll();

            User found = Utils.findUser(users, username);
            if (found == null) {
                return Utils.fail("User does not exist");
            }
            if (!Auth.checkPassword(password, found.Pwd)) {
                return Utils.fail("Wrong password");
            }

            String token = Auth.createToken(username);
            response.header("Authorization", token);

            return Utils.success();
        } catch (Exception e) {
            return Utils.fail("Invalid request");
        }
    }
}
