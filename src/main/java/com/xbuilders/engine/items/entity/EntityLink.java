/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.entity;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemType;

import java.util.function.Supplier;

/**
 * @author zipCoder933
 */
public class EntityLink extends Item {

    public final Supplier<Entity> supplier;

    public EntityLink(int id, String name, Supplier<Entity> supplier) {
        super(id, name, ItemType.ENTITY_LINK);
        this.supplier = supplier;
    }

}
