package dev.gr1.args;

import java.util.HashMap;

public class ArgParser {
    private final HashMap<String, Entry> configuredArgs;

    public ArgParser() {
        this.configuredArgs = new HashMap<>();
    }

    public void addArg(ArgFlavor flavor, String key, Object defaultValue) {
        this.configuredArgs.put(key, new Entry(flavor, defaultValue.toString()));
    }

    public Args parse(String[] passedArgs) {
        Args args = new Args(configuredArgs.size());

        configuredArgs.forEach((k, e) -> {
            args.addValue(k, e.defaultValue);
        });

        for (int i = 0; i < passedArgs.length; i++) {
            //strip leading -
            String key = passedArgs[i].substring(1);
            if ("help".equals(key)) {
                //awesome help functionality lmao
                System.out.println("Possible arguments:");
                configuredArgs.forEach((k, f) -> {
                    System.out.printf("%s (%s)\n", k, f.flavor);
                });
                break;
            }

            Entry entry = configuredArgs.get(key);
            if (entry == null) {
                throw new IllegalArgumentException(String.format("'%s' is not a valid argument! Run with -help for a list of values...", key));
            }

            if (entry.flavor == ArgFlavor.Flag) {
                args.addValue(key, "true");
            } else {
                String value = passedArgs[++i];
                args.addValue(key, value);
            }
        }
        return args;
    }

    private record Entry(ArgFlavor flavor, String defaultValue) {}

    public enum ArgFlavor {
        Flag,
        Value
    }
}
