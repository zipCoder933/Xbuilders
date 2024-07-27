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
public class ToolList extends ItemGroup<Tool> {

    File iconDirectory;
    int defaultIcon;

    public ToolList() {
        super(Tool.class);
    }

    public void init(File iconDirectory, int defaultIcon) throws IOException {
        this.iconDirectory = iconDirectory;
        this.defaultIcon = defaultIcon;
    }

    @Override
    public void setItems(Tool[] inputBlocks) {
        setList(inputBlocks);
        int i = 0;
        try {
            for (Tool block : getList()) {
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
