package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.rendering.entity.block.BlockMeshBundle;
import com.xbuilders.engine.rendering.entity.EntityShader_ArrayTexture;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import com.xbuilders.window.render.MVP;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import java.io.IOException;
import java.util.ArrayList;

public class PasteTool extends BlockTool {
    public PasteTool(BlockTools tools, CursorRay cursorRay) {
        super("Paste", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\paste.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toolDescription() {
        return (additionMode ? "Additive " : "") + "Paste (offset=" + (offsetMode + 1) + "/" + offsetMaxMode + ")";
    }

    MVP modelMatrix = new MVP();
    public static ChunkVoxels clipboard = new ChunkVoxels(16, 16, 16);


    public static ArrayList<Entity> clipboard_entities = new ArrayList<>(); //TODO: Add clipboard entities
    private final static EntityShader_ArrayTexture blockShader = new EntityShader_ArrayTexture();
    static BlockMeshBundle mesh = new BlockMeshBundle();
    static Box box = new Box();

    public static void updateMesh() {
        mesh.compute(clipboard);
        box.setPosAndSize(0, 0, 0, clipboard.size.x, clipboard.size.y, clipboard.size.z);
        mesh.sendToGPU();
    }

    static {
        box.setLineWidth(2);
        box.setColor(new Vector4f(1, 0, 0, 1));
    }


    @Override
    public boolean activationKey(int key, int scancode, int action, int mods) {
        //Only activate with Ctrl+C
        if (key == GLFW.GLFW_KEY_V && (mods & GLFW.GLFW_MOD_CONTROL) != 0) {
            return true;
        }
        return false;
    }

    public void activate() {
        cursorRay.disableBoundaryMode();
        if (PasteTool.clipboard == null) {
            PasteTool.clipboard = new ChunkVoxels(0, 0, 0);
        }
        PasteTool.updateMesh();
        offsetMode = 1;
        positioningMode = true;
        additionMode = true;
    }

    public Vector3i getOffset() {
        if (placeOnHit) offset.set(cursorRay.getHitPos());
        else offset.set(cursorRay.getHitPosPlusNormal());

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
        if (positioningMode) {
            offset = getOffset();
            if (!ray.hitTarget()) return false;
        }

        blockShader.bind();
        blockShader.updateProjectionViewMatrix(proj, view);
        modelMatrix.identity().translate(offset.x, offset.y, offset.z);
        modelMatrix.updateAndSendToShader(blockShader.getID(), blockShader.uniform_modelMatrix);
        mesh.draw();

        box.setPosition(offset.x, offset.y, offset.z);
        box.draw(proj, view);
        return true;
    }

    public boolean setBlock(Block item, final CursorRay ray, boolean isCreationMode) {

        if (positioningMode) {
            positioningMode = false;
        } else {
            if (isCreationMode) {
                paste();
            }
            positioningMode = true;
        }
        return true;
    }

    private void paste() {
        for (int x = 0; x < clipboard.size.x; x++) {
            for (int y = 0; y < clipboard.size.y; y++) {
                for (int z = 0; z < clipboard.size.z; z++) {
                    if (clipboard.getBlock(x, y, z) != BlockList.BLOCK_AIR.id || !additionMode) {
                        GameScene.player.setBlock(clipboard.getBlock(x, y, z), clipboard.getBlockData(x, y, z), x + offset.x, y + offset.y, z + offset.z);
                    }
                }
            }
        }
    }

    public static void rotatePasteBox() {
        if (clipboard != null) {
            clipboard_entities.clear();
            ChunkVoxels newClipboard = new ChunkVoxels(clipboard.size.z, clipboard.size.y, clipboard.size.x);
            for (int x = 0; x < clipboard.size.x; x++) {
                for (int y = 0; y < clipboard.size.y; y++) {
                    for (int z = 0; z < clipboard.size.z; z++) {
                        int newX = (clipboard.size.z - 1) - z;
                        int newZ = x;
                        int newY = y;

                        Block block = ItemList.getBlock(clipboard.getBlock(x, y, z));
                        newClipboard.setBlock(newX, newY, newZ, block.id);

                        BlockData oldData = clipboard.getBlockData(x, y, z);
                        if (oldData != null) {
                            BlockData newData = new BlockData(oldData); //Create a copy
                            block.getRenderType().rotateBlockData(newData, false);
                            newClipboard.setBlockData(newX, newY, newZ, newData);
                        }

                    }
                }
            }
            clipboard = newClipboard;
        }
        updateMesh();
    }

    boolean additionMode = false;
    Vector3i offset = new Vector3i();
    int offsetMaxMode = 8;
    int offsetMode = 1;
    boolean positioningMode = false;

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (positioningMode) {
            offsetMode = (offsetMode + ((int) yoffset)) % offsetMaxMode;
        } else offset.y += (int) yoffset;
        return true;
    }

    boolean placeOnHit = false;

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_K) {
                placeOnHit = true;
                return true;
            } else if (!positioningMode) {
                if (key == GLFW.GLFW_KEY_LEFT) {
                    offset.x--;
                    return true;
                } else if (key == GLFW.GLFW_KEY_RIGHT) {
                    offset.x++;
                    return true;
                } else if (key == GLFW.GLFW_KEY_UP) {
                    if (mods == GLFW.GLFW_MOD_SHIFT) {
                        offset.y--;
                    } else {
                        offset.z--;
                    }
                    return true;
                } else if (key == GLFW.GLFW_KEY_DOWN) {
                    if (mods == GLFW.GLFW_MOD_SHIFT) {
                        offset.y++;
                    } else {
                        offset.z++;
                    }
                    return true;
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_O) {
                offsetMode = (offsetMode + 1) % offsetMaxMode;
                System.out.println("Offset mode: " + offsetMode);
                return true;
            } else if (key == GLFW.GLFW_KEY_M) {
                additionMode = !additionMode;
                return true;
            } else if (key == GLFW.GLFW_KEY_R) {
                rotatePasteBox();
                return true;
            } else if (key == GLFW.GLFW_KEY_K) {
                placeOnHit = false;
                return true;
            } else if (!positioningMode && key == GLFW.GLFW_KEY_ENTER) {
                paste();
                positioningMode = true;
                return true;
            }
        }
        return false;
    }
}
