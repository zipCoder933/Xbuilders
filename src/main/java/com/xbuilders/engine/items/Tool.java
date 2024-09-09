/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.player.CursorRay;
import java.util.function.Consumer;

/**
 *
 * @author zipCoder933
 */
public class Tool extends Item {

    public Consumer<Tool> initializationCallback;

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
        public void run(CursorRay ray, boolean creationMode);
    }

    OnClickEvent clickEvent = null;

    public void setClickEvent(OnClickEvent createClickEvent) {
        this.clickEvent = createClickEvent;
    }


    public void run_ClickEvent(CursorRay ray, boolean creationMode) {
        if (clickEvent != null) {
            clickEvent.run(ray, creationMode);
        }
    }
    // </editor-fold>
}
