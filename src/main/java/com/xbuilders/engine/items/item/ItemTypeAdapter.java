/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.item;

/**
 *
 * @author zipCoder933
 */
import com.google.gson.*;
import com.xbuilders.engine.items.Registrys;

import java.lang.reflect.Type;

public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
//        System.out.println("Using custom type adapter");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("itemType", src.itemType.toString());
        jsonObject.addProperty("id", src.id);
        return jsonObject;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ItemType itemType = ItemType.valueOf(jsonObject.get("itemType").getAsString());
        short id = jsonObject.get("id").getAsShort();
        // You may need to adjust this part based on your constructor
       return Registrys.getItem(id, itemType);
    }
}
