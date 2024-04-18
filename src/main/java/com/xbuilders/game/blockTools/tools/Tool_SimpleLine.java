package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.game.Main;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.window.KeyCombination;
import org.joml.Vector3i;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

public class Tool_SimpleLine extends BlockTool {
    public Tool_SimpleLine() {
        super("Simple Line", new int[]{
                GLFW.GLFW_KEY_3//TODO: Add key combinations
        });
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
       if (button == UserControlledPlayer.CREATE_MOUSE_BUTTON && action == GLFW.GLFW_PRESS) {

            Block block = (Block) Main.game.getSelectedItem();
            int originX = (int) GameScene.player.camera.cursorRay.getHitPos().x;
            int originY = (int) GameScene.player.camera.cursorRay.getHitPos().y;
            int originZ = (int) GameScene.player.camera.cursorRay.getHitPos().z;

            int x = (int) GameScene.player.camera.cursorRay.getHitPos().x;
            int y = (int) GameScene.player.camera.cursorRay.getHitPos().y;
            int z = (int) GameScene.player.camera.cursorRay.getHitPos().z;
            Vector3i normal = GameScene.player.camera.cursorRay.getHitNormalAsInt();

            for (int i = 0; i < 20; i++) {
                if (GameScene.world.getBlock(x, y, z).solid
                        && x != originX
                        && y != originY
                        && z != originZ) break;

                GameScene.player.setBlock(block, x, y, z);
                x += normal.x;
                y += normal.y;
                z += normal.z;
            }
            return true;
        }
        return true;
    }
}
