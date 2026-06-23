package dev.gr1;

import dev.gr1.args.ArgParser;
import dev.gr1.args.Args;
import dev.gr1.db.Database;
import dev.gr1.db.bind.Geld;
import dev.gr1.db.bind.Projekt;
import dev.gr1.db.bind.ProjektVersion;
import dev.gr1.db.dao.GeldDao;
import dev.gr1.db.dao.ProjektDao;
import dev.gr1.db.dao.ProjektVersionDao;
import dev.gr1.routes.auth.DeleteAccRouter;
import dev.gr1.routes.auth.LoginRouter;
import dev.gr1.routes.auth.RegisterRouter;
import dev.gr1.routes.calc.CalcAllRouter;
import dev.gr1.routes.calc.CalcLatestRouter;
import dev.gr1.routes.calc.CalcVersionRouter;
import dev.gr1.routes.proj.*;
import spark.Spark;

import java.sql.SQLException;

public class Main {
    public static Database DB;

    public static void main(String[] args) throws SQLException {
        DB = new Database("DB.sqlite");
        DB.registerCustomDao(Projekt.class, ProjektDao::new);
        DB.registerCustomDao(Geld.class, GeldDao::new);
        DB.registerCustomDao(ProjektVersion.class, ProjektVersionDao::new);




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
            response.header("Access-Control-Expose-Headers", "Authorization");
            response.type("application/json");
        });

        Spark.post("/register", new RegisterRouter());
        Spark.post("/login", new LoginRouter());
        Spark.get("/deleteAcc", new DeleteAccRouter());
        Spark.get("/project/:id/info", new ProjInfoRouter());
        Spark.post("/project/create", new ProjCreateRouter());
        Spark.post("/project/:id/delete", new ProjDeleteRouter());
        Spark.get("/project/version/:vid/info", new ProjVerInfoRouter());
        Spark.post("/project/:pid/version/create", new ProjVerCreateRouter());
        Spark.get("/project/:id/calc/latest", new CalcLatestRouter());
        Spark.get("/project/calc/version/:id", new CalcVersionRouter());
        Spark.get("/project/:id/calc/all", new CalcAllRouter());

        Spark.init();

        System.out.printf("Server started on port %d.\n", port);

        //Spark.awaitStop();
        //DB.close();
    }
}
