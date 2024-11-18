/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.entity;


/**
 * @author zipCoder933
 */
public class EntitySupplier {
    public final short id;
    private final Supplier2 supplier;

    public EntitySupplier(int id, Supplier2 supplier) {
        this.id = (short) id;
        this.supplier = supplier;
    }

    public Entity get(long uniqueIdentifier) {
        return supplier.get(uniqueIdentifier);
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