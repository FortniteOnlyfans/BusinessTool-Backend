package dev.gr1;

import dev.gr1.args.ArgParser;
import dev.gr1.args.Args;
import dev.gr1.db.Database;
import dev.gr1.db.bind.User;
import dev.gr1.db.dao.Dao;
import dev.gr1.routes.DeleteAccRouter;
import dev.gr1.routes.LoginRouter;
import dev.gr1.routes.RegisterRouter;
import spark.Spark;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static Database DB;

    public static void main(String[] args) throws SQLException {
        DB = new Database("DB.sqlite");

        ArgParser argParser = new ArgParser();
        argParser.addArg(ArgParser.ArgFlavor.Value, "port", 4100);

        Args parsedArgs = argParser.parse(args);

        int port = parsedArgs.getInt("port");

        Spark.port(port);

        // DEV CORS
        Spark.options("/*", (request, response) -> {

            String accessControlRequestHeaders =
                    request.headers("Access-Control-Request-Headers");

            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }

            String accessControlRequestMethod =
                    request.headers("Access-Control-Request-Method");

            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
            }

            return "OK";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods",
                    "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers",
                    "Content-Type,Authorization");
            response.type("application/json");
        });

        Spark.post("/register", new RegisterRouter());
        Spark.post("/login", new LoginRouter());
        Spark.get("/deleteAcc", new DeleteAccRouter());

        Spark.init();

        System.out.printf("Server started on port %d.\n", port);
    }
}
