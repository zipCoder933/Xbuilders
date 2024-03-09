/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import java.util.function.Consumer;

/**
 *
 * @author zipCoder933
 */
public class Tool extends Item {

    protected Consumer<Tool> initializationCallback;

    public Tool(int id, String name) {
        super(id, name, ItemType.TOOL);
    }

    public Tool(int id, String name, Consumer<Tool> initializationCallback) {
        super(id, name, ItemType.TOOL);
        this.initializationCallback = initializationCallback;
    }
}
