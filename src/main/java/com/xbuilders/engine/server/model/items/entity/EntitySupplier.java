/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.model.items.entity;


import com.xbuilders.engine.utils.WorldCoord;

import java.util.Objects;

/**
 * @author zipCoder933
 */
public class EntitySupplier {
    public final short id;
    private final Supplier2 supplier;
    public WorldCoord spawnCondition;
    public boolean isAutonomous;

    public EntitySupplier(int id, Supplier2 supplier) {
        this.id = (short) id;
        this.supplier = Objects.requireNonNull(supplier);
        //Init the supplier
        supplier.get(0).initSupplier(this);
    }

    public Entity get(long uniqueIdentifier) {
        Entity e = supplier.get(uniqueIdentifier);
        return e;
    }

    @FunctionalInterface
    public static interface Supplier2 {
        /**
         * Gets a result.
         *
         * @return a result
         */
        Entity get(long uniqueIdentifier);
    }
}