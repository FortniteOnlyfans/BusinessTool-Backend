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

public class RegisterRouter implements Route {
    @Override
    public Object handle(Request request, Response response) {
        String body = request.body();
        try {
            JSONObject object = new JSONObject(body);
            String username = object.getString("username");
            String password = object.getString("password");
            //check if exists
            Database db = Main.DB;
            Dao<User> usersDAO = db.dao();
            List<User> users = usersDAO.selectAll();
            for (User user : users) {
                boolean isEq = user.Name.equals(username);
                if (isEq) return Utils.fail("Username taken");
            }

            String hashedPW = Auth.hashPassword(password);
            //save to db
            User newUser = new User();
            newUser.Name = username;
            newUser.Pwd = hashedPW;
            usersDAO.insert(newUser);

            return Utils.success();
        } catch (Exception e) {
            return Utils.fail("Invalid request");
        }
    }
    /*
    {
        "username": "name",
        "password": "pwd"
    }

    =>

    {
        "status": "success",
    }
    ,
    {
        "status": "fail",
        "reason": "Username taken"
    }
    */
}
