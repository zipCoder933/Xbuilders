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
public class ToolList extends ItemGroup<Item> {

    File iconDirectory;
    int defaultIcon;

    public ToolList() {
        super(Item.class);
    }

    public void init(File iconDirectory, int defaultIcon) throws IOException {
        this.iconDirectory = iconDirectory;
        this.defaultIcon = defaultIcon;
    }

    @Override
    public void setItems(Item[] inputBlocks) {
        setList(inputBlocks);
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
