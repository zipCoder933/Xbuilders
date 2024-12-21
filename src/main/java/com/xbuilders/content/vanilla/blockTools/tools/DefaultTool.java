package com.xbuilders.content.vanilla.blockTools.tools;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.client.player.raycasting.CursorRay;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.content.vanilla.blockTools.BlockTool;
import com.xbuilders.content.vanilla.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class DefaultTool extends BlockTool {
    public DefaultTool(BlockTools tools, CursorRay cursorRay) {
        super("Default", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\default.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean activationKey(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_1) return true;
        return false;
    }

    @Override
    public void activate() {
        GameScene.player.camera.cursorRay.disableBoundaryMode();
    }
}
