package com.tessera.engine.common.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tessera.engine.utils.json.gson.BoundedFloatTypeAdapter;
import com.tessera.engine.utils.json.gson.BoundedIntTypeAdapter;
import com.tessera.engine.utils.option.BoundedFloat;
import com.tessera.engine.utils.option.BoundedInt;

class EngineSettingsUtils {//This is supposed to be a private class
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(BoundedInt.class, new BoundedIntTypeAdapter())
            .registerTypeAdapter(BoundedFloat.class, new BoundedFloatTypeAdapter())
            .create();
}
