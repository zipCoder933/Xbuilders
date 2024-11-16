///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.engine.player.data;
//
///**
// * @author zipCoder933
// */
//
//import com.google.gson.*;
//import com.xbuilders.engine.items.item.Item;
//import com.xbuilders.engine.items.item.ItemStack;
//
//import java.lang.reflect.Type;
//
///**
// * Note that this is for saving and loading items as a JSON file, it is not used for item ids
// */
//public class PlayerStuffTypeAdapter implements JsonSerializer<PlayerStuff>, JsonDeserializer<PlayerStuff> {
//
//    @Override
//    public JsonElement serialize(PlayerStuff src, Type typeOfSrc, JsonSerializationContext context) {
//        JsonObject jsonObject = new JsonObject();
//        //serialize the array
//        ItemStack[] arr = src.items;
//
//        for (int i = 0; i < arr.length; i++) {
//            if (arr[i] != null) {
//                jsonObject.add("item" + i, context.serialize(arr[i]));
//            }
//        }
//        return jsonObject;
//    }
//
//    /**
//     * This should be called AFTER blocks and entities are loaded
//     * @param json
//     * @param typeOfT
//     * @param context
//     * @return
//     * @throws JsonParseException
//     */
//    @Override
//    public PlayerStuff deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        JsonObject jsonObject = json.getAsJsonObject();
//        String id = jsonObject.get("id").getAsString();
//        String name = jsonObject.get("name").getAsString();
//
//        Item item = new Item(id,name);
//
//        if(jsonObject.has("block")){
//            short blockID = jsonObject.get("block").getAsShort();
//            item.setBlock(blockID);
//        }
//        if(jsonObject.has("entity")){
//            short entityID = jsonObject.get("entity").getAsShort();
//            item.setEntity(entityID);
//        }
//        if (jsonObject.has("icon"))
//            item.iconFilename = jsonObject.get("icon").getAsString();
//
//        return item;
//    }
//}
