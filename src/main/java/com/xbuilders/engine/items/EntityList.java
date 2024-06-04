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

    public EntityList() {
        super(EntityLink.class);
    }

    public void init(File iconDirectory, int defaultIcon) {
        this.iconDirectory = iconDirectory;
        this.defaultIcon = defaultIcon;
    }

    @Override
    public void setItems(EntityLink[] inputBlocks) {
        setList(inputBlocks);

        int i = 0;
        try {
            for (EntityLink entity : getList()) {//in
                if (entity.initializationCallback != null) {
                    entity.initializationCallback.accept(entity);
                }
                entity.initIcon(iconDirectory, defaultIcon);
                i++;
            }
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        }
    }

}
