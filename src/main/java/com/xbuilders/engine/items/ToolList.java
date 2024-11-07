/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.IntMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public void setItems(List<Item> inputBlocks) {
        list = inputBlocks.toArray(new Item[0]);

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
