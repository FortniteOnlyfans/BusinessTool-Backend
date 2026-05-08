package dev.gr1;

import dev.gr1.args.ArgParser;
import dev.gr1.args.Args;
import dev.gr1.db.Database;
import dev.gr1.db.bind.User;
import dev.gr1.db.dao.Dao;
import spark.Spark;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        Database database = new Database("DB.sqlite");
        Dao<User> userDao = database.dao();

        User user = new User();
        user.NAME = "TestPerson";
        user.PWDHASH = "lololol";
        userDao.insert(user);
        System.out.println("Inserted new user with id: " + user.ID);

        List<User> users = userDao.selectAll();
        System.out.println(users);

        System.exit(0);


        ArgParser argParser = new ArgParser();
        argParser.addArg(ArgParser.ArgFlavor.Value, "port", 4000);

        Args parsedArgs = argParser.parse(args);

        int port = parsedArgs.getInt("port");

        Spark.port(port);

        Spark.get("/", (request, response) -> {
            return "Hello World!";
        });

        System.out.printf("Server started on port %d.\n", port);
    }
}
