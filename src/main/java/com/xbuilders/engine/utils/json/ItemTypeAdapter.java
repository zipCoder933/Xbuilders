/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.json;

/**
 * @author zipCoder933
 */

import com.google.gson.*;
import com.xbuilders.engine.items.item.Item;

import java.lang.reflect.Type;

/**
 * Note that this is for saving and loading items as a JSON file, it is not used for item ids
 */
public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.id);
        jsonObject.addProperty("name", src.name);
        if (src.getBlock() != null) {
            jsonObject.addProperty("block", src.getBlock().id);
        }
        if (src.getEntity() != null) {
            jsonObject.addProperty("entity", src.getEntity().id);
        }
        jsonObject.addProperty("icon", src.iconFilename);

        return jsonObject;
    }

    /**
     * This should be called AFTER blocks and entities are loaded
     * @param json
     * @param typeOfT
     * @param context
     * @return
     * @throws JsonParseException
     */
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        String name = jsonObject.get("name").getAsString();

        Item item = new Item(id,name);

        if(jsonObject.has("block")){
            short blockID = jsonObject.get("block").getAsShort();
            item.setBlock(blockID);
        }
        if(jsonObject.has("entity")){
            short entityID = jsonObject.get("entity").getAsShort();
            item.setEntity(entityID);
        }
        if (jsonObject.has("icon"))
            item.iconFilename = jsonObject.get("icon").getAsString();

        return item;
    }
}
