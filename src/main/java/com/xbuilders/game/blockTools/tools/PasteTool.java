package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.rendering.chunk.BlockMeshBundle;
import com.xbuilders.engine.rendering.chunk.BlockShader;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import com.xbuilders.window.render.MVP;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class PasteTool extends BlockTool {
    public PasteTool(BlockTools tools,CursorRay cursorRay) {
        super("Paste", tools, cursorRay);
        shader = new BlockShader(BlockShader.FRAG_MODE_DIRECT);
    }

    MVP mvp = new MVP();
    public static ChunkVoxels clipboard = new ChunkVoxels(16, 16, 16);
    static BlockMeshBundle mesh = new BlockMeshBundle();
    static BlockShader shader;


    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        //Only activate with Ctrl+C
        if (key == GLFW.GLFW_KEY_V && (mods & GLFW.GLFW_MOD_CONTROL) != 0) {
            return true;
        }
        return false;
    }

    public void activate() {
        cursorRay.disableBoundaryMode();
    }

    final Matrix4f model = new Matrix4f();

    @Override
    public boolean drawCursor(CursorRay ray, Matrix4f proj, Matrix4f view) {
//        ray.cursorBox.setPosAndSize(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z, 2, 2, 2);
//        ray.cursorBox.setColor(new Vector4f(1, 0, 0, 1));
//        ray.cursorBox.draw(proj, view);
        model.identity().translate(ray.getHitPosPlusNormal().x, ray.getHitPosPlusNormal().y, ray.getHitPosPlusNormal().z);
        mvp.update(proj, view, model);
        mvp.sendToShader(shader.getID(), shader.mvpUniform);
        mesh.opaqueMesh.draw(shader, true);
        return true;
    }

    public boolean setBlock(Item item, final CursorRay ray, boolean isCreationMode) {
        Vector3i offset = ray.getHitPosPlusNormal();
        for (int x = 0; x < clipboard.size.x; x++) {
            for (int y = 0; y < clipboard.size.y; y++) {
                for (int z = 0; z < clipboard.size.z; z++) {
                    GameScene.player.setBlock(clipboard.getBlock(x, y, z),
                            x + offset.x, y + offset.y, z + offset.z);
                }
            }
        }
        return false;
    }


    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }
}