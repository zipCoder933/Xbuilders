package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.utils.BFS.ChunkNode;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.pillar.PillarInformation;
import com.xbuilders.engine.world.light.SunlightUtils;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;

public class LightFixTool extends BlockTool {
    public LightFixTool(BlockTools tools, CursorRay cursorRay) {
        super("Light Fix Tool", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\lightbulb.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean resetChunksMode;

    public String toolDescription() {
        return "Light Fix (Mode: " + (resetChunksMode ? "Reset Chunks" : "Light Fix Pen") + ")";
    }

    public static boolean fixSunlightPillar(HashSet<Chunk> affectedChunks, Chunk pillarChunk1) {

        ArrayList<ChunkNode> repropQueue = new ArrayList<>();

        for (Chunk chunk : pillarChunk1.pillarInformation.chunks) {
            chunk.data.resetSun();
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
                boolean addSun = true;
                for (Chunk chunk : pillarChunk1.pillarInformation.chunks) {// Go DOWN from Y
                    affectedChunks.add(chunk);
                    for (int y = 0; y < Chunk.WIDTH; y++) {
                        Block block = ItemList.getBlock(chunk.data.getBlock(x, y, z));
                        if (addSun) {
                            if (block.opaque) {
                                chunk.data.setSun(x, y, z, (byte) 0);
                                addSun = false;
                            }
                        } else {
                            chunk.data.setSun(x, y, z, (byte) 0);
                        }
                    }
                }
            }
        }


        SunlightUtils.propagateSunlight(repropQueue, affectedChunks);


        return true;
    }

    public boolean setBlock(Block item, final CursorRay ray, boolean isCreationMode) {
        HashSet<Chunk> affectedChunks = new HashSet<>();
        WCCi wcc = new WCCi().set(ray.getHitPosPlusNormal());
        Chunk startChunk = GameScene.world.getChunk(wcc.chunk);

        if (startChunk == null) return true;

        if (resetChunksMode) {
            System.out.println("Light Fix Tool at " + startChunk.toString());
            PillarInformation pillarInformation = startChunk.pillarInformation;
            fixSunlightPillar(affectedChunks, pillarInformation.getTopPillar());
        } else {
            ArrayList<ChunkNode> filledPropagator = new ArrayList<>();
            ArrayList<ChunkNode> emptyPropagator = new ArrayList<>();

            for (int x = -8; x < 16; x++) {
                for (int z = -8; z < 16; z++) {
                    filledPropagator.add(new ChunkNode(new WCCi().set(
                            ray.getHitPosPlusNormal().x + x,
                            World.WORLD_TOP_Y + 1,
                            ray.getHitPosPlusNormal().z + z), GameScene.world));
                }
            }

            SunlightUtils.updateFromQueue(filledPropagator, emptyPropagator, affectedChunks, null);
            SunlightUtils.updateFromQueue(emptyPropagator, filledPropagator, affectedChunks, null);
        }


        for (Chunk chunk : affectedChunks) {
            chunk.generateMesh();
            chunk.markAsModifiedByUser();
        }
        return true;
    }

    public void changeMode() {
        resetChunksMode = !resetChunksMode;
    }

    @Override
    public boolean activationKey(int key, int scancode, int action, int mods) {
        return false;
    }

    @Override
    public void activate() {
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE && key == GLFW.GLFW_KEY_M) {
            changeMode();
            return true;
        }
        return false;
    }
}
