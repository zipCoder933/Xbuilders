/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.utils.ErrorHandler;

import java.io.File;
import java.io.IOException;

/**
 * @author zipCoder933
 */
public class EntityList extends ItemGroup<EntityLink> {

    File blockIconDirectory, iconDirectory;
    int defaultIcon;

    public void init(File iconDirectory, int defaultIcon) {
        this.iconDirectory = iconDirectory;
        this.defaultIcon = defaultIcon;
    }

    @Override
    public void setItems(EntityLink[] inputBlocks) {
        if (inputBlocks == null) {
            itemList = new EntityLink[0];
            return;
        }
        assignIDMapAndCheckIDs(inputBlocks);
        itemList = new EntityLink[idMap.size()];
        int i = 0;
        try {
            for (EntityLink block : getIdMap().values()) {
                itemList[i] = block;
                if (block.initializationCallback != null) {
                    block.initializationCallback.accept(block);
                }
                block.initIcon(iconDirectory, defaultIcon);
                i++;
            }
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        }
    }

}
