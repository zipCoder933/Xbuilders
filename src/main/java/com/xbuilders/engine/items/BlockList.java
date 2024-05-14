/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.BlockAir;
import com.xbuilders.engine.items.block.BlockArrayTexture;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.DefaultBlockType;
import com.xbuilders.engine.utils.ErrorHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author zipCoder933
 */
public class BlockList extends ItemGroup<Block> {

    public BlockArrayTexture textures;
    private final HashMap<Integer, BlockType> blockTypes = new HashMap<>();
    private final HashMap<String, Integer> stringBlockTypes = new HashMap<>();

    public final static int DEFAULT_BLOCK_TYPE_ID = 0;
    public final static DefaultBlockType defaultBlockType = new DefaultBlockType();
    public final static Block BLOCK_AIR = new BlockAir();
    public final static Block BLOCK_UNKNOWN = new Block(0, "Unknown");

    static {
        BLOCK_UNKNOWN.opaque = false;
        BLOCK_UNKNOWN.solid = true;
    }

    File blockIconDirectory, iconDirectory;
    int defaultIcon;

    public BlockList() {
        blockTypes.put(DEFAULT_BLOCK_TYPE_ID, defaultBlockType);
    }

    public void init(File textureDirectory,
            File blockIconDirectory,
            File iconDirectory,
            int defaultIcon) throws IOException {
        this.blockIconDirectory = blockIconDirectory;
        this.iconDirectory = iconDirectory;
        this.defaultIcon = defaultIcon;
        textures = new BlockArrayTexture(textureDirectory);
    }

    @Override
    public void setItems(Block[] inputBlocks) {
        setIdMap(inputBlocks);
        idMap.put(BLOCK_AIR.id, BLOCK_AIR);
        try {
            itemList = new Block[getIdMap().size() + 1];
            itemList[0] = BLOCK_AIR;
            int i = 1;
            for (Block block : getIdMap().values()) {
                block.initTextureAndIcon(textures, blockIconDirectory, iconDirectory, defaultIcon);
                itemList[i] = block;
                i++;
            }
        } catch (IOException ex) {
            ErrorHandler.handleFatalError(ex);
        }
    }

    public void addBlockType(String typeName, int typeID, BlockType type) {
        if (blockTypes.containsKey(typeID)) {
            throw new IllegalArgumentException("Type ID " + DEFAULT_BLOCK_TYPE_ID + " already in use");
        }
        stringBlockTypes.put(typeName.toLowerCase().trim(), typeID);
        blockTypes.put(typeID, type);
    }

    // public HashMap<Integer,AABBIterator> getTypeCollision_AABBIterator(AABB box){
    // HashMap<Integer,AABBIterator> map = new HashMap<>();
    // blockTypes.forEach((int val, BlockType type) -> {
    // map.put(val, value).getCollisionBoxes(box);
    // });
    // }

    public BlockType getBlockTypeID(int typeID) {
        BlockType type = blockTypes.get(typeID);
        if (type == null) //To make the code more robust
            return defaultBlockType;
        return type;
    }

    public Integer getBlockTypeID(String type) {
        type = type.toLowerCase().trim();
        if (stringBlockTypes.containsKey(type))
            return stringBlockTypes.get(type);
        else
            throw new IllegalArgumentException("Block type \"" + type + "\" not recognized");
    }

    public float calculateTextureLayer(int textureLayer) {
        int d = textures.layerCount - 1;// layer count
        double type = Math.floor(textureLayer - 0.5f);// -1+0.5
        return (float) Math.max(0, Math.min(d, type));
    }

    @Override
    public Block getItem(short blockID) {
        Block block = getIdMap().get(blockID);
        if (block == null)
            block = BLOCK_UNKNOWN; // Important to prevent bugs with proceses not knowing how to handle null blocks
        return block;
    }
}
