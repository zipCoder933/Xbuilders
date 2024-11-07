/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.block;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.DefaultBlockType;
import com.xbuilders.engine.builtinMechanics.liquid.LiquidBlockType;
import com.xbuilders.engine.utils.IntMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static com.xbuilders.engine.utils.ArrayUtils.combineArrays;

/**
 * @author zipCoder933
 */
public class BlockRegistry {

    public BlockArrayTexture textures;
    private final HashMap<Integer, BlockType> blockTypesHashmap = new HashMap<>();
    private final IntMap<BlockType> blockTypesIntMap = new IntMap<>(BlockType.class);
    private final HashMap<String, Integer> stringBlockTypes = new HashMap<>();

    public final static int DEFAULT_BLOCK_TYPE_ID = 0;
    public final static int LIQUID_BLOCK_TYPE_ID = -1;
    public final static DefaultBlockType defaultBlockType = new DefaultBlockType();
    public final static LiquidBlockType liquidBlockType = new LiquidBlockType();

    final IntMap<Block> idMap = new IntMap<>(Block.class);
    private Block[] list;

    public Block[] getList() {
        return list;
    }


    //Predefined Blocks
    public final static Block BLOCK_AIR = new BlockAir();

    public BlockRegistry(File textureDirectory) throws IOException {
        textures = new BlockArrayTexture(textureDirectory);
        blockTypesHashmap.put(DEFAULT_BLOCK_TYPE_ID, defaultBlockType);
        blockTypesIntMap.setList(blockTypesHashmap);
        addBlockType("liquid", LIQUID_BLOCK_TYPE_ID, liquidBlockType);
    }


    private int assignMapAndVerify(List<Block> inputItems) {
        System.out.println("\nChecking block IDs");
        int highestId = 0;
        HashMap<Integer, Block> map = new HashMap<>();

        for (int i = 0; i < inputItems.size(); i++) {
            if (inputItems.get(i) == null) {
                System.err.println("item at index " + i + " is null");
                continue;
            }
            int id = inputItems.get(i).id;
            if (map.get(id) != null) {
                System.err.println("Block " + inputItems.get(i) + " ID conflicts with an existing ID: " + id);
            }
            map.put(id, inputItems.get(i));
            if (id > highestId) {
                highestId = id;
            }
        }
        System.out.println("\t(The highest item ID is: " + highestId + ")");
        System.out.print("\tID Gaps: ");
        for (int id = 1; id < highestId; id++) {
            boolean found = false;
            for (Block item : inputItems) {
                if (item.id == id) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.print(id + ", ");
            }
        }
        System.out.println("");
        idMap.setList(map);
        return highestId;
    }

    public void setAndInit(List<Block> blockArray) {
        blockArray.add(BLOCK_AIR);

        assignMapAndVerify(blockArray);
        list = blockArray.toArray(new Block[0]);

        //Initialize all blocks
        for (Block block : getList()) {
            if (block.texture != null) { //ALWAYS init the texture first
                block.texture.init(textures);
            }
            //Run initialization callbacks
            if (Registrys.blocks.getBlockType(block.renderType) != null) {
                Consumer<Block> typeInitCallback = Registrys.blocks.getBlockType(block.renderType).initializationCallback;
                if (typeInitCallback != null) typeInitCallback.accept(block);
            }
            //Run our custom initialization callback last
            if (block.initializationCallback != null) {
                block.initializationCallback.accept(block);
            }
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

    public Block getItem(short blockID) {
        Block block = idMap.get(blockID);
        if (block == null)
            block = BLOCK_AIR; // Important to prevent bugs with proceses not knowing how to handle null blocks
        return block;
    }
}
