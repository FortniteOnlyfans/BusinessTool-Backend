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
        argParser.addArg(ArgParser.ArgFlavor.Value, "port", 4000);

        Args parsedArgs = argParser.parse(args);

        int port = parsedArgs.getInt("port");

        Spark.port(port);

        Spark.post("/register", new RegisterRouter());
        Spark.post("/login", new LoginRouter());
        Spark.get("/deleteAcc", new DeleteAccRouter());

        System.out.printf("Server started on port %d.\n", port);
    }
}
