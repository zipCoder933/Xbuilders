/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.items.Tool;
import com.xbuilders.engine.utils.ArrayUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.game.items.blocks.*;
import com.xbuilders.game.UI.Hotbar;
import com.xbuilders.game.UI.Inventory;
import com.xbuilders.game.blockTools.BlockTools;
import com.xbuilders.game.items.blocks.trees.*;
import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.json.JsonManager;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.game.items.blocks.type.*;
import com.xbuilders.game.items.entities.animal.*;
import com.xbuilders.game.items.tools.AnimalFeed;
import com.xbuilders.game.items.tools.Hoe;
import com.xbuilders.game.items.tools.Saddle;
import com.xbuilders.game.terrain.BasicTerrain;
import com.xbuilders.game.terrain.DevTerrain;
import com.xbuilders.game.terrain.TestTerrain;
import com.xbuilders.game.terrain.complexTerrain.ComplexTerrain;
import com.xbuilders.game.terrain.defaultTerrain.TerrainV2;
import com.xbuilders.window.NKWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class MyGame extends Game {

    public static class GameInfo {
        public final Item[] playerBackpack;

        public GameInfo() {
            playerBackpack = new Item[22];
        }
    }

    public MyGame() {
        json = new JsonManager();
    }

    JsonManager json;
    GameInfo gameInfo;


    Inventory inventory;
    Hotbar hotbar;
    BlockTools blockTools;

    @Override
    public Item getSelectedItem() {
        return gameInfo.playerBackpack[hotbar.getSelectedItemIndex()];
    }

    NKWindow window;


    @Override
    public void uiDraw(MemoryStack stack) {
        inventory.draw(stack);
        if (!inventory.isOpen()) {
            blockTools.draw(stack);
            hotbar.draw(stack);
        }
    }

    @Override
    public void uiInit(NkContext ctx, NKWindow window, UIResources uires, GameUI gameUI) {
        this.window = window;
        try {
            hotbar = new Hotbar(ctx, window, uires);
            inventory = new Inventory(ctx, ItemList.getAllItems(), window, uires, hotbar);
            blockTools = new BlockTools(ctx, window, uires);
        } catch (IOException ex) {
            Logger.getLogger(MyGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (!inventory.isOpen()) {
            if (!blockTools.mouseScrollEvent(scroll, xoffset, yoffset)) {
                hotbar.mouseScrollEvent(scroll, xoffset, yoffset);
            }
        }
        return false;
    }

    @Override
    public boolean uiKeyEvent(int key, int scancode, int action, int mods) {
        inventory.keyEvent(key, scancode, action, mods);
        if (!inventory.isOpen()) {
            if (!blockTools.keyEvent(key, scancode, action, mods)) {
                hotbar.keyEvent(key, scancode, action, mods);
            }
        }
        return false;
    }

    @Override
    public boolean uiMouseButtonEvent(int button, int action, int mods) {
        if (!inventory.isOpen()) {
            if (!blockTools.mouseButtonEvent(button, action, mods)) {
                hotbar.mouseButtonEvent(button, action, mods);
            }
        }
        return false;
    }

    private static Block[] getAllJsonBlocks(File jsonDirectory) {
        System.out.println("Adding all json blocks from " + jsonDirectory.getAbsolutePath());
        if (!jsonDirectory.exists()) jsonDirectory.mkdirs();
        Block[] allBlocks = new Block[0];
        try {
            for (File file : jsonDirectory.listFiles()) {
                if (!file.getName().endsWith(".json")) continue;
                String jsonString = Files.readString(file.toPath());
                Block[] jsonBlocks2 = JsonManager.gson_jsonBlock.fromJson(jsonString, Block[].class);
                if (jsonBlocks2 != null && jsonBlocks2.length > 0) {
                    //append to list
                    allBlocks = ArrayUtils.concatenateArrays(allBlocks, jsonBlocks2); //concatenateArrays
                }
            }
            StringBuilder blockClasses = new StringBuilder();
            StringBuilder blockIDs = new StringBuilder();
            for (Block block : allBlocks) {
                if (block == null) {
                    System.err.println("A block is null");
                    continue;
                }
                String nameTitle = block.name.toUpperCase();
                nameTitle = nameTitle.
                        replaceAll("hidden", "")
                        .replaceAll("[^A-Z0-9_]", "")
                        .replaceAll("\\s+", "_");

                blockClasses.append("public static Block BLOCK_" + nameTitle + " = ItemList.getBlock((short)(short)").append(block.id).append(");").append("\n");
                blockIDs.append("public static short BLOCK_" + nameTitle + " = ").append(block.id).append(";").append("\n");
            }
            Files.writeString(new File(jsonDirectory, "BlockClasses.java").toPath(), blockClasses.toString());
            Files.writeString(new File(jsonDirectory, "BlockIDs.java").toPath(), blockIDs.toString());
            return allBlocks;
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        }
        return null;
    }


    public static void exportBlocksToJson(List<Block> list, File out) {
        //Save list as json
        try {
            String jsonString = JsonManager.gson_jsonBlock.toJson(list);
            Files.writeString(out.toPath(), jsonString);
            System.out.println("Saved " + list.size() + " blocks to " + out.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void synthesizeBlocks(Block[] blocks) {
        // List<Block> syBlocks = new ArrayList<>();
        // int blockID = 800;
        // for(Block block : blocks) {
        //     if(block.type == RenderType.FENCE) {
        //         String name =block.name.toLowerCase().replace("fence", "fence gate");
        //         //Make the first letter of every word uppercase using regex (this is how its done in js:  /(\b[a-z](?!\s))/g; )
        //         name = MiscUtils.capitalizeWords(name);


        //         Block fenceGate = new Block(blockID, name, block.texture);
        //         fenceGate.opaque=false;
        //         fenceGate.solid=true;
        //         fenceGate.type=RenderType.FENCE_GATE;
        //         syBlocks.add(fenceGate);
        //         blockID++;
        //     }
        // }
        // exportBlocksToJson(syBlocks, ResourceUtils.resource("items\\blocks\\json\\fence gates.json"));

        // int i=0;
        // syBlocks.clear();
        // for(File tex : ResourceUtils.resource("items\\blocks\\textures\\trapdoor").listFiles()) {

        //         Block fenceGate = new Block(blockID, 
        //                     tex.getName().replace(".png", "")+" trapdoor", 
        //                      new BlockTexture("trapdoor\\"+tex.getName()));
        //         fenceGate.opaque=false;
        //         fenceGate.solid=true;
        //         i++;
        //         fenceGate.type=RenderType.TRAPDOOR;
        //         syBlocks.add(fenceGate);
        //         blockID++;
        // }
        // exportBlocksToJson(syBlocks, ResourceUtils.resource("items\\blocks\\json\\trapdoors.json"));

        // //DOORS
        // syBlocks.clear();
        // HashMap<String,Block> topDoors = new HashMap<>();
        // for(File tex : ResourceUtils.resource("items\\blocks\\textures\\door").listFiles()) { //top doors first
        //     String[] parts = tex.getName().replace(".png", "").split("_");
        //     // System.out.println(Arrays.toString(parts));
        //     if(parts[2].equals("0")){
        //         Block fenceGate = new Block(blockID, parts[0]+" door (top, hidden)", new BlockTexture("door\\"+tex.getName()));
        //         fenceGate.opaque=false;
        //         fenceGate.solid=true;
        //         fenceGate.type=RenderType.DOOR_HALF;
        //         topDoors.put(parts[0], fenceGate);
        //         fenceGate.properties.put("placement", "top");
        //         syBlocks.add(fenceGate);
        //         blockID++;
        //     }
        // }
        //  i=0;
        // for(File tex : ResourceUtils.resource("items\\blocks\\textures\\door").listFiles()) { //bottom doors
        //     String[] parts = tex.getName().replace(".png", "").split("_");
        //     if(parts[2].equals("1")){
        //         Block fenceGate = new Block(blockID, parts[0]+" door", new BlockTexture("door\\"+tex.getName()));
        //         fenceGate.opaque=false;
        //         fenceGate.solid=true;
        //         fenceGate.type=RenderType.DOOR_HALF;
        //         fenceGate.setIcon("icon_"+i+"_0.png");
        //         i++;
        //         fenceGate.properties.put("placement", "bottom");
        //         //Make vertical pairs
        //         fenceGate.properties.put("vertical-pair", topDoors.get(parts[0]).id+"");
        //         topDoors.get(parts[0]).properties.put("vertical-pair", fenceGate.id+"");

        //         syBlocks.add(fenceGate);
        //         blockID++;
        //     }
        // }
        // exportBlocksToJson(syBlocks, ResourceUtils.resource("items\\blocks\\json\\doors.json"));
    }

    @Override
    public void initialize(NKWindow window) throws Exception {

        //Add block types FIRST. We need them to be able to setup blocks properly
        ItemList.blocks.addBlockType("sprite", RenderType.SPRITE, new SpriteRenderer());
        ItemList.blocks.addBlockType("floor", RenderType.FLOOR, new FloorItemRenderer());
        ItemList.blocks.addBlockType("orientable", RenderType.ORIENTABLE_BLOCK, new OrientableBlockRenderer());
        ItemList.blocks.addBlockType("slab", RenderType.SLAB, new SlabRenderer());
        ItemList.blocks.addBlockType("stairs", RenderType.STAIRS, new StairsRenderer());
        ItemList.blocks.addBlockType("fence", RenderType.FENCE, new FenceRenderer());
        ItemList.blocks.addBlockType("wall", RenderType.WALL_ITEM, new WallItemRenderer());
        ItemList.blocks.addBlockType("lamp", RenderType.LAMP, new LampRenderer());
        ItemList.blocks.addBlockType("pane", RenderType.PANE, new PaneRenderer());
        ItemList.blocks.addBlockType("track", RenderType.RAISED_TRACK, new RaisedTrackRenderer());
        ItemList.blocks.addBlockType("torch", RenderType.TORCH, new TorchRenderer());
        ItemList.blocks.addBlockType("pillar", RenderType.PILLAR, new PillarRenderer());
        ItemList.blocks.addBlockType("trapdoor", RenderType.TRAPDOOR, new TrapdoorRenderer());
        ItemList.blocks.addBlockType("fence gate", RenderType.FENCE_GATE, new FenceGateRenderer());
        ItemList.blocks.addBlockType("door half", RenderType.DOOR_HALF, new DoorHalfRenderer());


        System.out.println("Initializing items...");
        Block[] blockList = new Block[]{//                BLOCK_BEDROCK, BLOCK_BIRCH_LOG, BLOCK_BIRCH_LEAVES, BLOCK_BRICK, BLOCK_VINES, BLOCK_DIRT, BLOCK_PANSIES, BLOCK_GRASS, BLOCK_GRAVEL, BLOCK_OAK_LOG, BLOCK_OAK_LEAVES, BLOCK_SEA_LIGHT, BLOCK_PLANT_GRASS, BLOCK_BAMBOO, BLOCK_SAND, BLOCK_SANDSTONE, BLOCK_ANDESITE, BLOCK_STONE_BRICK, BLOCK_TORCH, BLOCK_WATER, BLOCK_WOOL, BLOCK_SNOW, BLOCK_BOOKSHELF, BLOCK_LAVA, BLOCK_TALL_DRY_GRASS.topBlock, BLOCK_TALL_DRY_GRASS.bottomBlock, BLOCK_CRACKED_STONE, BLOCK_STONE_WITH_VINES, BLOCK_TNT_ACTIVE, BLOCK_JUNGLE_PLANKS, BLOCK_JUNGLE_PLANKS_SLAB, BLOCK_JUNGLE_PLANKS_STAIRS, BLOCK_HONEYCOMB_BLOCK, BLOCK_MOSAIC_BAMBOO_WOOD, BLOCK_MUSIC_BOX, BLOCK_CAKE, BLOCK_JUNGLE_SAPLING, BLOCK_OBSIDIAN, BLOCK_BURGUNDY_BRICK, BLOCK_JUNGLE_FENCE, BLOCK_RED_FLOWER, BLOCK_RED_CANDLE, BLOCK_YELLOW_FLOWER, BLOCK_COAL_ORE, BLOCK_COAL_BLOCK, BLOCK_JUNGLE_LEAVES, BLOCK_JUNGLE_LOG, BLOCK_TALL_GRASS.topBlock, BLOCK_TALL_GRASS.bottomBlock, BLOCK_CONTROL_PANEL, BLOCK_BEEHIVE, BLOCK_DIORITE, BLOCK_POLISHED_DIORITE, BLOCK_EDISON_LIGHT, BLOCK_POLISHED_ANDESITE, BLOCK_SPRUCE_PLANKS, BLOCK_AZURE_BLUET, BLOCK_DANDELION, BLOCK_BLUE_ORCHID, BLOCK_FERN, BLOCK_GRANITE_BRICK, BLOCK_ACACIA_PLANKS, BLOCK_AMETHYST_CRYSTAL, BLOCK_CLAY, BLOCK_YELLOW_CONCRETE, BLOCK_YELLOW_GLAZED_TERACOTTA, BLOCK_BLACK_CONCRETE, BLOCK_BLACK_GLAZED_TERACOTTA, BLOCK_BLUE_CONCRETE, BLOCK_BLUE_GLAZED_TERACOTTA, BLOCK_BROWN_CONCRETE, BLOCK_BROWN_GLAZED_TERACOTTA, BLOCK_CYAN_CONCRETE, BLOCK_CYAN_GLAZED_TERACOTTA, BLOCK_GRAY_CONCRETE, BLOCK_GRAY_GLAZED_TERACOTTA, BLOCK_GREEN_CONCRETE, BLOCK_GREEN_GLAZED_TERACOTTA, BLOCK_LIGHT_BLUE_CONCRETE, BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA, BLOCK_LIGHT_GRAY_CONCRETE, BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA, BLOCK_LIME_CONCRETE, BLOCK_LIME_GLAZED_TERACOTTA, BLOCK_MAGENTA_CONCRETE, BLOCK_MAGENTA_GLAZED_TERACOTTA, BLOCK_ORANGE_CONCRETE, BLOCK_ORANGE_GLAZED_TERACOTTA, BLOCK_PINK_CONCRETE, BLOCK_PINK_GLAZED_TERACOTTA, BLOCK_PURPLE_CONCRETE, BLOCK_PURPLE_GLAZED_TERACOTTA, BLOCK_RED_CONCRETE, BLOCK_RED_GLAZED_TERACOTTA, BLOCK_WHITE_CONCRETE, BLOCK_WHITE_GLAZED_TERACOTTA, BLOCK_BRAIN_CORAL, BLOCK_DIAMOND_ORE, BLOCK_QUARTZ_PILLAR_BLOCK, BLOCK_COBBLESTONE, BLOCK_WOOL_TURQUOISE, BLOCK_WOOL_ORANGE, BLOCK_RED_SAND, BLOCK_WATERMELON, BLOCK_PHANTOM_STONE, BLOCK_PHANTOM_STONE_BRICK, BLOCK_CACTUS, BLOCK_PALISADE_STONE, BLOCK_RED_SANDSTONE, BLOCK_FIRE_CORAL_BLOCK, BLOCK_CANDLE, BLOCK_PALISADE_STONE_2, BLOCK_FIRE_CORAL, BLOCK_HORN_CORAL_BLOCK, BLOCK_GOLD_BLOCK, BLOCK_HORN_CORAL_FAN, BLOCK_TNT, BLOCK_WHEAT, BLOCK_CARROTS, BLOCK_MINI_CACTUS, BLOCK_MUSHROOM, BLOCK_MUSHROOM_2, BLOCK_ROSES, BLOCK_WOOL_PURPLE, BLOCK_BIRCH_PLANKS, BLOCK_RED_STAINED_WOOD, BLOCK_OAK_PLANKS, BLOCK_WOOL_RED, BLOCK_WOOL_PINK, BLOCK_WOOL_YELLOW, BLOCK_WOOL_BROWN, BLOCK_TUBE_CORAL_BLOCK, BLOCK_TUBE_CORAL, BLOCK_TUBE_CORAL_FAN, BLOCK_WOOL_DEEP_BLUE, BLOCK_WOOL_SKY_BLUE, BLOCK_WOOL_DARK_GREEN, BLOCK_WOOL_GREEN, BLOCK_WOOL_GRAY, BLOCK_BRAIN_CORAL_BLOCK, BLOCK_DIAMOND_BLOCK, BLOCK_POTATOES_PLANT, BLOCK_HONEYCOMB_BLOCK_STAIRS, BLOCK_OAK_FENCE, BLOCK_BIRCH_FENCE, BLOCK_BUBBLE_CORAL_BLOCK, BLOCK_BUBBLE_CORAL, BLOCK_BUBBLE_CORAL_FAN, BLOCK_JUNGLE_GRASS, BLOCK_LILY_PAD, BLOCK_TRACK, BLOCK_WOOL_MAGENTA, BLOCK_WOOL_BLACK, BLOCK_GRANITE_BRICK_STAIRS, BLOCK_CEMENT, BLOCK_OAK_SAPLING, BLOCK_BIRCH_SAPLING, BLOCK_WHEAT_SEEDS, BLOCK_CARROT_SEEDS, BLOCK_POTATO_SEEDS, BLOCK_A1, BLOCK_A2, BLOCK_B1, BLOCK_B2, BLOCK_B3, BLOCK_B4, BLOCK_B5, BLOCK_B6, BLOCK_ELECTRIC_LIGHT, BLOCK_RED_PALISADE_SANDSTONE, BLOCK_PHANTOM_SANDSTONE, BLOCK_PALISADE_SANDSTONE, BLOCK_GLOW_ROCK, BLOCK_DARK_OAK_FENCE, BLOCK_BIRCH_PLANKS_STAIRS, BLOCK_OAK_PLANKS_STAIRS, BLOCK_DARK_OAK_PLANKS_STAIRS, BLOCK_HONEYCOMB_BLOCK_SLAB, BLOCK_JUNGLE_GRASS_PLANT, BLOCK_PRISMARINE_BRICK_STAIRS, BLOCK_SANDSTONE_STAIRS, BLOCK_CAVE_VINES_FLAT, BLOCK_POLISHED_DIORITE_STAIRS, BLOCK_BIRCH_PLANKS_SLAB, BLOCK_OAK_PLANKS_SLAB, BLOCK_DARK_OAK_PLANKS_SLAB, BLOCK_BAMBOO_WOOD_STAIRS, BLOCK_STONE_BRICK_SLAB, BLOCK_RED_SANDSTONE_SLAB, BLOCK_SANDSTONE_SLAB, BLOCK_DRY_GRASS, BLOCK_POLISHED_ANDESITE_SLAB, BLOCK_IRON_LADDER, BLOCK_RED_VINES_FLAT, BLOCK_YELLOW_CANDLE, BLOCK_CAVE_VINES, BLOCK_GREEN_CANDLE, BLOCK_GRANITE, BLOCK_BLUE_CANDLE, BLOCK_RED_VINES, BLOCK_FLAT_VINES, BLOCK_DARK_OAK_LADDER, BLOCK_DRY_GRASS_PLANT, BLOCK_ACACIA_LEAVES, BLOCK_ACACIA_LOG, BLOCK_FIRE_CORAL_FAN, BLOCK_LAPIS_LAZUL_ORE, BLOCK_LAPIS_LAZUL_BLOCK, BLOCK_ACACIA_SAPLING, BLOCK_BRAIN_CORAL_FAN, BLOCK_WHITE_ROSE, BLOCK_GOLD_ORE, BLOCK_HORN_CORAL, BLOCK_RED_ROSE, BLOCK_EMERALD_ORE, BLOCK_EMERALD_BLOCK, BLOCK_BLACK_EYE_SUSAN, BLOCK_ORANGE_TULIP, BLOCK_DEAD_BUSH, BLOCK_HAY_BAIL, BLOCK_POLISHED_ANDESITE_STAIRS, BLOCK_POLISHED_DIORITE_SLAB, BLOCK_CURVED_TRACK, BLOCK_BEETS, BLOCK_BEETROOT_SEEDS, BLOCK_BAMBOO_LADDER, BLOCK_ACACIA_FENCE, BLOCK_ACACIA_PLANKS_STAIRS, BLOCK_ACACIA_PLANKS_SLAB, BLOCK_RAISED_TRACK, BLOCK_SEA_GRASS, BLOCK_BLUE_STAINED_WOOD, BLOCK_RUBY_CRYSTAL, BLOCK_JADE_CRYSTAL, BLOCK_AQUAMARINE_CRYSTAL, BLOCK_BAMBOO_WOOD_SLAB, BLOCK_RED_SANDSTONE_STAIRS, BLOCK_STONE_BRICK_STAIRS, BLOCK_STONE_BRICK_FENCE, BLOCK_BRICK_STAIRS, BLOCK_BRICK_SLAB, BLOCK_SNOW_BLOCK, BLOCK_COBBLESTONE_STAIRS, BLOCK_COBBLESTONE_SLAB, BLOCK_PALISADE_STONE_STAIRS, BLOCK_PALISADE_STONE_SLAB, BLOCK_PALISADE_STONE_FENCE, BLOCK_PALISADE_STONE_2_STAIRS, BLOCK_PALISADE_STONE_2_SLAB, BLOCK_PALISADE_STONE_2_FENCE, BLOCK_POLISHED_DIORITE_FENCE, BLOCK_POLISHED_ANDESITE_FENCE, BLOCK_CRACKED_STONE_STAIRS, BLOCK_CRACKED_STONE_SLAB, BLOCK_CRACKED_STONE_FENCE, BLOCK_STONE_WITH_VINES_STAIRS, BLOCK_STONE_WITH_VINES_SLAB, BLOCK_STONE_WITH_VINES_FENCE, BLOCK_BURGUNDY_BRICK_STAIRS, BLOCK_BURGUNDY_BRICK_SLAB, BLOCK_BURGUNDY_BRICK_FENCE, BLOCK_SWITCH_JUNCTION, BLOCK_TRACK_STOP, BLOCK_RED_PALISADE_SANDSTONE_STAIRS, BLOCK_RED_PALISADE_SANDSTONE_SLAB, BLOCK_RED_PALISADE_SANDSTONE_FENCE, BLOCK_PALISADE_SANDSTONE_STAIRS, BLOCK_PALISADE_SANDSTONE_SLAB, BLOCK_PALISADE_SANDSTONE_FENCE, BLOCK_WOOL_STAIRS, BLOCK_WOOL_SLAB, BLOCK_WOOL_GRAY_STAIRS, BLOCK_WOOL_GRAY_SLAB, BLOCK_WOOL_RED_STAIRS, BLOCK_WOOL_RED_SLAB, BLOCK_WOOL_PINK_STAIRS, BLOCK_WOOL_PINK_SLAB, BLOCK_WOOL_ORANGE_STAIRS, BLOCK_WOOL_ORANGE_SLAB, BLOCK_WOOL_YELLOW_STAIRS, BLOCK_WOOL_YELLOW_SLAB, BLOCK_WOOL_GREEN_STAIRS, BLOCK_WOOL_GREEN_SLAB, BLOCK_WOOL_DARK_GREEN_STAIRS, BLOCK_WOOL_DARK_GREEN_SLAB, BLOCK_WOOL_TURQUOISE_STAIRS, BLOCK_WOOL_TURQUOISE_SLAB, BLOCK_WOOL_DEEP_BLUE_STAIRS, BLOCK_WOOL_DEEP_BLUE_SLAB, BLOCK_WOOL_SKY_BLUE_STAIRS, BLOCK_WOOL_SKY_BLUE_SLAB, BLOCK_WOOL_BROWN_STAIRS, BLOCK_WOOL_BROWN_SLAB, BLOCK_WOOL_PURPLE_STAIRS, BLOCK_WOOL_PURPLE_SLAB, BLOCK_WOOL_MAGENTA_STAIRS, BLOCK_WOOL_MAGENTA_SLAB, BLOCK_WOOL_BLACK_STAIRS, BLOCK_WOOL_BLACK_SLAB, BLOCK_YELLOW_CONCRETE_STAIRS, BLOCK_YELLOW_CONCRETE_SLAB, BLOCK_YELLOW_CONCRETE_FENCE, BLOCK_BLACK_CONCRETE_STAIRS, BLOCK_BLACK_CONCRETE_SLAB, BLOCK_BLACK_CONCRETE_FENCE, BLOCK_BLUE_CONCRETE_STAIRS, BLOCK_BLUE_CONCRETE_SLAB, BLOCK_BLUE_CONCRETE_FENCE, BLOCK_BROWN_CONCRETE_STAIRS, BLOCK_BROWN_CONCRETE_SLAB, BLOCK_BROWN_CONCRETE_FENCE, BLOCK_CYAN_CONCRETE_STAIRS, BLOCK_CYAN_CONCRETE_SLAB, BLOCK_CYAN_CONCRETE_FENCE, BLOCK_GRAY_CONCRETE_STAIRS, BLOCK_GRAY_CONCRETE_SLAB, BLOCK_GRAY_CONCRETE_FENCE, BLOCK_GREEN_CONCRETE_STAIRS, BLOCK_GREEN_CONCRETE_SLAB, BLOCK_GREEN_CONCRETE_FENCE, BLOCK_LIGHT_BLUE_CONCRETE_STAIRS, BLOCK_LIGHT_BLUE_CONCRETE_SLAB, BLOCK_LIGHT_BLUE_CONCRETE_FENCE, BLOCK_LIGHT_GRAY_CONCRETE_STAIRS, BLOCK_LIGHT_GRAY_CONCRETE_SLAB, BLOCK_LIGHT_GRAY_CONCRETE_FENCE, BLOCK_LIME_CONCRETE_STAIRS, BLOCK_LIME_CONCRETE_SLAB, BLOCK_LIME_CONCRETE_FENCE, BLOCK_MAGENTA_CONCRETE_STAIRS, BLOCK_MAGENTA_CONCRETE_SLAB, BLOCK_MAGENTA_CONCRETE_FENCE, BLOCK_ORANGE_CONCRETE_STAIRS, BLOCK_ORANGE_CONCRETE_SLAB, BLOCK_ORANGE_CONCRETE_FENCE, BLOCK_PINK_CONCRETE_STAIRS, BLOCK_PINK_CONCRETE_SLAB, BLOCK_PINK_CONCRETE_FENCE, BLOCK_PURPLE_CONCRETE_STAIRS, BLOCK_PURPLE_CONCRETE_SLAB, BLOCK_PURPLE_CONCRETE_FENCE, BLOCK_RED_CONCRETE_STAIRS, BLOCK_RED_CONCRETE_SLAB, BLOCK_RED_CONCRETE_FENCE, BLOCK_WHITE_CONCRETE_STAIRS, BLOCK_WHITE_CONCRETE_SLAB, BLOCK_WHITE_CONCRETE_FENCE, BLOCK_YELLOW_GLAZED_TERACOTTA_STAIRS, BLOCK_YELLOW_GLAZED_TERACOTTA_SLAB, BLOCK_BLACK_GLAZED_TERACOTTA_STAIRS, BLOCK_BLACK_GLAZED_TERACOTTA_SLAB, BLOCK_BLUE_GLAZED_TERACOTTA_STAIRS, BLOCK_BLUE_GLAZED_TERACOTTA_SLAB, BLOCK_BROWN_GLAZED_TERACOTTA_STAIRS, BLOCK_BROWN_GLAZED_TERACOTTA_SLAB, BLOCK_CYAN_GLAZED_TERACOTTA_STAIRS, BLOCK_CYAN_GLAZED_TERACOTTA_SLAB, BLOCK_GRAY_GLAZED_TERACOTTA_STAIRS, BLOCK_GRAY_GLAZED_TERACOTTA_SLAB, BLOCK_GREEN_GLAZED_TERACOTTA_STAIRS, BLOCK_GREEN_GLAZED_TERACOTTA_SLAB, BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_STAIRS, BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_SLAB, BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA_STAIRS, BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA_SLAB, BLOCK_LIME_GLAZED_TERACOTTA_STAIRS, BLOCK_LIME_GLAZED_TERACOTTA_SLAB, BLOCK_MAGENTA_GLAZED_TERACOTTA_STAIRS, BLOCK_MAGENTA_GLAZED_TERACOTTA_SLAB, BLOCK_ORANGE_GLAZED_TERACOTTA_STAIRS, BLOCK_ORANGE_GLAZED_TERACOTTA_SLAB, BLOCK_PINK_GLAZED_TERACOTTA_STAIRS, BLOCK_PINK_GLAZED_TERACOTTA_SLAB, BLOCK_PURPLE_GLAZED_TERACOTTA_STAIRS, BLOCK_PURPLE_GLAZED_TERACOTTA_SLAB, BLOCK_RED_GLAZED_TERACOTTA_STAIRS, BLOCK_RED_GLAZED_TERACOTTA_SLAB, BLOCK_WHITE_GLAZED_TERACOTTA_STAIRS, BLOCK_WHITE_GLAZED_TERACOTTA_SLAB, BLOCK_OAK_LADDER, BLOCK_MINECART_ROAD_BLOCK, BLOCK_STONE, BLOCK_BAMBOO_WOOD_FENCE, BLOCK_CROISSANT, BLOCK_MINECART_ROAD_SLAB, BLOCK_PRISMARINE_BRICKS, BLOCK_DARK_PRISMARINE_BRICKS, BLOCK_BLUE_TORCH, BLOCK_CEMENT_STAIRS, BLOCK_CEMENT_FENCE, BLOCK_CEMENT_SLAB, BLOCK_OBSIDIAN_SLAB, BLOCK_OBSIDIAN_FENCE, BLOCK_LAPIS_LAZUL_STAIRS, BLOCK_LAPIS_LAZUL_BLOCK_SLAB, BLOCK_LAPIS_LAZUL_FENCE, BLOCK_IRON_STAIRS, BLOCK_IRON_BLOCK_SLAB, BLOCK_IRON_FENCE, BLOCK_GOLD_STAIRS, BLOCK_GOLD_BLOCK_SLAB, BLOCK_GOLD_FENCE, BLOCK_EMERALD_STAIRS, BLOCK_EMERALD_BLOCK_SLAB, BLOCK_EMERALD_FENCE, BLOCK_DIAMOND_STAIRS, BLOCK_DIAMOND_BLOCK_SLAB, BLOCK_DIAMOND_FENCE, BLOCK_CROSSWALK_PAINT, BLOCK_DARK_PRISMARINE_BRICK_STAIRS, BLOCK_PRISMARINE_BRICK_SLAB, BLOCK_OBSIDIAN_STAIRS, BLOCK_PRISMARINE_BRICK_FENCE, BLOCK_RED_SANDSTONE_PILLAR, BLOCK_STONE_BRICK_PILLAR, BLOCK_PALISADE_SANDSTONE_PILLAR, BLOCK_PALISADE_STONE_PILLAR, BLOCK_PALISADE_STONE_2_PILLAR, BLOCK_CRACKED_STONE_PILLAR, BLOCK_STONE_WITH_VINES_PILLAR, BLOCK_FLAT_DRAGON_VINES, BLOCK_DRAGON_VINES, BLOCK_RED_PALISADE_SANDSTONE_PILLAR, BLOCK_MARBLE_PILLAR_BLOCK, BLOCK_QUARTZ_PILLAR, BLOCK_MARBLE_PILLAR, BLOCK_FARMLAND, BLOCK_ROAD_MARKINGS, BLOCK_GRANITE_BRICK_PILLAR, BLOCK_GRANITE_BRICK_SLAB, BLOCK_GRANITE_BRICK_FENCE, BLOCK_CAMPFIRE, BLOCK_DARK_PRISMARINE_BRICK_SLAB, BLOCK_DARK_PRISMARINE_BRICK_FENCE, BLOCK_ENGRAVED_SANDSTONE, BLOCK_ENGRAVED_RED_SANDSTONE, BLOCK_ORANGE_MARBLE_TILE, BLOCK_CHECKERBOARD_CHISELED_MARBLE, BLOCK_CHISELED_MARBLE, BLOCK_CHISELED_QUARTZ, BLOCK_MARBLE_TILE, BLOCK_BLUE_MARBLE_TILE, BLOCK_GREEN_MARBLE_TILE, BLOCK_ORANGE_MARBLE_TILE_SLAB, BLOCK_GRAY_MARBLE_TILE, BLOCK_ORANGE_MARBLE_TILE_STAIRS, BLOCK_GREEN_MARBLE_TILE_SLAB, BLOCK_CHECKERBOARD_CHISELED_MARBLE_STAIRS, BLOCK_CHECKERBOARD_CHISELED_MARBLE_SLAB, BLOCK_CHISELED_MARBLE_STAIRS, BLOCK_CHISELED_MARBLE_SLAB, BLOCK_MARBLE_TILE_STAIRS, BLOCK_MARBLE_TILE_SLAB, BLOCK_GRAY_MARBLE_TILE_STAIRS, BLOCK_GRAY_MARBLE_TILE_SLAB, BLOCK_BLUE_MARBLE_TILE_STAIRS, BLOCK_BLUE_MARBLE_TILE_SLAB, BLOCK_GREEN_MARBLE_TILE_STAIRS, BLOCK_CHISELED_QUARTZ_STAIRS, BLOCK_CHISELED_QUARTZ_SLAB, BLOCK_CHISELED_QUARTZ_PILLAR, BLOCK_GRAY_MARBLE_TILE_PILLAR, BLOCK_BLUE_MARBLE_TILE_PILLAR, BLOCK_GREEN_MARBLE_TILE_PILLAR, BLOCK_ORANGE_MARBLE_TILE_PILLAR, BLOCK_MARBLE_TILE_PILLAR, BLOCK_LAMP, BLOCK_BLUE_LAMP, BLOCK_START_BOUNDARY_BLOCK, BLOCK_PASTE_BLOCK, BLOCK_YELLOW_CHISELED_MARBLE_TILE, BLOCK_MEGA_TNT, BLOCK_CROSSTRACK, BLOCK_ADDITIVE_PASTE_BLOCK, BLOCK_PASTE_ROTATE_BLOCK, BLOCK_BLACK_CHISELED_MARBLE_TILE, BLOCK_BLUE_CHISELED_MARBLE_TILE, BLOCK_BROWN_CHISELED_MARBLE_TILE, BLOCK_CYAN_CHISELED_MARBLE_TILE, BLOCK_GRAY_CHISELED_MARBLE_TILE, BLOCK_GREEN_CHISELED_MARBLE_TILE, BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE, BLOCK_MAGENTA_CHISELED_MARBLE_TILE, BLOCK_ORANGE_CHISELED_MARBLE_TILE, BLOCK_PINK_CHISELED_MARBLE_TILE, BLOCK_PURPLE_CHISELED_MARBLE_TILE, BLOCK_BURGUNDY_CHISELED_MARBLE_TILE, BLOCK_BAMBOO_BLOCK, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE, BLOCK_YELLOW_MARBLE_TILE, BLOCK_BLACK_MARBLE_TILE, BLOCK_ICE_BLOCK, BLOCK_BROWN_MARBLE_TILE, BLOCK_CYAN_MARBLE_TILE, BLOCK_BAMBOO_WOOD, BLOCK_BOTTLE, BLOCK_PASTEL_BLUE_MARBLE_TILE, BLOCK_PASTEL_GREEN_MARBLE_TILE, BLOCK_MAGENTA_MARBLE_TILE, BLOCK_CUP, BLOCK_PINK_MARBLE_TILE, BLOCK_PURPLE_MARBLE_TILE, BLOCK_BURGUNDY_MARBLE_TILE, BLOCK_PASTEL_RED_MARBLE_TILE, BLOCK_WINE_GLASS, BLOCK_YELLOW_STAINED_WOOD, BLOCK_BLACK_STAINED_WOOD, BLOCK_YELLOW_MARBLE_TILE_STAIRS, BLOCK_YELLOW_MARBLE_TILE_SLAB, BLOCK_YELLOW_MARBLE_TILE_PILLAR, BLOCK_BLACK_MARBLE_TILE_STAIRS, BLOCK_BLACK_MARBLE_TILE_SLAB, BLOCK_BLACK_MARBLE_TILE_PILLAR, BLOCK_BREAD, BLOCK_CYAN_STAINED_WOOD, BLOCK_PINK_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BROWN_MARBLE_TILE_STAIRS, BLOCK_BROWN_MARBLE_TILE_SLAB, BLOCK_BROWN_MARBLE_TILE_PILLAR, BLOCK_CYAN_MARBLE_TILE_STAIRS, BLOCK_CYAN_MARBLE_TILE_SLAB, BLOCK_CYAN_MARBLE_TILE_PILLAR, BLOCK_PURPLE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PURPLE_CHISELED_MARBLE_TILE_SLAB, BLOCK_PURPLE_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_SLAB, BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PASTEL_BLUE_MARBLE_TILE_STAIRS, BLOCK_PASTEL_BLUE_MARBLE_TILE_SLAB, BLOCK_PASTEL_BLUE_MARBLE_TILE_PILLAR, BLOCK_PASTEL_GREEN_MARBLE_TILE_STAIRS, BLOCK_PASTEL_GREEN_MARBLE_TILE_SLAB, BLOCK_PASTEL_GREEN_MARBLE_TILE_PILLAR, BLOCK_MAGENTA_MARBLE_TILE_STAIRS, BLOCK_MAGENTA_MARBLE_TILE_SLAB, BLOCK_MAGENTA_MARBLE_TILE_PILLAR, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_SLAB, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PINK_MARBLE_TILE_STAIRS, BLOCK_PINK_MARBLE_TILE_SLAB, BLOCK_PINK_MARBLE_TILE_PILLAR, BLOCK_PURPLE_MARBLE_TILE_STAIRS, BLOCK_PURPLE_MARBLE_TILE_SLAB, BLOCK_PURPLE_MARBLE_TILE_PILLAR, BLOCK_BURGUNDY_MARBLE_TILE_STAIRS, BLOCK_BURGUNDY_MARBLE_TILE_SLAB, BLOCK_BURGUNDY_MARBLE_TILE_PILLAR, BLOCK_PASTEL_RED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_RED_MARBLE_TILE_SLAB, BLOCK_PASTEL_RED_MARBLE_TILE_PILLAR, BLOCK_YELLOW_CHISELED_MARBLE_TILE_STAIRS, BLOCK_YELLOW_CHISELED_MARBLE_TILE_SLAB, BLOCK_YELLOW_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BLACK_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BLACK_CHISELED_MARBLE_TILE_SLAB, BLOCK_BLACK_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BLUE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BLUE_CHISELED_MARBLE_TILE_SLAB, BLOCK_BLUE_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BROWN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BROWN_CHISELED_MARBLE_TILE_SLAB, BLOCK_BROWN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_CYAN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_CYAN_CHISELED_MARBLE_TILE_SLAB, BLOCK_CYAN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_GRAY_CHISELED_MARBLE_TILE_STAIRS, BLOCK_GRAY_CHISELED_MARBLE_TILE_SLAB, BLOCK_GRAY_CHISELED_MARBLE_TILE_PILLAR, BLOCK_GREEN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_GREEN_CHISELED_MARBLE_TILE_SLAB, BLOCK_GREEN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_SLAB, BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_SLAB, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_MAGENTA_CHISELED_MARBLE_TILE_STAIRS, BLOCK_MAGENTA_CHISELED_MARBLE_TILE_SLAB, BLOCK_MAGENTA_CHISELED_MARBLE_TILE_PILLAR, BLOCK_ORANGE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_ORANGE_CHISELED_MARBLE_TILE_SLAB, BLOCK_ORANGE_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PINK_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PINK_CHISELED_MARBLE_TILE_SLAB, BLOCK_GRAY_STAINED_WOOD, BLOCK_GREEN_STAINED_WOOD, BLOCK_LIGHT_BLUE_STAINED_WOOD, BLOCK_LIME_STAINED_WOOD, BLOCK_MAGENTA_STAINED_WOOD, BLOCK_ORANGE_STAINED_WOOD, BLOCK_PINK_STAINED_WOOD, BLOCK_PURPLE_STAINED_WOOD, BLOCK_WHITE_SPACE_TILE_STAIRS, BLOCK_WHITE_STAINED_WOOD, BLOCK_ENGRAVED_SANDSTONE_2, BLOCK_ENGRAVED_RED_SANDSTONE_2, BLOCK_PEONY_BUSH, BLOCK_ICE_BLOCK_STAIRS, BLOCK_ICE_BLOCK_SLAB, BLOCK_SOLAR_PANEL, BLOCK_MOSAIC_BAMBOO_WOOD_STAIRS, BLOCK_MOSAIC_BAMBOO_WOOD_SLAB, BLOCK_MOSAIC_BAMBOO_WOOD_FENCE, BLOCK_YELLOW_STAINED_WOOD_STAIRS, BLOCK_YELLOW_STAINED_WOOD_SLAB, BLOCK_YELLOW_STAINED_WOOD_FENCE, BLOCK_BLACK_STAINED_WOOD_STAIRS, BLOCK_BLACK_STAINED_WOOD_SLAB, BLOCK_BLACK_STAINED_WOOD_FENCE, BLOCK_BLUE_STAINED_WOOD_STAIRS, BLOCK_BLUE_STAINED_WOOD_SLAB, BLOCK_BLUE_STAINED_WOOD_FENCE, BLOCK_CYAN_STAINED_WOOD_STAIRS, BLOCK_CYAN_STAINED_WOOD_SLAB, BLOCK_CYAN_STAINED_WOOD_FENCE, BLOCK_GRAY_STAINED_WOOD_STAIRS, BLOCK_GRAY_STAINED_WOOD_SLAB, BLOCK_GRAY_STAINED_WOOD_FENCE, BLOCK_GREEN_STAINED_WOOD_STAIRS, BLOCK_GREEN_STAINED_WOOD_SLAB, BLOCK_GREEN_STAINED_WOOD_FENCE, BLOCK_LIGHT_BLUE_STAINED_WOOD_STAIRS, BLOCK_LIGHT_BLUE_STAINED_WOOD_SLAB, BLOCK_LIGHT_BLUE_STAINED_WOOD_FENCE, BLOCK_LIME_STAINED_WOOD_STAIRS, BLOCK_LIME_STAINED_WOOD_SLAB, BLOCK_LIME_STAINED_WOOD_FENCE, BLOCK_MAGENTA_STAINED_WOOD_STAIRS, BLOCK_MAGENTA_STAINED_WOOD_SLAB, BLOCK_MAGENTA_STAINED_WOOD_FENCE, BLOCK_ORANGE_STAINED_WOOD_STAIRS, BLOCK_ORANGE_STAINED_WOOD_SLAB, BLOCK_ORANGE_STAINED_WOOD_FENCE, BLOCK_PINK_STAINED_WOOD_STAIRS, BLOCK_PINK_STAINED_WOOD_SLAB, BLOCK_PINK_STAINED_WOOD_FENCE, BLOCK_PURPLE_STAINED_WOOD_STAIRS, BLOCK_PURPLE_STAINED_WOOD_SLAB, BLOCK_PURPLE_STAINED_WOOD_FENCE, BLOCK_RED_STAINED_WOOD_STAIRS, BLOCK_RED_STAINED_WOOD_SLAB, BLOCK_RED_STAINED_WOOD_FENCE, BLOCK_WHITE_STAINED_WOOD_STAIRS, BLOCK_WHITE_STAINED_WOOD_SLAB, BLOCK_WHITE_STAINED_WOOD_FENCE, BLOCK_WHITE_SPACE_TILE, BLOCK_GRAY_SPACE_TILE, BLOCK_WHITE_SPACE_TILE_SLAB, BLOCK_GRAY_SPACE_TILE_STAIRS, BLOCK_GRAY_SPACE_TILE_SLAB, BLOCK_SPRUCE_LOG, BLOCK_SPRUCE_LEAVES, BLOCK_SPRUCE_SAPLING, BLOCK_PASTE_VERTEX_BLOCK, BLOCK_MERGE_TRACK,
        };
        Block blocks[] = getAllJsonBlocks(ResourceUtils.resource("\\items\\blocks\\json"));

        synthesizeBlocks(blocks);

        blockList = ArrayUtils.concatenateArrays(blockList, blocks);

        EntityLink[] entityList = new EntityLink[]{
                //Foxes
                new FoxLink(window, 0, "Red Fox", "red.png"),
                new FoxLink(window, 1, "Gray Fox", "gray.png"),
                new FoxLink(window, 2, "White Fox", "white.png"),
                //Cats
                new CatLink(window, 3, "Black Cat", "black.png"),
                new CatLink(window, 4, "British Shorthair Cat", "british_shorthair.png"),
                new CatLink(window, 5, "Calico Cat", "calico.png"),
                new CatLink(window, 6, "Calico Cat", "calico2.png"),
                new CatLink(window, 7, "Jellie Cat", "jellie.png"),
                new CatLink(window, 8, "Ocelot", "ocelot.png"),
                new CatLink(window, 9, "Persian Cat", "persian.png"),
                new CatLink(window, 10, "Ragdoll Cat", "ragdoll.png"),
                new CatLink(window, 11, "Red Cat", "red.png"),
                new CatLink(window, 12, "Siamese Cat", "siamese.png"),
                new CatLink(window, 13, "Tabby Cat", "tabby.png"),
                new CatLink(window, 14, "White Cat", "white.png"),
                //Rabbits
                new RabbitLink(window, 15, "Black Rabbit", "black.png"),
                new RabbitLink(window, 16, "White Rabbit", "white.png"),
                new RabbitLink(window, 17, "Brown Rabbit", "brown.png"),
                new RabbitLink(window, 18, "Caerbannog Rabbit", "caerbannog.png"),
                new RabbitLink(window, 19, "Gold Rabbit", "gold.png"),
                new RabbitLink(window, 20, "Salt Rabbit", "salt.png"),
                new RabbitLink(window, 21, "Toast Rabbit", "toast.png"),
                new RabbitLink(window, 22, "White Splotched Rabbit", "white_splotched.png"),

                //Horses
                new HorseLink(window, 23, "Black Horse", "black.png"),
                new HorseLink(window, 24, "Brown Horse", "brown.png"),
                new HorseLink(window, 25, "Chestnut Horse", "chestnut.png"),
                new HorseLink(window, 26, "Creamy Horse", "creamy.png"),
                new HorseLink(window, 27, "Dark Brown Horse", "darkbrown.png"),
                new HorseLink(window, 28, "White Horse", "white.png"),
                new HorseLink(window, 29, "Gray Horse", "gray.png"),
                //Mules
                new MuleLink(window, 30, "Mule", "mule.png"),
                new MuleLink(window, 31, "Donkey", "donkey.png"),

                //Dogs
                new DogLink(window, 32, "Black Dog", "black.png"),
                new DogLink(window, 33, "Brown Dog", "brown.png"),
                new DogLink(window, 34, "Gold Dog", "gold.png"),
                new DogLink(window, 35, "White Dog", "white.png"),
        };

        Tool[] tools = new Tool[]{
            new Saddle(),
            new Hoe(),
            new AnimalFeed()
        };


        //Add terrains
        terrainsList.add(new TestTerrain());
        terrainsList.add(new BasicTerrain());
        terrainsList.add(new TerrainV2(true));
        terrainsList.add(new TerrainV2(false));
        terrainsList.add(new DevTerrain());
        terrainsList.add(new ComplexTerrain());

        //Set items AFTER setting block types
        ItemList.setAllItems(blockList, entityList, tools);
        initializeAllItems();
    }

    private void initializeAllItems() {
        BlockEventUtils.setTNTEvents(ItemList.getBlock(BLOCK_TNT), 5, 1000);
        BlockEventUtils.setTNTEvents(ItemList.getBlock(BLOCK_MEGA_TNT), 10, 1000);

        Plant.makePlant(ItemList.getBlock(BLOCK_BEETROOT_SEEDS), BLOCK_A1, BLOCK_A2, BLOCK_BEETS);
        Plant.makePlant(ItemList.getBlock(BLOCK_CARROT_SEEDS), BLOCK_A1, BLOCK_A2, BLOCK_CARROTS_PLANT);
        Plant.makePlant(ItemList.getBlock(BLOCK_POTATO_SEEDS), BLOCK_A1, BLOCK_A2, BLOCK_POTATOES_PLANT);
        Plant.makePlant(ItemList.getBlock(BLOCK_WHEAT_SEEDS), BLOCK_B1, BLOCK_B2, BLOCK_B3, BLOCK_B5, BLOCK_B6, BLOCK_WHEAT);

        ItemList.getBlock(BLOCK_OAK_SAPLING).setBlockEvent(OakTreeUtils.setBlockEvent);
        ItemList.getBlock(BLOCK_SPRUCE_SAPLING).setBlockEvent(SpruceTreeUtils.setBlockEvent);
        ItemList.getBlock(BLOCK_BIRCH_SAPLING).setBlockEvent(BirchTreeUtils.setBlockEvent);
        ItemList.getBlock(BLOCK_JUNGLE_SAPLING).setBlockEvent(JungleTreeUtils.setBlockEvent);
        ItemList.getBlock(BLOCK_ACACIA_SAPLING).setBlockEvent(AcaciaTreeUtils.setBlockEvent);

        BlockEventUtils.makeVerticalPairedBlock(BLOCK_TALL_GRASS_TOP, BLOCK_TALL_GRASS);
        BlockEventUtils.makeVerticalPairedBlock(BLOCK_TALL_DRY_GRASS_TOP, BLOCK_TALL_DRY_GRASS);
    }

    @Override
    public boolean menusAreOpen() {
        return inventory.isOpen();
    }

    WorldInfo currentWorld;

    @Override
    public void startGame(WorldInfo worldInfo) {
        this.currentWorld = worldInfo;
        try {
            File f = new File(currentWorld.getDirectory() + "\\game.json");
            if (f.exists()) {
                System.out.println("Loading it");
                gameInfo = json.gson.fromJson(new FileReader(f), GameInfo.class);
                if (gameInfo == null) {
                    gameInfo = new GameInfo();
                }
            } else {
                System.out.println("Making new game info");
                gameInfo = new GameInfo();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyGame.class.getName()).log(Level.SEVERE, null, ex);
        }

        inventory.setPlayerInfo(gameInfo);
        hotbar.setPlayerInfo(gameInfo);
    }

    @Override
    public void saveState() {
        if (gameInfo != null) {
            File f = new File(currentWorld.getDirectory() + "\\game.json");
            try (FileWriter writer = new FileWriter(f)) {
                json.gson.toJson(gameInfo, writer);
            } catch (IOException ex) {
                Logger.getLogger(MyGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean includeBlockIcon(Block block) {
        return block.type != RenderType.SPRITE;
    }

    // <editor-fold defaultstate="collapsed" desc="Block IDs">
    public static short BLOCK_BEDROCK = 1;
    public static short BLOCK_BIRCH_LOG = 2;
    public static short BLOCK_BIRCH_LEAVES = 3;
    public static short BLOCK_BRICK = 4;
    public static short BLOCK_VINES = 5;
    public static short BLOCK_DIRT = 6;
    public static short BLOCK_PANSIES = 7;
    public static short BLOCK_GLASS = 8;
    public static short BLOCK_GRASS = 9;
    public static short BLOCK_GRAVEL = 10;
    public static short BLOCK_OAK_LOG = 11;
    public static short BLOCK_OAK_LEAVES = 12;
    public static short BLOCK_PLANT_GRASS = 14;
    public static short BLOCK_BAMBOO = 15;
    public static short BLOCK_SAND = 16;
    public static short BLOCK_SANDSTONE = 17;
    public static short BLOCK_ANDESITE = 18;
    public static short BLOCK_STONE_BRICK = 19;
    public static short BLOCK_TORCH = 20;
    public static short BLOCK_WATER = 21;
    public static short BLOCK_WOOL = 22;
    public static short BLOCK_SNOW = 23;
    public static short BLOCK_BOOKSHELF = 24;
    public static short BLOCK_LAVA = 25;
    public static short BLOCK_TALL_DRY_GRASS_TOP = 26;
    public static short BLOCK_CRACKED_STONE = 27;
    public static short BLOCK_STONE_WITH_VINES = 28;
    public static short BLOCK_TNT_ACTIVE = 29;
    public static short BLOCK_JUNGLE_PLANKS = 30;
    public static short BLOCK_JUNGLE_PLANKS_SLAB = 31;
    public static short BLOCK_JUNGLE_PLANKS_STAIRS = 32;
    public static short BLOCK_HONEYCOMB_BLOCK = 33;
    public static short BLOCK_MOSAIC_BAMBOO_WOOD = 34;
    public static short BLOCK_MUSIC_BOX = 35;
    public static short BLOCK_CAKE = 36;
    public static short BLOCK_JUNGLE_SAPLING = 37;
    public static short BLOCK_OBSIDIAN = 38;
    public static short BLOCK_BURGUNDY_BRICK = 39;
    public static short BLOCK_JUNGLE_FENCE = 40;
    public static short BLOCK_RED_FLOWER = 41;
    public static short BLOCK_TALL_DRY_GRASS = 42;
    public static short BLOCK_RED_CANDLE = 43;
    public static short BLOCK_YELLOW_FLOWER = 44;
    public static short BLOCK_COAL_BLOCK = 46;
    public static short BLOCK_JUNGLE_LEAVES = 47;
    public static short BLOCK_JUNGLE_LOG = 48;
    public static short BLOCK_TALL_GRASS_TOP = 49;
    public static short BLOCK_30_HP_ENGINE = 51;
    public static short BLOCK_BEEHIVE = 52;
    public static short BLOCK_DIORITE = 53;
    public static short BLOCK_POLISHED_DIORITE = 54;
    public static short BLOCK_EDISON_LIGHT = 55;
    public static short BLOCK_POLISHED_ANDESITE = 56;
    public static short BLOCK_SPRUCE_PLANKS = 57;
    public static short BLOCK_AZURE_BLUET = 58;
    public static short BLOCK_DANDELION = 59;
    public static short BLOCK_BLUE_ORCHID = 60;
    public static short BLOCK_FERN = 61;
    public static short BLOCK_GRANITE_BRICK = 62;
    public static short BLOCK_ACACIA_PLANKS = 63;
    public static short BLOCK_AMETHYST_CRYSTAL = 64;
    public static short BLOCK_CLAY = 65;
    public static short BLOCK_YELLOW_CONCRETE = 66;
    public static short BLOCK_YELLOW_GLAZED_TERACOTTA = 67;
    public static short BLOCK_BLACK_CONCRETE = 68;
    public static short BLOCK_BLACK_GLAZED_TERACOTTA = 69;
    public static short BLOCK_BLUE_CONCRETE = 70;
    public static short BLOCK_BLUE_GLAZED_TERACOTTA = 71;
    public static short BLOCK_BROWN_CONCRETE = 72;
    public static short BLOCK_BROWN_GLAZED_TERACOTTA = 73;
    public static short BLOCK_CYAN_CONCRETE = 74;
    public static short BLOCK_CYAN_GLAZED_TERACOTTA = 75;
    public static short BLOCK_GRAY_CONCRETE = 76;
    public static short BLOCK_GRAY_GLAZED_TERACOTTA = 77;
    public static short BLOCK_GREEN_CONCRETE = 78;
    public static short BLOCK_GREEN_GLAZED_TERACOTTA = 79;
    public static short BLOCK_LIGHT_BLUE_CONCRETE = 80;
    public static short BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA = 81;
    public static short BLOCK_LIGHT_GRAY_CONCRETE = 82;
    public static short BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA = 83;
    public static short BLOCK_LIME_CONCRETE = 84;
    public static short BLOCK_LIME_GLAZED_TERACOTTA = 85;
    public static short BLOCK_MAGENTA_CONCRETE = 86;
    public static short BLOCK_MAGENTA_GLAZED_TERACOTTA = 87;
    public static short BLOCK_ORANGE_CONCRETE = 88;
    public static short BLOCK_ORANGE_GLAZED_TERACOTTA = 89;
    public static short BLOCK_PINK_CONCRETE = 90;
    public static short BLOCK_PINK_GLAZED_TERACOTTA = 91;
    public static short BLOCK_PURPLE_CONCRETE = 92;
    public static short BLOCK_PURPLE_GLAZED_TERACOTTA = 93;
    public static short BLOCK_RED_CONCRETE = 94;
    public static short BLOCK_RED_GLAZED_TERACOTTA = 95;
    public static short BLOCK_WHITE_CONCRETE = 96;
    public static short BLOCK_WHITE_GLAZED_TERACOTTA = 97;
    public static short BLOCK_BRAIN_CORAL = 98;
    public static short BLOCK_QUARTZ_PILLAR_BLOCK = 100;
    public static short BLOCK_COBBLESTONE = 101;
    public static short BLOCK_WOOL_TURQUOISE = 102;
    public static short BLOCK_WOOL_ORANGE = 103;
    public static short BLOCK_RED_SAND = 104;
    public static short BLOCK_55_HP_ENGINE = 106;
    public static short BLOCK_PHANTOM_STONE = 107;
    public static short BLOCK_PHANTOM_STONE_BRICK = 108;
    public static short BLOCK_CACTUS = 109;
    public static short BLOCK_PALISADE_STONE = 110;
    public static short BLOCK_RED_SANDSTONE = 111;
    public static short BLOCK_FIRE_CORAL_BLOCK = 112;
    public static short BLOCK_CANDLE = 113;
    public static short BLOCK_PALISADE_STONE_2 = 114;
    public static short BLOCK_FIRE_CORAL = 115;
    public static short BLOCK_TALL_GRASS = 116;
    public static short BLOCK_HORN_CORAL_BLOCK = 117;
    public static short BLOCK_GOLD_BLOCK = 118;
    public static short BLOCK_HORN_CORAL_FAN = 119;
    public static short BLOCK_TNT = 120;
    public static short BLOCK_WHEAT = 121;
    public static short BLOCK_CARROTS_PLANT = 122;
    public static short BLOCK_MINI_CACTUS = 123;
    public static short BLOCK_MUSHROOM = 124;
    public static short BLOCK_MUSHROOM_2 = 125;
    public static short BLOCK_ROSES = 126;
    public static short BLOCK_WOOL_PURPLE = 127;
    public static short BLOCK_BIRCH_PLANKS = 128;
    public static short BLOCK_RED_STAINED_WOOD = 129;
    public static short BLOCK_OAK_PLANKS = 130;
    public static short BLOCK_WOOL_RED = 131;
    public static short BLOCK_WOOL_PINK = 132;
    public static short BLOCK_WOOL_YELLOW = 133;
    public static short BLOCK_WOOL_BROWN = 134;
    public static short BLOCK_TUBE_CORAL_BLOCK = 135;
    public static short BLOCK_TUBE_CORAL = 136;
    public static short BLOCK_TUBE_CORAL_FAN = 137;
    public static short BLOCK_WOOL_DEEP_BLUE = 138;
    public static short BLOCK_WOOL_SKY_BLUE = 139;
    public static short BLOCK_WOOL_DARK_GREEN = 140;
    public static short BLOCK_WOOL_GREEN = 141;
    public static short BLOCK_WOOL_GRAY = 142;
    public static short BLOCK_BRAIN_CORAL_BLOCK = 143;
    public static short BLOCK_DIAMOND_BLOCK = 144;
    public static short BLOCK_STEEL_BLOCK = 145;
    public static short BLOCK_POTATOES_PLANT = 146;
    public static short BLOCK_HONEYCOMB_BLOCK_STAIRS = 147;
    public static short BLOCK_OAK_FENCE = 148;
    public static short BLOCK_BIRCH_FENCE = 149;
    public static short BLOCK_BUBBLE_CORAL_BLOCK = 150;
    public static short BLOCK_BUBBLE_CORAL = 151;
    public static short BLOCK_BUBBLE_CORAL_FAN = 152;
    public static short BLOCK_JUNGLE_GRASS = 153;
    public static short BLOCK_LILY_PAD = 154;
    public static short BLOCK_TRACK = 155;
    public static short BLOCK_WOOL_MAGENTA = 156;
    public static short BLOCK_WOOL_BLACK = 157;
    public static short BLOCK_GRANITE_BRICK_STAIRS = 158;
    public static short BLOCK_CEMENT = 159;
    public static short BLOCK_OAK_SAPLING = 160;
    public static short BLOCK_BIRCH_SAPLING = 161;
    public static short BLOCK_WHEAT_SEEDS = 162;
    public static short BLOCK_CARROT_SEEDS = 163;
    public static short BLOCK_METAL_GRATE = 164;
    public static short BLOCK_POTATO_SEEDS = 165;
    public static short BLOCK_A1 = 166;
    public static short BLOCK_A2 = 167;
    public static short BLOCK_B1 = 168;
    public static short BLOCK_B2 = 169;
    public static short BLOCK_B3 = 170;
    public static short BLOCK_B4 = 171;
    public static short BLOCK_B5 = 172;
    public static short BLOCK_B6 = 173;
    public static short BLOCK_RED_PALISADE_SANDSTONE = 175;
    public static short BLOCK_PHANTOM_SANDSTONE = 176;
    public static short BLOCK_PALISADE_SANDSTONE = 177;
    public static short BLOCK_GLOW_ROCK = 178;
    public static short BLOCK_BLACKSTAINED_GLASS = 179;
    public static short BLOCK_BLUESTAINED_GLASS = 180;
    public static short BLOCK_BROWNSTAINED_GLASS = 181;
    public static short BLOCK_CYANSTAINED_GLASS = 182;
    public static short BLOCK_GRAYSTAINED_GLASS = 183;
    public static short BLOCK_GREENSTAINED_GLASS = 184;
    public static short BLOCK_LIGHT_BLUESTAINED_GLASS = 185;
    public static short BLOCK_LIGHT_GRAYSTAINED_GLASS = 186;
    public static short BLOCK_LIMESTAINED_GLASS = 187;
    public static short BLOCK_MAGENTASTAINED_GLASS = 188;
    public static short BLOCK_ORANGESTAINED_GLASS = 189;
    public static short BLOCK_PINKSTAINED_GLASS = 190;
    public static short BLOCK_PURPLESTAINED_GLASS = 191;
    public static short BLOCK_REDSTAINED_GLASS = 192;
    public static short BLOCK_WHITESTAINED_GLASS = 193;
    public static short BLOCK_YELLOWSTAINED_GLASS = 194;
    public static short BLOCK_DARK_OAK_FENCE = 195;
    public static short BLOCK_BIRCH_PLANKS_STAIRS = 196;
    public static short BLOCK_OAK_PLANKS_STAIRS = 197;
    public static short BLOCK_DARK_OAK_PLANKS_STAIRS = 198;
    public static short BLOCK_HONEYCOMB_BLOCK_SLAB = 199;
    public static short BLOCK_JUNGLE_GRASS_PLANT = 200;
    public static short BLOCK_PRISMARINE_BRICK_STAIRS = 201;
    public static short BLOCK_SANDSTONE_STAIRS = 202;
    public static short BLOCK_CAVE_VINES_FLAT = 203;
    public static short BLOCK_POLISHED_DIORITE_STAIRS = 204;
    public static short BLOCK_BIRCH_PLANKS_SLAB = 205;
    public static short BLOCK_OAK_PLANKS_SLAB = 206;
    public static short BLOCK_DARK_OAK_PLANKS_SLAB = 207;
    public static short BLOCK_BAMBOO_WOOD_STAIRS = 208;
    public static short BLOCK_STONE_BRICK_SLAB = 209;
    public static short BLOCK_RED_SANDSTONE_SLAB = 210;
    public static short BLOCK_SANDSTONE_SLAB = 211;
    public static short BLOCK_DRY_GRASS = 212;
    public static short BLOCK_POLISHED_ANDESITE_SLAB = 213;
    public static short BLOCK_IRON_LADDER = 214;
    public static short BLOCK_RED_VINES_FLAT = 215;
    public static short BLOCK_YELLOW_CANDLE = 216;
    public static short BLOCK_CAVE_VINES = 217;
    public static short BLOCK_GREEN_CANDLE = 218;
    public static short BLOCK_GRANITE = 219;
    public static short BLOCK_BLUE_CANDLE = 220;
    public static short BLOCK_RED_VINES = 221;
    public static short BLOCK_FLAT_VINES = 222;
    public static short BLOCK_DARK_OAK_LADDER = 223;
    public static short BLOCK_DRY_GRASS_PLANT = 224;
    public static short BLOCK_ACACIA_LEAVES = 225;
    public static short BLOCK_ACACIA_LOG = 226;
    public static short BLOCK_FIRE_CORAL_FAN = 227;
    public static short BLOCK_LAPIS_LAZUL_BLOCK = 229;
    public static short BLOCK_ACACIA_SAPLING = 230;
    public static short BLOCK_BRAIN_CORAL_FAN = 232;
    public static short BLOCK_WHITE_ROSE = 233;
    public static short BLOCK_HORN_CORAL = 235;
    public static short BLOCK_RED_ROSE = 236;
    public static short BLOCK_EMERALD_BLOCK = 238;
    public static short BLOCK_BLACKEYE_SUSAN = 239;
    public static short BLOCK_ORANGE_TULIP = 240;
    public static short BLOCK_DEAD_BUSH = 241;
    public static short BLOCK_HAY_BAIL = 242;
    public static short BLOCK_POLISHED_ANDESITE_STAIRS = 243;
    public static short BLOCK_POLISHED_DIORITE_SLAB = 244;
    public static short BLOCK_CURVED_TRACK = 245;
    public static short BLOCK_BEETS = 246;
    public static short BLOCK_BEETROOT_SEEDS = 247;
    public static short BLOCK_BAMBOO_LADDER = 248;
    public static short BLOCK_ACACIA_FENCE = 249;
    public static short BLOCK_ACACIA_PLANKS_STAIRS = 250;
    public static short BLOCK_ACACIA_PLANKS_SLAB = 251;
    public static short BLOCK_RAISED_TRACK = 252;
    public static short BLOCK_BLUE_STAINED_WOOD = 254;
    public static short BLOCK_RUBY_CRYSTAL = 255;
    public static short BLOCK_JADE_CRYSTAL = 256;
    public static short BLOCK_AQUAMARINE_CRYSTAL = 257;
    public static short BLOCK_BAMBOO_WOOD_SLAB = 258;
    public static short BLOCK_RED_SANDSTONE_STAIRS = 259;
    public static short BLOCK_STONE_BRICK_STAIRS = 260;
    public static short BLOCK_DRIVERS_SEAT = 261;
    public static short BLOCK_STONE_BRICK_FENCE = 262;
    public static short BLOCK_BRICK_STAIRS = 263;
    public static short BLOCK_BRICK_SLAB = 264;
    public static short BLOCK_SNOW_BLOCK = 265;
    public static short BLOCK_COBBLESTONE_STAIRS = 266;
    public static short BLOCK_COBBLESTONE_SLAB = 267;
    public static short BLOCK_PALISADE_STONE_STAIRS = 268;
    public static short BLOCK_PALISADE_STONE_SLAB = 269;
    public static short BLOCK_PALISADE_STONE_FENCE = 270;
    public static short BLOCK_PALISADE_STONE_2_STAIRS = 271;
    public static short BLOCK_PALISADE_STONE_2_SLAB = 272;
    public static short BLOCK_PALISADE_STONE_2_FENCE = 273;
    public static short BLOCK_POLISHED_DIORITE_FENCE = 274;
    public static short BLOCK_POLISHED_ANDESITE_FENCE = 275;
    public static short BLOCK_CRACKED_STONE_STAIRS = 276;
    public static short BLOCK_CRACKED_STONE_SLAB = 277;
    public static short BLOCK_CRACKED_STONE_FENCE = 278;
    public static short BLOCK_STONE_WITH_VINES_STAIRS = 279;
    public static short BLOCK_STONE_WITH_VINES_SLAB = 280;
    public static short BLOCK_STONE_WITH_VINES_FENCE = 281;
    public static short BLOCK_BURGUNDY_BRICK_STAIRS = 282;
    public static short BLOCK_BURGUNDY_BRICK_SLAB = 283;
    public static short BLOCK_BURGUNDY_BRICK_FENCE = 284;
    public static short BLOCK_SWITCH_JUNCTION = 285;
    public static short BLOCK_TRACK_STOP = 286;
    public static short BLOCK_RED_PALISADE_SANDSTONE_STAIRS = 287;
    public static short BLOCK_RED_PALISADE_SANDSTONE_SLAB = 288;
    public static short BLOCK_RED_PALISADE_SANDSTONE_FENCE = 289;
    public static short BLOCK_PALISADE_SANDSTONE_STAIRS = 290;
    public static short BLOCK_PALISADE_SANDSTONE_SLAB = 291;
    public static short BLOCK_PALISADE_SANDSTONE_FENCE = 292;
    public static short BLOCK_WOOL_STAIRS = 293;
    public static short BLOCK_WOOL_SLAB = 294;
    public static short BLOCK_WOOL_GRAY_STAIRS = 295;
    public static short BLOCK_WOOL_GRAY_SLAB = 296;
    public static short BLOCK_WOOL_RED_STAIRS = 297;
    public static short BLOCK_WOOL_RED_SLAB = 298;
    public static short BLOCK_WOOL_PINK_STAIRS = 299;
    public static short BLOCK_WOOL_PINK_SLAB = 300;
    public static short BLOCK_WOOL_ORANGE_STAIRS = 301;
    public static short BLOCK_WOOL_ORANGE_SLAB = 302;
    public static short BLOCK_WOOL_YELLOW_STAIRS = 303;
    public static short BLOCK_WOOL_YELLOW_SLAB = 304;
    public static short BLOCK_WOOL_GREEN_STAIRS = 305;
    public static short BLOCK_WOOL_GREEN_SLAB = 306;
    public static short BLOCK_WOOL_DARK_GREEN_STAIRS = 307;
    public static short BLOCK_WOOL_DARK_GREEN_SLAB = 308;
    public static short BLOCK_WOOL_TURQUOISE_STAIRS = 309;
    public static short BLOCK_WOOL_TURQUOISE_SLAB = 310;
    public static short BLOCK_WOOL_DEEP_BLUE_STAIRS = 311;
    public static short BLOCK_WOOL_DEEP_BLUE_SLAB = 312;
    public static short BLOCK_WOOL_SKY_BLUE_STAIRS = 313;
    public static short BLOCK_WOOL_SKY_BLUE_SLAB = 314;
    public static short BLOCK_WOOL_BROWN_STAIRS = 315;
    public static short BLOCK_WOOL_BROWN_SLAB = 316;
    public static short BLOCK_WOOL_PURPLE_STAIRS = 317;
    public static short BLOCK_WOOL_PURPLE_SLAB = 318;
    public static short BLOCK_WOOL_MAGENTA_STAIRS = 319;
    public static short BLOCK_WOOL_MAGENTA_SLAB = 320;
    public static short BLOCK_WOOL_BLACK_STAIRS = 321;
    public static short BLOCK_WOOL_BLACK_SLAB = 322;
    public static short BLOCK_YELLOW_CONCRETE_STAIRS = 323;
    public static short BLOCK_YELLOW_CONCRETE_SLAB = 324;
    public static short BLOCK_YELLOW_CONCRETE_FENCE = 325;
    public static short BLOCK_BLACK_CONCRETE_STAIRS = 326;
    public static short BLOCK_BLACK_CONCRETE_SLAB = 327;
    public static short BLOCK_BLACK_CONCRETE_FENCE = 328;
    public static short BLOCK_BLUE_CONCRETE_STAIRS = 329;
    public static short BLOCK_BLUE_CONCRETE_SLAB = 330;
    public static short BLOCK_BLUE_CONCRETE_FENCE = 331;
    public static short BLOCK_BROWN_CONCRETE_STAIRS = 332;
    public static short BLOCK_BROWN_CONCRETE_SLAB = 333;
    public static short BLOCK_BROWN_CONCRETE_FENCE = 334;
    public static short BLOCK_CYAN_CONCRETE_STAIRS = 335;
    public static short BLOCK_CYAN_CONCRETE_SLAB = 336;
    public static short BLOCK_CYAN_CONCRETE_FENCE = 337;
    public static short BLOCK_GRAY_CONCRETE_STAIRS = 338;
    public static short BLOCK_GRAY_CONCRETE_SLAB = 339;
    public static short BLOCK_GRAY_CONCRETE_FENCE = 340;
    public static short BLOCK_GREEN_CONCRETE_STAIRS = 341;
    public static short BLOCK_GREEN_CONCRETE_SLAB = 342;
    public static short BLOCK_GREEN_CONCRETE_FENCE = 343;
    public static short BLOCK_LIGHT_BLUE_CONCRETE_STAIRS = 344;
    public static short BLOCK_LIGHT_BLUE_CONCRETE_SLAB = 345;
    public static short BLOCK_LIGHT_BLUE_CONCRETE_FENCE = 346;
    public static short BLOCK_LIGHT_GRAY_CONCRETE_STAIRS = 347;
    public static short BLOCK_LIGHT_GRAY_CONCRETE_SLAB = 348;
    public static short BLOCK_LIGHT_GRAY_CONCRETE_FENCE = 349;
    public static short BLOCK_LIME_CONCRETE_STAIRS = 350;
    public static short BLOCK_LIME_CONCRETE_SLAB = 351;
    public static short BLOCK_LIME_CONCRETE_FENCE = 352;
    public static short BLOCK_MAGENTA_CONCRETE_STAIRS = 353;
    public static short BLOCK_MAGENTA_CONCRETE_SLAB = 354;
    public static short BLOCK_MAGENTA_CONCRETE_FENCE = 355;
    public static short BLOCK_ORANGE_CONCRETE_STAIRS = 356;
    public static short BLOCK_ORANGE_CONCRETE_SLAB = 357;
    public static short BLOCK_ORANGE_CONCRETE_FENCE = 358;
    public static short BLOCK_PINK_CONCRETE_STAIRS = 359;
    public static short BLOCK_PINK_CONCRETE_SLAB = 360;
    public static short BLOCK_PINK_CONCRETE_FENCE = 361;
    public static short BLOCK_PURPLE_CONCRETE_STAIRS = 362;
    public static short BLOCK_PURPLE_CONCRETE_SLAB = 363;
    public static short BLOCK_PURPLE_CONCRETE_FENCE = 364;
    public static short BLOCK_RED_CONCRETE_STAIRS = 365;
    public static short BLOCK_RED_CONCRETE_SLAB = 366;
    public static short BLOCK_RED_CONCRETE_FENCE = 367;
    public static short BLOCK_WHITE_CONCRETE_STAIRS = 368;
    public static short BLOCK_WHITE_CONCRETE_SLAB = 369;
    public static short BLOCK_WHITE_CONCRETE_FENCE = 370;
    public static short BLOCK_YELLOW_GLAZED_TERACOTTA_STAIRS = 371;
    public static short BLOCK_YELLOW_GLAZED_TERACOTTA_SLAB = 372;
    public static short BLOCK_BLACK_GLAZED_TERACOTTA_STAIRS = 373;
    public static short BLOCK_BLACK_GLAZED_TERACOTTA_SLAB = 374;
    public static short BLOCK_BLUE_GLAZED_TERACOTTA_STAIRS = 375;
    public static short BLOCK_BLUE_GLAZED_TERACOTTA_SLAB = 376;
    public static short BLOCK_BROWN_GLAZED_TERACOTTA_STAIRS = 377;
    public static short BLOCK_BROWN_GLAZED_TERACOTTA_SLAB = 378;
    public static short BLOCK_CYAN_GLAZED_TERACOTTA_STAIRS = 379;
    public static short BLOCK_CYAN_GLAZED_TERACOTTA_SLAB = 380;
    public static short BLOCK_GRAY_GLAZED_TERACOTTA_STAIRS = 381;
    public static short BLOCK_GRAY_GLAZED_TERACOTTA_SLAB = 382;
    public static short BLOCK_GREEN_GLAZED_TERACOTTA_STAIRS = 383;
    public static short BLOCK_GREEN_GLAZED_TERACOTTA_SLAB = 384;
    public static short BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_STAIRS = 385;
    public static short BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_SLAB = 386;
    public static short BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA_STAIRS = 387;
    public static short BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA_SLAB = 388;
    public static short BLOCK_LIME_GLAZED_TERACOTTA_STAIRS = 389;
    public static short BLOCK_LIME_GLAZED_TERACOTTA_SLAB = 390;
    public static short BLOCK_MAGENTA_GLAZED_TERACOTTA_STAIRS = 391;
    public static short BLOCK_MAGENTA_GLAZED_TERACOTTA_SLAB = 392;
    public static short BLOCK_ORANGE_GLAZED_TERACOTTA_STAIRS = 393;
    public static short BLOCK_ORANGE_GLAZED_TERACOTTA_SLAB = 394;
    public static short BLOCK_PINK_GLAZED_TERACOTTA_STAIRS = 395;
    public static short BLOCK_PINK_GLAZED_TERACOTTA_SLAB = 396;
    public static short BLOCK_PURPLE_GLAZED_TERACOTTA_STAIRS = 397;
    public static short BLOCK_PURPLE_GLAZED_TERACOTTA_SLAB = 398;
    public static short BLOCK_RED_GLAZED_TERACOTTA_STAIRS = 399;
    public static short BLOCK_RED_GLAZED_TERACOTTA_SLAB = 400;
    public static short BLOCK_WHITE_GLAZED_TERACOTTA_STAIRS = 401;
    public static short BLOCK_WHITE_GLAZED_TERACOTTA_SLAB = 402;
    public static short BLOCK_OAK_LADDER = 403;
    public static short BLOCK_MINECART_ROAD_BLOCK = 404;
    public static short BLOCK_STONE = 405;
    public static short BLOCK_BAMBOO_WOOD_FENCE = 406;
    public static short BLOCK_CROISSANT = 407;
    public static short BLOCK_MINECART_ROAD_SLAB = 408;
    public static short BLOCK_PRISMARINE_BRICKS = 409;
    public static short BLOCK_DARK_PRISMARINE_BRICKS = 410;
    public static short BLOCK_GLASS_STAIRS = 411;
    public static short BLOCK_GLASS_SLAB = 412;
    public static short BLOCK_BLUE_TORCH = 413;
    public static short BLOCK_CEMENT_STAIRS = 414;
    public static short BLOCK_CEMENT_FENCE = 415;
    public static short BLOCK_CEMENT_SLAB = 416;
    public static short BLOCK_OBSIDIAN_SLAB = 417;
    public static short BLOCK_OBSIDIAN_FENCE = 418;
    public static short BLOCK_LAPIS_LAZUL_STAIRS = 419;
    public static short BLOCK_LAPIS_LAZUL_BLOCK_SLAB = 420;
    public static short BLOCK_LAPIS_LAZUL_FENCE = 421;
    public static short BLOCK_STEEL_STAIRS = 422;
    public static short BLOCK_STEEL_BLOCK_SLAB = 423;
    public static short BLOCK_STEEL_FENCE = 424;
    public static short BLOCK_GOLD_STAIRS = 425;
    public static short BLOCK_GOLD_BLOCK_SLAB = 426;
    public static short BLOCK_GOLD_FENCE = 427;
    public static short BLOCK_EMERALD_STAIRS = 428;
    public static short BLOCK_EMERALD_BLOCK_SLAB = 429;
    public static short BLOCK_EMERALD_FENCE = 430;
    public static short BLOCK_DIAMOND_STAIRS = 431;
    public static short BLOCK_DIAMOND_BLOCK_SLAB = 432;
    public static short BLOCK_DIAMOND_FENCE = 433;
    public static short BLOCK_CROSSWALK_PAINT = 434;
    public static short BLOCK_DARK_PRISMARINE_BRICK_STAIRS = 435;
    public static short BLOCK_PRISMARINE_BRICK_SLAB = 436;
    public static short BLOCK_OBSIDIAN_STAIRS = 437;
    public static short BLOCK_PRISMARINE_BRICK_FENCE = 438;
    public static short BLOCK_RED_SANDSTONE_PILLAR = 439;
    public static short BLOCK_STONE_BRICK_PILLAR = 440;
    public static short BLOCK_PALISADE_SANDSTONE_PILLAR = 441;
    public static short BLOCK_PALISADE_STONE_PILLAR = 442;
    public static short BLOCK_PALISADE_STONE_2_PILLAR = 443;
    public static short BLOCK_CRACKED_STONE_PILLAR = 444;
    public static short BLOCK_STONE_WITH_VINES_PILLAR = 445;
    public static short BLOCK_FLAT_DRAGON_VINES = 446;
    public static short BLOCK_DRAGON_VINES = 447;
    public static short BLOCK_RED_PALISADE_SANDSTONE_PILLAR = 448;
    public static short BLOCK_MARBLE_PILLAR_BLOCK = 449;
    public static short BLOCK_QUARTZ_PILLAR = 450;
    public static short BLOCK_MARBLE_PILLAR = 451;
    public static short BLOCK_FARMLAND = 452;
    public static short BLOCK_ROAD_MARKINGS = 453;
    public static short BLOCK_GRANITE_BRICK_PILLAR = 454;
    public static short BLOCK_GRANITE_BRICK_SLAB = 455;
    public static short BLOCK_GRANITE_BRICK_FENCE = 456;
    public static short BLOCK_CAMPFIRE = 457;
    public static short BLOCK_DARK_PRISMARINE_BRICK_SLAB = 458;
    public static short BLOCK_DARK_PRISMARINE_BRICK_FENCE = 459;
    public static short BLOCK_ENGRAVED_SANDSTONE = 460;
    public static short BLOCK_ENGRAVED_RED_SANDSTONE = 461;
    public static short BLOCK_ORANGE_MARBLE_TILE = 462;
    public static short BLOCK_CHECKERBOARD_CHISELED_MARBLE = 463;
    public static short BLOCK_CHISELED_MARBLE = 464;
    public static short BLOCK_CHISELED_QUARTZ = 465;
    public static short BLOCK_MARBLE_TILE = 466;
    public static short BLOCK_BLUE_MARBLE_TILE = 467;
    public static short BLOCK_GREEN_MARBLE_TILE = 468;
    public static short BLOCK_ORANGE_MARBLE_TILE_SLAB = 469;
    public static short BLOCK_GRAY_MARBLE_TILE = 470;
    public static short BLOCK_ORANGE_MARBLE_TILE_STAIRS = 471;
    public static short BLOCK_GREEN_MARBLE_TILE_SLAB = 472;
    public static short BLOCK_YELLOWSTAINED_GLASS_STAIRS = 473;
    public static short BLOCK_YELLOWSTAINED_GLASS_SLAB = 474;
    public static short BLOCK_BLACKSTAINED_GLASS_STAIRS = 475;
    public static short BLOCK_BLACKSTAINED_GLASS_SLAB = 476;
    public static short BLOCK_BLUESTAINED_GLASS_STAIRS = 477;
    public static short BLOCK_BLUESTAINED_GLASS_SLAB = 478;
    public static short BLOCK_BROWNSTAINED_GLASS_STAIRS = 479;
    public static short BLOCK_BROWNSTAINED_GLASS_SLAB = 480;
    public static short BLOCK_CYANSTAINED_GLASS_STAIRS = 481;
    public static short BLOCK_CYANSTAINED_GLASS_SLAB = 482;
    public static short BLOCK_GRAYSTAINED_GLASS_STAIRS = 483;
    public static short BLOCK_GRAYSTAINED_GLASS_SLAB = 484;
    public static short BLOCK_GREENSTAINED_GLASS_STAIRS = 485;
    public static short BLOCK_GREENSTAINED_GLASS_SLAB = 486;
    public static short BLOCK_LIGHT_BLUESTAINED_GLASS_STAIRS = 487;
    public static short BLOCK_LIGHT_BLUESTAINED_GLASS_SLAB = 488;
    public static short BLOCK_LIGHT_GRAYSTAINED_GLASS_STAIRS = 489;
    public static short BLOCK_LIGHT_GRAYSTAINED_GLASS_SLAB = 490;
    public static short BLOCK_LIMESTAINED_GLASS_STAIRS = 491;
    public static short BLOCK_LIMESTAINED_GLASS_SLAB = 492;
    public static short BLOCK_MAGENTASTAINED_GLASS_STAIRS = 493;
    public static short BLOCK_MAGENTASTAINED_GLASS_SLAB = 494;
    public static short BLOCK_ORANGESTAINED_GLASS_STAIRS = 495;
    public static short BLOCK_ORANGESTAINED_GLASS_SLAB = 496;
    public static short BLOCK_PINKSTAINED_GLASS_STAIRS = 497;
    public static short BLOCK_PINKSTAINED_GLASS_SLAB = 498;
    public static short BLOCK_PURPLESTAINED_GLASS_STAIRS = 499;
    public static short BLOCK_PURPLESTAINED_GLASS_SLAB = 500;
    public static short BLOCK_REDSTAINED_GLASS_STAIRS = 501;
    public static short BLOCK_REDSTAINED_GLASS_SLAB = 502;
    public static short BLOCK_WHITESTAINED_GLASS_STAIRS = 503;
    public static short BLOCK_WHITESTAINED_GLASS_SLAB = 504;
    public static short BLOCK_CHECKERBOARD_CHISELED_MARBLE_STAIRS = 505;
    public static short BLOCK_CHECKERBOARD_CHISELED_MARBLE_SLAB = 506;
    public static short BLOCK_CHISELED_MARBLE_STAIRS = 507;
    public static short BLOCK_CHISELED_MARBLE_SLAB = 508;
    public static short BLOCK_MARBLE_TILE_STAIRS = 509;
    public static short BLOCK_MARBLE_TILE_SLAB = 510;
    public static short BLOCK_GRAY_MARBLE_TILE_STAIRS = 511;
    public static short BLOCK_GRAY_MARBLE_TILE_SLAB = 512;
    public static short BLOCK_BLUE_MARBLE_TILE_STAIRS = 513;
    public static short BLOCK_BLUE_MARBLE_TILE_SLAB = 514;
    public static short BLOCK_GREEN_MARBLE_TILE_STAIRS = 515;
    public static short BLOCK_CHISELED_QUARTZ_STAIRS = 516;
    public static short BLOCK_CHISELED_QUARTZ_SLAB = 517;
    public static short BLOCK_CHISELED_QUARTZ_PILLAR = 518;
    public static short BLOCK_GRAY_MARBLE_TILE_PILLAR = 519;
    public static short BLOCK_BLUE_MARBLE_TILE_PILLAR = 520;
    public static short BLOCK_GREEN_MARBLE_TILE_PILLAR = 521;
    public static short BLOCK_ORANGE_MARBLE_TILE_PILLAR = 522;
    public static short BLOCK_MARBLE_TILE_PILLAR = 523;
    public static short BLOCK_LAMP = 524;
    public static short BLOCK_BLUE_LAMP = 525;
    public static short BLOCK_GLASS_PANE = 526;
    public static short BLOCK_YELLOWSTAINED_GLASS_PANE = 527;
    public static short BLOCK_BLACKSTAINED_GLASS_PANE = 528;
    public static short BLOCK_BLUESTAINED_GLASS_PANE = 529;
    public static short BLOCK_BROWNSTAINED_GLASS_PANE = 530;
    public static short BLOCK_CYANSTAINED_GLASS_PANE = 531;
    public static short BLOCK_GRAYSTAINED_GLASS_PANE = 532;
    public static short BLOCK_GREENSTAINED_GLASS_PANE = 533;
    public static short BLOCK_LIGHT_BLUESTAINED_GLASS_PANE = 534;
    public static short BLOCK_LIGHT_GRAYSTAINED_GLASS_PANE = 535;
    public static short BLOCK_LIMESTAINED_GLASS_PANE = 536;
    public static short BLOCK_MAGENTASTAINED_GLASS_PANE = 537;
    public static short BLOCK_ORANGESTAINED_GLASS_PANE = 538;
    public static short BLOCK_PINKSTAINED_GLASS_PANE = 539;
    public static short BLOCK_PURPLESTAINED_GLASS_PANE = 540;
    public static short BLOCK_REDSTAINED_GLASS_PANE = 541;
    public static short BLOCK_WHITESTAINED_GLASS_PANE = 542;
    public static short BLOCK_START_BOUNDARY = 543;
    public static short BLOCK_YELLOW_CHISELED_MARBLE_TILE = 545;
    public static short BLOCK_SUNFLOWER = 546;
    public static short BLOCK_SUNFLOWER_STALK = 547;
    public static short BLOCK_MEGA_TNT = 548;
    public static short BLOCK_SUNFLOWER_SEEDS = 549;
    public static short BLOCK_CROSSTRACK = 550;
    public static short BLOCK_BLACK_CHISELED_MARBLE_TILE = 553;
    public static short BLOCK_BLUE_CHISELED_MARBLE_TILE = 554;
    public static short BLOCK_BROWN_CHISELED_MARBLE_TILE = 555;
    public static short BLOCK_CYAN_CHISELED_MARBLE_TILE = 556;
    public static short BLOCK_GRAY_CHISELED_MARBLE_TILE = 557;
    public static short BLOCK_GREEN_CHISELED_MARBLE_TILE = 558;
    public static short BLOCK_PASTELBLUE_CHISELED_MARBLE_TILE = 559;
    public static short BLOCK_PASTELGREEN_CHISELED_MARBLE_TILE = 560;
    public static short BLOCK_MAGENTA_CHISELED_MARBLE_TILE = 561;
    public static short BLOCK_ORANGE_CHISELED_MARBLE_TILE = 562;
    public static short BLOCK_PINK_CHISELED_MARBLE_TILE = 563;
    public static short BLOCK_PURPLE_CHISELED_MARBLE_TILE = 564;
    public static short BLOCK_BURGUNDY_CHISELED_MARBLE_TILE = 565;
    public static short BLOCK_BAMBOO_BLOCK = 566;
    public static short BLOCK_PASTELRED_CHISELED_MARBLE_TILE = 567;
    public static short BLOCK_YELLOW_MARBLE_TILE = 568;
    public static short BLOCK_BLACK_MARBLE_TILE = 569;
    public static short BLOCK_ICE_BLOCK = 570;
    public static short BLOCK_BROWN_MARBLE_TILE = 571;
    public static short BLOCK_CYAN_MARBLE_TILE = 572;
    public static short BLOCK_BAMBOO_WOOD = 573;
    public static short BLOCK_BOTTLE = 574;
    public static short BLOCK_PASTELBLUE_MARBLE_TILE = 575;
    public static short BLOCK_PASTELGREEN_MARBLE_TILE = 576;
    public static short BLOCK_MAGENTA_MARBLE_TILE = 577;
    public static short BLOCK_CUP = 578;
    public static short BLOCK_PINK_MARBLE_TILE = 579;
    public static short BLOCK_PURPLE_MARBLE_TILE = 580;
    public static short BLOCK_BURGUNDY_MARBLE_TILE = 581;
    public static short BLOCK_PASTELRED_MARBLE_TILE = 582;
    public static short BLOCK_WINE_GLASS = 583;
    public static short BLOCK_YELLOW_STAINED_WOOD = 584;
    public static short BLOCK_BLACK_STAINED_WOOD = 585;
    public static short BLOCK_YELLOW_MARBLE_TILE_STAIRS = 586;
    public static short BLOCK_YELLOW_MARBLE_TILE_SLAB = 587;
    public static short BLOCK_YELLOW_MARBLE_TILE_PILLAR = 588;
    public static short BLOCK_BLACK_MARBLE_TILE_STAIRS = 589;
    public static short BLOCK_BLACK_MARBLE_TILE_SLAB = 590;
    public static short BLOCK_BLACK_MARBLE_TILE_PILLAR = 591;
    public static short BLOCK_BREAD = 592;
    public static short BLOCK_CYAN_STAINED_WOOD = 593;
    public static short BLOCK_PINK_CHISELED_MARBLE_TILE_PILLAR = 594;
    public static short BLOCK_BROWN_MARBLE_TILE_STAIRS = 595;
    public static short BLOCK_BROWN_MARBLE_TILE_SLAB = 596;
    public static short BLOCK_BROWN_MARBLE_TILE_PILLAR = 597;
    public static short BLOCK_CYAN_MARBLE_TILE_STAIRS = 598;
    public static short BLOCK_CYAN_MARBLE_TILE_SLAB = 599;
    public static short BLOCK_CYAN_MARBLE_TILE_PILLAR = 600;
    public static short BLOCK_PURPLE_CHISELED_MARBLE_TILE_STAIRS = 601;
    public static short BLOCK_PURPLE_CHISELED_MARBLE_TILE_SLAB = 602;
    public static short BLOCK_PURPLE_CHISELED_MARBLE_TILE_PILLAR = 603;
    public static short BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_STAIRS = 604;
    public static short BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_SLAB = 605;
    public static short BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_PILLAR = 606;
    public static short BLOCK_PASTELBLUE_MARBLE_TILE_STAIRS = 607;
    public static short BLOCK_PASTELBLUE_MARBLE_TILE_SLAB = 608;
    public static short BLOCK_PASTELBLUE_MARBLE_TILE_PILLAR = 609;
    public static short BLOCK_PASTELGREEN_MARBLE_TILE_STAIRS = 610;
    public static short BLOCK_PASTELGREEN_MARBLE_TILE_SLAB = 611;
    public static short BLOCK_PASTELGREEN_MARBLE_TILE_PILLAR = 612;
    public static short BLOCK_MAGENTA_MARBLE_TILE_STAIRS = 613;
    public static short BLOCK_MAGENTA_MARBLE_TILE_SLAB = 614;
    public static short BLOCK_MAGENTA_MARBLE_TILE_PILLAR = 615;
    public static short BLOCK_PASTELRED_CHISELED_MARBLE_TILE_STAIRS = 616;
    public static short BLOCK_PASTELRED_CHISELED_MARBLE_TILE_SLAB = 617;
    public static short BLOCK_PASTELRED_CHISELED_MARBLE_TILE_PILLAR = 618;
    public static short BLOCK_PINK_MARBLE_TILE_STAIRS = 619;
    public static short BLOCK_PINK_MARBLE_TILE_SLAB = 620;
    public static short BLOCK_PINK_MARBLE_TILE_PILLAR = 621;
    public static short BLOCK_PURPLE_MARBLE_TILE_STAIRS = 622;
    public static short BLOCK_PURPLE_MARBLE_TILE_SLAB = 623;
    public static short BLOCK_PURPLE_MARBLE_TILE_PILLAR = 624;
    public static short BLOCK_BURGUNDY_MARBLE_TILE_STAIRS = 625;
    public static short BLOCK_BURGUNDY_MARBLE_TILE_SLAB = 626;
    public static short BLOCK_BURGUNDY_MARBLE_TILE_PILLAR = 627;
    public static short BLOCK_PASTELRED_MARBLE_TILE_STAIRS = 628;
    public static short BLOCK_PASTELRED_MARBLE_TILE_SLAB = 629;
    public static short BLOCK_PASTELRED_MARBLE_TILE_PILLAR = 630;
    public static short BLOCK_YELLOW_CHISELED_MARBLE_TILE_STAIRS = 631;
    public static short BLOCK_YELLOW_CHISELED_MARBLE_TILE_SLAB = 632;
    public static short BLOCK_YELLOW_CHISELED_MARBLE_TILE_PILLAR = 633;
    public static short BLOCK_BLACK_CHISELED_MARBLE_TILE_STAIRS = 634;
    public static short BLOCK_BLACK_CHISELED_MARBLE_TILE_SLAB = 635;
    public static short BLOCK_BLACK_CHISELED_MARBLE_TILE_PILLAR = 636;
    public static short BLOCK_BLUE_CHISELED_MARBLE_TILE_STAIRS = 637;
    public static short BLOCK_BLUE_CHISELED_MARBLE_TILE_SLAB = 638;
    public static short BLOCK_BLUE_CHISELED_MARBLE_TILE_PILLAR = 639;
    public static short BLOCK_BROWN_CHISELED_MARBLE_TILE_STAIRS = 640;
    public static short BLOCK_BROWN_CHISELED_MARBLE_TILE_SLAB = 641;
    public static short BLOCK_BROWN_CHISELED_MARBLE_TILE_PILLAR = 642;
    public static short BLOCK_CYAN_CHISELED_MARBLE_TILE_STAIRS = 643;
    public static short BLOCK_CYAN_CHISELED_MARBLE_TILE_SLAB = 644;
    public static short BLOCK_CYAN_CHISELED_MARBLE_TILE_PILLAR = 645;
    public static short BLOCK_GRAY_CHISELED_MARBLE_TILE_STAIRS = 646;
    public static short BLOCK_GRAY_CHISELED_MARBLE_TILE_SLAB = 647;
    public static short BLOCK_GRAY_CHISELED_MARBLE_TILE_PILLAR = 648;
    public static short BLOCK_GREEN_CHISELED_MARBLE_TILE_STAIRS = 649;
    public static short BLOCK_GREEN_CHISELED_MARBLE_TILE_SLAB = 650;
    public static short BLOCK_GREEN_CHISELED_MARBLE_TILE_PILLAR = 651;
    public static short BLOCK_PASTELBLUE_CHISELED_MARBLE_TILE_STAIRS = 652;
    public static short BLOCK_PASTELBLUE_CHISELED_MARBLE_TILE_SLAB = 653;
    public static short BLOCK_PASTELBLUE_CHISELED_MARBLE_TILE_PILLAR = 654;
    public static short BLOCK_PASTELGREEN_CHISELED_MARBLE_TILE_STAIRS = 655;
    public static short BLOCK_PASTELGREEN_CHISELED_MARBLE_TILE_SLAB = 656;
    public static short BLOCK_PASTELGREEN_CHISELED_MARBLE_TILE_PILLAR = 657;
    public static short BLOCK_MAGENTA_CHISELED_MARBLE_TILE_STAIRS = 658;
    public static short BLOCK_MAGENTA_CHISELED_MARBLE_TILE_SLAB = 659;
    public static short BLOCK_MAGENTA_CHISELED_MARBLE_TILE_PILLAR = 660;
    public static short BLOCK_ORANGE_CHISELED_MARBLE_TILE_STAIRS = 661;
    public static short BLOCK_ORANGE_CHISELED_MARBLE_TILE_SLAB = 662;
    public static short BLOCK_ORANGE_CHISELED_MARBLE_TILE_PILLAR = 663;
    public static short BLOCK_PINK_CHISELED_MARBLE_TILE_STAIRS = 664;
    public static short BLOCK_PINK_CHISELED_MARBLE_TILE_SLAB = 665;
    public static short BLOCK_GRAY_STAINED_WOOD = 666;
    public static short BLOCK_GREEN_STAINED_WOOD = 667;
    public static short BLOCK_LIGHT_BLUE_STAINED_WOOD = 668;
    public static short BLOCK_LIME_STAINED_WOOD = 669;
    public static short BLOCK_MAGENTA_STAINED_WOOD = 670;
    public static short BLOCK_ORANGE_STAINED_WOOD = 671;
    public static short BLOCK_PINK_STAINED_WOOD = 672;
    public static short BLOCK_PURPLE_STAINED_WOOD = 673;
    public static short BLOCK_WHITE_SPACE_TILE_STAIRS = 674;
    public static short BLOCK_WHITE_STAINED_WOOD = 675;
    public static short BLOCK_ENGRAVED_SANDSTONE_2 = 676;
    public static short BLOCK_ENGRAVED_RED_SANDSTONE_2 = 677;
    public static short BLOCK_PEONY_BUSH = 678;
    public static short BLOCK_ICE_BLOCK_STAIRS = 679;
    public static short BLOCK_ICE_BLOCK_SLAB = 680;
    public static short BLOCK_SOLAR_PANEL = 681;
    public static short BLOCK_MOSAIC_BAMBOO_WOOD_STAIRS = 682;
    public static short BLOCK_MOSAIC_BAMBOO_WOOD_SLAB = 683;
    public static short BLOCK_MOSAIC_BAMBOO_WOOD_FENCE = 684;
    public static short BLOCK_YELLOW_STAINED_WOOD_STAIRS = 685;
    public static short BLOCK_YELLOW_STAINED_WOOD_SLAB = 686;
    public static short BLOCK_YELLOW_STAINED_WOOD_FENCE = 687;
    public static short BLOCK_BLACK_STAINED_WOOD_STAIRS = 688;
    public static short BLOCK_BLACK_STAINED_WOOD_SLAB = 689;
    public static short BLOCK_BLACK_STAINED_WOOD_FENCE = 690;
    public static short BLOCK_BLUE_STAINED_WOOD_STAIRS = 691;
    public static short BLOCK_BLUE_STAINED_WOOD_SLAB = 692;
    public static short BLOCK_BLUE_STAINED_WOOD_FENCE = 693;
    public static short BLOCK_CYAN_STAINED_WOOD_STAIRS = 694;
    public static short BLOCK_CYAN_STAINED_WOOD_SLAB = 695;
    public static short BLOCK_CYAN_STAINED_WOOD_FENCE = 696;
    public static short BLOCK_GRAY_STAINED_WOOD_STAIRS = 697;
    public static short BLOCK_GRAY_STAINED_WOOD_SLAB = 698;
    public static short BLOCK_GRAY_STAINED_WOOD_FENCE = 699;
    public static short BLOCK_GREEN_STAINED_WOOD_STAIRS = 700;
    public static short BLOCK_GREEN_STAINED_WOOD_SLAB = 701;
    public static short BLOCK_GREEN_STAINED_WOOD_FENCE = 702;
    public static short BLOCK_LIGHT_BLUE_STAINED_WOOD_STAIRS = 703;
    public static short BLOCK_LIGHT_BLUE_STAINED_WOOD_SLAB = 704;
    public static short BLOCK_LIGHT_BLUE_STAINED_WOOD_FENCE = 705;
    public static short BLOCK_LIME_STAINED_WOOD_STAIRS = 706;
    public static short BLOCK_LIME_STAINED_WOOD_SLAB = 707;
    public static short BLOCK_LIME_STAINED_WOOD_FENCE = 708;
    public static short BLOCK_MAGENTA_STAINED_WOOD_STAIRS = 709;
    public static short BLOCK_MAGENTA_STAINED_WOOD_SLAB = 710;
    public static short BLOCK_MAGENTA_STAINED_WOOD_FENCE = 711;
    public static short BLOCK_ORANGE_STAINED_WOOD_STAIRS = 712;
    public static short BLOCK_ORANGE_STAINED_WOOD_SLAB = 713;
    public static short BLOCK_ORANGE_STAINED_WOOD_FENCE = 714;
    public static short BLOCK_PINK_STAINED_WOOD_STAIRS = 715;
    public static short BLOCK_PINK_STAINED_WOOD_SLAB = 716;
    public static short BLOCK_PINK_STAINED_WOOD_FENCE = 717;
    public static short BLOCK_PURPLE_STAINED_WOOD_STAIRS = 718;
    public static short BLOCK_PURPLE_STAINED_WOOD_SLAB = 719;
    public static short BLOCK_PURPLE_STAINED_WOOD_FENCE = 720;
    public static short BLOCK_RED_STAINED_WOOD_STAIRS = 721;
    public static short BLOCK_RED_STAINED_WOOD_SLAB = 722;
    public static short BLOCK_RED_STAINED_WOOD_FENCE = 723;
    public static short BLOCK_WHITE_STAINED_WOOD_STAIRS = 724;
    public static short BLOCK_WHITE_STAINED_WOOD_SLAB = 725;
    public static short BLOCK_WHITE_STAINED_WOOD_FENCE = 726;
    public static short BLOCK_WHITE_SPACE_TILE = 727;
    public static short BLOCK_GRAY_SPACE_TILE = 728;
    public static short BLOCK_WHITE_SPACE_TILE_SLAB = 729;
    public static short BLOCK_GRAY_SPACE_TILE_STAIRS = 730;
    public static short BLOCK_GRAY_SPACE_TILE_SLAB = 731;
    public static short BLOCK_SPRUCE_LOG = 732;
    public static short BLOCK_SPRUCE_LEAVES = 733;
    public static short BLOCK_SPRUCE_SAPLING = 734;
    public static short BLOCK_MERGE_TRACK = 736;
    public static short BLOCK_RED_WIRE = 737;
    public static short BLOCK_GREEN_WIRE = 738;
    public static short BLOCK_BLUE_WIRE = 739;
    public static short BLOCK_GRAY_WIRE = 740;
    public static short BLOCK_20_HP_ENGINE = 741;
    public static short BLOCK_70_HP_ENGINE = 744;
    public static short BLOCK_SILVER_BRICK = 747;
    public static short BLOCK_LARGE_HELICOPTER_BLADE = 749;
    public static short BLOCK_SMALL_HELICOPTER_BLADE = 750;
    public static short BLOCK_YELLOW_CARPET = 752;
    public static short BLOCK_BLACK_CARPET = 753;
    public static short BLOCK_BLUE_CARPET = 754;
    public static short BLOCK_BROWN_CARPET = 755;
    public static short BLOCK_CYAN_CARPET = 756;
    public static short BLOCK_GRAY_CARPET = 757;
    public static short BLOCK_GREEN_CARPET = 758;
    public static short BLOCK_LIGHT_BLUE_CARPET = 759;
    public static short BLOCK_LIGHT_GRAY_CARPET = 760;
    public static short BLOCK_LIME_CARPET = 761;
    public static short BLOCK_MAGENTA_CARPET = 762;
    public static short BLOCK_ORANGE_CARPET = 763;
    public static short BLOCK_PINK_CARPET = 764;
    public static short BLOCK_PURPLE_CARPET = 765;
    public static short BLOCK_RED_CARPET = 766;
    public static short BLOCK_WHITE_CARPET = 767;
    public static short BLOCK_GOLD_SEAT = 768;
    public static short BLOCK_GOLD_SIDING = 769;
    public static short BLOCK_TAN_SEAT = 770;
    public static short BLOCK_TAN_SIDING = 771;
    public static short BLOCK_BROWN_SEAT = 772;
    public static short BLOCK_BROWN_SIDING = 773;
    public static short BLOCK_CYAN_SEAT = 774;
    public static short BLOCK_CYAN_SIDING = 775;
    public static short BLOCK_GRAY_SEAT = 776;
    public static short BLOCK_GRAY_SIDING = 777;
    public static short BLOCK_GREEN_SEAT = 778;
    public static short BLOCK_GREEN_SIDING = 779;
    public static short BLOCK_BLUE_SEAT = 780;
    public static short BLOCK_BLUE_SIDING = 781;
    public static short BLOCK_LIGHT_GRAY_SEAT = 782;
    public static short BLOCK_LIGHT_GRAY_SIDING = 783;
    public static short BLOCK_LIGHT_GREEN_SEAT = 784;
    public static short BLOCK_LIGHT_GREEN_SIDING = 785;
    public static short BLOCK_MAGENTA_SEAT = 786;
    public static short BLOCK_MAGENTA_SIDING = 787;
    public static short BLOCK_ORANGE_SEAT = 788;
    public static short BLOCK_ORANGE_SIDING = 789;
    public static short BLOCK_PINK_SEAT = 790;
    public static short BLOCK_PINK_SIDING = 791;
    public static short BLOCK_PURPLE_SEAT = 792;
    public static short BLOCK_PURPLE_SIDING = 793;
    public static short BLOCK_RED_SEAT = 794;
    public static short BLOCK_RED_SIDING = 795;
    public static short BLOCK_WHITE_SEAT = 796;
    public static short BLOCK_WHITE_SIDING = 797;
    public static short BLOCK_YELLOW_SEAT = 798;
    public static short BLOCK_YELLOW_SIDING = 799;
    // public static final Block BLOCK_SEA_GRASS = new Block(253, "Sea Grass", new BlockTexture("sea grass.png"), RenderType.SPRITE);
    


    /*
     public static final Block BLOCK_BEDROCK = new Block(1, "Bedrock", new BlockTexture("bedrock.png", "bedrock.png", "bedrock.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BIRCH_LOG = new Block(2, "Birch Log", new BlockTexture("birch log.png", "birch log.png", "birch log front.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BIRCH_LEAVES = new Block(3, "Birch Leaves", new BlockTexture("birch leaves.png", "birch leaves.png", "birch leaves.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BRICK = new Block(4, "Brick", new BlockTexture("brick.png", "brick.png", "brick.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_VINES = new Block(5, "Vines", new BlockTexture("vines.png", "vines.png", "vines.png"), RenderType.SPRITE);
    public static final Block BLOCK_DIRT = new Block(6, "Dirt", new BlockTexture("dirt.png", "dirt.png", "dirt.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PANSIES = new Block(7, "Pansies", new BlockTexture("pansies.png", "pansies.png", "pansies.png"), RenderType.SPRITE);
    public static final Block BLOCK_GRASS = new Block(9, "Grass", new BlockTexture("grass.png", "dirt.png", "grass front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GRAVEL = new Block(10, "Gravel", new BlockTexture("gravel.png", "gravel.png", "gravel.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_OAK_LOG = new Block(11, "Oak Log", new BlockTexture("oak log.png", "oak log.png", "oak log front.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_OAK_LEAVES = new Block(12, "Oak Leaves", new BlockTexture("oak leaves.png", "oak leaves.png", "oak leaves.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SEA_LIGHT = new Block(13, "Sea Light", new BlockTexture("sea light.png", "sea light.png", "sea light.png"), (b) -> {
        b.opaque = false;
        b.solid = true;
        b.torchlightStartingValue = 15;
    });
    public static final Block BLOCK_PLANT_GRASS = new Block(14, "Plant Grass", new BlockTexture("plant grass.png", "plant grass.png", "plant grass.png"), RenderType.SPRITE);
    public static final Block BLOCK_BAMBOO = new Block(15, "Bamboo", new BlockTexture("bamboo.png", "bamboo.png", "bamboo.png"), RenderType.SPRITE);
    public static final Block BLOCK_SAND = new Block(16, "Sand", new BlockTexture("sand.png", "sand.png", "sand.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SANDSTONE = new Block(17, "Sandstone", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ANDESITE = new Block(18, "Andesite", new BlockTexture("andesite.png", "andesite.png", "andesite.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_STONE_BRICK = new Block(19, "Stone Brick", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TORCH = new Block(20, "Torch", new BlockTexture("torch.png"), (b) -> {
        b.opaque = false;
        b.solid = false;
        b.type = RenderType.TORCH;
        b.torchlightStartingValue = 15;
        b.setIcon("torch.png");
    });
    public static final Block BLOCK_WATER = new Block(21, "Water", new BlockTexture("water.png", "water.png", "water.png"), (b) -> {
        b.opaque = false;
        b.solid = false;
        b.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
    });
    public static final Block BLOCK_WOOL = new Block(22, "Wool", new BlockTexture("wool.png", "wool.png", "wool.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SNOW = new Block(23, "Snow", new BlockTexture("snow.png", "dirt.png", "snow front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BOOKSHELF = new Block(24, "Bookshelf", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LAVA = new Block(25, "Lava",
            new BlockTexture("lava.png", "lava.png", "lava.png"), (b) -> {
        b.opaque = false;
        b.solid = false;
        b.torchlightStartingValue = 5;
        b.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        System.out.println("Lava animation length: " + b.texture.getNEG_X().animationLength);
        b.texture.getNEG_Y().animationLength = 1;
    });

    //Plants:
    public static final Block BLOCK_A1 = new Plant(166, "A1 hidden", new BlockTexture("a1.png", "a1.png", "a1.png"));
    public static final Block BLOCK_A2 = new Plant(167, "A2 hidden", new BlockTexture("a2.png", "a2.png", "a2.png"));
    public static final Block BLOCK_B1 = new Plant(168, "B1 hidden", new BlockTexture("b1.png", "b1.png", "b1.png"));
    public static final Block BLOCK_B2 = new Plant(169, "B2 hidden", new BlockTexture("b2.png", "b2.png", "b2.png"));
    public static final Block BLOCK_B3 = new Plant(170, "B3 hidden", new BlockTexture("b3.png", "b3.png", "b3.png"));
    public static final Block BLOCK_B4 = new Plant(171, "B4 hidden", new BlockTexture("b4.png", "b4.png", "b4.png"));
    public static final Block BLOCK_B5 = new Plant(172, "B5 hidden", new BlockTexture("b5.png", "b5.png", "b5.png"));
    public static final Block BLOCK_B6 = new Plant(173, "B6 hidden", new BlockTexture("b6.png", "b6.png", "b6.png"));


    public static final Block BLOCK_WHEAT = new Plant(121, "Wheat hidden", new BlockTexture("wheat.png", "wheat.png", "wheat.png"));
    public static final Block BLOCK_CARROTS = new Plant(122, "Carrots hidden <PLANT>", new BlockTexture("carrots  plant.png", "carrots  plant.png", "carrots  plant.png"));
    public static final Block BLOCK_MINI_CACTUS = new Plant(123, "Mini Cactus", new BlockTexture("mini cactus.png", "mini cactus.png", "mini cactus.png"));
    public static final Block BLOCK_MUSHROOM = new Plant(124, "Mushroom", new BlockTexture("mushroom.png", "mushroom.png", "mushroom.png"));
    public static final Block BLOCK_MUSHROOM_2 = new Plant(125, "Mushroom 2", new BlockTexture("mushroom 2.png", "mushroom 2.png", "mushroom 2.png"));
    public static final Block BLOCK_ROSES = new Plant(126, "Roses", new BlockTexture("roses.png", "roses.png", "roses.png"));
    public static final Block BLOCK_BEETS = new Plant(246, "Beets hidden", new BlockTexture("beets.png", "beets.png", "beets.png"));
    public static final Block BLOCK_POTATOES_PLANT = new Plant(146, "Potatoes hidden<PLANT>", new BlockTexture("potatoes plant.png", "potatoes plant.png", "potatoes plant.png"));


    public static final Block BLOCK_CRACKED_STONE = new Block(27, "Cracked Stone", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_STONE_WITH_VINES = new Block(28, "Stone with Vines", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TNT_ACTIVE = new Block(29, "TNT Active hidden", new BlockTexture("tnt active.png", "tnt active bottom.png", "tnt active front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_PLANKS = new Block(30, "Jungle Planks", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_PLANKS_SLAB = new Block(31, "Jungle Planks Slab", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), RenderType.SLAB);
    public static final Block BLOCK_JUNGLE_PLANKS_STAIRS = new Block(32, "Jungle Planks Stairs", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), RenderType.STAIRS);
    public static final Block BLOCK_HONEYCOMB_BLOCK = new Block(33, "Honeycomb Block", new BlockTexture("honeycomb.png", "honeycomb.png", "honeycomb.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD = new Block(34, "Mosaic Bamboo Wood", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MUSIC_BOX = new Block(35, "Music Box", new BlockTexture("music box.png", "music box front.png", "music box front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CAKE = new Block(36, "Cake", new BlockTexture("cake.png", "cake.png", "cake.png"), RenderType.SPRITE);


    public static final Block BLOCK_OBSIDIAN = new Block(38, "Obsidian", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BURGUNDY_BRICK = new Block(39, "Burgundy Brick", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_FENCE = new Block(40, "Jungle Fence", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), RenderType.FENCE);
    public static final Block BLOCK_RED_FLOWER = new Block(41, "Red Flower", new BlockTexture("red flower.png", "red flower.png", "red flower.png"), RenderType.SPRITE);
    public static final VerticalPairedBlock BLOCK_TALL_DRY_GRASS = new VerticalPairedBlock("Tall Dry Grass", 26, 42, new BlockTexture("tall dry grass top.png"), new BlockTexture("tall dry grass.png"), RenderType.SPRITE);


    public static final Block BLOCK_YELLOW_FLOWER = new Block(44, "Yellow Flower", new BlockTexture("yellow flower.png", "yellow flower.png", "yellow flower.png"), RenderType.SPRITE);
    public static final Block BLOCK_COAL_ORE = new Block(45, "Coal Ore", new BlockTexture("coal ore.png", "coal ore.png", "coal ore.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_COAL_BLOCK = new Block(46, "Coal Block", new BlockTexture("coal.png", "coal.png", "coal.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_LEAVES = new Block(47, "Jungle Leaves", new BlockTexture("jungle leaves.png", "jungle leaves.png", "jungle leaves.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_LOG = new Block(48, "Jungle Log", new BlockTexture("jungle log.png", "jungle log.png", "jungle log front.png"), RenderType.ORIENTABLE_BLOCK);

    public static final VerticalPairedBlock BLOCK_TALL_GRASS = new VerticalPairedBlock("Tall Grass", 116, 49, new BlockTexture("tall grass top.png"), new BlockTexture("tall grass.png"), RenderType.SPRITE);

    public static final Block BLOCK_CONTROL_PANEL = new Block(50, "Control Panel", new BlockTexture("control l.png", "control l front.png", "control l front.png"), RenderType.SLAB);
    public static final Block BLOCK_BEEHIVE = new Block(52, "Beehive", new BlockTexture("beehive.png", "beehive bottom.png", "beehive front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_DIORITE = new Block(53, "Diorite", new BlockTexture("diorite.png", "diorite.png", "diorite.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POLISHED_DIORITE = new Block(54, "Polished Diorite", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_EDISON_LIGHT = new Block(55, "Edison Light", new BlockTexture("edison light.png", "edison light.png", "edison light.png"), (b) -> {
        b.opaque = false;
        b.solid = true;
        b.torchlightStartingValue = 15;
    });
    public static final Block BLOCK_POLISHED_ANDESITE = new Block(56, "Polished Andesite", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SPRUCE_PLANKS = new Block(57, "Spruce Planks", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_AZURE_BLUET = new Block(58, "Azure Bluet", new BlockTexture("azure bluet.png", "azure bluet.png", "azure bluet.png"), RenderType.SPRITE);
    public static final Block BLOCK_DANDELION = new Block(59, "Dandelion", new BlockTexture("dandelion.png", "dandelion.png", "dandelion.png"), RenderType.SPRITE);
    public static final Block BLOCK_BLUE_ORCHID = new Block(60, "Blue Orchid", new BlockTexture("blue orchid.png", "blue orchid.png", "blue orchid.png"), RenderType.SPRITE);
    public static final Block BLOCK_FERN = new Block(61, "Fern", new BlockTexture("fern.png", "fern.png", "fern.png"), RenderType.SPRITE);
    public static final Block BLOCK_GRANITE_BRICK = new Block(62, "Granite Brick", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_ACACIA_PLANKS = new Block(63, "Acacia Planks", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_AMETHYST_CRYSTAL = new Block(64, "Amethyst Crystal", new BlockTexture("amethyst crystal.png", "amethyst crystal.png", "amethyst crystal.png"), RenderType.SPRITE);
    public static final Block BLOCK_CLAY = new Block(65, "Clay", new BlockTexture("clay.png", "clay.png", "clay.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_CONCRETE = new Block(66, "Yellow Concrete", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_GLAZED_TERACOTTA = new Block(67, "Yellow Glazed Teracotta", new BlockTexture("yellow glazed teracotta.png", "yellow glazed teracotta.png", "yellow glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BLACK_CONCRETE = new Block(68, "Black Concrete", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_GLAZED_TERACOTTA = new Block(69, "Black Glazed Teracotta", new BlockTexture("black glazed teracotta.png", "black glazed teracotta.png", "black glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BLUE_CONCRETE = new Block(70, "Blue Concrete", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_GLAZED_TERACOTTA = new Block(71, "Blue Glazed Teracotta", new BlockTexture("blue glazed teracotta.png", "blue glazed teracotta.png", "blue glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BROWN_CONCRETE = new Block(72, "Brown Concrete", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BROWN_GLAZED_TERACOTTA = new Block(73, "Brown Glazed Teracotta", new BlockTexture("brown glazed teracotta.png", "brown glazed teracotta.png", "brown glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_CYAN_CONCRETE = new Block(74, "Cyan Concrete", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CYAN_GLAZED_TERACOTTA = new Block(75, "Cyan Glazed Teracotta", new BlockTexture("cyan glazed teracotta.png", "cyan glazed teracotta.png", "cyan glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_GRAY_CONCRETE = new Block(76, "Gray Concrete", new BlockTexture("gray concrete.png", "gray concrete.png", "gray concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GRAY_GLAZED_TERACOTTA = new Block(77, "Gray Glazed Teracotta", new BlockTexture("gray glazed teracotta.png", "gray glazed teracotta.png", "gray glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_GREEN_CONCRETE = new Block(78, "Green Concrete", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_GLAZED_TERACOTTA = new Block(79, "Green Glazed Teracotta", new BlockTexture("green glazed teracotta.png", "green glazed teracotta.png", "green glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE = new Block(80, "Light Blue Concrete", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA = new Block(81, "Light Blue Glazed Teracotta", new BlockTexture("light blue glazed teracotta.png", "light blue glazed teracotta.png", "light blue glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_LIGHT_GRAY_CONCRETE = new Block(82, "Light Gray Concrete", new BlockTexture("light gray concrete.png", "light gray concrete.png", "light gray concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA = new Block(83, "Light Gray Glazed Teracotta", new BlockTexture("light gray glazed teracotta.png", "light gray glazed teracotta.png", "light gray glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_LIME_CONCRETE = new Block(84, "Lime Concrete", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIME_GLAZED_TERACOTTA = new Block(85, "Lime Glazed Teracotta", new BlockTexture("lime glazed teracotta.png", "lime glazed teracotta.png", "lime glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_MAGENTA_CONCRETE = new Block(86, "Magenta Concrete", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_GLAZED_TERACOTTA = new Block(87, "Magenta Glazed Teracotta", new BlockTexture("magenta glazed teracotta.png", "magenta glazed teracotta.png", "magenta glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_ORANGE_CONCRETE = new Block(88, "Orange Concrete", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_GLAZED_TERACOTTA = new Block(89, "Orange Glazed Teracotta", new BlockTexture("orange glazed teracotta.png", "orange glazed teracotta.png", "orange glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_PINK_CONCRETE = new Block(90, "Pink Concrete", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_GLAZED_TERACOTTA = new Block(91, "Pink Glazed Teracotta", new BlockTexture("pink glazed teracotta.png", "pink glazed teracotta.png", "pink glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_PURPLE_CONCRETE = new Block(92, "Purple Concrete", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_GLAZED_TERACOTTA = new Block(93, "Purple Glazed Teracotta", new BlockTexture("purple glazed teracotta.png", "purple glazed teracotta.png", "purple glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_RED_CONCRETE = new Block(94, "Red Concrete", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_GLAZED_TERACOTTA = new Block(95, "Red Glazed Teracotta", new BlockTexture("red glazed teracotta.png", "red glazed teracotta.png", "red glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_WHITE_CONCRETE = new Block(96, "White Concrete", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WHITE_GLAZED_TERACOTTA = new Block(97, "White Glazed Teracotta", new BlockTexture("white glazed teracotta.png", "white glazed teracotta.png", "white glazed teracotta.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BRAIN_CORAL = new Block(98, "Brain Coral", new BlockTexture("brain coral.png", "brain coral.png", "brain coral.png"), RenderType.SPRITE);
    public static final Block BLOCK_DIAMOND_ORE = new Block(99, "Diamond Ore", new BlockTexture("diamond ore.png", "diamond ore.png", "diamond ore.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_QUARTZ_PILLAR_BLOCK = new Block(100, "Quartz Pillar Block", new BlockTexture("quartz.png", "quartz.png", "quartz front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_COBBLESTONE = new Block(101, "Cobblestone", new BlockTexture("cobblestone.png", "cobblestone.png", "cobblestone.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_TURQUOISE = new Block(102, "Wool Turquoise", new BlockTexture("wool turquoise.png", "wool turquoise.png", "wool turquoise.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_ORANGE = new Block(103, "Wool Orange", new BlockTexture("wool orange.png", "wool orange.png", "wool orange.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_SAND = new Block(104, "Red Sand", new BlockTexture("red sand.png", "red sand.png", "red sand.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WATERMELON = new Block(105, "Watermelon", new BlockTexture("watermelon.png", "watermelon.png", "watermelon front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PHANTOM_STONE = new Block(107, "Phantom Stone", new BlockTexture("andesite.png", "andesite.png", "andesite.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PHANTOM_STONE_BRICK = new Block(108, "Phantom Stone Brick", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CACTUS = new Block(109, "Cactus", new BlockTexture("cactus.png", "cactus bottom.png", "cactus front.png"), RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_STONE = new Block(110, "Palisade Stone", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_SANDSTONE = new Block(111, "Red Sandstone", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_FIRE_CORAL_BLOCK = new Block(112, "Fire Coral Block", new BlockTexture("fire coral.png", "fire coral.png", "fire coral.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);

    public static final Block BLOCK_CANDLE = new Block(113, "Candle", new BlockTexture("candle.png", "candle.png", "candle.png"), (b) -> {
        b.solid = false;
        b.type = RenderType.SPRITE;
        b.opaque = false;
        b.torchlightStartingValue = 6;
    });
    public static final Block BLOCK_RED_CANDLE = new Block(43, "Red Candle", new BlockTexture("red candle.png", "red candle.png", "red candle.png"), (b) -> {
        b.solid = false;
        b.type = RenderType.SPRITE;
        b.opaque = false;
        b.torchlightStartingValue = 6;
    });
    public static final Block BLOCK_GREEN_CANDLE = new Block(218, "Green Candle", new BlockTexture("green candle.png", "green candle.png", "green candle.png"), (b) -> {
        b.solid = false;
        b.type = RenderType.SPRITE;
        b.opaque = false;
        b.torchlightStartingValue = 6;
    });
    public static final Block BLOCK_YELLOW_CANDLE = new Block(216, "Yellow Candle", new BlockTexture("yellow candle.png", "yellow candle.png", "yellow candle.png"), (b) -> {
        b.solid = false;
        b.type = RenderType.SPRITE;
        b.opaque = false;
        b.torchlightStartingValue = 6;
    });
    public static final Block BLOCK_BLUE_CANDLE = new Block(220, "Blue Candle", new BlockTexture("blue candle.png", "blue candle.png", "blue candle.png"), (b) -> {
        b.solid = false;
        b.type = RenderType.SPRITE;
        b.opaque = false;
        b.torchlightStartingValue = 6;
    });


    public static final Block BLOCK_PALISADE_STONE_2 = new Block(114, "Palisade Stone 2", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_FIRE_CORAL = new Block(115, "Fire Coral", new BlockTexture("fire coral.png", "fire coral.png", "fire coral.png"), RenderType.SPRITE);
    public static final Block BLOCK_HORN_CORAL_BLOCK = new Block(117, "Horn Coral Block", new BlockTexture("horn coral.png", "horn coral.png", "horn coral.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GOLD_BLOCK = new Block(118, "Gold Block", new BlockTexture("gold.png", "gold.png", "gold.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_HORN_CORAL_FAN = new Block(119, "Horn Coral Fan", new BlockTexture("horn coral fan.png", "horn coral fan.png", "horn coral fan.png"), RenderType.SPRITE);
    public static final Block BLOCK_TNT = new Block(120, "TNT", new BlockTexture("tnt.png", "tnt bottom.png", "tnt front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID, (b) -> {
        TNTUtils.setTNTEvents(b, 5, 1000);
    });
    public static final Block BLOCK_WOOL_PURPLE = new Block(127, "Wool Purple", new BlockTexture("wool purple.png", "wool purple.png", "wool purple.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BIRCH_PLANKS = new Block(128, "Birch Planks", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_STAINED_WOOD = new Block(129, "Red Stained Wood", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_OAK_PLANKS = new Block(130, "Oak Planks", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_RED = new Block(131, "Wool Red", new BlockTexture("wool red.png", "wool red.png", "wool red.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_PINK = new Block(132, "Wool Pink", new BlockTexture("wool pink.png", "wool pink.png", "wool pink.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_YELLOW = new Block(133, "Wool Yellow", new BlockTexture("wool yellow.png", "wool yellow.png", "wool yellow.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_BROWN = new Block(134, "Wool Brown", new BlockTexture("wool brown.png", "wool brown.png", "wool brown.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TUBE_CORAL_BLOCK = new Block(135, "Tube Coral Block", new BlockTexture("tube coral.png", "tube coral.png", "tube coral.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TUBE_CORAL = new Block(136, "Tube Coral", new BlockTexture("tube coral.png", "tube coral.png", "tube coral.png"), RenderType.SPRITE);
    public static final Block BLOCK_TUBE_CORAL_FAN = new Block(137, "Tube Coral Fan", new BlockTexture("tube coral fan.png", "tube coral fan.png", "tube coral fan.png"), RenderType.SPRITE);
    public static final Block BLOCK_WOOL_DEEP_BLUE = new Block(138, "Wool Deep Blue", new BlockTexture("wool deep blue.png", "wool deep blue.png", "wool deep blue.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_SKY_BLUE = new Block(139, "Wool Sky Blue", new BlockTexture("wool sky blue.png", "wool sky blue.png", "wool sky blue.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_DARK_GREEN = new Block(140, "Wool Dark Green", new BlockTexture("wool dark green.png", "wool dark green.png", "wool dark green.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_GREEN = new Block(141, "Wool Green", new BlockTexture("wool green.png", "wool green.png", "wool green.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_GRAY = new Block(142, "Wool Gray", new BlockTexture("wool gray.png", "wool gray.png", "wool gray.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BRAIN_CORAL_BLOCK = new Block(143, "Brain Coral Block", new BlockTexture("brain coral.png", "brain coral.png", "brain coral.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_DIAMOND_BLOCK = new Block(144, "Diamond Block", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_HONEYCOMB_BLOCK_STAIRS = new Block(147, "Honeycomb Block Stairs", new BlockTexture("honeycomb.png", "honeycomb.png", "honeycomb.png"), RenderType.STAIRS);
    public static final Block BLOCK_OAK_FENCE = new Block(148, "Oak Fence", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), RenderType.FENCE);
    public static final Block BLOCK_BIRCH_FENCE = new Block(149, "Birch Fence", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), RenderType.FENCE);
    public static final Block BLOCK_BUBBLE_CORAL_BLOCK = new Block(150, "Bubble Coral Block", new BlockTexture("bubble coral.png", "bubble coral.png", "bubble coral.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BUBBLE_CORAL = new Block(151, "Bubble Coral", new BlockTexture("bubble coral.png", "bubble coral.png", "bubble coral.png"), RenderType.SPRITE);
    public static final Block BLOCK_BUBBLE_CORAL_FAN = new Block(152, "Bubble Coral Fan", new BlockTexture("bubble coral fan.png", "bubble coral fan.png", "bubble coral fan.png"), RenderType.SPRITE);
    public static final Block BLOCK_JUNGLE_GRASS = new Block(153, "Jungle Grass", new BlockTexture("jungle grass.png", "dirt.png", "jungle grass front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LILY_PAD = new Block(154, "Lily Pad", new BlockTexture("lily pad.png", "lily pad.png", "lily pad.png"), RenderType.FLOOR);
    public static final Block BLOCK_TRACK = new StraightTrack(155);
    public static final Block BLOCK_WOOL_MAGENTA = new Block(156, "Wool Magenta", new BlockTexture("wool magenta.png", "wool magenta.png", "wool magenta.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_BLACK = new Block(157, "Wool Black", new BlockTexture("wool black.png", "wool black.png", "wool black.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GRANITE_BRICK_STAIRS = new Block(158, "Granite Brick Stairs", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), RenderType.STAIRS);
    public static final Block BLOCK_CEMENT = new Block(159, "Cement", new BlockTexture("cement.png", "cement.png", "cement.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);

    //Saplings
    public static final Block BLOCK_OAK_SAPLING = new Block(160, "Oak Sapling",
            new BlockTexture("oak sapling.png"), RenderType.SPRITE, (b) -> {
        b.setBlockEvent(OakTreeUtils.setBlockEvent);
    });
    public static final Block BLOCK_BIRCH_SAPLING = new Block(161, "Birch Sapling", new BlockTexture("birch sapling.png", "birch sapling.png", "birch sapling.png"), RenderType.SPRITE, (b) -> {
        b.setBlockEvent(BirchTreeUtils.setBlockEvent);
    });
    public static final Block BLOCK_ACACIA_SAPLING = new Block(230, "Acacia Sapling", new BlockTexture("acacia sapling.png", "acacia sapling.png", "acacia sapling.png")
            , RenderType.SPRITE, (b) -> {
        b.setBlockEvent(AcaciaTreeUtils.setBlockEvent);
    });
    public static final Block BLOCK_JUNGLE_SAPLING = new Block(37, "Jungle Sapling", new BlockTexture("jungle sapling.png", "jungle sapling.png", "jungle sapling.png")
            , RenderType.SPRITE, (b) -> {
        b.setBlockEvent(JungleTreeUtils.setBlockEvent);
    });
    public static final Block BLOCK_SPRUCE_SAPLING = new Block(734, "Spruce Sapling", new BlockTexture("spruce sapling.png", "spruce sapling.png", "spruce sapling.png")
            , RenderType.SPRITE, (b) -> {
        b.setBlockEvent(SpruceTreeUtils.setBlockEvent);
    });


    public static final Block BLOCK_ELECTRIC_LIGHT = new Block(174, "Electric Light", new BlockTexture("electric light.png", "electric light.png", "electric light.png"), (b) -> {
        b.opaque = false;
        b.solid = true;
        b.type = RenderType.LAMP;
        b.torchlightStartingValue = 15;
    });
    public static final Block BLOCK_RED_PALISADE_SANDSTONE = new Block(175, "Red Palisade Sandstone", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PHANTOM_SANDSTONE = new Block(176, "Phantom Sandstone", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PALISADE_SANDSTONE = new Block(177, "Palisade Sandstone", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GLOW_ROCK = new Block(178, "Glow Rock", new BlockTexture("glow rock.png", "glow rock.png", "glow rock.png"), (b) -> {
        b.opaque = false;
        b.solid = true;
        b.type = RenderType.LAMP;
        b.torchlightStartingValue = 7;
    });
    public static final Block BLOCK_DARK_OAK_FENCE = new Block(195, "Dark Oak Fence", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), RenderType.FENCE);
    public static final Block BLOCK_BIRCH_PLANKS_STAIRS = new Block(196, "Birch Planks Stairs", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), RenderType.STAIRS);
    public static final Block BLOCK_OAK_PLANKS_STAIRS = new Block(197, "Oak Planks Stairs", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), RenderType.STAIRS);
    public static final Block BLOCK_DARK_OAK_PLANKS_STAIRS = new Block(198, "Dark Oak Planks Stairs", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), RenderType.STAIRS);
    public static final Block BLOCK_HONEYCOMB_BLOCK_SLAB = new Block(199, "Honeycomb Block Slab", new BlockTexture("honeycomb.png", "honeycomb.png", "honeycomb.png"), RenderType.SLAB);
    public static final Block BLOCK_JUNGLE_GRASS_PLANT = new Plant(200, "Jungle Grass Plant", new BlockTexture("jungle grass plant.png", "jungle grass plant.png", "jungle grass plant.png"));
    public static final Block BLOCK_PRISMARINE_BRICK_STAIRS = new Block(201, "Prismarine Brick Stairs", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), RenderType.STAIRS);
    public static final Block BLOCK_SANDSTONE_STAIRS = new Block(202, "Sandstone Stairs", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone front.png"), RenderType.STAIRS);
    public static final Block BLOCK_CAVE_VINES_FLAT = new Block(203, "Cave Vines Flat", new BlockTexture("cave vines flat.png", "cave vines flat.png", "cave vines flat.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_POLISHED_DIORITE_STAIRS = new Block(204, "Polished Diorite Stairs", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), RenderType.STAIRS);
    public static final Block BLOCK_BIRCH_PLANKS_SLAB = new Block(205, "Birch Planks Slab", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), RenderType.SLAB);
    public static final Block BLOCK_OAK_PLANKS_SLAB = new Block(206, "Oak Planks Slab", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), RenderType.SLAB);
    public static final Block BLOCK_DARK_OAK_PLANKS_SLAB = new Block(207, "Dark Oak Planks Slab", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), RenderType.SLAB);
    public static final Block BLOCK_BAMBOO_WOOD_STAIRS = new Block(208, "Bamboo Wood Stairs", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_STONE_BRICK_SLAB = new Block(209, "Stone Brick Slab", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), RenderType.SLAB);
    public static final Block BLOCK_RED_SANDSTONE_SLAB = new Block(210, "Red Sandstone Slab", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone front.png"), RenderType.SLAB);
    public static final Block BLOCK_SANDSTONE_SLAB = new Block(211, "Sandstone Slab", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone front.png"), RenderType.SLAB);
    public static final Block BLOCK_DRY_GRASS = new Block(212, "Dry Grass", new BlockTexture("dry grass.png", "dirt.png", "dry grass front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POLISHED_ANDESITE_SLAB = new Block(213, "Polished Andesite Slab", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), RenderType.SLAB);
    public static final Block BLOCK_IRON_LADDER = new Block(214, "Iron Ladder", new BlockTexture("iron ladder.png", "iron ladder.png", "iron ladder.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_RED_VINES_FLAT = new Block(215, "Red Vines Flat", new BlockTexture("red vines flat.png", "red vines flat.png", "red vines flat.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_CAVE_VINES = new Block(217, "Cave Vines", new BlockTexture("cave vines flat.png", "cave vines flat.png", "cave vines flat.png"), RenderType.SPRITE);

    public static final Block BLOCK_GRANITE = new Block(219, "Granite", new BlockTexture("granite.png", "granite.png", "granite.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_VINES = new Block(221, "Red Vines", new BlockTexture("red vines flat.png", "red vines flat.png", "red vines flat.png"), RenderType.SPRITE);
    public static final Block BLOCK_FLAT_VINES = new Block(222, "Flat Vines", new BlockTexture("vines.png", "vines.png", "vines.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_DARK_OAK_LADDER = new Block(223, "Dark Oak Ladder", new BlockTexture("dark oak ladder.png", "dark oak ladder.png", "dark oak ladder.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_DRY_GRASS_PLANT = new Block(224, "Dry Grass Plant", new BlockTexture("dry grass plant.png", "dry grass plant.png", "dry grass plant.png"), RenderType.SPRITE);
    public static final Block BLOCK_ACACIA_LEAVES = new Block(225, "Acacia Leaves", new BlockTexture("acacia leaves.png", "acacia leaves.png", "acacia leaves.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ACACIA_LOG = new Block(226, "Acacia Log", new BlockTexture("acacia log.png", "acacia log.png", "acacia log front.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_FIRE_CORAL_FAN = new Block(227, "Fire Coral Fan", new BlockTexture("fire coral fan.png", "fire coral fan.png", "fire coral fan.png"), RenderType.SPRITE);
    public static final Block BLOCK_LAPIS_LAZUL_ORE = new Block(228, "Lapis Lazul Ore", new BlockTexture("lapis lazul ore.png", "lapis lazul ore.png", "lapis lazul ore.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LAPIS_LAZUL_BLOCK = new Block(229, "Lapis Lazul Block", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);

   public static final Block BLOCK_BRAIN_CORAL_FAN = new Block(232, "Brain Coral Fan", new BlockTexture("brain coral fan.png", "brain coral fan.png", "brain coral fan.png"), RenderType.SPRITE);
    public static final Block BLOCK_WHITE_ROSE = new Block(233, "White Rose", new BlockTexture("white rose.png", "white rose.png", "white rose.png"), RenderType.SPRITE);
    public static final Block BLOCK_GOLD_ORE = new Block(234, "Gold Ore", new BlockTexture("gold ore.png", "gold ore.png", "gold ore.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_HORN_CORAL = new Block(235, "Horn Coral", new BlockTexture("horn coral.png", "horn coral.png", "horn coral.png"), RenderType.SPRITE);
    public static final Block BLOCK_RED_ROSE = new Block(236, "Red Rose", new BlockTexture("red rose.png", "red rose.png", "red rose.png"), RenderType.SPRITE);
    public static final Block BLOCK_EMERALD_ORE = new Block(237, "Emerald Ore", new BlockTexture("emerald ore.png", "emerald ore.png", "emerald ore.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_EMERALD_BLOCK = new Block(238, "Emerald Block", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_EYE_SUSAN = new Block(239, "Black-Eye Susan", new BlockTexture("black eye susan.png", "black eye susan.png", "black eye susan.png"), RenderType.SPRITE);
    public static final Block BLOCK_ORANGE_TULIP = new Block(240, "Orange Tulip", new BlockTexture("orange tulip.png", "orange tulip.png", "orange tulip.png"), RenderType.SPRITE);
    public static final Block BLOCK_DEAD_BUSH = new Block(241, "Dead Bush", new BlockTexture("dead bush.png", "dead bush.png", "dead bush.png"), RenderType.SPRITE);
    public static final Block BLOCK_HAY_BAIL = new Block(242, "Hay Bail", new BlockTexture("hay bail.png", "hay bail.png", "hay bail front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POLISHED_ANDESITE_STAIRS = new Block(243, "Polished Andesite Stairs", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), RenderType.STAIRS);
    public static final Block BLOCK_POLISHED_DIORITE_SLAB = new Block(244, "Polished Diorite Slab", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), RenderType.SLAB);
    public static final Block BLOCK_CURVED_TRACK = new Block(245, "Curved Track hidden", new BlockTexture("curved track.png", "curved track.png", "curved track.png"), RenderType.FLOOR);
    public static final Block BLOCK_BAMBOO_LADDER = new Block(248, "Bamboo Ladder", new BlockTexture("bamboo ladder.png", "bamboo ladder.png", "bamboo ladder.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_ACACIA_FENCE = new Block(249, "Acacia Fence", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), RenderType.FENCE);
    public static final Block BLOCK_ACACIA_PLANKS_STAIRS = new Block(250, "Acacia Planks Stairs", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), RenderType.STAIRS);
    public static final Block BLOCK_ACACIA_PLANKS_SLAB = new Block(251, "Acacia Planks Slab", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), RenderType.SLAB);
    public static final Block BLOCK_RAISED_TRACK = new Block(252, "Raised Track hidden", new BlockTexture("track.png", "track.png", "track.png"), RenderType.RAISED_TRACK);
    public static final Block BLOCK_SEA_GRASS = new Block(253, "Sea Grass", new BlockTexture("sea grass.png", "sea grass.png", "sea grass.png"), RenderType.SPRITE);
    public static final Block BLOCK_BLUE_STAINED_WOOD = new Block(254, "Blue Stained Wood", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RUBY_CRYSTAL = new Block(255, "Ruby Crystal", new BlockTexture("ruby crystal.png", "ruby crystal.png", "ruby crystal.png"), RenderType.SPRITE);
    public static final Block BLOCK_JADE_CRYSTAL = new Block(256, "Jade Crystal", new BlockTexture("jade crystal.png", "jade crystal.png", "jade crystal.png"), RenderType.SPRITE);
    public static final Block BLOCK_AQUAMARINE_CRYSTAL = new Block(257, "Aquamarine Crystal", new BlockTexture("aquamarine crystal.png", "aquamarine crystal.png", "aquamarine crystal.png"), RenderType.SPRITE);
    public static final Block BLOCK_BAMBOO_WOOD_SLAB = new Block(258, "Bamboo Wood Slab", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), RenderType.SLAB);
    public static final Block BLOCK_RED_SANDSTONE_STAIRS = new Block(259, "Red Sandstone Stairs", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone front.png"), RenderType.STAIRS);
    public static final Block BLOCK_STONE_BRICK_STAIRS = new Block(260, "Stone Brick Stairs", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), RenderType.STAIRS);
    public static final Block BLOCK_STONE_BRICK_FENCE = new Block(262, "Stone Brick Fence", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), RenderType.FENCE);
    public static final Block BLOCK_BRICK_STAIRS = new Block(263, "Brick Stairs", new BlockTexture("brick.png", "brick.png", "brick.png"), RenderType.STAIRS);
    public static final Block BLOCK_BRICK_SLAB = new Block(264, "Brick Slab", new BlockTexture("brick.png", "brick.png", "brick.png"), RenderType.SLAB);
    public static final Block BLOCK_SNOW_BLOCK = new Block(265, "Snow Block", new BlockTexture("snow.png", "snow.png", "snow.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_COBBLESTONE_STAIRS = new Block(266, "Cobblestone Stairs", new BlockTexture("cobblestone.png", "cobblestone.png", "cobblestone.png"), RenderType.STAIRS);
    public static final Block BLOCK_COBBLESTONE_SLAB = new Block(267, "Cobblestone Slab", new BlockTexture("cobblestone.png", "cobblestone.png", "cobblestone.png"), RenderType.SLAB);
    public static final Block BLOCK_PALISADE_STONE_STAIRS = new Block(268, "Palisade Stone Stairs", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone front.png"), RenderType.STAIRS);
    public static final Block BLOCK_PALISADE_STONE_SLAB = new Block(269, "Palisade Stone Slab", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone front.png"), RenderType.SLAB);
    public static final Block BLOCK_PALISADE_STONE_FENCE = new Block(270, "Palisade Stone Fence", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone front.png"), RenderType.FENCE);
    public static final Block BLOCK_PALISADE_STONE_2_STAIRS = new Block(271, "Palisade Stone 2 Stairs", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), RenderType.STAIRS);
    public static final Block BLOCK_PALISADE_STONE_2_SLAB = new Block(272, "Palisade Stone 2 Slab", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), RenderType.SLAB);
    public static final Block BLOCK_PALISADE_STONE_2_FENCE = new Block(273, "Palisade Stone 2 Fence", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), RenderType.FENCE);
    public static final Block BLOCK_POLISHED_DIORITE_FENCE = new Block(274, "Polished Diorite Fence", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), RenderType.FENCE);
    public static final Block BLOCK_POLISHED_ANDESITE_FENCE = new Block(275, "Polished Andesite Fence", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), RenderType.FENCE);
    public static final Block BLOCK_CRACKED_STONE_STAIRS = new Block(276, "Cracked Stone Stairs", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), RenderType.STAIRS);
    public static final Block BLOCK_CRACKED_STONE_SLAB = new Block(277, "Cracked Stone Slab", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), RenderType.SLAB);
    public static final Block BLOCK_CRACKED_STONE_FENCE = new Block(278, "Cracked Stone Fence", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), RenderType.FENCE);
    public static final Block BLOCK_STONE_WITH_VINES_STAIRS = new Block(279, "Stone with Vines Stairs", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), RenderType.STAIRS);
    public static final Block BLOCK_STONE_WITH_VINES_SLAB = new Block(280, "Stone with Vines Slab", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), RenderType.SLAB);
    public static final Block BLOCK_STONE_WITH_VINES_FENCE = new Block(281, "Stone with Vines Fence", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), RenderType.FENCE);
    public static final Block BLOCK_BURGUNDY_BRICK_STAIRS = new Block(282, "Burgundy Brick Stairs", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), RenderType.STAIRS);
    public static final Block BLOCK_BURGUNDY_BRICK_SLAB = new Block(283, "Burgundy Brick Slab", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), RenderType.SLAB);
    public static final Block BLOCK_BURGUNDY_BRICK_FENCE = new Block(284, "Burgundy Brick Fence", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), RenderType.FENCE);
    public static final Block BLOCK_SWITCH_JUNCTION = new Block(285, "Switch Junction", new BlockTexture("switch junction.png", "switch junction.png", "switch junction.png"), RenderType.FLOOR);
    public static final Block BLOCK_TRACK_STOP = new Block(286, "Track Stop", new BlockTexture("track stop.png", "track stop.png", "track stop.png"), RenderType.FLOOR);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_STAIRS = new Block(287, "Red Palisade Sandstone Stairs", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone front.png"), RenderType.STAIRS);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_SLAB = new Block(288, "Red Palisade Sandstone Slab", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone front.png"), RenderType.SLAB);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_FENCE = new Block(289, "Red Palisade Sandstone Fence", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone front.png"), RenderType.FENCE);
    public static final Block BLOCK_PALISADE_SANDSTONE_STAIRS = new Block(290, "Palisade Sandstone Stairs", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone front.png"), RenderType.STAIRS);
    public static final Block BLOCK_PALISADE_SANDSTONE_SLAB = new Block(291, "Palisade Sandstone Slab", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone front.png"), RenderType.SLAB);
    public static final Block BLOCK_PALISADE_SANDSTONE_FENCE = new Block(292, "Palisade Sandstone Fence", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone front.png"), RenderType.FENCE);
    public static final Block BLOCK_WOOL_STAIRS = new Block(293, "Wool Stairs", new BlockTexture("wool.png", "wool.png", "wool.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_SLAB = new Block(294, "Wool Slab", new BlockTexture("wool.png", "wool.png", "wool.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_GRAY_STAIRS = new Block(295, "Wool Gray Stairs", new BlockTexture("wool gray.png", "wool gray.png", "wool gray.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_GRAY_SLAB = new Block(296, "Wool Gray Slab", new BlockTexture("wool gray.png", "wool gray.png", "wool gray.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_RED_STAIRS = new Block(297, "Wool Red Stairs", new BlockTexture("wool red.png", "wool red.png", "wool red.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_RED_SLAB = new Block(298, "Wool Red Slab", new BlockTexture("wool red.png", "wool red.png", "wool red.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_PINK_STAIRS = new Block(299, "Wool Pink Stairs", new BlockTexture("wool pink.png", "wool pink.png", "wool pink.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_PINK_SLAB = new Block(300, "Wool Pink Slab", new BlockTexture("wool pink.png", "wool pink.png", "wool pink.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_ORANGE_STAIRS = new Block(301, "Wool Orange Stairs", new BlockTexture("wool orange.png", "wool orange.png", "wool orange.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_ORANGE_SLAB = new Block(302, "Wool Orange Slab", new BlockTexture("wool orange.png", "wool orange.png", "wool orange.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_YELLOW_STAIRS = new Block(303, "Wool Yellow Stairs", new BlockTexture("wool yellow.png", "wool yellow.png", "wool yellow.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_YELLOW_SLAB = new Block(304, "Wool Yellow Slab", new BlockTexture("wool yellow.png", "wool yellow.png", "wool yellow.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_GREEN_STAIRS = new Block(305, "Wool Green Stairs", new BlockTexture("wool green.png", "wool green.png", "wool green.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_GREEN_SLAB = new Block(306, "Wool Green Slab", new BlockTexture("wool green.png", "wool green.png", "wool green.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_DARK_GREEN_STAIRS = new Block(307, "Wool Dark Green Stairs", new BlockTexture("wool dark green.png", "wool dark green.png", "wool dark green.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_DARK_GREEN_SLAB = new Block(308, "Wool Dark Green Slab", new BlockTexture("wool dark green.png", "wool dark green.png", "wool dark green.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_TURQUOISE_STAIRS = new Block(309, "Wool Turquoise Stairs", new BlockTexture("wool turquoise.png", "wool turquoise.png", "wool turquoise.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_TURQUOISE_SLAB = new Block(310, "Wool Turquoise Slab", new BlockTexture("wool turquoise.png", "wool turquoise.png", "wool turquoise.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_DEEP_BLUE_STAIRS = new Block(311, "Wool Deep Blue Stairs", new BlockTexture("wool deep blue.png", "wool deep blue.png", "wool deep blue.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_DEEP_BLUE_SLAB = new Block(312, "Wool Deep Blue Slab", new BlockTexture("wool deep blue.png", "wool deep blue.png", "wool deep blue.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_SKY_BLUE_STAIRS = new Block(313, "Wool Sky Blue Stairs", new BlockTexture("wool sky blue.png", "wool sky blue.png", "wool sky blue.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_SKY_BLUE_SLAB = new Block(314, "Wool Sky Blue Slab", new BlockTexture("wool sky blue.png", "wool sky blue.png", "wool sky blue.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_BROWN_STAIRS = new Block(315, "Wool Brown Stairs", new BlockTexture("wool brown.png", "wool brown.png", "wool brown.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_BROWN_SLAB = new Block(316, "Wool Brown Slab", new BlockTexture("wool brown.png", "wool brown.png", "wool brown.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_PURPLE_STAIRS = new Block(317, "Wool Purple Stairs", new BlockTexture("wool purple.png", "wool purple.png", "wool purple.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_PURPLE_SLAB = new Block(318, "Wool Purple Slab", new BlockTexture("wool purple.png", "wool purple.png", "wool purple.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_MAGENTA_STAIRS = new Block(319, "Wool Magenta Stairs", new BlockTexture("wool magenta.png", "wool magenta.png", "wool magenta.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_MAGENTA_SLAB = new Block(320, "Wool Magenta Slab", new BlockTexture("wool magenta.png", "wool magenta.png", "wool magenta.png"), RenderType.SLAB);
    public static final Block BLOCK_WOOL_BLACK_STAIRS = new Block(321, "Wool Black Stairs", new BlockTexture("wool black.png", "wool black.png", "wool black.png"), RenderType.STAIRS);
    public static final Block BLOCK_WOOL_BLACK_SLAB = new Block(322, "Wool Black Slab", new BlockTexture("wool black.png", "wool black.png", "wool black.png"), RenderType.SLAB);
    public static final Block BLOCK_YELLOW_CONCRETE_STAIRS = new Block(323, "Yellow Concrete Stairs", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_CONCRETE_SLAB = new Block(324, "Yellow Concrete Slab", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_YELLOW_CONCRETE_FENCE = new Block(325, "Yellow Concrete Fence", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_BLACK_CONCRETE_STAIRS = new Block(326, "Black Concrete Stairs", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLACK_CONCRETE_SLAB = new Block(327, "Black Concrete Slab", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_BLACK_CONCRETE_FENCE = new Block(328, "Black Concrete Fence", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_BLUE_CONCRETE_STAIRS = new Block(329, "Blue Concrete Stairs", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLUE_CONCRETE_SLAB = new Block(330, "Blue Concrete Slab", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_BLUE_CONCRETE_FENCE = new Block(331, "Blue Concrete Fence", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_BROWN_CONCRETE_STAIRS = new Block(332, "Brown Concrete Stairs", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_BROWN_CONCRETE_SLAB = new Block(333, "Brown Concrete Slab", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_BROWN_CONCRETE_FENCE = new Block(334, "Brown Concrete Fence", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_CYAN_CONCRETE_STAIRS = new Block(335, "Cyan Concrete Stairs", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_CYAN_CONCRETE_SLAB = new Block(336, "Cyan Concrete Slab", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_CYAN_CONCRETE_FENCE = new Block(337, "Cyan Concrete Fence", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_GRAY_CONCRETE_STAIRS = new Block(338, "Gray Concrete Stairs", new BlockTexture("gray concrete.png", "gray concrete.png", "gray concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_GRAY_CONCRETE_SLAB = new Block(339, "Gray Concrete Slab", new BlockTexture("gray concrete.png", "gray concrete.png", "gray concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_CONCRETE_FENCE = new Block(340, "Gray Concrete Fence", new BlockTexture("gray concrete.png", "gray concrete.png", "gray concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_GREEN_CONCRETE_STAIRS = new Block(341, "Green Concrete Stairs", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_GREEN_CONCRETE_SLAB = new Block(342, "Green Concrete Slab", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_GREEN_CONCRETE_FENCE = new Block(343, "Green Concrete Fence", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE_STAIRS = new Block(344, "Light Blue Concrete Stairs", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE_SLAB = new Block(345, "Light Blue Concrete Slab", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE_FENCE = new Block(346, "Light Blue Concrete Fence", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_LIGHT_GRAY_CONCRETE_STAIRS = new Block(347, "Light Gray Concrete Stairs", new BlockTexture("light gray concrete.png", "light gray concrete.png", "light gray concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_GRAY_CONCRETE_SLAB = new Block(348, "Light Gray Concrete Slab", new BlockTexture("light gray concrete.png", "light gray concrete.png", "light gray concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_LIGHT_GRAY_CONCRETE_FENCE = new Block(349, "Light Gray Concrete Fence", new BlockTexture("light gray concrete.png", "light gray concrete.png", "light gray concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_LIME_CONCRETE_STAIRS = new Block(350, "Lime Concrete Stairs", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIME_CONCRETE_SLAB = new Block(351, "Lime Concrete Slab", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_LIME_CONCRETE_FENCE = new Block(352, "Lime Concrete Fence", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_MAGENTA_CONCRETE_STAIRS = new Block(353, "Magenta Concrete Stairs", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_CONCRETE_SLAB = new Block(354, "Magenta Concrete Slab", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_CONCRETE_FENCE = new Block(355, "Magenta Concrete Fence", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_ORANGE_CONCRETE_STAIRS = new Block(356, "Orange Concrete Stairs", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_CONCRETE_SLAB = new Block(357, "Orange Concrete Slab", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_ORANGE_CONCRETE_FENCE = new Block(358, "Orange Concrete Fence", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_PINK_CONCRETE_STAIRS = new Block(359, "Pink Concrete Stairs", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_PINK_CONCRETE_SLAB = new Block(360, "Pink Concrete Slab", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_PINK_CONCRETE_FENCE = new Block(361, "Pink Concrete Fence", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_PURPLE_CONCRETE_STAIRS = new Block(362, "Purple Concrete Stairs", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_CONCRETE_SLAB = new Block(363, "Purple Concrete Slab", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_PURPLE_CONCRETE_FENCE = new Block(364, "Purple Concrete Fence", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_RED_CONCRETE_STAIRS = new Block(365, "Red Concrete Stairs", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_RED_CONCRETE_SLAB = new Block(366, "Red Concrete Slab", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_RED_CONCRETE_FENCE = new Block(367, "Red Concrete Fence", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_WHITE_CONCRETE_STAIRS = new Block(368, "White Concrete Stairs", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), RenderType.STAIRS);
    public static final Block BLOCK_WHITE_CONCRETE_SLAB = new Block(369, "White Concrete Slab", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), RenderType.SLAB);
    public static final Block BLOCK_WHITE_CONCRETE_FENCE = new Block(370, "White Concrete Fence", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), RenderType.FENCE);
    public static final Block BLOCK_YELLOW_GLAZED_TERACOTTA_STAIRS = new Block(371, "Yellow Glazed Teracotta Stairs", new BlockTexture("yellow glazed teracotta.png", "yellow glazed teracotta.png", "yellow glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_GLAZED_TERACOTTA_SLAB = new Block(372, "Yellow Glazed Teracotta Slab", new BlockTexture("yellow glazed teracotta.png", "yellow glazed teracotta.png", "yellow glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_BLACK_GLAZED_TERACOTTA_STAIRS = new Block(373, "Black Glazed Teracotta Stairs", new BlockTexture("black glazed teracotta.png", "black glazed teracotta.png", "black glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLACK_GLAZED_TERACOTTA_SLAB = new Block(374, "Black Glazed Teracotta Slab", new BlockTexture("black glazed teracotta.png", "black glazed teracotta.png", "black glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_BLUE_GLAZED_TERACOTTA_STAIRS = new Block(375, "Blue Glazed Teracotta Stairs", new BlockTexture("blue glazed teracotta.png", "blue glazed teracotta.png", "blue glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLUE_GLAZED_TERACOTTA_SLAB = new Block(376, "Blue Glazed Teracotta Slab", new BlockTexture("blue glazed teracotta.png", "blue glazed teracotta.png", "blue glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_BROWN_GLAZED_TERACOTTA_STAIRS = new Block(377, "Brown Glazed Teracotta Stairs", new BlockTexture("brown glazed teracotta.png", "brown glazed teracotta.png", "brown glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_BROWN_GLAZED_TERACOTTA_SLAB = new Block(378, "Brown Glazed Teracotta Slab", new BlockTexture("brown glazed teracotta.png", "brown glazed teracotta.png", "brown glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_CYAN_GLAZED_TERACOTTA_STAIRS = new Block(379, "Cyan Glazed Teracotta Stairs", new BlockTexture("cyan glazed teracotta.png", "cyan glazed teracotta.png", "cyan glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_CYAN_GLAZED_TERACOTTA_SLAB = new Block(380, "Cyan Glazed Teracotta Slab", new BlockTexture("cyan glazed teracotta.png", "cyan glazed teracotta.png", "cyan glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_GLAZED_TERACOTTA_STAIRS = new Block(381, "Gray Glazed Teracotta Stairs", new BlockTexture("gray glazed teracotta.png", "gray glazed teracotta.png", "gray glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_GRAY_GLAZED_TERACOTTA_SLAB = new Block(382, "Gray Glazed Teracotta Slab", new BlockTexture("gray glazed teracotta.png", "gray glazed teracotta.png", "gray glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_GREEN_GLAZED_TERACOTTA_STAIRS = new Block(383, "Green Glazed Teracotta Stairs", new BlockTexture("green glazed teracotta.png", "green glazed teracotta.png", "green glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_GREEN_GLAZED_TERACOTTA_SLAB = new Block(384, "Green Glazed Teracotta Slab", new BlockTexture("green glazed teracotta.png", "green glazed teracotta.png", "green glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_STAIRS = new Block(385, "Light Blue Glazed Teracotta Stairs", new BlockTexture("light blue glazed teracotta.png", "light blue glazed teracotta.png", "light blue glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_SLAB = new Block(386, "Light Blue Glazed Teracotta Slab", new BlockTexture("light blue glazed teracotta.png", "light blue glazed teracotta.png", "light blue glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA_STAIRS = new Block(387, "Light Gray Glazed Teracotta Stairs", new BlockTexture("light gray glazed teracotta.png", "light gray glazed teracotta.png", "light gray glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_GRAY_GLAZED_TERACOTTA_SLAB = new Block(388, "Light Gray Glazed Teracotta Slab", new BlockTexture("light gray glazed teracotta.png", "light gray glazed teracotta.png", "light gray glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_LIME_GLAZED_TERACOTTA_STAIRS = new Block(389, "Lime Glazed Teracotta Stairs", new BlockTexture("lime glazed teracotta.png", "lime glazed teracotta.png", "lime glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIME_GLAZED_TERACOTTA_SLAB = new Block(390, "Lime Glazed Teracotta Slab", new BlockTexture("lime glazed teracotta.png", "lime glazed teracotta.png", "lime glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_GLAZED_TERACOTTA_STAIRS = new Block(391, "Magenta Glazed Teracotta Stairs", new BlockTexture("magenta glazed teracotta.png", "magenta glazed teracotta.png", "magenta glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_GLAZED_TERACOTTA_SLAB = new Block(392, "Magenta Glazed Teracotta Slab", new BlockTexture("magenta glazed teracotta.png", "magenta glazed teracotta.png", "magenta glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_ORANGE_GLAZED_TERACOTTA_STAIRS = new Block(393, "Orange Glazed Teracotta Stairs", new BlockTexture("orange glazed teracotta.png", "orange glazed teracotta.png", "orange glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_GLAZED_TERACOTTA_SLAB = new Block(394, "Orange Glazed Teracotta Slab", new BlockTexture("orange glazed teracotta.png", "orange glazed teracotta.png", "orange glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_PINK_GLAZED_TERACOTTA_STAIRS = new Block(395, "Pink Glazed Teracotta Stairs", new BlockTexture("pink glazed teracotta.png", "pink glazed teracotta.png", "pink glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_PINK_GLAZED_TERACOTTA_SLAB = new Block(396, "Pink Glazed Teracotta Slab", new BlockTexture("pink glazed teracotta.png", "pink glazed teracotta.png", "pink glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_PURPLE_GLAZED_TERACOTTA_STAIRS = new Block(397, "Purple Glazed Teracotta Stairs", new BlockTexture("purple glazed teracotta.png", "purple glazed teracotta.png", "purple glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_GLAZED_TERACOTTA_SLAB = new Block(398, "Purple Glazed Teracotta Slab", new BlockTexture("purple glazed teracotta.png", "purple glazed teracotta.png", "purple glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_RED_GLAZED_TERACOTTA_STAIRS = new Block(399, "Red Glazed Teracotta Stairs", new BlockTexture("red glazed teracotta.png", "red glazed teracotta.png", "red glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_RED_GLAZED_TERACOTTA_SLAB = new Block(400, "Red Glazed Teracotta Slab", new BlockTexture("red glazed teracotta.png", "red glazed teracotta.png", "red glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_WHITE_GLAZED_TERACOTTA_STAIRS = new Block(401, "White Glazed Teracotta Stairs", new BlockTexture("white glazed teracotta.png", "white glazed teracotta.png", "white glazed teracotta.png"), RenderType.STAIRS);
    public static final Block BLOCK_WHITE_GLAZED_TERACOTTA_SLAB = new Block(402, "White Glazed Teracotta Slab", new BlockTexture("white glazed teracotta.png", "white glazed teracotta.png", "white glazed teracotta.png"), RenderType.SLAB);
    public static final Block BLOCK_OAK_LADDER = new Block(403, "Oak Ladder", new BlockTexture("oak ladder.png", "oak ladder.png", "oak ladder.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_MINECART_ROAD_BLOCK = new Block(404, "Minecart Road Block", new BlockTexture("minecart road.png", "minecart road front.png", "minecart road front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_STONE = new Block(405, "Stone", new BlockTexture("stone.png", "stone.png", "stone.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BAMBOO_WOOD_FENCE = new Block(406, "Bamboo Wood Fence", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), RenderType.FENCE);
    public static final Block BLOCK_CROISSANT = new Block(407, "Croissant", new BlockTexture("croissant.png", "croissant.png", "croissant.png"), RenderType.SPRITE);
    public static final Block BLOCK_MINECART_ROAD_SLAB = new Block(408, "Minecart Road Slab", new BlockTexture("minecart road.png", "minecart road front.png", "minecart road front.png"), RenderType.SLAB);
    public static final Block BLOCK_PRISMARINE_BRICKS = new Block(409, "Prismarine Bricks", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_DARK_PRISMARINE_BRICKS = new Block(410, "Dark Prismarine Bricks", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_TORCH = new Block(413, "Blue Torch", new BlockTexture("blue torch.png", "blue torch.png", "blue torch.png"), (b) -> {
        b.opaque = false;
        b.solid = false;
        b.type = RenderType.TORCH;
        b.torchlightStartingValue = 15;
        b.setIcon("blue_torch.png");
    });
    public static final Block BLOCK_CEMENT_STAIRS = new Block(414, "Cement Stairs", new BlockTexture("cement.png", "cement.png", "cement.png"), RenderType.STAIRS);
    public static final Block BLOCK_CEMENT_FENCE = new Block(415, "Cement Fence", new BlockTexture("cement.png", "cement.png", "cement.png"), RenderType.FENCE);
    public static final Block BLOCK_CEMENT_SLAB = new Block(416, "Cement Slab", new BlockTexture("cement.png", "cement.png", "cement.png"), RenderType.SLAB);
    public static final Block BLOCK_OBSIDIAN_SLAB = new Block(417, "Obsidian Slab", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), RenderType.SLAB);
    public static final Block BLOCK_OBSIDIAN_FENCE = new Block(418, "Obsidian Fence", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), RenderType.FENCE);
    public static final Block BLOCK_LAPIS_LAZUL_STAIRS = new Block(419, "Lapis Lazul Stairs", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), RenderType.STAIRS);
    public static final Block BLOCK_LAPIS_LAZUL_BLOCK_SLAB = new Block(420, "Lapis Lazul Block Slab", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), RenderType.SLAB);
    public static final Block BLOCK_LAPIS_LAZUL_FENCE = new Block(421, "Lapis Lazul Fence", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), RenderType.FENCE);
    public static final Block BLOCK_IRON_STAIRS = new Block(422, "Iron Stairs", new BlockTexture("iron.png", "iron.png", "iron.png"), RenderType.STAIRS);
    public static final Block BLOCK_IRON_BLOCK_SLAB = new Block(423, "Iron Block Slab", new BlockTexture("iron.png", "iron.png", "iron.png"), RenderType.SLAB);
    public static final Block BLOCK_IRON_FENCE = new Block(424, "Iron Fence", new BlockTexture("iron.png", "iron.png", "iron.png"), RenderType.FENCE);
    public static final Block BLOCK_GOLD_STAIRS = new Block(425, "Gold Stairs", new BlockTexture("gold.png", "gold.png", "gold.png"), RenderType.STAIRS);
    public static final Block BLOCK_GOLD_BLOCK_SLAB = new Block(426, "Gold Block Slab", new BlockTexture("gold.png", "gold.png", "gold.png"), RenderType.SLAB);
    public static final Block BLOCK_GOLD_FENCE = new Block(427, "Gold Fence", new BlockTexture("gold.png", "gold.png", "gold.png"), RenderType.FENCE);
    public static final Block BLOCK_EMERALD_STAIRS = new Block(428, "Emerald Stairs", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), RenderType.STAIRS);
    public static final Block BLOCK_EMERALD_BLOCK_SLAB = new Block(429, "Emerald Block Slab", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), RenderType.SLAB);
    public static final Block BLOCK_EMERALD_FENCE = new Block(430, "Emerald Fence", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), RenderType.FENCE);
    public static final Block BLOCK_DIAMOND_STAIRS = new Block(431, "Diamond Stairs", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), RenderType.STAIRS);
    public static final Block BLOCK_DIAMOND_BLOCK_SLAB = new Block(432, "Diamond Block Slab", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), RenderType.SLAB);
    public static final Block BLOCK_DIAMOND_FENCE = new Block(433, "Diamond Fence", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), RenderType.FENCE);
    public static final Block BLOCK_CROSSWALK_PAINT = new Block(434, "Crosswalk Paint", new BlockTexture("crosswalk paint.png", "crosswalk paint.png", "crosswalk paint.png"), RenderType.FLOOR);
    public static final Block BLOCK_DARK_PRISMARINE_BRICK_STAIRS = new Block(435, "Dark Prismarine Brick Stairs", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), RenderType.STAIRS);
    public static final Block BLOCK_PRISMARINE_BRICK_SLAB = new Block(436, "Prismarine Brick Slab", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), RenderType.SLAB);
    public static final Block BLOCK_OBSIDIAN_STAIRS = new Block(437, "Obsidian Stairs", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), RenderType.STAIRS);
    public static final Block BLOCK_PRISMARINE_BRICK_FENCE = new Block(438, "Prismarine Brick Fence", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), RenderType.FENCE);
    public static final Block BLOCK_RED_SANDSTONE_PILLAR = new Block(439, "Red Sandstone Pillar", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone front.png"), RenderType.PILLAR);
    public static final Block BLOCK_STONE_BRICK_PILLAR = new Block(440, "Stone Brick Pillar", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_SANDSTONE_PILLAR = new Block(441, "Palisade Sandstone Pillar", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone front.png"), RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_STONE_PILLAR = new Block(442, "Palisade Stone Pillar", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone front.png"), RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_STONE_2_PILLAR = new Block(443, "Palisade Stone 2 Pillar", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), RenderType.PILLAR);
    public static final Block BLOCK_CRACKED_STONE_PILLAR = new Block(444, "Cracked Stone Pillar", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), RenderType.PILLAR);
    public static final Block BLOCK_STONE_WITH_VINES_PILLAR = new Block(445, "Stone with Vines Pillar", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), RenderType.PILLAR);
    public static final Block BLOCK_FLAT_DRAGON_VINES = new Block(446, "Flat Dragon Vines", new BlockTexture("flat dragon vines.png", "flat dragon vines.png", "flat dragon vines.png"), RenderType.WALL_ITEM);
    public static final Block BLOCK_DRAGON_VINES = new Block(447, "Dragon Vines", new BlockTexture("flat dragon vines.png", "flat dragon vines.png", "flat dragon vines.png"), RenderType.SPRITE);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_PILLAR = new Block(448, "Red Palisade Sandstone Pillar", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone front.png"), RenderType.PILLAR);
    public static final Block BLOCK_MARBLE_PILLAR_BLOCK = new Block(449, "Marble Pillar Block", new BlockTexture("marble.png", "marble.png", "marble front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_QUARTZ_PILLAR = new Block(450, "Quartz Pillar", new BlockTexture("quartz.png", "quartz.png", "quartz front.png"), RenderType.PILLAR);
    public static final Block BLOCK_MARBLE_PILLAR = new Block(451, "Marble Pillar", new BlockTexture("marble.png", "marble.png", "marble front.png"), RenderType.PILLAR);
    public static final Block BLOCK_FARMLAND = new Block(452, "Farmland hidden", new BlockTexture("farmland.png", "dirt.png", "dirt.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ROAD_MARKINGS = new Block(453, "Road Markings", new BlockTexture("road markings.png", "road markings.png", "road markings.png"), RenderType.FLOOR);
    public static final Block BLOCK_GRANITE_BRICK_PILLAR = new Block(454, "Granite Brick Pillar", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), RenderType.PILLAR);
    public static final Block BLOCK_GRANITE_BRICK_SLAB = new Block(455, "Granite Brick Slab", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), RenderType.SLAB);
    public static final Block BLOCK_GRANITE_BRICK_FENCE = new Block(456, "Granite Brick Fence", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), RenderType.FENCE);
    public static final Block BLOCK_CAMPFIRE = new Block(457, "Campfire", new BlockTexture("campfire.png", "campfire.png", "campfire.png"), RenderType.SPRITE);
    public static final Block BLOCK_DARK_PRISMARINE_BRICK_SLAB = new Block(458, "Dark Prismarine Brick Slab", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), RenderType.SLAB);
    public static final Block BLOCK_DARK_PRISMARINE_BRICK_FENCE = new Block(459, "Dark Prismarine Brick Fence", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), RenderType.FENCE);
    public static final Block BLOCK_ENGRAVED_SANDSTONE = new Block(460, "Engraved Sandstone", new BlockTexture("sandstone.png", "sandstone.png", "engraved sandstone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ENGRAVED_RED_SANDSTONE = new Block(461, "Engraved Red Sandstone", new BlockTexture("red sandstone.png", "red sandstone.png", "engraved red sandstone front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_MARBLE_TILE = new Block(462, "Orange Marble Tile", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CHECKERBOARD_CHISELED_MARBLE = new Block(463, "Checkerboard Chiseled Marble", new BlockTexture("checkerboard chiseled marble.png", "checkerboard chiseled marble.png", "checkerboard chiseled marble.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CHISELED_MARBLE = new Block(464, "Chiseled Marble", new BlockTexture("chiseled marble.png", "chiseled marble.png", "chiseled marble.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CHISELED_QUARTZ = new Block(465, "Chiseled Quartz", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MARBLE_TILE = new Block(466, "Marble Tile", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_MARBLE_TILE = new Block(467, "Blue Marble Tile", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_MARBLE_TILE = new Block(468, "Green Marble Tile", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_MARBLE_TILE_SLAB = new Block(469, "Orange Marble Tile Slab", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_MARBLE_TILE = new Block(470, "Gray Marble Tile", new BlockTexture("gray marble tile.png", "gray marble tile.png", "gray marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_MARBLE_TILE_STAIRS = new Block(471, "Orange Marble Tile Stairs", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_GREEN_MARBLE_TILE_SLAB = new Block(472, "Green Marble Tile Slab", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_CHECKERBOARD_CHISELED_MARBLE_STAIRS = new Block(505, "Checkerboard Chiseled Marble Stairs", new BlockTexture("checkerboard chiseled marble.png", "checkerboard chiseled marble.png", "checkerboard chiseled marble.png"), RenderType.STAIRS);
    public static final Block BLOCK_CHECKERBOARD_CHISELED_MARBLE_SLAB = new Block(506, "Checkerboard Chiseled Marble Slab", new BlockTexture("checkerboard chiseled marble.png", "checkerboard chiseled marble.png", "checkerboard chiseled marble.png"), RenderType.SLAB);
    public static final Block BLOCK_CHISELED_MARBLE_STAIRS = new Block(507, "Chiseled Marble Stairs", new BlockTexture("chiseled marble.png", "chiseled marble.png", "chiseled marble.png"), RenderType.STAIRS);
    public static final Block BLOCK_CHISELED_MARBLE_SLAB = new Block(508, "Chiseled Marble Slab", new BlockTexture("chiseled marble.png", "chiseled marble.png", "chiseled marble.png"), RenderType.SLAB);
    public static final Block BLOCK_MARBLE_TILE_STAIRS = new Block(509, "Marble Tile Stairs", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_MARBLE_TILE_SLAB = new Block(510, "Marble Tile Slab", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_MARBLE_TILE_STAIRS = new Block(511, "Gray Marble Tile Stairs", new BlockTexture("gray marble tile.png", "gray marble tile.png", "gray marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_GRAY_MARBLE_TILE_SLAB = new Block(512, "Gray Marble Tile Slab", new BlockTexture("gray marble tile.png", "gray marble tile.png", "gray marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BLUE_MARBLE_TILE_STAIRS = new Block(513, "Blue Marble Tile Stairs", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLUE_MARBLE_TILE_SLAB = new Block(514, "Blue Marble Tile Slab", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_GREEN_MARBLE_TILE_STAIRS = new Block(515, "Green Marble Tile Stairs", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_CHISELED_QUARTZ_STAIRS = new Block(516, "Chiseled Quartz Stairs", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), RenderType.STAIRS);
    public static final Block BLOCK_CHISELED_QUARTZ_SLAB = new Block(517, "Chiseled Quartz Slab", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), RenderType.SLAB);
    public static final Block BLOCK_CHISELED_QUARTZ_PILLAR = new Block(518, "Chiseled Quartz Pillar", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), RenderType.PILLAR);
    public static final Block BLOCK_GRAY_MARBLE_TILE_PILLAR = new Block(519, "Gray Marble Tile Pillar", new BlockTexture("gray marble tile.png", "gray marble tile.png", "gray marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BLUE_MARBLE_TILE_PILLAR = new Block(520, "Blue Marble Tile Pillar", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_GREEN_MARBLE_TILE_PILLAR = new Block(521, "Green Marble Tile Pillar", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_ORANGE_MARBLE_TILE_PILLAR = new Block(522, "Orange Marble Tile Pillar", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_MARBLE_TILE_PILLAR = new Block(523, "Marble Tile Pillar", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_LAMP = new Block(524, "Lamp", new BlockTexture("lamp.png", "lamp.png", "lamp.png"), (b) -> {
        b.opaque = false;
        b.solid = true;
        b.type = RenderType.LAMP;
    });
    public static final Block BLOCK_BLUE_LAMP = new Block(525, "Blue Lamp", new BlockTexture("blue lamp.png", "blue lamp.png", "blue lamp.png"), (b) -> {
        b.opaque = false;
        b.solid = true;
        b.type = RenderType.LAMP;
    });
    public static final Block BLOCK_START_BOUNDARY_BLOCK = new Block(543, "Start Boundary Block", new BlockTexture("start boundary.png", "start boundary.png", "start boundary front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTE_BLOCK = new Block(544, "Paste Block", new BlockTexture("paste.png", "paste.png", "paste.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE = new Block(545, "Yellow Chiseled Marble Tile", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MEGA_TNT = new Block(548, "Mega TNT", new BlockTexture("tnt.png", "tnt bottom.png", "mega tnt front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID, (b) -> {
        TNTUtils.setTNTEvents(b, 15, 1000);
    });
    public static final Block BLOCK_CROSSTRACK = new Block(550, "CrossTrack", new BlockTexture("crosstrack.png", "crosstrack.png", "crosstrack.png"), RenderType.FLOOR);
    public static final Block BLOCK_ADDITIVE_PASTE_BLOCK = new Block(551, "Additive Paste Block", new BlockTexture("additive paste.png", "additive paste.png", "additive paste.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTE_ROTATE_BLOCK = new Block(552, "Paste Rotate Block", new BlockTexture("paste rotate.png", "paste rotate.png", "paste rotate front.png"), RenderType.SLAB);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE = new Block(553, "Black Chiseled Marble Tile", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE = new Block(554, "Blue Chiseled Marble Tile", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE = new Block(555, "Brown Chiseled Marble Tile", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE = new Block(556, "Cyan Chiseled Marble Tile", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GRAY_CHISELED_MARBLE_TILE = new Block(557, "Gray Chiseled Marble Tile", new BlockTexture("gray chiseled marble tile.png", "gray chiseled marble tile.png", "gray chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE = new Block(558, "Green Chiseled Marble Tile", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE = new Block(559, "Pastel-Blue Chiseled Marble Tile", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE = new Block(560, "Pastel-Green Chiseled Marble Tile", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE = new Block(561, "Magenta Chiseled Marble Tile", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE = new Block(562, "Orange Chiseled Marble Tile", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE = new Block(563, "Pink Chiseled Marble Tile", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE = new Block(564, "Purple Chiseled Marble Tile", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE = new Block(565, "Burgundy Chiseled Marble Tile", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BAMBOO_BLOCK = new Block(566, "Bamboo Block", new BlockTexture("bamboo.png", "bamboo.png", "bamboo front.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE = new Block(567, "Pastel-Red Chiseled Marble Tile", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_MARBLE_TILE = new Block(568, "Yellow Marble Tile", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_MARBLE_TILE = new Block(569, "Black Marble Tile", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ICE_BLOCK = new Block(570, "Ice Block", new BlockTexture("ice.png", "ice.png", "ice.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BROWN_MARBLE_TILE = new Block(571, "Brown Marble Tile", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CYAN_MARBLE_TILE = new Block(572, "Cyan Marble Tile", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BAMBOO_WOOD = new Block(573, "Bamboo Wood", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BOTTLE = new Block(574, "Bottle", new BlockTexture("bottle.png", "bottle.png", "bottle.png"), RenderType.SPRITE);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE = new Block(575, "Pastel-Blue Marble Tile", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE = new Block(576, "Pastel-Green Marble Tile", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE = new Block(577, "Magenta Marble Tile", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CUP = new Block(578, "Cup", new BlockTexture("cup.png", "cup.png", "cup.png"), RenderType.SPRITE);
    public static final Block BLOCK_PINK_MARBLE_TILE = new Block(579, "Pink Marble Tile", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_MARBLE_TILE = new Block(580, "Purple Marble Tile", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE = new Block(581, "Burgundy Marble Tile", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE = new Block(582, "Pastel-Red Marble Tile", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WINE_GLASS = new Block(583, "Wine Glass", new BlockTexture("wine glass.png", "wine glass.png", "wine glass.png"), RenderType.SPRITE);
    public static final Block BLOCK_YELLOW_STAINED_WOOD = new Block(584, "Yellow Stained Wood", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_STAINED_WOOD = new Block(585, "Black Stained Wood", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_MARBLE_TILE_STAIRS = new Block(586, "Yellow Marble Tile Stairs", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_MARBLE_TILE_SLAB = new Block(587, "Yellow Marble Tile Slab", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_YELLOW_MARBLE_TILE_PILLAR = new Block(588, "Yellow Marble Tile Pillar", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BLACK_MARBLE_TILE_STAIRS = new Block(589, "Black Marble Tile Stairs", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLACK_MARBLE_TILE_SLAB = new Block(590, "Black Marble Tile Slab", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BLACK_MARBLE_TILE_PILLAR = new Block(591, "Black Marble Tile Pillar", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BREAD = new Block(592, "Bread", new BlockTexture("bread.png", "bread.png", "bread.png"), RenderType.SPRITE);
    public static final Block BLOCK_CYAN_STAINED_WOOD = new Block(593, "Cyan Stained Wood", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE_PILLAR = new Block(594, "Pink Chiseled Marble Tile Pillar", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BROWN_MARBLE_TILE_STAIRS = new Block(595, "Brown Marble Tile Stairs", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BROWN_MARBLE_TILE_SLAB = new Block(596, "Brown Marble Tile Slab", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BROWN_MARBLE_TILE_PILLAR = new Block(597, "Brown Marble Tile Pillar", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_CYAN_MARBLE_TILE_STAIRS = new Block(598, "Cyan Marble Tile Stairs", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_CYAN_MARBLE_TILE_SLAB = new Block(599, "Cyan Marble Tile Slab", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_CYAN_MARBLE_TILE_PILLAR = new Block(600, "Cyan Marble Tile Pillar", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE_STAIRS = new Block(601, "Purple Chiseled Marble Tile Stairs", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE_SLAB = new Block(602, "Purple Chiseled Marble Tile Slab", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE_PILLAR = new Block(603, "Purple Chiseled Marble Tile Pillar", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_STAIRS = new Block(604, "Burgundy Chiseled Marble Tile Stairs", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_SLAB = new Block(605, "Burgundy Chiseled Marble Tile Slab", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_PILLAR = new Block(606, "Burgundy Chiseled Marble Tile Pillar", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE_STAIRS = new Block(607, "Pastel-Blue Marble Tile Stairs", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE_SLAB = new Block(608, "Pastel-Blue Marble Tile Slab", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE_PILLAR = new Block(609, "Pastel-Blue Marble Tile Pillar", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE_STAIRS = new Block(610, "Pastel-Green Marble Tile Stairs", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE_SLAB = new Block(611, "Pastel-Green Marble Tile Slab", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE_PILLAR = new Block(612, "Pastel-Green Marble Tile Pillar", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE_STAIRS = new Block(613, "Magenta Marble Tile Stairs", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE_SLAB = new Block(614, "Magenta Marble Tile Slab", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE_PILLAR = new Block(615, "Magenta Marble Tile Pillar", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_STAIRS = new Block(616, "Pastel-Red Chiseled Marble Tile Stairs", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_SLAB = new Block(617, "Pastel-Red Chiseled Marble Tile Slab", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_PILLAR = new Block(618, "Pastel-Red Chiseled Marble Tile Pillar", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PINK_MARBLE_TILE_STAIRS = new Block(619, "Pink Marble Tile Stairs", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PINK_MARBLE_TILE_SLAB = new Block(620, "Pink Marble Tile Slab", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PINK_MARBLE_TILE_PILLAR = new Block(621, "Pink Marble Tile Pillar", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PURPLE_MARBLE_TILE_STAIRS = new Block(622, "Purple Marble Tile Stairs", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_MARBLE_TILE_SLAB = new Block(623, "Purple Marble Tile Slab", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PURPLE_MARBLE_TILE_PILLAR = new Block(624, "Purple Marble Tile Pillar", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE_STAIRS = new Block(625, "Burgundy Marble Tile Stairs", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE_SLAB = new Block(626, "Burgundy Marble Tile Slab", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE_PILLAR = new Block(627, "Burgundy Marble Tile Pillar", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE_STAIRS = new Block(628, "Pastel-Red Marble Tile Stairs", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE_SLAB = new Block(629, "Pastel-Red Marble Tile Slab", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE_PILLAR = new Block(630, "Pastel-Red Marble Tile Pillar", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE_STAIRS = new Block(631, "Yellow Chiseled Marble Tile Stairs", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE_SLAB = new Block(632, "Yellow Chiseled Marble Tile Slab", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE_PILLAR = new Block(633, "Yellow Chiseled Marble Tile Pillar", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE_STAIRS = new Block(634, "Black Chiseled Marble Tile Stairs", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE_SLAB = new Block(635, "Black Chiseled Marble Tile Slab", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE_PILLAR = new Block(636, "Black Chiseled Marble Tile Pillar", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE_STAIRS = new Block(637, "Blue Chiseled Marble Tile Stairs", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE_SLAB = new Block(638, "Blue Chiseled Marble Tile Slab", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE_PILLAR = new Block(639, "Blue Chiseled Marble Tile Pillar", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE_STAIRS = new Block(640, "Brown Chiseled Marble Tile Stairs", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE_SLAB = new Block(641, "Brown Chiseled Marble Tile Slab", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE_PILLAR = new Block(642, "Brown Chiseled Marble Tile Pillar", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE_STAIRS = new Block(643, "Cyan Chiseled Marble Tile Stairs", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE_SLAB = new Block(644, "Cyan Chiseled Marble Tile Slab", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE_PILLAR = new Block(645, "Cyan Chiseled Marble Tile Pillar", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_GRAY_CHISELED_MARBLE_TILE_STAIRS = new Block(646, "Gray Chiseled Marble Tile Stairs", new BlockTexture("gray chiseled marble tile.png", "gray chiseled marble tile.png", "gray chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_GRAY_CHISELED_MARBLE_TILE_SLAB = new Block(647, "Gray Chiseled Marble Tile Slab", new BlockTexture("gray chiseled marble tile.png", "gray chiseled marble tile.png", "gray chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_CHISELED_MARBLE_TILE_PILLAR = new Block(648, "Gray Chiseled Marble Tile Pillar", new BlockTexture("gray chiseled marble tile.png", "gray chiseled marble tile.png", "gray chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE_STAIRS = new Block(649, "Green Chiseled Marble Tile Stairs", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE_SLAB = new Block(650, "Green Chiseled Marble Tile Slab", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE_PILLAR = new Block(651, "Green Chiseled Marble Tile Pillar", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_STAIRS = new Block(652, "Pastel-Blue Chiseled Marble Tile Stairs", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_SLAB = new Block(653, "Pastel-Blue Chiseled Marble Tile Slab", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_PILLAR = new Block(654, "Pastel-Blue Chiseled Marble Tile Pillar", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_STAIRS = new Block(655, "Pastel-Green Chiseled Marble Tile Stairs", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_SLAB = new Block(656, "Pastel-Green Chiseled Marble Tile Slab", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_PILLAR = new Block(657, "Pastel-Green Chiseled Marble Tile Pillar", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE_STAIRS = new Block(658, "Magenta Chiseled Marble Tile Stairs", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE_SLAB = new Block(659, "Magenta Chiseled Marble Tile Slab", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE_PILLAR = new Block(660, "Magenta Chiseled Marble Tile Pillar", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE_STAIRS = new Block(661, "Orange Chiseled Marble Tile Stairs", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE_SLAB = new Block(662, "Orange Chiseled Marble Tile Slab", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE_PILLAR = new Block(663, "Orange Chiseled Marble Tile Pillar", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), RenderType.PILLAR);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE_STAIRS = new Block(664, "Pink Chiseled Marble Tile Stairs", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE_SLAB = new Block(665, "Pink Chiseled Marble Tile Slab", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_STAINED_WOOD = new Block(666, "Gray Stained Wood", new BlockTexture("gray stained wood.png", "gray stained wood.png", "gray stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_STAINED_WOOD = new Block(667, "Green Stained Wood", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD = new Block(668, "Light Blue Stained Wood", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIME_STAINED_WOOD = new Block(669, "Lime Stained Wood", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD = new Block(670, "Magenta Stained Wood", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_STAINED_WOOD = new Block(671, "Orange Stained Wood", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_STAINED_WOOD = new Block(672, "Pink Stained Wood", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_STAINED_WOOD = new Block(673, "Purple Stained Wood", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WHITE_SPACE_TILE_STAIRS = new Block(674, "White Space Tile Stairs", new BlockTexture("white space tile.png", "white space tile.png", "white space tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_WHITE_STAINED_WOOD = new Block(675, "White Stained Wood", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ENGRAVED_SANDSTONE_2 = new Block(676, "Engraved Sandstone 2", new BlockTexture("sandstone.png", "sandstone.png", "engraved sandstone 2 front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ENGRAVED_RED_SANDSTONE_2 = new Block(677, "Engraved Red Sandstone 2", new BlockTexture("red sandstone.png", "red sandstone.png", "engraved red sandstone 2 front.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PEONY_BUSH = new Block(678, "Peony Bush", new BlockTexture("peony bush.png", "peony bush.png", "peony bush.png"), RenderType.SPRITE);
    public static final Block BLOCK_ICE_BLOCK_STAIRS = new Block(679, "Ice Block Stairs", new BlockTexture("ice.png", "ice.png", "ice.png"), RenderType.STAIRS);
    public static final Block BLOCK_ICE_BLOCK_SLAB = new Block(680, "Ice Block Slab", new BlockTexture("ice.png", "ice.png", "ice.png"), RenderType.SLAB);
    public static final Block BLOCK_SOLAR_PANEL = new Block(681, "Solar Panel", new BlockTexture("solar l.png", "solar l front.png", "solar l front.png"), RenderType.SLAB);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD_STAIRS = new Block(682, "Mosaic Bamboo Wood Stairs", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD_SLAB = new Block(683, "Mosaic Bamboo Wood Slab", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), RenderType.SLAB);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD_FENCE = new Block(684, "Mosaic Bamboo Wood Fence", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), RenderType.FENCE);
    public static final Block BLOCK_YELLOW_STAINED_WOOD_STAIRS = new Block(685, "Yellow Stained Wood Stairs", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_STAINED_WOOD_SLAB = new Block(686, "Yellow Stained Wood Slab", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_YELLOW_STAINED_WOOD_FENCE = new Block(687, "Yellow Stained Wood Fence", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_BLACK_STAINED_WOOD_STAIRS = new Block(688, "Black Stained Wood Stairs", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLACK_STAINED_WOOD_SLAB = new Block(689, "Black Stained Wood Slab", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_BLACK_STAINED_WOOD_FENCE = new Block(690, "Black Stained Wood Fence", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_BLUE_STAINED_WOOD_STAIRS = new Block(691, "Blue Stained Wood Stairs", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_BLUE_STAINED_WOOD_SLAB = new Block(692, "Blue Stained Wood Slab", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_BLUE_STAINED_WOOD_FENCE = new Block(693, "Blue Stained Wood Fence", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_CYAN_STAINED_WOOD_STAIRS = new Block(694, "Cyan Stained Wood Stairs", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_CYAN_STAINED_WOOD_SLAB = new Block(695, "Cyan Stained Wood Slab", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_CYAN_STAINED_WOOD_FENCE = new Block(696, "Cyan Stained Wood Fence", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_GRAY_STAINED_WOOD_STAIRS = new Block(697, "Gray Stained Wood Stairs", new BlockTexture("gray stained wood.png", "gray stained wood.png", "gray stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_GRAY_STAINED_WOOD_SLAB = new Block(698, "Gray Stained Wood Slab", new BlockTexture("gray stained wood.png", "gray stained wood.png", "gray stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_STAINED_WOOD_FENCE = new Block(699, "Gray Stained Wood Fence", new BlockTexture("gray stained wood.png", "gray stained wood.png", "gray stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_GREEN_STAINED_WOOD_STAIRS = new Block(700, "Green Stained Wood Stairs", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_GREEN_STAINED_WOOD_SLAB = new Block(701, "Green Stained Wood Slab", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_GREEN_STAINED_WOOD_FENCE = new Block(702, "Green Stained Wood Fence", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD_STAIRS = new Block(703, "Light Blue Stained Wood Stairs", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD_SLAB = new Block(704, "Light Blue Stained Wood Slab", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD_FENCE = new Block(705, "Light Blue Stained Wood Fence", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_LIME_STAINED_WOOD_STAIRS = new Block(706, "Lime Stained Wood Stairs", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_LIME_STAINED_WOOD_SLAB = new Block(707, "Lime Stained Wood Slab", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_LIME_STAINED_WOOD_FENCE = new Block(708, "Lime Stained Wood Fence", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD_STAIRS = new Block(709, "Magenta Stained Wood Stairs", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD_SLAB = new Block(710, "Magenta Stained Wood Slab", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD_FENCE = new Block(711, "Magenta Stained Wood Fence", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_ORANGE_STAINED_WOOD_STAIRS = new Block(712, "Orange Stained Wood Stairs", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_STAINED_WOOD_SLAB = new Block(713, "Orange Stained Wood Slab", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_ORANGE_STAINED_WOOD_FENCE = new Block(714, "Orange Stained Wood Fence", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_PINK_STAINED_WOOD_STAIRS = new Block(715, "Pink Stained Wood Stairs", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_PINK_STAINED_WOOD_SLAB = new Block(716, "Pink Stained Wood Slab", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_PINK_STAINED_WOOD_FENCE = new Block(717, "Pink Stained Wood Fence", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_PURPLE_STAINED_WOOD_STAIRS = new Block(718, "Purple Stained Wood Stairs", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_STAINED_WOOD_SLAB = new Block(719, "Purple Stained Wood Slab", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_PURPLE_STAINED_WOOD_FENCE = new Block(720, "Purple Stained Wood Fence", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_RED_STAINED_WOOD_STAIRS = new Block(721, "Red Stained Wood Stairs", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_RED_STAINED_WOOD_SLAB = new Block(722, "Red Stained Wood Slab", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_RED_STAINED_WOOD_FENCE = new Block(723, "Red Stained Wood Fence", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_WHITE_STAINED_WOOD_STAIRS = new Block(724, "White Stained Wood Stairs", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), RenderType.STAIRS);
    public static final Block BLOCK_WHITE_STAINED_WOOD_SLAB = new Block(725, "White Stained Wood Slab", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), RenderType.SLAB);
    public static final Block BLOCK_WHITE_STAINED_WOOD_FENCE = new Block(726, "White Stained Wood Fence", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), RenderType.FENCE);
    public static final Block BLOCK_WHITE_SPACE_TILE = new Block(727, "White Space Tile", new BlockTexture("white space tile.png", "white space tile.png", "white space tile.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_GRAY_SPACE_TILE = new Block(728, "Gray Space Tile", new BlockTexture("gray space tile.png", "gray space tile.png", "gray space tile.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_WHITE_SPACE_TILE_SLAB = new Block(729, "White Space Tile Slab", new BlockTexture("white space tile.png", "white space tile.png", "white space tile.png"), RenderType.SLAB);
    public static final Block BLOCK_GRAY_SPACE_TILE_STAIRS = new Block(730, "Gray Space Tile Stairs", new BlockTexture("gray space tile.png", "gray space tile.png", "gray space tile.png"), RenderType.STAIRS);
    public static final Block BLOCK_GRAY_SPACE_TILE_SLAB = new Block(731, "Gray Space Tile Slab", new BlockTexture("gray space tile.png", "gray space tile.png", "gray space tile.png"), RenderType.SLAB);
    public static final Block BLOCK_SPRUCE_LOG = new Block(732, "Spruce Log", new BlockTexture("spruce log.png", "spruce log.png", "spruce log front.png"), RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_SPRUCE_LEAVES = new Block(733, "Spruce Leaves", new BlockTexture("spruce leaves.png", "spruce leaves.png", "spruce leaves.png"), BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTE_VERTEX_BLOCK = new Block(735, "Paste Vertex Block", new BlockTexture("paste rotate.png", "paste rotate.png", "paste vertex front.png"), RenderType.SLAB);
    public static final Block BLOCK_MERGE_TRACK = new Block(736, "Merge Track", new BlockTexture("merge track.png", "merge track.png", "merge track.png"), RenderType.FLOOR);

     */

// </editor-fold>
}
