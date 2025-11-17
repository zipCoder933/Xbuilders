package com.tessera.content.vanilla.blockTools.tools;

import com.tessera.engine.client.Client;
import com.tessera.engine.client.player.raycasting.CursorRay;
import com.tessera.engine.utils.resource.ResourceUtils;
import com.tessera.content.vanilla.blockTools.BlockTool;
import com.tessera.content.vanilla.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class DefaultTool extends BlockTool {
    public DefaultTool(BlockTools tools, CursorRay cursorRay) {
        super("Default", tools, cursorRay);
        try {
            setIcon(ResourceUtils.file("blockTools\\default.png"));
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
        Client.userPlayer.camera.cursorRay.disableBoundaryMode();
    }
}
