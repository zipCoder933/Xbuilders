/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.IntMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zipCoder933
 */
public class ToolList {

    File iconDirectory;
    int defaultIcon;

    final IntMap<Item> idMap = new IntMap<>(Item.class);
    private Item[] list;

    public Item[] getList() {
        return list;
    }

    public Item getItem(short blockID) {
        return idMap.get(blockID);
    }

    public void init(File iconDirectory, int defaultIcon) throws IOException {
        this.iconDirectory = iconDirectory;
        this.defaultIcon = defaultIcon;
    }


    private int assignMapAndVerify(List<Item> inputItems) {
        System.out.println("\nChecking block IDs");
        int highestId = 0;
        HashMap<Integer, Item> map = new HashMap<>();

        for (int i = 0; i < inputItems.size(); i++) {
            if (inputItems.get(i) == null) {
                System.err.println("item at index " + i + " is null");
                continue;
            }
            int id = inputItems.get(i).id;
            if (map.get(id) != null) {
                System.err.println("Block " + inputItems.get(i) + " ID conflicts with an existing ID: " + id);
            }
            map.put(id, inputItems.get(i));
            if (id > highestId) {
                highestId = id;
            }
        }
        System.out.println("\t(The highest item ID is: " + highestId + ")");
        System.out.print("\tID Gaps: ");
        for (int id = 1; id < highestId; id++) {
            boolean found = false;
            for (Item item : inputItems) {
                if (item.id == id) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.print(id + ", ");
            }
        }
        System.out.println("");
        idMap.setList(map);
        return highestId;
    }

    public void setItems(List<Item> inputBlocks) {
        list = inputBlocks.toArray(new Item[0]);
        assignMapAndVerify(inputBlocks);

        int i = 0;
        try {
            for (Item block : getList()) {
                if (block.initializationCallback != null) {
                    block.initializationCallback.accept(block);
                }
                block.initIcon(iconDirectory, defaultIcon);
                i++;
            }
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }

}
