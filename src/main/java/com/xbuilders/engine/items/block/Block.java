package com.xbuilders.engine.items.block;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.window.utils.texture.Texture;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Vector3i;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

public class Block extends Item {

    //A block texture is a REQUIRED field
    public final BlockTexture texture;
    public int type = 0;
    public boolean solid = true;
    public boolean liquid = false;
    public boolean opaque = true;
    public byte torchlightStartingValue = 0;
    private Consumer<Block> initializationCallback = null;

    public final boolean isLuminous() {
        return torchlightStartingValue > 0;
    }

    // <editor-fold defaultstate="collapsed" desc="block events">
    //Create a functional interface for setBlockEvent
    @FunctionalInterface
    public interface SetBlockEvent {

        /**
         * Sets a block event at the specified coordinates with the given block data.
         *
         * @param x    the x-coordinate of the block
         * @param y    the y-coordinate of the block
         * @param z    the z-coordinate of the block
         * @param data the block data to set
         * @return true if the block event was successfully set, false otherwise
         */
        public void run(int x, int y, int z, BlockData data);
    }

    //A functional interface for onLocalChange
    @FunctionalInterface
    public interface OnLocalChange {

        /**
         * A description of the entire Java function.
         *
         * @param history         description of parameter
         * @param changedPosition description of parameter
         * @param thisPosition    description of parameter
         * @return description of return value
         */
        public void run(BlockHistory history, Vector3i changedPosition, Vector3i thisPosition);
    }

    SetBlockEvent setBlockEvent = null;
    OnLocalChange onLocalChange = null;
    boolean setBlockEvent_runOnAnotherThread = false;

    public boolean allowSet(int worldX, int worldY, int worldZ, BlockData blockData) {
        return true;
    }

    public void setBlockEvent(boolean runOnAnotherThread, SetBlockEvent setBlockEvent) {
        this.setBlockEvent = setBlockEvent;
        this.setBlockEvent_runOnAnotherThread = runOnAnotherThread;
    }

    public void onLocalChange(OnLocalChange onLocalChange) {
        this.onLocalChange = onLocalChange;
    }

    public boolean allowSet(Vector3i worldPos, BlockData data) {
        if (allowSet(worldPos.x, worldPos.y, worldPos.z, data)) {//Check if the block is allowed to be set
            BlockType type = ItemList.blocks.getBlockType(this.type);//Test if the blockType is ok with setting
            if (type == null || type.allowToBeSet(this, data, worldPos.x, worldPos.y, worldPos.z)) {
                return true;
            }
        }
        return false;
    }


    public void run_SetBlockEvent(ThreadPoolExecutor eventThread, Vector3i worldPos, BlockData data) {
        if (setBlockEvent != null) {
            if (setBlockEvent_runOnAnotherThread) {
                eventThread.submit(() -> setBlockEvent.run(worldPos.x, worldPos.y, worldPos.z, data));
            } else {
                setBlockEvent.run(worldPos.x, worldPos.y, worldPos.z, data);
            }
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
        //TODO: the item list initialization callback does not work here because item list must be initialized first
//        //Run initialization callbacks
//        Consumer<Block> typeInitCallback = ItemList.blocks.getBlockType(id).initializationCallback;
//        if (typeInitCallback != null) typeInitCallback.accept(this);
    }

    public Block(int id, String name, BlockTexture texture) {
        super(id, name, ItemType.BLOCK);
        this.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        this.solid = true;
        this.opaque = true;
        this.texture = texture;
//        //Run initialization callbacks
//        Consumer<Block> typeInitCallback = ItemList.blocks.getBlockType(id).initializationCallback;
//        if (typeInitCallback != null) typeInitCallback.accept(this);
    }

    public Block(int id, String name, BlockTexture texture, Consumer<Block> initialization) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;

//        //Run initialization callbacks
//        Consumer<Block> typeInitCallback = ItemList.blocks.getBlockType(id).initializationCallback;
//        if (typeInitCallback != null) typeInitCallback.accept(this);
        //Execute our custom callback last
        this.initializationCallback = initialization;
    }


    public Block(int id, String name, BlockTexture texture, boolean solid, boolean opaque) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.type = BlockList.DEFAULT_BLOCK_TYPE_ID;

//        //Run initialization callbacks
//        Consumer<Block> typeInitCallback = ItemList.blocks.getBlockType(id).initializationCallback;
//        if (typeInitCallback != null) typeInitCallback.accept(this);

        this.solid = solid;
        this.opaque = opaque;
    }

    public Block(int id, String name, BlockTexture texture, boolean solid, boolean opaque, int renderType) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.type = renderType;

//        //Run initialization callbacks
//        Consumer<Block> typeInitCallback = ItemList.blocks.getBlockType(id).initializationCallback;
//        if (typeInitCallback != null) typeInitCallback.accept(this);

        this.solid = solid;
        this.opaque = opaque;
    }


    @Override
    public String toString() {
        return "\"" + name + "\" Block (id: " + id + ")";
    }
}
