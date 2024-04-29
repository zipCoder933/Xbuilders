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
    public boolean liquid = false;
    public boolean opaque = true;
    public byte torchlightStartingValue = 0;
    public Consumer<Block> initializationCallback = null;

    public final boolean isLuminous() {
        return torchlightStartingValue > 0;
    }

    // <editor-fold defaultstate="collapsed" desc="block events">
    //Create a functional interface for setBlockEvent
    @FunctionalInterface
    public interface SetBlockEvent {

        public void run(int x, int y, int z, BlockData data);
    }

    @FunctionalInterface
    public interface RemoveBlockEvent {

        public void run(int x, int y, int z);
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


    public boolean allowExistence(int worldX, int worldY, int worldZ) {
        return true;
    }

    public void setBlockEvent(SetBlockEvent setBlockEvent) {
        this.setBlockEvent = setBlockEvent;
    }

    public void setBlockEvent_multithreaded(SetBlockEvent setBlockEvent) {
        this.multithreadedSetBlockEvent = setBlockEvent;
    }

    public void removeBlockEvent(RemoveBlockEvent removeBlockEvent) {
        this.removeBlockEvent = removeBlockEvent;
    }

    public void onLocalChange(OnLocalChange onLocalChange) {
        this.onLocalChange = onLocalChange;
    }


    public void run_RemoveBlockEvent(Vector3i worldPos) {
        if (removeBlockEvent != null) {
            removeBlockEvent.run(worldPos.x, worldPos.y, worldPos.z);
        }
    }

    public void run_SetBlockEvent(PriorityThreadPoolExecutor eventThread, Vector3i worldPos, BlockData data) {
        if (setBlockEvent != null) {
            setBlockEvent.run(worldPos.x, worldPos.y, worldPos.z, data);
        }
        if (multithreadedSetBlockEvent != null) {
            eventThread.submit(System.currentTimeMillis(),
                    () -> multithreadedSetBlockEvent.run(worldPos.x, worldPos.y, worldPos.z, data));
        }
    }

    public void run_OnLocalChange(BlockHistory history, Vector3i changedPosition, Vector3i thisPosition) {
        if (onLocalChange != null) {
            onLocalChange.run(history, changedPosition, thisPosition);
        }
    }
    // </editor-fold>


    public int playerHeadEnterBlockEvent() {
        return -1;
    }

    public boolean isAir() {
        return false;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public final void initTextureAndIcon(BlockArrayTexture textures,
                                         File blockIconDirectory,
                                         File iconDirectory,
                                         int defaultIcon) throws IOException {
        if (this.texture != null) {
            this.texture.init(textures);
        }


//        //Run our custom callback first if we dont have a block type
//        if (ItemList.blocks.getBlockType(type) == null && initializationCallback != null) {
//            initializationCallback.accept(this);
//        }
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
            } else {
                File file = textures.getTextureFile(this.texture.POS_Y_NAME);
                if (file != null) {
                    Texture tex = TextureUtils.loadTexture(
                            file.getAbsolutePath(), false);
                    super.setIcon(tex.id);
                } else {
                    super.setIcon(defaultIcon);
                }
            }
        } else {
            super.setIcon(defaultIcon);
        }
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
