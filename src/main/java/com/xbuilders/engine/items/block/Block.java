package com.xbuilders.engine.items.block;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.PriorityThreadPoolExecutor;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.window.utils.texture.Texture;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Vector3i;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class Block extends Item {

    //A block texture is a REQUIRED field
    public final BlockTexture texture;
    public int type = 0;
    public boolean solid = true;
    public boolean climbable = false;
    public boolean opaque = true;
    public byte torchlightStartingValue = 0;
    public Consumer<Block> initializationCallback = null;
    public int liquidMaxFlow;
    public final float[] colorInPlayerHead = {0, 0, 0, 0};//If set to null, we default to drawing block texture in player head



    public final boolean isLuminous() {
        return torchlightStartingValue > 0;
    }

    public final boolean isLiquid() {
        return type == BlockList.LIQUID_BLOCK_TYPE_ID;
    }

    // <editor-fold defaultstate="collapsed" desc="block events">
    //Create a functional interface for setBlockEvent
    @FunctionalInterface
    public interface SetBlockEvent {

        public void run(int x, int y, int z);
    }

    @FunctionalInterface
    public interface RemoveBlockEvent {

        public void run(int x, int y, int z, BlockHistory history);
    }

    @FunctionalInterface
    public interface ClickEvent {

        public void run(int x, int y, int z, BlockData data);
    }

    //A functional interface for onLocalChange
    @FunctionalInterface
    public interface OnLocalChange {

        public void run(BlockHistory history, Vector3i changedPosition, Vector3i thisPosition);
    }

    SetBlockEvent multithreadedSetBlockEvent = null;
    SetBlockEvent setBlockEvent = null;
    OnLocalChange onLocalChange = null;
    RemoveBlockEvent removeBlockEvent = null;
    ClickEvent clickEvent = null;


    public boolean allowExistence(int worldX, int worldY, int worldZ) {
        return true;
    }

    public void setBlockEvent(SetBlockEvent setBlockEvent) {
        this.setBlockEvent = setBlockEvent;
    }

    public void setBlockEvent_multithreaded(SetBlockEvent setBlockEvent) {
        this.multithreadedSetBlockEvent = setBlockEvent;
    }

    public void clickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    public void removeBlockEvent(RemoveBlockEvent removeBlockEvent) {
        this.removeBlockEvent = removeBlockEvent;
    }

    public void onLocalChange(OnLocalChange onLocalChange) {
        this.onLocalChange = onLocalChange;
    }

    public boolean clickThrough() {
        return clickEvent == null;
    }

    public void run_ClickEvent(Vector3i worldPos) {
        if (clickEvent != null) {
            WCCi wcc = new WCCi();
            wcc.set(worldPos);
            Chunk chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) return;
            BlockData data = chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
            clickEvent.run(worldPos.x, worldPos.y, worldPos.z, data);
            chunk.updateMesh(false, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
        }
    }

    public void run_RemoveBlockEvent(Vector3i worldPos, BlockHistory history) {
        if (removeBlockEvent != null) {
            removeBlockEvent.run(worldPos.x, worldPos.y, worldPos.z, history);
        }
    }

    public void run_SetBlockEvent(PriorityThreadPoolExecutor eventThread, Vector3i worldPos) {
        if (setBlockEvent != null) {
            setBlockEvent.run(worldPos.x, worldPos.y, worldPos.z);
        }
        if (multithreadedSetBlockEvent != null) {
            eventThread.submit(System.currentTimeMillis(),
                    () -> multithreadedSetBlockEvent.run(worldPos.x, worldPos.y, worldPos.z));
        }
    }

    public void run_OnLocalChange(BlockHistory history, Vector3i changedPosition, Vector3i thisPosition) {
        if (onLocalChange != null) {
            onLocalChange.run(history, changedPosition, thisPosition);
        }
    }
    // </editor-fold>

    public boolean isAir() {
        return false;
    }

    public final void initTextureAndIcon(BlockArrayTexture textures,
                                         File blockIconDirectory,
                                         File iconDirectory,
                                         int defaultIcon) throws IOException {
        if (this.texture != null) { //ALWAYS init the texture first
            this.texture.init(textures);
        }

        //Run initialization callbacks
        if (ItemList.blocks.getBlockType(type) != null) {
            Consumer<Block> typeInitCallback = ItemList.blocks.getBlockType(type).initializationCallback;
            if (typeInitCallback != null) typeInitCallback.accept(this);
        }
        //Run our custom initialization callback last
        if (initializationCallback != null) {
            initializationCallback.accept(this);
        }

        //Init the icon
        if (initIcon(iconDirectory, defaultIcon)) {
            //Init the regular icon
        } else if (this.texture != null) {


            File blockIcon = new File(blockIconDirectory, id + ".png");
            if (blockIcon.exists()) {
                Texture icon = TextureUtils.loadTexture(blockIcon.getAbsolutePath(), true);
                super.setIcon(icon.id);
            } else {//If there is no generated block icon, default to the texture
                File file = textures.getTextureFile(this.texture.NEG_Y_NAME);
                if (file != null) {
                    super.setIcon(loadBlockTexture(file));
                } else {
                    super.setIcon(defaultIcon);
                }
            }
        } else {
            super.setIcon(defaultIcon);
        }
    }

    private int loadBlockTexture(File file) throws IOException {
        Texture tex;
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer w = stack.mallocInt(1);
//            IntBuffer h = stack.mallocInt(1);
//            IntBuffer channels = stack.mallocInt(1);
//            ByteBuffer buffer = STBImage.stbi_load(file.getAbsolutePath(), w, h, channels, 4);
//
//            int maxWidth = Math.min(w.get(), h.get());
//            w.put(maxWidth);
//            h.put(maxWidth);
//
//            TextureFile textureFile = new TextureFile(null, 0, 0, maxWidth, maxWidth);
//            buffer = TextureUtils.makeRegionOfImage(buffer, textureFile, w.get(), h.get());
//
//            tex = TextureUtils.loadTexture(buffer, w.get(), h.get(), false);
//        }
        tex = TextureUtils.loadTexture(file.getAbsolutePath(), false);
        return tex.id;
    }

    public Block(int id, String name) {
        super(id, name, ItemType.BLOCK);
        this.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        this.texture = null;
    }

    public Block(int id, String name, BlockTexture texture) {
        super(id, name, ItemType.BLOCK);
        this.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        this.texture = texture;
    }

    public Block(int id, String name, BlockTexture texture, int renderType) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.type = renderType;
    }

    public Block(int id, String name, BlockTexture texture, int renderType, Consumer<Block> initialization) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.type = renderType;
        this.initializationCallback = initialization;
    }

    public Block(int id, String name, BlockTexture texture, Consumer<Block> initialization) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        this.initializationCallback = initialization;
    }


    @Override
    public String toString() {
        return "\"" + name + "\" Block (id: " + id + ")";
    }
}
