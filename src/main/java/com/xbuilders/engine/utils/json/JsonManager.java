/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.json;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;

/**
 * @author zipCoder933
 */
public class JsonManager {
    public static BlockTextureTypeAdapter textureAdapter = new BlockTextureTypeAdapter();
    public static Gson gson_itemAdapter = new GsonBuilder()
            .registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter())
            .create();

    public static Gson gson_itemStackAdapter = new GsonBuilder()
            .registerTypeHierarchyAdapter(Item.class, new ItemStackTypeAdapter())
            .create();

    public static Gson gson_blockAdapter = new GsonBuilder()
            .registerTypeHierarchyAdapter(Block.class, new BlockTypeAdapter())
            .registerTypeAdapter(BlockTexture.class, textureAdapter)
            .create();

    public static String printSmileData(byte[] data) {
        return (data.length + " bytes\t " + new String(data).replaceAll("\n", ""));
    }
}