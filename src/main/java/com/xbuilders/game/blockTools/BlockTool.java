package com.xbuilders.game.blockTools;

import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import org.joml.Matrix4f;
import org.lwjgl.nuklear.NkVec2;

public abstract class BlockTool {
    public final String name;
    public final BlockTools blockTools;

    public boolean usesSize = false;
    int size;

    public BlockTool(String name, BlockTools blockTools) {
        this.name = name;
        this.blockTools = blockTools;
    }


    public String toolDescription() {
        return name + (usesSize ? " (x" + size + ")" : "");
    }

    public abstract boolean shouldActivate(int key, int scancode, int action, int mods);

    public void activate() {
    }

    public void changeMode() {
    }

    public void deactivate() {
    }

    public boolean drawCursor(CursorRay ray, Matrix4f proj, Matrix4f view) {
        return false;
    }


    public boolean setBlock(Item item, final CursorRay ray, final BlockData data, boolean isCreationMode) {
        return false;
    }


    /**
     * @param scroll
     * @param xoffset
     * @param yoffset
     * @return true if the event was consumed
     */
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        return false;
    }

    /**
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





