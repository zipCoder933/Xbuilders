/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.player.camera.CursorRay;
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

    // <editor-fold defaultstate="collapsed" desc="tool events">
    //Create a functional interface for setBlockEvent


    //A functional interface for onLocalChange
    @FunctionalInterface
    public interface OnClickEvent {

        public void run(CursorRay ray);
    }

    OnClickEvent createClickEvent = null;
    OnClickEvent deleteClickEvent = null;


    public void setCreateClickEvent(OnClickEvent createClickEvent) {
        this.createClickEvent = createClickEvent;
    }


    public void setDeleteClickEvent(OnClickEvent deleteClickEvent) {
        this.deleteClickEvent = deleteClickEvent;
    }

    public void run_createClickEvent(CursorRay ray) {
        if (createClickEvent != null) {
            createClickEvent.run(ray);
        }
    }

    public void run_deleteClickEvent(CursorRay ray) {
        if (deleteClickEvent != null) {
            deleteClickEvent.run(ray);
        }
    }
    // </editor-fold>
}
