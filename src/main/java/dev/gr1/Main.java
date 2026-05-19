package dev.gr1;

import dev.gr1.args.ArgParser;
import dev.gr1.args.Args;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        ArgParser argParser = new ArgParser();
        argParser.addArg(ArgParser.ArgFlavor.Value, "port", 4100);

        Args parsedArgs = argParser.parse(args);

        int port = parsedArgs.getInt("port");

        Spark.port(port);

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });

        Spark.get("/cost/test", (request, response) -> {
            return "{cost:100,currency:\"eur\"}";
        });

        Spark.init();

        System.out.printf("Server started on port %d.\n", port);
    }
}
