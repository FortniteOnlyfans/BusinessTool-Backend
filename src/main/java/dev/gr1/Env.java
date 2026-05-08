package dev.gr1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class Env {
    private final static HashMap<String, String> LOCALENV;

    static {
        LOCALENV = new HashMap<>();
        try {
            InputStream fis = new FileInputStream(".env");
            Reader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            List<String> lines = reader.readAllLines();
            for (String line : lines) {
                String[] parts = line.split("=");
                LOCALENV.put(parts[0], parts[1]);
            }
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getInt(String name) {
        String v = getString(name);
        return Integer.parseInt(v);
    }

    public static String getString(String name) {
        String v = LOCALENV.get(name);
        if (v == null) {
            throw new IllegalStateException("Local Env. " + name + " is not defined!");
        }
        return v;
    }
}
