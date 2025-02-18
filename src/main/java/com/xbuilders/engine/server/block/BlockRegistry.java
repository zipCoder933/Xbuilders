/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.block;

import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.construction.BlockType;
import com.xbuilders.engine.server.block.construction.DefaultBlockType;
import com.xbuilders.engine.server.builtinMechanics.liquid.LiquidBlockType;
import com.xbuilders.engine.utils.IntMap;
import com.xbuilders.engine.utils.ResourceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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

    public final IntMap<Block> idToBlockMap = new IntMap<>(Block.class);
    public final HashMap<String, Short> aliasToIDMap = new HashMap<>();

    private Block[] list;

    public Block[] getList() {
        return list;
    }

    public IntMap<Block> getIdToBlockMap() {
        return idToBlockMap;
    }


    //Predefined Blocks
    public final static Block BLOCK_AIR = new BlockAir();

    public BlockRegistry() {
        blockTypesHashmap.put(DEFAULT_BLOCK_TYPE_ID, defaultBlockType);
        blockTypesIntMap.setList(blockTypesHashmap);
        addBlockType("liquid", LIQUID_BLOCK_TYPE_ID, liquidBlockType);
    }


    private int assignMapAndVerify(HashSet<String> uniqueAliases, List<Block> inputItems) {
        System.out.println("\nChecking block IDs");

        //Assign all the maps
        int highestId = 0;

        HashMap<Integer, Block> idToBlock_temp = new HashMap<>();
        aliasToIDMap.clear();

        for (int i = 0; i < inputItems.size(); i++) {
            Block block = inputItems.get(i);
            if (block == null) {
                System.err.println("item at index " + i + " is null");
                continue;
            }
            int id = block.id;
            if (idToBlock_temp.get(id) != null) {
                System.err.println("Block " + block + " ID conflicts with an existing ID: " + id);
            }
            if (uniqueAliases.contains(block.alias)) {
                System.err.println("Block " + block + " alias conflicts with an existing alias: " + block.alias);
            } else uniqueAliases.add(block.alias);

            idToBlock_temp.put(id, block);
            aliasToIDMap.put(block.alias, (short) id);

            if (id > highestId) {
                highestId = id;
            }
        }
        idToBlockMap.setList(idToBlock_temp);

        //Check for gaps
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
        return highestId;
    }

    public void setup(List<Block> blockArray) throws IOException {
        HashSet<String> uniqueAliases = new HashSet<>();
        textures = new BlockArrayTexture(ResourceUtils.BLOCK_TEXTURE_DIR, ResourceUtils.BLOCK_BUILTIN_TEXTURE_DIR);

        blockArray.add(BLOCK_AIR);

        assignMapAndVerify(uniqueAliases, blockArray);
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
    // blockTypes.forEach((int val, BlockType typeReference) -> {
    // map.put(val, value).getCollisionBoxes(box);
    // });
    // }

    public Block getBlock(String alias) {
        short id = aliasToIDMap.get(alias);
        return idToBlockMap.get(id);
    }

    public Block getBlock(short blockID) {
        Block block = idToBlockMap.get(blockID);
        if (block == null)
            block = BLOCK_AIR; // Important to prevent bugs with proceses not knowing how to handle null blocks
        return block;
    }

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
            throw new IllegalArgumentException("Block typeReference \"" + type + "\" not recognized");
    }

    public float calculateTextureLayer(int textureLayer) {
        int d = textures.layerCount - 1;// layer count
        double type = Math.floor(textureLayer - 0.5f);// -1+0.5
        return (float) Math.max(0, Math.min(d, type));
    }


}
