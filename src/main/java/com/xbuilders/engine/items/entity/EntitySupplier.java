/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.entity;

import java.util.HashSet;
import java.util.function.Supplier;

/**
 * @author zipCoder933
 */
public class EntitySupplier {
    public final short id;
    public final String name;
    public final Supplier<Entity> supplier;

    public EntitySupplier(int id, String name, Supplier<Entity> supplier) {
        this.id = (short)id;
        this.name = name;
        this.supplier = supplier;
    }
}
