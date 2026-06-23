package dev.gr1.routes.auth;

import dev.gr1.Main;
import dev.gr1.auth.Auth;
import dev.gr1.db.Database;
import dev.gr1.db.bind.User;
import dev.gr1.db.dao.Dao;
import dev.gr1.routes.Utils;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class DeleteAccRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String token = request.headers("Authorization");
        if (!Auth.trustworthy(token)) {
            return Utils.expired();
        }
        String username = Auth.getSub(token);
        Database db = Main.DB;
        Dao<User> usersDAO = db.dao();
        List<User> users = usersDAO.selectAll();
        User found = Utils.findUser(users, username);
        if (found != null) {
            usersDAO.delete(found);
        }

        Dao<User> userDao = Main.DB.dao();
        List<User> allUsers = userDao.selectAll();

        return Utils.success();
    }
}
