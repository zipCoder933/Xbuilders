/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.json;

/**
 * @author zipCoder933
 */

import com.google.gson.*;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class BlockTypeAdapter implements JsonSerializer<Block>, JsonDeserializer<Block> {

    @Override
    public JsonElement serialize(Block src, Type typeOfSrc, JsonSerializationContext context) {
        // System.out.println("Using custom type adapter");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.name);
        jsonObject.addProperty("id", src.id);
        jsonObject.add("texture", JsonManager.textureAdapter.serialize(src.texture, typeOfSrc, context));
        jsonObject.addProperty("icon", src.iconFilename);
        jsonObject.addProperty("solid", src.solid);
        jsonObject.addProperty("opaque", src.opaque);
        jsonObject.addProperty("torch", src.torchlightStartingValue);
        jsonObject.addProperty("type", src.renderType);
        if (src.colorInPlayerHead != null) {
            JsonElement colorElement = context.serialize(src.colorInPlayerHead);
            jsonObject.add("colorInPlayerHead", colorElement);
        }
        // Export tags (arraylist)
        JsonElement tagsElement = context.serialize(src.tags);
        jsonObject.add("tags", tagsElement);

        // Export properties (hashmap)
        JsonElement propertiesElement = context.serialize(src.properties);
        jsonObject.add("properties", propertiesElement);

        return jsonObject;
    }

    @Override
    public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        short id = jsonObject.get("id").getAsShort();
        String name = jsonObject.get("name").getAsString();
        JsonObject textureProperty = jsonObject.get("texture").getAsJsonObject();
        BlockTexture texture = JsonManager.textureAdapter.deserialize(textureProperty, BlockTexture.class, context);

        Block block = new Block(id, name, texture);
        if (jsonObject.has("solid"))
            block.solid = jsonObject.get("solid").getAsBoolean();
        if (jsonObject.has("opaque"))
            block.opaque = jsonObject.get("opaque").getAsBoolean();
        if (jsonObject.has("torch"))
            block.torchlightStartingValue = jsonObject.get("torch").getAsByte();

        if (jsonObject.has("type")) {
            String typeStr = jsonObject.get("type").getAsString();
            if (typeStr == null || isInteger(typeStr)) {// If the type is an integer
                block.renderType = jsonObject.get("type").getAsInt();
            } else { // Otherwise it's a string
                block.renderType = ItemList.blocks.getBlockType(typeStr);
            }
        }
        if (jsonObject.has("icon"))
            block.iconFilename = jsonObject.get("icon").getAsString();

        // Import tags (arraylist)
        if (jsonObject.has("tags")) {
            JsonArray tagsArray = jsonObject.get("tags").getAsJsonArray();
            for (JsonElement tagElement : tagsArray) {
                block.tags.add(tagElement.getAsString());
            }
        }

        if (jsonObject.has("colorInPlayerHead")) {
            JsonElement colorElement = jsonObject.get("colorInPlayerHead");
            float[] jsonColor = context.deserialize(colorElement, float[].class);
            block.colorInPlayerHead[0] = jsonColor[0];
            block.colorInPlayerHead[1] = jsonColor[1];
            block.colorInPlayerHead[2] = jsonColor[2];
            if(jsonColor.length >= 4) block.colorInPlayerHead[3] = jsonColor[3];
            else block.colorInPlayerHead[3] = 1.0f;
        }

        // Import properties (hashmap)
        if (jsonObject.has("properties")) {
            JsonObject propertiesObject = jsonObject.get("properties").getAsJsonObject();
            //Convert it to hashmap
            Set<Map.Entry<String, JsonElement>> entries = propertiesObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().getAsString());
                block.properties.put(entry.getKey(), entry.getValue().getAsString());
            }
            // System.out.println(" Properties: " + block.properties);
        }

        return block;
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
