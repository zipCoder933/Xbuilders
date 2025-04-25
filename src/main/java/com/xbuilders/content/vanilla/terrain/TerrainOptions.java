package com.xbuilders.content.vanilla.terrain;

import com.xbuilders.engine.client.settings.BoundedFloat;
import com.xbuilders.engine.client.settings.BoundedInt;

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

    public BoundedInt getInt(String key) {
        Object o = get(key);
        if (o instanceof BoundedInt) return (BoundedInt) o;
        return null;
    }

    public BoundedFloat getFloat(String key) {
        Object o = get(key);
        if (o instanceof BoundedFloat) return (BoundedFloat) o;
        return null;
    }

    public void setBoolean(String key, Boolean value) {
        put(key, value);
    }

    public void setInt(String key, BoundedInt value) {
        put(key, value);
    }

    public void setFloat(String key, BoundedFloat value) {
        put(key, value);
    }
}
