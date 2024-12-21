package com.xbuilders.engine.client.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xbuilders.engine.utils.json.BoundedFloatTypeAdapter;
import com.xbuilders.engine.utils.json.BoundedIntTypeAdapter;

class EngineSettingsUtils {//This is supposed to be a private class
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(BoundedInt.class, new BoundedIntTypeAdapter())
            .registerTypeAdapter(BoundedFloat.class, new BoundedFloatTypeAdapter())
            .create();
}
