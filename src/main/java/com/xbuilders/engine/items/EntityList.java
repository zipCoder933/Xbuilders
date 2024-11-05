/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.items.entity.ItemDropEntityLink;
import com.xbuilders.engine.utils.ErrorHandler;

import java.io.File;
import java.io.IOException;

import static com.xbuilders.engine.utils.ArrayUtils.combineArrays;

/**
 * @author zipCoder933
 */
public class EntityList extends ItemGroup<EntityLink> {

    File iconDirectory;
    int defaultIcon;

    //Predefined entities
   public static ItemDropEntityLink ENTITY_ITEM_DROP = new ItemDropEntityLink();

    public EntityList() {
        super(EntityLink.class);
    }

    public void init(File iconDirectory, int defaultIcon) {
        this.iconDirectory = iconDirectory;
        this.defaultIcon = defaultIcon;
    }

    @Override
    public void setItems(EntityLink[] inputBlocks) {
        inputBlocks = combineArrays(
                new EntityLink[]{ENTITY_ITEM_DROP},
                inputBlocks);

        setList(inputBlocks);

        int i = 0;
        try {
            for (EntityLink entity : getList()) {//init all entities
                entity.initIcon(iconDirectory, defaultIcon);
                i++;
            }
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }

}