package com.xbuilders.engine.items.block;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.player.pipeline.BlockHistory;
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
    private Consumer<Block> initializationCallback = null;

    public final boolean isLuminous() {
        return torchlightStartingValue > 0;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param data
     * @return if the block should be set
     */
    public boolean setBlockEvent(int x, int y, int z, BlockData data) {
        return true;
    }

    public void onLocalChange(BlockHistory history, Vector3i changedPosition, Vector3i thisPosition) {
    }

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
    }

    public Block(int id, String name, BlockTexture texture) {
        super(id, name, ItemType.BLOCK);
        this.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        this.solid = true;
        this.opaque = true;
        this.texture = texture;
    }

    public Block(int id, String name, BlockTexture texture, Consumer<Block> initialization) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.initializationCallback = initialization;
    }


    public Block(int id, String name, BlockTexture texture, boolean solid, boolean opaque) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        this.solid = solid;
        this.opaque = opaque;
    }

    public Block(int id, String name, BlockTexture texture, boolean solid, boolean opaque, int renderType) {
        super(id, name, ItemType.BLOCK);
        this.texture = texture;
        this.type = renderType;
        this.solid = solid;
        this.opaque = opaque;
    }


    @Override
    public String toString() {
        return "\"" + name + "\" Block (id: " + id + ")";
    }
}
