package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.rendering.chunk.BlockMeshBundle;
import com.xbuilders.engine.rendering.chunk.BlockShader;
import com.xbuilders.engine.rendering.wireframeBox.Box;
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
    public PasteTool(BlockTools tools, CursorRay cursorRay) {
        super("Paste", tools, cursorRay);
        shader = new BlockShader(BlockShader.FRAG_MODE_DIRECT);
    }


    @Override
    public String toolDescription() {
        return (additionMode ? "Additive " : "") + "Paste (offset=" + (offsetMode + 1) + "/" + offsetMaxMode + ")";
    }

    MVP mvp = new MVP();
    public static ChunkVoxels clipboard = new ChunkVoxels(16, 16, 16);
    static BlockMeshBundle mesh = new BlockMeshBundle();
    static Box box = new Box();

    static {
        box.setLineWidth(2);
        box.setColor(new Vector4f(1, 0, 0, 1));
    }

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
        offsetMode = 1;
        additionMode = true;
    }

    final Matrix4f model = new Matrix4f();

    public Vector3i getOffset() {
        offset.set(cursorRay.getHitPosPlusNormal());
        switch (offsetMode) {
            //Up
            case 1 -> {
                offset.y -= clipboard.size.y - 1;
            }
            case 2 -> {
                offset.y -= clipboard.size.y - 1;
                offset.x -= clipboard.size.x - 1;
            }
            case 3 -> {
                offset.y -= clipboard.size.y - 1;
                offset.z -= clipboard.size.z - 1;
            }
            case 4 -> {
                offset.x -= clipboard.size.x - 1;
                offset.y -= clipboard.size.y - 1;
                offset.z -= clipboard.size.z - 1;
            }
            //Down (0 included)
            case 5 -> {
                offset.x -= clipboard.size.x - 1;
            }
            case 6 -> {
                offset.z -= clipboard.size.z - 1;
            }
            case 7 -> {
                offset.x -= clipboard.size.x - 1;
                offset.z -= clipboard.size.z - 1;
            }

        }
        return offset;
    }

    @Override
    public boolean drawCursor(CursorRay ray, Matrix4f proj, Matrix4f view) {
//        ray.cursorBox.setPosAndSize(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z, 2, 2, 2);
//        ray.cursorBox.setColor(new Vector4f(1, 0, 0, 1));
//        ray.cursorBox.draw(proj, view);
        model.identity().translate(getOffset().x, getOffset().y, getOffset().z);
        mvp.update(proj, view, model);
        mvp.sendToShader(shader.getID(), shader.mvpUniform);
        mesh.opaqueMesh.draw(shader, true);
        box.setPosition(getOffset().x, getOffset().y, getOffset().z);
        box.draw(proj, view);
        return true;
    }

    public boolean setBlock(Item item, final CursorRay ray, boolean isCreationMode) {
        Vector3i offset = getOffset();
        for (int x = 0; x < clipboard.size.x; x++) {
            for (int y = 0; y < clipboard.size.y; y++) {
                for (int z = 0; z < clipboard.size.z; z++) {
                    if (clipboard.getBlock(x, y, z) != BlockList.BLOCK_AIR.id || !additionMode) {
                        GameScene.player.setBlock(clipboard.getBlock(x, y, z),
                                x + offset.x, y + offset.y, z + offset.z);
                    }
                }
            }
        }
        return true;
    }

    boolean additionMode = false;
    Vector3i offset = new Vector3i();
    int offsetMaxMode = 8;
    int offsetMode = 1;

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_V) {
                offsetMode = (offsetMode + 1) % offsetMaxMode;
                System.out.println("Offset mode: " + offsetMode);
                return true;
            } else if (key == GLFW.GLFW_KEY_M) {
                additionMode = !additionMode;
                return true;
            } else if (key == GLFW.GLFW_KEY_R) {
                //TODO: Rotate the paste
                return true;
            }
        }
        return false;
    }
}
