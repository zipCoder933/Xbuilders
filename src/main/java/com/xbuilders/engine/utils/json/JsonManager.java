/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemTypeAdapter;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.game.MyGame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zipCoder933
 */
public class JsonManager {

    public final Gson gson;

    public JsonManager() {
        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter())//Make it work for all extended classes
                //                   .registerTypeAdapter(Block.class, new ItemTypeAdapter())
                .create();
    }

}
