package dev.gr1;

import dev.gr1.args.ArgParser;
import dev.gr1.args.Args;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
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
