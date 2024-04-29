/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.json;

/**
 * @author zipCoder933
 */

import com.google.gson.*;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;

import java.lang.reflect.Type;

public class BlockTypeAdapter implements JsonSerializer<Block>, JsonDeserializer<Block> {

    @Override
    public JsonElement serialize(Block src, Type typeOfSrc, JsonSerializationContext context) {
//        System.out.println("Using custom type adapter");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.name);
        jsonObject.addProperty("id", src.id);
        jsonObject.add("texture", JsonManager.textureAdapter.serialize(src.texture, typeOfSrc, context));

        jsonObject.addProperty("solid", src.solid);
        jsonObject.addProperty("opaque", src.opaque);
        jsonObject.addProperty("torch", src.torchlightStartingValue);
        jsonObject.addProperty("type", src.type);

        return jsonObject;
    }

    @Override
    public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        short id = jsonObject.get("id").getAsShort();
        String name = jsonObject.get("name").getAsString();
        JsonObject textureProperty = jsonObject.get("texture").getAsJsonObject();
        BlockTexture texture = JsonManager.textureAdapter.deserialize(textureProperty, BlockTexture.class, context);

        Block block = new Block(id, name, texture);
        if (jsonObject.has("solid")) block.solid = jsonObject.get("solid").getAsBoolean();
        if (jsonObject.has("opaque")) block.opaque = jsonObject.get("opaque").getAsBoolean();
        if (jsonObject.has("torch")) block.torchlightStartingValue = jsonObject.get("torch").getAsByte();
        if (jsonObject.has("type")) block.type = jsonObject.get("type").getAsInt();

        return block;
    }
}
