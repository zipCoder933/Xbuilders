package com.xbuilders.game.blockTools;

import com.xbuilders.engine.utils.math.AABB;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

public abstract class BlockTool {
    public final String name;
    public boolean useBlockBoundary = false;

    public BlockTool(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean shouldActivate(int key, int scancode, int action, int mods);

    public void blockBoundarySetEvent(AABB aabb,boolean created) {
        System.out.println("Block boundary event: " + aabb+" "+(created?"Created":"Deleted"));
    }

    /**
     *
     * @param scroll
     * @param xoffset
     * @param yoffset
     * @return true if the event was consumed
     */
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        return false;
    }

    /**
     *
     * @param key
     * @param scancode
     * @param action
     * @param mods
     * @return true if the event was consumed
     */
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }

    public boolean mouseButtonEvent(int button, int action, int mods) {
        return false;
    }
}
