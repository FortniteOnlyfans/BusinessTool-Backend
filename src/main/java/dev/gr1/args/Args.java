package dev.gr1.args;

import java.util.HashMap;

public class Args {
    private final HashMap<String, String> values;

    Args(int expectedAmt) {
        this.values = new HashMap<>(expectedAmt);
    }

    void addValue(String key, String value) {
        values.put(key, value);
    }

    public String getString(String key) {
        return values.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(values.get(key));
    }

    public boolean getBoolean(String key) {
        //awesome code
        return "true".equals(values.get(key));
    }
}
