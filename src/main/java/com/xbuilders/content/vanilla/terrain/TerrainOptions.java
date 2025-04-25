package com.xbuilders.content.vanilla.terrain;

import java.util.HashMap;

public class TerrainOptions extends HashMap<String, Object> {

    public TerrainOptions(TerrainOptions options) {
        super(options);
    }


    public TerrainOptions() {
        super();
    }

    public Boolean getBoolean(String key) {
        Object o = get(key);
        if (o instanceof Boolean) return (Boolean) o;
        return false;
    }

    public int getInt(String key) {
        Object o = get(key);
        if (o instanceof Integer) return (Integer) o;
        return 0;
    }

    public float getFloat(String key) {
        Object o = get(key);
        if (o instanceof Float) return (Float) o;
        return 0;
    }

    public void setBoolean(String key, Boolean value) {
        put(key, value);
    }

    public void setInt(String key, int value) {
        put(key, value);
    }

    public void setFloat(String key, float value) {
        put(key, value);
    }
}
