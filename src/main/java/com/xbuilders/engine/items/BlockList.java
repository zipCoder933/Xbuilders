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
import com.xbuilders.engine.items.block.construction.LiquidBlockType;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.IntMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author zipCoder933
 */
public class BlockList extends ItemGroup<Block> {

    public BlockArrayTexture textures;
    private final HashMap<Integer, BlockType> blockTypesHashmap = new HashMap<>();
    private final IntMap<BlockType> blockTypesIntMap = new IntMap<>(BlockType.class);
    private final HashMap<String, Integer> stringBlockTypes = new HashMap<>();

    public final static int DEFAULT_BLOCK_TYPE_ID = 0;
    public final static int LIQUID_BLOCK_TYPE_ID = -1;
    public final static DefaultBlockType defaultBlockType = new DefaultBlockType();
    public final static LiquidBlockType liquidBlockType = new LiquidBlockType();
    public final static Block BLOCK_AIR = new BlockAir();
    public final static Block BLOCK_UNKNOWN = new Block(0, "Unknown");

    static {
        BLOCK_UNKNOWN.opaque = false;
        BLOCK_UNKNOWN.solid = true;
    }

    File blockIconDirectory, iconDirectory;
    int defaultIcon;

    public BlockList() {
        super(Block.class);
        blockTypesHashmap.put(DEFAULT_BLOCK_TYPE_ID, defaultBlockType);
        blockTypesIntMap.setList(blockTypesHashmap);
        addBlockType("liquid", LIQUID_BLOCK_TYPE_ID, liquidBlockType);
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
        //Add air to input blocks
        Block[] inputBlocks2 = new Block[inputBlocks.length + 1];
        for (int i = 0; i < inputBlocks.length; i++) {
            inputBlocks2[i] = inputBlocks[i];
        }
        inputBlocks2[inputBlocks.length] = BLOCK_AIR;
        //Set ID Map
        setList(inputBlocks2);
        try {//Initialize all blocks
            for (Block block : getList()) {
                block.initTextureAndIcon(textures, blockIconDirectory, iconDirectory, defaultIcon);
            }
        } catch (IOException ex) {
            ErrorHandler.handleFatalError(ex);
        }
    }

    public void addBlockType(String typeName, int typeID, BlockType type) {
        if (blockTypesHashmap.get(typeID) != null) {
            throw new IllegalArgumentException("Type ID " + DEFAULT_BLOCK_TYPE_ID + " already in use");
        }
        type.name = (typeName);
        stringBlockTypes.put(typeName.toLowerCase().trim(), typeID);
        blockTypesHashmap.put(typeID, type);
        blockTypesIntMap.setList(blockTypesHashmap);
    }

    // public HashMap<Integer,AABBIterator> getTypeCollision_AABBIterator(AABB box){
    // HashMap<Integer,AABBIterator> map = new HashMap<>();
    // blockTypes.forEach((int val, BlockType type) -> {
    // map.put(val, value).getCollisionBoxes(box);
    // });
    // }

    public BlockType getBlockType(int typeID) {
        BlockType type = blockTypesIntMap.get(typeID);//Using an intmap is easier on memory
        if (type == null) //To make the code more robust
            return defaultBlockType;
        return type;
    }

    public Integer getBlockType(String type) {
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
        Block block = idMap.get(blockID);
        if (block == null)
            block = BLOCK_UNKNOWN; // Important to prevent bugs with proceses not knowing how to handle null blocks
        return block;
    }
}
