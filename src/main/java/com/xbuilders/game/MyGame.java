/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.game.items.blocks.entities.Fox;
import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.json.JsonManager;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.game.items.blocks.RenderType;
import com.xbuilders.game.items.blocks.type.*;
import com.xbuilders.game.terrain.BasicTerrain;
import com.xbuilders.game.terrain.DevTerrain;
import com.xbuilders.game.terrain.TestTerrain;
import com.xbuilders.game.terrain.defaultTerrain.ComplexTerrain;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.utils.texture.Texture;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class MyGame extends Game {

    static class GameInfo {

        public Item[] playerBackpack;

        public GameInfo() {
            playerBackpack = new Item[22];
        }
    }

    public MyGame() {
        json = new JsonManager();
    }

    JsonManager json;
    GameInfo gameInfo;
    Item[] itemList;


    Inventory inventory;
    Hotbar hotbar;

    @Override
    public Item getSelectedItem() {
        return gameInfo.playerBackpack[hotbar.selectedItem];
    }

    NKWindow window;

    boolean showInventory;

    @Override
    public void uiDraw(MemoryStack stack) {
        if (showInventory) {
            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            inventory.draw(stack);
        } else {
            hotbar.draw(stack);
        }
    }

    @Override
    public void uiInit(NkContext ctx, NKWindow window, UIResources uires, GameUI gameUI) {
        this.window = window;
        try {
            hotbar = new Hotbar(ctx, window, uires);
            inventory = new Inventory(ctx, window, uires, itemList);
        } catch (IOException ex) {
            Logger.getLogger(MyGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (!showInventory) {
            hotbar.mouseScrollEvent(scroll, xoffset, yoffset);
        }
    }

    @Override
    public void uiKeyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_I -> showInventory = !showInventory;
            }
        }
        if (!showInventory) {
            hotbar.keyEvent(key, scancode, action, mods);
        }
    }

    @Override
    public void uiMouseButtonEvent(int button, int action, int mods) {
    }

    @Override
    public void initialize() throws Exception {
        System.out.println("Initializing items...");
        Block[] blockList = new Block[]{
                BLOCK_BEDROCK, BLOCK_BIRCH_LOG, BLOCK_BIRCH_LEAVES, BLOCK_BRICK, BLOCK_VINES, BLOCK_DIRT, BLOCK_PANSIES, BLOCK_GLASS, BLOCK_GRASS, BLOCK_GRAVEL, BLOCK_OAK_LOG, BLOCK_OAK_LEAVES, BLOCK_SEA_LIGHT, BLOCK_PLANT_GRASS, BLOCK_BAMBOO, BLOCK_SAND, BLOCK_SANDSTONE, BLOCK_ANDESITE, BLOCK_STONE_BRICK, BLOCK_TORCH, BLOCK_WATER, BLOCK_WOOL, BLOCK_SNOW, BLOCK_BOOKSHELF, BLOCK_LAVA, BLOCK_TALL_DRY_GRASS_TOP, BLOCK_CRACKED_STONE, BLOCK_STONE_WITH_VINES, BLOCK_TNT_ACTIVE, BLOCK_JUNGLE_PLANKS, BLOCK_JUNGLE_PLANKS_SLAB, BLOCK_JUNGLE_PLANKS_STAIRS, BLOCK_HONEYCOMB_BLOCK, BLOCK_MOSAIC_BAMBOO_WOOD, BLOCK_MUSIC_BOX, BLOCK_CAKE, BLOCK_JUNGLE_SAPLING, BLOCK_OBSIDIAN, BLOCK_BURGUNDY_BRICK, BLOCK_JUNGLE_FENCE, BLOCK_RED_FLOWER, BLOCK_TALL_DRY_GRASS, BLOCK_RED_CANDLE, BLOCK_YELLOW_FLOWER, BLOCK_COAL_ORE, BLOCK_COAL_BLOCK, BLOCK_JUNGLE_LEAVES, BLOCK_JUNGLE_LOG, BLOCK_TALL_GRASS_TOP,
                BLOCK_CONTROL_PANEL, BLOCK_BEEHIVE, BLOCK_DIORITE, BLOCK_POLISHED_DIORITE, BLOCK_EDISON_LIGHT, BLOCK_POLISHED_ANDESITE, BLOCK_SPRUCE_PLANKS, BLOCK_AZURE_BLUET, BLOCK_DANDELION, BLOCK_BLUE_ORCHID, BLOCK_FERN, BLOCK_GRANITE_BRICK, BLOCK_ACACIA_PLANKS, BLOCK_AMETHYST_CRYSTAL, BLOCK_CLAY, BLOCK_YELLOW_CONCRETE, BLOCK_YELLOW_GLAZED_TERACOTTA, BLOCK_BLACK_CONCRETE, BLOCK_BLACK_GLAZED_TERACOTTA, BLOCK_BLUE_CONCRETE, BLOCK_BLUE_GLAZED_TERACOTTA, BLOCK_BROWN_CONCRETE, BLOCK_BROWN_GLAZED_TERACOTTA, BLOCK_CYAN_CONCRETE, BLOCK_CYAN_GLAZED_TERACOTTA, BLOCK_GREY_CONCRETE, BLOCK_GREY_GLAZED_TERACOTTA, BLOCK_GREEN_CONCRETE, BLOCK_GREEN_GLAZED_TERACOTTA, BLOCK_LIGHT_BLUE_CONCRETE, BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA, BLOCK_LIGHT_GREY_CONCRETE, BLOCK_LIGHT_GREY_GLAZED_TERACOTTA, BLOCK_LIME_CONCRETE, BLOCK_LIME_GLAZED_TERACOTTA, BLOCK_MAGENTA_CONCRETE, BLOCK_MAGENTA_GLAZED_TERACOTTA, BLOCK_ORANGE_CONCRETE, BLOCK_ORANGE_GLAZED_TERACOTTA, BLOCK_PINK_CONCRETE, BLOCK_PINK_GLAZED_TERACOTTA, BLOCK_PURPLE_CONCRETE, BLOCK_PURPLE_GLAZED_TERACOTTA, BLOCK_RED_CONCRETE, BLOCK_RED_GLAZED_TERACOTTA, BLOCK_WHITE_CONCRETE, BLOCK_WHITE_GLAZED_TERACOTTA, BLOCK_BRAIN_CORAL, BLOCK_DIAMOND_ORE, BLOCK_QUARTZ_PILLAR_BLOCK,
                BLOCK_COBBLESTONE, BLOCK_WOOL_TURQUOISE, BLOCK_WOOL_ORANGE, BLOCK_RED_SAND, BLOCK_WATERMELON, BLOCK_PHANTOM_STONE, BLOCK_PHANTOM_STONE_BRICK, BLOCK_CACTUS, BLOCK_PALISADE_STONE, BLOCK_RED_SANDSTONE, BLOCK_FIRE_CORAL_BLOCK, BLOCK_CANDLE, BLOCK_PALISADE_STONE_2, BLOCK_FIRE_CORAL, BLOCK_TALL_GRASS, BLOCK_HORN_CORAL_BLOCK, BLOCK_GOLD_BLOCK, BLOCK_HORN_CORAL_FAN, BLOCK_TNT, BLOCK_WHEAT, BLOCK_CARROTS__PLANT, BLOCK_MINI_CACTUS, BLOCK_MUSHROOM, BLOCK_MUSHROOM_2, BLOCK_ROSES, BLOCK_WOOL_PURPLE, BLOCK_BIRCH_PLANKS, BLOCK_RED_STAINED_WOOD, BLOCK_OAK_PLANKS, BLOCK_WOOL_RED, BLOCK_WOOL_PINK, BLOCK_WOOL_YELLOW, BLOCK_WOOL_BROWN, BLOCK_TUBE_CORAL_BLOCK, BLOCK_TUBE_CORAL, BLOCK_TUBE_CORAL_FAN, BLOCK_WOOL_DEEP_BLUE, BLOCK_WOOL_SKY_BLUE, BLOCK_WOOL_DARK_GREEN, BLOCK_WOOL_GREEN, BLOCK_WOOL_GREY, BLOCK_BRAIN_CORAL_BLOCK, BLOCK_DIAMOND_BLOCK, BLOCK_IRON_BLOCK, BLOCK_POTATOES_PLANT, BLOCK_HONEYCOMB_BLOCK_STAIRS, BLOCK_OAK_FENCE, BLOCK_BIRCH_FENCE, BLOCK_BUBBLE_CORAL_BLOCK, BLOCK_BUBBLE_CORAL,
                BLOCK_BUBBLE_CORAL_FAN, BLOCK_JUNGLE_GRASS, BLOCK_LILY_PAD, BLOCK_TRACK, BLOCK_WOOL_MAGENTA, BLOCK_WOOL_BLACK, BLOCK_GRANITE_BRICK_STAIRS, BLOCK_CEMENT, BLOCK_OAK_SAPLING, BLOCK_BIRCH_SAPLING, BLOCK_WHEAT_SEEDS, BLOCK_CARROT_SEEDS, BLOCK_POTATO_SEEDS, BLOCK_A1, BLOCK_A2, BLOCK_B1, BLOCK_B2, BLOCK_B3, BLOCK_B4, BLOCK_B5, BLOCK_B6, BLOCK_ELECTRIC_LIGHT, BLOCK_RED_PALISADE_SANDSTONE, BLOCK_PHANTOM_SANDSTONE, BLOCK_PALISADE_SANDSTONE, BLOCK_GLOW_ROCK, BLOCK_BLACK_STAINED_GLASS, BLOCK_BLUE_STAINED_GLASS, BLOCK_BROWN_STAINED_GLASS, BLOCK_CYAN_STAINED_GLASS, BLOCK_GREY_STAINED_GLASS, BLOCK_GREEN_STAINED_GLASS, BLOCK_LIGHT_BLUE_STAINED_GLASS, BLOCK_LIGHT_GREY_STAINED_GLASS, BLOCK_LIME_STAINED_GLASS, BLOCK_MAGENTA_STAINED_GLASS, BLOCK_ORANGE_STAINED_GLASS, BLOCK_PINK_STAINED_GLASS, BLOCK_PURPLE_STAINED_GLASS, BLOCK_RED_STAINED_GLASS, BLOCK_WHITE_STAINED_GLASS, BLOCK_YELLOW_STAINED_GLASS, BLOCK_DARK_OAK_FENCE, BLOCK_BIRCH_PLANKS_STAIRS, BLOCK_OAK_PLANKS_STAIRS, BLOCK_DARK_OAK_PLANKS_STAIRS, BLOCK_HONEYCOMB_BLOCK_SLAB, BLOCK_JUNGLE_GRASS_PLANT, BLOCK_PRISMARINE_BRICK_STAIRS, BLOCK_SANDSTONE_STAIRS,
                BLOCK_CAVE_VINES_FLAT, BLOCK_POLISHED_DIORITE_STAIRS, BLOCK_BIRCH_PLANKS_SLAB, BLOCK_OAK_PLANKS_SLAB, BLOCK_DARK_OAK_PLANKS_SLAB, BLOCK_BAMBOO_WOOD_STAIRS, BLOCK_STONE_BRICK_SLAB, BLOCK_RED_SANDSTONE_SLAB, BLOCK_SANDSTONE_SLAB, BLOCK_DRY_GRASS, BLOCK_POLISHED_ANDESITE_SLAB, BLOCK_IRON_LADDER, BLOCK_RED_VINES_FLAT, BLOCK_YELLOW_CANDLE, BLOCK_CAVE_VINES, BLOCK_GREEN_CANDLE, BLOCK_GRANITE, BLOCK_BLUE_CANDLE, BLOCK_RED_VINES, BLOCK_FLAT_VINES, BLOCK_DARK_OAK_LADDER, BLOCK_DRY_GRASS_PLANT, BLOCK_ACACIA_LEAVES, BLOCK_ACACIA_LOG, BLOCK_FIRE_CORAL_FAN, BLOCK_LAPIS_LAZUL_ORE, BLOCK_LAPIS_LAZUL_BLOCK, BLOCK_ACACIA_SAPLING, BLOCK_IRON_ORE, BLOCK_BRAIN_CORAL_FAN, BLOCK_WHITE_ROSE, BLOCK_GOLD_ORE, BLOCK_HORN_CORAL, BLOCK_RED_ROSE, BLOCK_EMERALD_ORE, BLOCK_EMERALD_BLOCK, BLOCK_BLACK_EYE_SUSAN, BLOCK_ORANGE_TULIP, BLOCK_DEAD_BUSH, BLOCK_HAY_BAIL, BLOCK_POLISHED_ANDESITE_STAIRS, BLOCK_POLISHED_DIORITE_SLAB, BLOCK_CURVED_TRACK, BLOCK_BEETS, BLOCK_BEETROOT_SEEDS, BLOCK_BAMBOO_LADDER, BLOCK_ACACIA_FENCE, BLOCK_ACACIA_PLANKS_STAIRS, BLOCK_ACACIA_PLANKS_SLAB, BLOCK_RAISED_TRACK,
                BLOCK_SEA_GRASS, BLOCK_BLUE_STAINED_WOOD, BLOCK_RUBY_CRYSTAL, BLOCK_JADE_CRYSTAL, BLOCK_AQUAMARINE_CRYSTAL, BLOCK_BAMBOO_WOOD_SLAB, BLOCK_RED_SANDSTONE_STAIRS, BLOCK_STONE_BRICK_STAIRS, BLOCK_STONE_BRICK_FENCE, BLOCK_BRICK_STAIRS, BLOCK_BRICK_SLAB, BLOCK_SNOW_BLOCK, BLOCK_COBBLESTONE_STAIRS, BLOCK_COBBLESTONE_SLAB, BLOCK_PALISADE_STONE_STAIRS, BLOCK_PALISADE_STONE_SLAB, BLOCK_PALISADE_STONE_FENCE, BLOCK_PALISADE_STONE_2_STAIRS, BLOCK_PALISADE_STONE_2_SLAB, BLOCK_PALISADE_STONE_2_FENCE, BLOCK_POLISHED_DIORITE_FENCE, BLOCK_POLISHED_ANDESITE_FENCE, BLOCK_CRACKED_STONE_STAIRS, BLOCK_CRACKED_STONE_SLAB, BLOCK_CRACKED_STONE_FENCE, BLOCK_STONE_WITH_VINES_STAIRS, BLOCK_STONE_WITH_VINES_SLAB, BLOCK_STONE_WITH_VINES_FENCE, BLOCK_BURGUNDY_BRICK_STAIRS, BLOCK_BURGUNDY_BRICK_SLAB, BLOCK_BURGUNDY_BRICK_FENCE, BLOCK_SWITCH_JUNCTION, BLOCK_TRACK_STOP, BLOCK_RED_PALISADE_SANDSTONE_STAIRS, BLOCK_RED_PALISADE_SANDSTONE_SLAB, BLOCK_RED_PALISADE_SANDSTONE_FENCE, BLOCK_PALISADE_SANDSTONE_STAIRS, BLOCK_PALISADE_SANDSTONE_SLAB, BLOCK_PALISADE_SANDSTONE_FENCE, BLOCK_WOOL_STAIRS, BLOCK_WOOL_SLAB, BLOCK_WOOL_GREY_STAIRS, BLOCK_WOOL_GREY_SLAB, BLOCK_WOOL_RED_STAIRS, BLOCK_WOOL_RED_SLAB, BLOCK_WOOL_PINK_STAIRS, BLOCK_WOOL_PINK_SLAB, BLOCK_WOOL_ORANGE_STAIRS, BLOCK_WOOL_ORANGE_SLAB, BLOCK_WOOL_YELLOW_STAIRS,
                BLOCK_WOOL_YELLOW_SLAB, BLOCK_WOOL_GREEN_STAIRS, BLOCK_WOOL_GREEN_SLAB, BLOCK_WOOL_DARK_GREEN_STAIRS, BLOCK_WOOL_DARK_GREEN_SLAB, BLOCK_WOOL_TURQUOISE_STAIRS, BLOCK_WOOL_TURQUOISE_SLAB, BLOCK_WOOL_DEEP_BLUE_STAIRS, BLOCK_WOOL_DEEP_BLUE_SLAB, BLOCK_WOOL_SKY_BLUE_STAIRS, BLOCK_WOOL_SKY_BLUE_SLAB, BLOCK_WOOL_BROWN_STAIRS, BLOCK_WOOL_BROWN_SLAB, BLOCK_WOOL_PURPLE_STAIRS, BLOCK_WOOL_PURPLE_SLAB, BLOCK_WOOL_MAGENTA_STAIRS, BLOCK_WOOL_MAGENTA_SLAB, BLOCK_WOOL_BLACK_STAIRS, BLOCK_WOOL_BLACK_SLAB, BLOCK_YELLOW_CONCRETE_STAIRS, BLOCK_YELLOW_CONCRETE_SLAB, BLOCK_YELLOW_CONCRETE_FENCE, BLOCK_BLACK_CONCRETE_STAIRS, BLOCK_BLACK_CONCRETE_SLAB, BLOCK_BLACK_CONCRETE_FENCE, BLOCK_BLUE_CONCRETE_STAIRS, BLOCK_BLUE_CONCRETE_SLAB, BLOCK_BLUE_CONCRETE_FENCE, BLOCK_BROWN_CONCRETE_STAIRS, BLOCK_BROWN_CONCRETE_SLAB, BLOCK_BROWN_CONCRETE_FENCE, BLOCK_CYAN_CONCRETE_STAIRS, BLOCK_CYAN_CONCRETE_SLAB, BLOCK_CYAN_CONCRETE_FENCE, BLOCK_GREY_CONCRETE_STAIRS, BLOCK_GREY_CONCRETE_SLAB, BLOCK_GREY_CONCRETE_FENCE, BLOCK_GREEN_CONCRETE_STAIRS, BLOCK_GREEN_CONCRETE_SLAB, BLOCK_GREEN_CONCRETE_FENCE, BLOCK_LIGHT_BLUE_CONCRETE_STAIRS, BLOCK_LIGHT_BLUE_CONCRETE_SLAB, BLOCK_LIGHT_BLUE_CONCRETE_FENCE, BLOCK_LIGHT_GREY_CONCRETE_STAIRS, BLOCK_LIGHT_GREY_CONCRETE_SLAB, BLOCK_LIGHT_GREY_CONCRETE_FENCE, BLOCK_LIME_CONCRETE_STAIRS, BLOCK_LIME_CONCRETE_SLAB, BLOCK_LIME_CONCRETE_FENCE, BLOCK_MAGENTA_CONCRETE_STAIRS,
                BLOCK_MAGENTA_CONCRETE_SLAB, BLOCK_MAGENTA_CONCRETE_FENCE, BLOCK_ORANGE_CONCRETE_STAIRS, BLOCK_ORANGE_CONCRETE_SLAB, BLOCK_ORANGE_CONCRETE_FENCE, BLOCK_PINK_CONCRETE_STAIRS, BLOCK_PINK_CONCRETE_SLAB, BLOCK_PINK_CONCRETE_FENCE, BLOCK_PURPLE_CONCRETE_STAIRS, BLOCK_PURPLE_CONCRETE_SLAB, BLOCK_PURPLE_CONCRETE_FENCE, BLOCK_RED_CONCRETE_STAIRS, BLOCK_RED_CONCRETE_SLAB, BLOCK_RED_CONCRETE_FENCE, BLOCK_WHITE_CONCRETE_STAIRS, BLOCK_WHITE_CONCRETE_SLAB, BLOCK_WHITE_CONCRETE_FENCE, BLOCK_YELLOW_GLAZED_TERACOTTA_STAIRS, BLOCK_YELLOW_GLAZED_TERACOTTA_SLAB, BLOCK_BLACK_GLAZED_TERACOTTA_STAIRS, BLOCK_BLACK_GLAZED_TERACOTTA_SLAB, BLOCK_BLUE_GLAZED_TERACOTTA_STAIRS, BLOCK_BLUE_GLAZED_TERACOTTA_SLAB, BLOCK_BROWN_GLAZED_TERACOTTA_STAIRS, BLOCK_BROWN_GLAZED_TERACOTTA_SLAB, BLOCK_CYAN_GLAZED_TERACOTTA_STAIRS, BLOCK_CYAN_GLAZED_TERACOTTA_SLAB, BLOCK_GREY_GLAZED_TERACOTTA_STAIRS, BLOCK_GREY_GLAZED_TERACOTTA_SLAB, BLOCK_GREEN_GLAZED_TERACOTTA_STAIRS, BLOCK_GREEN_GLAZED_TERACOTTA_SLAB, BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_STAIRS, BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_SLAB, BLOCK_LIGHT_GREY_GLAZED_TERACOTTA_STAIRS, BLOCK_LIGHT_GREY_GLAZED_TERACOTTA_SLAB, BLOCK_LIME_GLAZED_TERACOTTA_STAIRS, BLOCK_LIME_GLAZED_TERACOTTA_SLAB, BLOCK_MAGENTA_GLAZED_TERACOTTA_STAIRS, BLOCK_MAGENTA_GLAZED_TERACOTTA_SLAB, BLOCK_ORANGE_GLAZED_TERACOTTA_STAIRS, BLOCK_ORANGE_GLAZED_TERACOTTA_SLAB, BLOCK_PINK_GLAZED_TERACOTTA_STAIRS, BLOCK_PINK_GLAZED_TERACOTTA_SLAB, BLOCK_PURPLE_GLAZED_TERACOTTA_STAIRS, BLOCK_PURPLE_GLAZED_TERACOTTA_SLAB, BLOCK_RED_GLAZED_TERACOTTA_STAIRS, BLOCK_RED_GLAZED_TERACOTTA_SLAB, BLOCK_WHITE_GLAZED_TERACOTTA_STAIRS, BLOCK_WHITE_GLAZED_TERACOTTA_SLAB, BLOCK_OAK_LADDER,
                BLOCK_MINECART_ROAD_BLOCK, BLOCK_STONE, BLOCK_BAMBOO_WOOD_FENCE, BLOCK_CROISSANT, BLOCK_MINECART_ROAD_SLAB, BLOCK_PRISMARINE_BRICKS, BLOCK_DARK_PRISMARINE_BRICKS, BLOCK_GLASS_STAIRS, BLOCK_GLASS_SLAB, BLOCK_BLUE_TORCH, BLOCK_CEMENT_STAIRS, BLOCK_CEMENT_FENCE, BLOCK_CEMENT_SLAB, BLOCK_OBSIDIAN_SLAB, BLOCK_OBSIDIAN_FENCE, BLOCK_LAPIS_LAZUL_STAIRS, BLOCK_LAPIS_LAZUL_BLOCK_SLAB, BLOCK_LAPIS_LAZUL_FENCE, BLOCK_IRON_STAIRS, BLOCK_IRON_BLOCK_SLAB, BLOCK_IRON_FENCE, BLOCK_GOLD_STAIRS, BLOCK_GOLD_BLOCK_SLAB, BLOCK_GOLD_FENCE, BLOCK_EMERALD_STAIRS, BLOCK_EMERALD_BLOCK_SLAB, BLOCK_EMERALD_FENCE, BLOCK_DIAMOND_STAIRS, BLOCK_DIAMOND_BLOCK_SLAB, BLOCK_DIAMOND_FENCE, BLOCK_CROSSWALK_PAINT, BLOCK_DARK_PRISMARINE_BRICK_STAIRS, BLOCK_PRISMARINE_BRICK_SLAB, BLOCK_OBSIDIAN_STAIRS, BLOCK_PRISMARINE_BRICK_FENCE, BLOCK_RED_SANDSTONE_PILLAR, BLOCK_STONE_BRICK_PILLAR, BLOCK_PALISADE_SANDSTONE_PILLAR, BLOCK_PALISADE_STONE_PILLAR, BLOCK_PALISADE_STONE_2_PILLAR, BLOCK_CRACKED_STONE_PILLAR, BLOCK_STONE_WITH_VINES_PILLAR, BLOCK_FLAT_DRAGON_VINES, BLOCK_DRAGON_VINES, BLOCK_RED_PALISADE_SANDSTONE_PILLAR, BLOCK_MARBLE_PILLAR_BLOCK, BLOCK_QUARTZ_PILLAR, BLOCK_MARBLE_PILLAR, BLOCK_FARMLAND, BLOCK_ROAD_MARKINGS,
                BLOCK_GRANITE_BRICK_PILLAR, BLOCK_GRANITE_BRICK_SLAB, BLOCK_GRANITE_BRICK_FENCE, BLOCK_CAMPFIRE, BLOCK_DARK_PRISMARINE_BRICK_SLAB, BLOCK_DARK_PRISMARINE_BRICK_FENCE, BLOCK_ENGRAVED_SANDSTONE, BLOCK_ENGRAVED_RED_SANDSTONE, BLOCK_ORANGE_MARBLE_TILE, BLOCK_CHECKERBOARD_CHISELED_MARBLE, BLOCK_CHISELED_MARBLE, BLOCK_CHISELED_QUARTZ, BLOCK_MARBLE_TILE, BLOCK_BLUE_MARBLE_TILE, BLOCK_GREEN_MARBLE_TILE, BLOCK_ORANGE_MARBLE_TILE_SLAB, BLOCK_GREY_MARBLE_TILE, BLOCK_ORANGE_MARBLE_TILE_STAIRS, BLOCK_GREEN_MARBLE_TILE_SLAB, BLOCK_YELLOW_STAINED_GLASS_STAIRS, BLOCK_YELLOW_STAINED_GLASS_SLAB, BLOCK_BLACK_STAINED_GLASS_STAIRS, BLOCK_BLACK_STAINED_GLASS_SLAB, BLOCK_BLUE_STAINED_GLASS_STAIRS, BLOCK_BLUE_STAINED_GLASS_SLAB, BLOCK_BROWN_STAINED_GLASS_STAIRS, BLOCK_BROWN_STAINED_GLASS_SLAB, BLOCK_CYAN_STAINED_GLASS_STAIRS, BLOCK_CYAN_STAINED_GLASS_SLAB, BLOCK_GREY_STAINED_GLASS_STAIRS, BLOCK_GREY_STAINED_GLASS_SLAB, BLOCK_GREEN_STAINED_GLASS_STAIRS, BLOCK_GREEN_STAINED_GLASS_SLAB, BLOCK_LIGHT_BLUE_STAINED_GLASS_STAIRS, BLOCK_LIGHT_BLUE_STAINED_GLASS_SLAB, BLOCK_LIGHT_GREY_STAINED_GLASS_STAIRS, BLOCK_LIGHT_GREY_STAINED_GLASS_SLAB, BLOCK_LIME_STAINED_GLASS_STAIRS, BLOCK_LIME_STAINED_GLASS_SLAB, BLOCK_MAGENTA_STAINED_GLASS_STAIRS, BLOCK_MAGENTA_STAINED_GLASS_SLAB, BLOCK_ORANGE_STAINED_GLASS_STAIRS, BLOCK_ORANGE_STAINED_GLASS_SLAB, BLOCK_PINK_STAINED_GLASS_STAIRS, BLOCK_PINK_STAINED_GLASS_SLAB, BLOCK_PURPLE_STAINED_GLASS_STAIRS, BLOCK_PURPLE_STAINED_GLASS_SLAB, BLOCK_RED_STAINED_GLASS_STAIRS, BLOCK_RED_STAINED_GLASS_SLAB, BLOCK_WHITE_STAINED_GLASS_STAIRS,
                BLOCK_WHITE_STAINED_GLASS_SLAB, BLOCK_CHECKERBOARD_CHISELED_MARBLE_STAIRS, BLOCK_CHECKERBOARD_CHISELED_MARBLE_SLAB, BLOCK_CHISELED_MARBLE_STAIRS, BLOCK_CHISELED_MARBLE_SLAB, BLOCK_MARBLE_TILE_STAIRS, BLOCK_MARBLE_TILE_SLAB, BLOCK_GREY_MARBLE_TILE_STAIRS, BLOCK_GREY_MARBLE_TILE_SLAB, BLOCK_BLUE_MARBLE_TILE_STAIRS, BLOCK_BLUE_MARBLE_TILE_SLAB, BLOCK_GREEN_MARBLE_TILE_STAIRS, BLOCK_CHISELED_QUARTZ_STAIRS, BLOCK_CHISELED_QUARTZ_SLAB, BLOCK_CHISELED_QUARTZ_PILLAR, BLOCK_GREY_MARBLE_TILE_PILLAR, BLOCK_BLUE_MARBLE_TILE_PILLAR, BLOCK_GREEN_MARBLE_TILE_PILLAR, BLOCK_ORANGE_MARBLE_TILE_PILLAR, BLOCK_MARBLE_TILE_PILLAR, BLOCK_LAMP, BLOCK_BLUE_LAMP, BLOCK_GLASS_PANE, BLOCK_YELLOW_STAINED_GLASS_PANE, BLOCK_BLACK_STAINED_GLASS_PANE, BLOCK_BLUE_STAINED_GLASS_PANE, BLOCK_BROWN_STAINED_GLASS_PANE, BLOCK_CYAN_STAINED_GLASS_PANE, BLOCK_GREY_STAINED_GLASS_PANE, BLOCK_GREEN_STAINED_GLASS_PANE, BLOCK_LIGHT_BLUE_STAINED_GLASS_PANE, BLOCK_LIGHT_GREY_STAINED_GLASS_PANE, BLOCK_LIME_STAINED_GLASS_PANE, BLOCK_MAGENTA_STAINED_GLASS_PANE, BLOCK_ORANGE_STAINED_GLASS_PANE, BLOCK_PINK_STAINED_GLASS_PANE, BLOCK_PURPLE_STAINED_GLASS_PANE, BLOCK_RED_STAINED_GLASS_PANE, BLOCK_WHITE_STAINED_GLASS_PANE, BLOCK_START_BOUNDARY_BLOCK, BLOCK_PASTE_BLOCK, BLOCK_YELLOW_CHISELED_MARBLE_TILE, BLOCK_SUNFLOWER, BLOCK_SUNFLOWER_STALK, BLOCK_MEGA_TNT, BLOCK_SUNFLOWER_SEEDS, BLOCK_CROSSTRACK, BLOCK_ADDITIVE_PASTE_BLOCK, BLOCK_PASTE_ROTATE_BLOCK, BLOCK_BLACK_CHISELED_MARBLE_TILE,
                BLOCK_BLUE_CHISELED_MARBLE_TILE, BLOCK_BROWN_CHISELED_MARBLE_TILE, BLOCK_CYAN_CHISELED_MARBLE_TILE, BLOCK_GREY_CHISELED_MARBLE_TILE, BLOCK_GREEN_CHISELED_MARBLE_TILE, BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE, BLOCK_MAGENTA_CHISELED_MARBLE_TILE, BLOCK_ORANGE_CHISELED_MARBLE_TILE, BLOCK_PINK_CHISELED_MARBLE_TILE, BLOCK_PURPLE_CHISELED_MARBLE_TILE, BLOCK_BURGUNDY_CHISELED_MARBLE_TILE, BLOCK_BAMBOO_BLOCK, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE, BLOCK_YELLOW_MARBLE_TILE, BLOCK_BLACK_MARBLE_TILE, BLOCK_ICE_BLOCK, BLOCK_BROWN_MARBLE_TILE, BLOCK_CYAN_MARBLE_TILE, BLOCK_BAMBOO_WOOD, BLOCK_BOTTLE, BLOCK_PASTEL_BLUE_MARBLE_TILE, BLOCK_PASTEL_GREEN_MARBLE_TILE, BLOCK_MAGENTA_MARBLE_TILE, BLOCK_CUP, BLOCK_PINK_MARBLE_TILE, BLOCK_PURPLE_MARBLE_TILE, BLOCK_BURGUNDY_MARBLE_TILE, BLOCK_PASTEL_RED_MARBLE_TILE, BLOCK_WINE_GLASS, BLOCK_YELLOW_STAINED_WOOD, BLOCK_BLACK_STAINED_WOOD, BLOCK_YELLOW_MARBLE_TILE_STAIRS, BLOCK_YELLOW_MARBLE_TILE_SLAB, BLOCK_YELLOW_MARBLE_TILE_PILLAR, BLOCK_BLACK_MARBLE_TILE_STAIRS, BLOCK_BLACK_MARBLE_TILE_SLAB, BLOCK_BLACK_MARBLE_TILE_PILLAR, BLOCK_BREAD, BLOCK_CYAN_STAINED_WOOD, BLOCK_PINK_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BROWN_MARBLE_TILE_STAIRS, BLOCK_BROWN_MARBLE_TILE_SLAB, BLOCK_BROWN_MARBLE_TILE_PILLAR, BLOCK_CYAN_MARBLE_TILE_STAIRS, BLOCK_CYAN_MARBLE_TILE_SLAB, BLOCK_CYAN_MARBLE_TILE_PILLAR, BLOCK_PURPLE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PURPLE_CHISELED_MARBLE_TILE_SLAB, BLOCK_PURPLE_CHISELED_MARBLE_TILE_PILLAR,
                BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_SLAB, BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PASTEL_BLUE_MARBLE_TILE_STAIRS, BLOCK_PASTEL_BLUE_MARBLE_TILE_SLAB, BLOCK_PASTEL_BLUE_MARBLE_TILE_PILLAR, BLOCK_PASTEL_GREEN_MARBLE_TILE_STAIRS, BLOCK_PASTEL_GREEN_MARBLE_TILE_SLAB, BLOCK_PASTEL_GREEN_MARBLE_TILE_PILLAR, BLOCK_MAGENTA_MARBLE_TILE_STAIRS, BLOCK_MAGENTA_MARBLE_TILE_SLAB, BLOCK_MAGENTA_MARBLE_TILE_PILLAR, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_SLAB, BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PINK_MARBLE_TILE_STAIRS, BLOCK_PINK_MARBLE_TILE_SLAB, BLOCK_PINK_MARBLE_TILE_PILLAR, BLOCK_PURPLE_MARBLE_TILE_STAIRS, BLOCK_PURPLE_MARBLE_TILE_SLAB, BLOCK_PURPLE_MARBLE_TILE_PILLAR, BLOCK_BURGUNDY_MARBLE_TILE_STAIRS, BLOCK_BURGUNDY_MARBLE_TILE_SLAB, BLOCK_BURGUNDY_MARBLE_TILE_PILLAR, BLOCK_PASTEL_RED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_RED_MARBLE_TILE_SLAB, BLOCK_PASTEL_RED_MARBLE_TILE_PILLAR, BLOCK_YELLOW_CHISELED_MARBLE_TILE_STAIRS, BLOCK_YELLOW_CHISELED_MARBLE_TILE_SLAB, BLOCK_YELLOW_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BLACK_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BLACK_CHISELED_MARBLE_TILE_SLAB, BLOCK_BLACK_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BLUE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BLUE_CHISELED_MARBLE_TILE_SLAB, BLOCK_BLUE_CHISELED_MARBLE_TILE_PILLAR, BLOCK_BROWN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_BROWN_CHISELED_MARBLE_TILE_SLAB, BLOCK_BROWN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_CYAN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_CYAN_CHISELED_MARBLE_TILE_SLAB, BLOCK_CYAN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_GREY_CHISELED_MARBLE_TILE_STAIRS, BLOCK_GREY_CHISELED_MARBLE_TILE_SLAB, BLOCK_GREY_CHISELED_MARBLE_TILE_PILLAR, BLOCK_GREEN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_GREEN_CHISELED_MARBLE_TILE_SLAB, BLOCK_GREEN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_SLAB,
                BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_SLAB, BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_PILLAR, BLOCK_MAGENTA_CHISELED_MARBLE_TILE_STAIRS, BLOCK_MAGENTA_CHISELED_MARBLE_TILE_SLAB, BLOCK_MAGENTA_CHISELED_MARBLE_TILE_PILLAR, BLOCK_ORANGE_CHISELED_MARBLE_TILE_STAIRS, BLOCK_ORANGE_CHISELED_MARBLE_TILE_SLAB, BLOCK_ORANGE_CHISELED_MARBLE_TILE_PILLAR, BLOCK_PINK_CHISELED_MARBLE_TILE_STAIRS, BLOCK_PINK_CHISELED_MARBLE_TILE_SLAB, BLOCK_GREY_STAINED_WOOD, BLOCK_GREEN_STAINED_WOOD, BLOCK_LIGHT_BLUE_STAINED_WOOD, BLOCK_LIME_STAINED_WOOD, BLOCK_MAGENTA_STAINED_WOOD, BLOCK_ORANGE_STAINED_WOOD, BLOCK_PINK_STAINED_WOOD, BLOCK_PURPLE_STAINED_WOOD, BLOCK_WHITE_SPACE_TILE_STAIRS, BLOCK_WHITE_STAINED_WOOD, BLOCK_ENGRAVED_SANDSTONE_2, BLOCK_ENGRAVED_RED_SANDSTONE_2, BLOCK_PEONY_BUSH, BLOCK_ICE_BLOCK_STAIRS, BLOCK_ICE_BLOCK_SLAB, BLOCK_SOLAR_PANEL, BLOCK_MOSAIC_BAMBOO_WOOD_STAIRS, BLOCK_MOSAIC_BAMBOO_WOOD_SLAB, BLOCK_MOSAIC_BAMBOO_WOOD_FENCE, BLOCK_YELLOW_STAINED_WOOD_STAIRS, BLOCK_YELLOW_STAINED_WOOD_SLAB, BLOCK_YELLOW_STAINED_WOOD_FENCE, BLOCK_BLACK_STAINED_WOOD_STAIRS, BLOCK_BLACK_STAINED_WOOD_SLAB, BLOCK_BLACK_STAINED_WOOD_FENCE, BLOCK_BLUE_STAINED_WOOD_STAIRS, BLOCK_BLUE_STAINED_WOOD_SLAB, BLOCK_BLUE_STAINED_WOOD_FENCE, BLOCK_CYAN_STAINED_WOOD_STAIRS, BLOCK_CYAN_STAINED_WOOD_SLAB, BLOCK_CYAN_STAINED_WOOD_FENCE, BLOCK_GREY_STAINED_WOOD_STAIRS, BLOCK_GREY_STAINED_WOOD_SLAB, BLOCK_GREY_STAINED_WOOD_FENCE, BLOCK_GREEN_STAINED_WOOD_STAIRS, BLOCK_GREEN_STAINED_WOOD_SLAB, BLOCK_GREEN_STAINED_WOOD_FENCE, BLOCK_LIGHT_BLUE_STAINED_WOOD_STAIRS,
                BLOCK_LIGHT_BLUE_STAINED_WOOD_SLAB, BLOCK_LIGHT_BLUE_STAINED_WOOD_FENCE, BLOCK_LIME_STAINED_WOOD_STAIRS, BLOCK_LIME_STAINED_WOOD_SLAB, BLOCK_LIME_STAINED_WOOD_FENCE, BLOCK_MAGENTA_STAINED_WOOD_STAIRS, BLOCK_MAGENTA_STAINED_WOOD_SLAB, BLOCK_MAGENTA_STAINED_WOOD_FENCE, BLOCK_ORANGE_STAINED_WOOD_STAIRS, BLOCK_ORANGE_STAINED_WOOD_SLAB, BLOCK_ORANGE_STAINED_WOOD_FENCE, BLOCK_PINK_STAINED_WOOD_STAIRS, BLOCK_PINK_STAINED_WOOD_SLAB, BLOCK_PINK_STAINED_WOOD_FENCE, BLOCK_PURPLE_STAINED_WOOD_STAIRS, BLOCK_PURPLE_STAINED_WOOD_SLAB, BLOCK_PURPLE_STAINED_WOOD_FENCE, BLOCK_RED_STAINED_WOOD_STAIRS, BLOCK_RED_STAINED_WOOD_SLAB, BLOCK_RED_STAINED_WOOD_FENCE, BLOCK_WHITE_STAINED_WOOD_STAIRS, BLOCK_WHITE_STAINED_WOOD_SLAB, BLOCK_WHITE_STAINED_WOOD_FENCE, BLOCK_WHITE_SPACE_TILE, BLOCK_GRAY_SPACE_TILE, BLOCK_WHITE_SPACE_TILE_SLAB, BLOCK_GRAY_SPACE_TILE_STAIRS, BLOCK_GRAY_SPACE_TILE_SLAB, BLOCK_SPRUCE_LOG, BLOCK_SPRUCE_LEAVES, BLOCK_SPRUCE_SAPLING, BLOCK_PASTE_VERTEX_BLOCK, BLOCK_MERGE_TRACK,
        };

        EntityLink[] entityList = new EntityLink[]{
                new EntityLink(0, "Fox Animal",
                        () -> new Fox((BaseWindow) window, Main.gameScene.player),
                        (e) -> {
                            e.setIcon("egg.png");
                        }),};

        //Init blocks
        File blockTextures = new File(ResourceUtils.RESOURCE_DIR + "\\items\\blocks\\textures");
        File blockIconDirectory = new File(ResourceUtils.RESOURCE_DIR + "\\items\\blocks\\icons");
        File iconDirectory = new File(ResourceUtils.RESOURCE_DIR + "\\items\\icons");
        Texture defaultIcon = TextureUtils.loadTexture(ResourceUtils.DEFAULT_ICON.getAbsolutePath(), false);

        ItemList.initialize(blockTextures, blockIconDirectory, iconDirectory, defaultIcon.id);

        ItemList.setAllItems(
                blockList,
                entityList, null);

        //Add terrains
        terrainsList.add(new TestTerrain());
        terrainsList.add(new BasicTerrain());
        terrainsList.add(new DevTerrain());
        terrainsList.add(new ComplexTerrain());

        //Add block types
        ItemList.blocks.addBlockType(RenderType.SPRITE, new SpriteRenderer());
        ItemList.blocks.addBlockType(RenderType.FLOOR, new FloorItemRenderer());
        ItemList.blocks.addBlockType(RenderType.ORIENTABLE_BLOCK, new OrientableBlockRenderer());
        ItemList.blocks.addBlockType(RenderType.SLAB, new SlabRenderer());
        ItemList.blocks.addBlockType(RenderType.STAIRS, new StairsRenderer());
        ItemList.blocks.addBlockType(RenderType.FENCE, new FenceRenderer());
        ItemList.blocks.addBlockType(RenderType.WALL_ITEM, new WallItemRenderer());
//        ItemList.blocks.addBlockType(RenderType.LAMP, new LampRenderer());
        ItemList.blocks.addBlockType(RenderType.PANE, new PaneRenderer());
//        ItemList.blocks.addBlockType(RenderType.TRACK, new TrackRenderer());
//        ItemList.blocks.addBlockType(RenderType.SUNFLOWER_HEAD, new SunflowerHeadRenderer());
//        ItemList.blocks.addBlockType(RenderType.TORCH, new TorchRenderer());
        ItemList.blocks.addBlockType(RenderType.PILLAR, new PillarRenderer());

        itemList = ItemList.getAllItems();
    }


    @Override
    public boolean menusAreOpen() {
        return showInventory;
    }

    WorldInfo currentWorld;

    @Override
    public void newGame(WorldInfo worldInfo) {
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

        inventory.setPlayerBackpack(gameInfo.playerBackpack);
        hotbar.setPlayerBackpack(gameInfo.playerBackpack);
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

    public static final Block BLOCK_BEDROCK = new Block(1, "Bedrock", new BlockTexture("bedrock.png", "bedrock.png", "bedrock.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BIRCH_LOG = new Block(2, "Birch Log", new BlockTexture("birch log.png", "birch log.png", "birch log side.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BIRCH_LEAVES = new Block(3, "Birch Leaves", new BlockTexture("birch leaves.png", "birch leaves.png", "birch leaves.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BRICK = new Block(4, "Brick", new BlockTexture("brick.png", "brick.png", "brick.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_VINES = new Block(5, "Vines", new BlockTexture("vines.png", "vines.png", "vines.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_DIRT = new Block(6, "Dirt", new BlockTexture("dirt.png", "dirt.png", "dirt.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PANSIES = new Block(7, "Pansies", new BlockTexture("pansies.png", "pansies.png", "pansies.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_GLASS = new Block(8, "Glass", new BlockTexture("glass.png", "glass.png", "glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GRASS = new Block(9, "Grass", new BlockTexture("grass.png", "dirt.png", "grass side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GRAVEL = new Block(10, "Gravel", new BlockTexture("gravel.png", "gravel.png", "gravel.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_OAK_LOG = new Block(11, "Oak Log", new BlockTexture("oak log.png", "oak log.png", "oak log side.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_OAK_LEAVES = new Block(12, "Oak Leaves", new BlockTexture("oak leaves.png", "oak leaves.png", "oak leaves.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SEA_LIGHT = new Block(13, "Sea Light", new BlockTexture("sea light.png", "sea light.png", "sea light.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PLANT_GRASS = new Block(14, "Plant Grass", new BlockTexture("plant grass.png", "plant grass.png", "plant grass.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_BAMBOO = new Block(15, "Bamboo", new BlockTexture("bamboo.png", "bamboo.png", "bamboo.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_SAND = new Block(16, "Sand", new BlockTexture("sand.png", "sand.png", "sand.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SANDSTONE = new Block(17, "Sandstone", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ANDESITE = new Block(18, "Andesite", new BlockTexture("andesite.png", "andesite.png", "andesite.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_STONE_BRICK = new Block(19, "Stone Brick", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TORCH = new Block(20, "Torch", new BlockTexture("torch.png", "torch.png", "torch.png"), false, false, RenderType.TORCH);
    public static final Block BLOCK_WATER = new Block(21, "Water", new BlockTexture("water.png", "water.png", "water.png"), false, false);
    public static final Block BLOCK_WOOL = new Block(22, "Wool", new BlockTexture("wool.png", "wool.png", "wool.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SNOW = new Block(23, "Snow", new BlockTexture("snow.png", "dirt.png", "snow side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BOOKSHELF = new Block(24, "Bookshelf", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LAVA = new Block(25, "Lava",
            new BlockTexture("lava.png", "lava.png", "lava.png"), (b) -> {
        b.opaque = false;
        b.solid = false;
        b.type = BlockList.DEFAULT_BLOCK_TYPE_ID;
        System.out.println("Lava animation length: " + b.texture.getNEG_X().animationLength);
        b.texture.getNEG_Y().animationLength = 1;
    });
    public static final Block BLOCK_TALL_DRY_GRASS_TOP = new Block(26, "tall dry grass top (hidden)", new BlockTexture("tall dry grass top.png", "tall dry grass top.png", "tall dry grass top.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CRACKED_STONE = new Block(27, "Cracked Stone", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_STONE_WITH_VINES = new Block(28, "Stone with Vines", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TNT_ACTIVE = new Block(29, "TNT Active hidden", new BlockTexture("tnt active.png", "tnt active bottom.png", "tnt active side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_PLANKS = new Block(30, "Jungle Planks", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_PLANKS_SLAB = new Block(31, "Jungle Planks Slab", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_JUNGLE_PLANKS_STAIRS = new Block(32, "Jungle Planks Stairs", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_HONEYCOMB_BLOCK = new Block(33, "Honeycomb Block", new BlockTexture("honeycomb.png", "honeycomb.png", "honeycomb.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD = new Block(34, "Mosaic Bamboo Wood", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MUSIC_BOX = new Block(35, "Music Box", new BlockTexture("music box.png", "music box side.png", "music box side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CAKE = new Block(36, "Cake", new BlockTexture("cake.png", "cake.png", "cake.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_JUNGLE_SAPLING = new Block(37, "Jungle Sapling", new BlockTexture("jungle sapling.png", "jungle sapling.png", "jungle sapling.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_OBSIDIAN = new Block(38, "Obsidian", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BURGUNDY_BRICK = new Block(39, "Burgundy Brick", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_FENCE = new Block(40, "Jungle Fence", new BlockTexture("jungle planks.png", "jungle planks.png", "jungle planks.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_RED_FLOWER = new Block(41, "Red Flower", new BlockTexture("red flower.png", "red flower.png", "red flower.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_TALL_DRY_GRASS = new Block(42, "Tall Dry Grass", new BlockTexture("tall dry grass.png", "tall dry grass.png", "tall dry grass.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_RED_CANDLE = new Block(43, "Red Candle", new BlockTexture("red candle.png", "red candle.png", "red candle.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_YELLOW_FLOWER = new Block(44, "Yellow Flower", new BlockTexture("yellow flower.png", "yellow flower.png", "yellow flower.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_COAL_ORE = new Block(45, "Coal Ore", new BlockTexture("coal ore.png", "coal ore.png", "coal ore.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_COAL_BLOCK = new Block(46, "Coal Block", new BlockTexture("coal.png", "coal.png", "coal.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_LEAVES = new Block(47, "Jungle Leaves", new BlockTexture("jungle leaves.png", "jungle leaves.png", "jungle leaves.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_JUNGLE_LOG = new Block(48, "Jungle Log", new BlockTexture("jungle log.png", "jungle log.png", "jungle log side.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_TALL_GRASS_TOP = new Block(49, "tall grass top (hidden)", new BlockTexture("tall grass top.png", "tall grass top.png", "tall grass top.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CONTROL_PANEL = new Block(50, "Control Panel", new BlockTexture("control l.png", "control l side.png", "control l side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BEEHIVE = new Block(52, "Beehive", new BlockTexture("beehive.png", "beehive bottom.png", "beehive side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_DIORITE = new Block(53, "Diorite", new BlockTexture("diorite.png", "diorite.png", "diorite.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POLISHED_DIORITE = new Block(54, "Polished Diorite", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_EDISON_LIGHT = new Block(55, "Edison Light", new BlockTexture("edison light.png", "edison light.png", "edison light.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POLISHED_ANDESITE = new Block(56, "Polished Andesite", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SPRUCE_PLANKS = new Block(57, "Spruce Planks", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_AZURE_BLUET = new Block(58, "Azure Bluet", new BlockTexture("azure bluet.png", "azure bluet.png", "azure bluet.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_DANDELION = new Block(59, "Dandelion", new BlockTexture("dandelion.png", "dandelion.png", "dandelion.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_BLUE_ORCHID = new Block(60, "Blue Orchid", new BlockTexture("blue orchid.png", "blue orchid.png", "blue orchid.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_FERN = new Block(61, "Fern", new BlockTexture("fern.png", "fern.png", "fern.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_GRANITE_BRICK = new Block(62, "Granite Brick", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_ACACIA_PLANKS = new Block(63, "Acacia Planks", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_AMETHYST_CRYSTAL = new Block(64, "Amethyst Crystal", new BlockTexture("amethyst crystal.png", "amethyst crystal.png", "amethyst crystal.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CLAY = new Block(65, "Clay", new BlockTexture("clay.png", "clay.png", "clay.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_CONCRETE = new Block(66, "Yellow Concrete", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_GLAZED_TERACOTTA = new Block(67, "Yellow Glazed Teracotta", new BlockTexture("yellow glazed teracotta.png", "yellow glazed teracotta.png", "yellow glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BLACK_CONCRETE = new Block(68, "Black Concrete", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_GLAZED_TERACOTTA = new Block(69, "Black Glazed Teracotta", new BlockTexture("black glazed teracotta.png", "black glazed teracotta.png", "black glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BLUE_CONCRETE = new Block(70, "Blue Concrete", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_GLAZED_TERACOTTA = new Block(71, "Blue Glazed Teracotta", new BlockTexture("blue glazed teracotta.png", "blue glazed teracotta.png", "blue glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BROWN_CONCRETE = new Block(72, "Brown Concrete", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BROWN_GLAZED_TERACOTTA = new Block(73, "Brown Glazed Teracotta", new BlockTexture("brown glazed teracotta.png", "brown glazed teracotta.png", "brown glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_CYAN_CONCRETE = new Block(74, "Cyan Concrete", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CYAN_GLAZED_TERACOTTA = new Block(75, "Cyan Glazed Teracotta", new BlockTexture("cyan glazed teracotta.png", "cyan glazed teracotta.png", "cyan glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_GREY_CONCRETE = new Block(76, "Grey Concrete", new BlockTexture("grey concrete.png", "grey concrete.png", "grey concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREY_GLAZED_TERACOTTA = new Block(77, "Grey Glazed Teracotta", new BlockTexture("grey glazed teracotta.png", "grey glazed teracotta.png", "grey glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_GREEN_CONCRETE = new Block(78, "Green Concrete", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_GLAZED_TERACOTTA = new Block(79, "Green Glazed Teracotta", new BlockTexture("green glazed teracotta.png", "green glazed teracotta.png", "green glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE = new Block(80, "Light Blue Concrete", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA = new Block(81, "Light Blue Glazed Teracotta", new BlockTexture("light blue glazed teracotta.png", "light blue glazed teracotta.png", "light blue glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_LIGHT_GREY_CONCRETE = new Block(82, "Light Grey Concrete", new BlockTexture("light grey concrete.png", "light grey concrete.png", "light grey concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_GREY_GLAZED_TERACOTTA = new Block(83, "Light Grey Glazed Teracotta", new BlockTexture("light grey glazed teracotta.png", "light grey glazed teracotta.png", "light grey glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_LIME_CONCRETE = new Block(84, "Lime Concrete", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIME_GLAZED_TERACOTTA = new Block(85, "Lime Glazed Teracotta", new BlockTexture("lime glazed teracotta.png", "lime glazed teracotta.png", "lime glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_MAGENTA_CONCRETE = new Block(86, "Magenta Concrete", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_GLAZED_TERACOTTA = new Block(87, "Magenta Glazed Teracotta", new BlockTexture("magenta glazed teracotta.png", "magenta glazed teracotta.png", "magenta glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_ORANGE_CONCRETE = new Block(88, "Orange Concrete", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_GLAZED_TERACOTTA = new Block(89, "Orange Glazed Teracotta", new BlockTexture("orange glazed teracotta.png", "orange glazed teracotta.png", "orange glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_PINK_CONCRETE = new Block(90, "Pink Concrete", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_GLAZED_TERACOTTA = new Block(91, "Pink Glazed Teracotta", new BlockTexture("pink glazed teracotta.png", "pink glazed teracotta.png", "pink glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_PURPLE_CONCRETE = new Block(92, "Purple Concrete", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_GLAZED_TERACOTTA = new Block(93, "Purple Glazed Teracotta", new BlockTexture("purple glazed teracotta.png", "purple glazed teracotta.png", "purple glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_RED_CONCRETE = new Block(94, "Red Concrete", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_GLAZED_TERACOTTA = new Block(95, "Red Glazed Teracotta", new BlockTexture("red glazed teracotta.png", "red glazed teracotta.png", "red glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_WHITE_CONCRETE = new Block(96, "White Concrete", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WHITE_GLAZED_TERACOTTA = new Block(97, "White Glazed Teracotta", new BlockTexture("white glazed teracotta.png", "white glazed teracotta.png", "white glazed teracotta.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BRAIN_CORAL = new Block(98, "Brain Coral", new BlockTexture("brain coral.png", "brain coral.png", "brain coral.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_DIAMOND_ORE = new Block(99, "Diamond Ore", new BlockTexture("diamond ore.png", "diamond ore.png", "diamond ore.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_QUARTZ_PILLAR_BLOCK = new Block(100, "Quartz Pillar Block", new BlockTexture("quartz.png", "quartz.png", "quartz side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_COBBLESTONE = new Block(101, "Cobblestone", new BlockTexture("cobblestone.png", "cobblestone.png", "cobblestone.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_TURQUOISE = new Block(102, "Wool Turquoise", new BlockTexture("wool turquoise.png", "wool turquoise.png", "wool turquoise.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_ORANGE = new Block(103, "Wool Orange", new BlockTexture("wool orange.png", "wool orange.png", "wool orange.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_SAND = new Block(104, "Red Sand", new BlockTexture("red sand.png", "red sand.png", "red sand.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WATERMELON = new Block(105, "Watermelon", new BlockTexture("watermelon.png", "watermelon.png", "watermelon side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PHANTOM_STONE = new Block(107, "Phantom Stone", new BlockTexture("andesite.png", "andesite.png", "andesite.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PHANTOM_STONE_BRICK = new Block(108, "Phantom Stone Brick", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CACTUS = new Block(109, "Cactus", new BlockTexture("cactus.png", "cactus bottom.png", "cactus side.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_STONE = new Block(110, "Palisade Stone", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_SANDSTONE = new Block(111, "Red Sandstone", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_FIRE_CORAL_BLOCK = new Block(112, "Fire Coral Block", new BlockTexture("fire coral.png", "fire coral.png", "fire coral.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CANDLE = new Block(113, "Candle", new BlockTexture("candle.png", "candle.png", "candle.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_PALISADE_STONE_2 = new Block(114, "Palisade Stone 2", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_FIRE_CORAL = new Block(115, "Fire Coral", new BlockTexture("fire coral.png", "fire coral.png", "fire coral.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_TALL_GRASS = new Block(116, "Tall Grass", new BlockTexture("tall grass.png", "tall grass.png", "tall grass.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_HORN_CORAL_BLOCK = new Block(117, "Horn Coral Block", new BlockTexture("horn coral.png", "horn coral.png", "horn coral.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GOLD_BLOCK = new Block(118, "Gold Block", new BlockTexture("gold.png", "gold.png", "gold.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_HORN_CORAL_FAN = new Block(119, "Horn Coral Fan", new BlockTexture("horn coral fan.png", "horn coral fan.png", "horn coral fan.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_TNT = new Block(120, "TNT", new BlockTexture("tnt.png", "tnt bottom.png", "tnt side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WHEAT = new Block(121, "Wheat hidden", new BlockTexture("wheat.png", "wheat.png", "wheat.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CARROTS__PLANT = new Block(122, "Carrots hidden <PLANT>", new BlockTexture("carrots  plant.png", "carrots  plant.png", "carrots  plant.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_MINI_CACTUS = new Block(123, "Mini Cactus", new BlockTexture("mini cactus.png", "mini cactus.png", "mini cactus.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_MUSHROOM = new Block(124, "Mushroom", new BlockTexture("mushroom.png", "mushroom.png", "mushroom.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_MUSHROOM_2 = new Block(125, "Mushroom 2", new BlockTexture("mushroom 2.png", "mushroom 2.png", "mushroom 2.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_ROSES = new Block(126, "Roses", new BlockTexture("roses.png", "roses.png", "roses.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_WOOL_PURPLE = new Block(127, "Wool Purple", new BlockTexture("wool purple.png", "wool purple.png", "wool purple.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BIRCH_PLANKS = new Block(128, "Birch Planks", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_STAINED_WOOD = new Block(129, "Red Stained Wood", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_OAK_PLANKS = new Block(130, "Oak Planks", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_RED = new Block(131, "Wool Red", new BlockTexture("wool red.png", "wool red.png", "wool red.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_PINK = new Block(132, "Wool Pink", new BlockTexture("wool pink.png", "wool pink.png", "wool pink.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_YELLOW = new Block(133, "Wool Yellow", new BlockTexture("wool yellow.png", "wool yellow.png", "wool yellow.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_BROWN = new Block(134, "Wool Brown", new BlockTexture("wool brown.png", "wool brown.png", "wool brown.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TUBE_CORAL_BLOCK = new Block(135, "Tube Coral Block", new BlockTexture("tube coral.png", "tube coral.png", "tube coral.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_TUBE_CORAL = new Block(136, "Tube Coral", new BlockTexture("tube coral.png", "tube coral.png", "tube coral.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_TUBE_CORAL_FAN = new Block(137, "Tube Coral Fan", new BlockTexture("tube coral fan.png", "tube coral fan.png", "tube coral fan.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_WOOL_DEEP_BLUE = new Block(138, "Wool Deep Blue", new BlockTexture("wool deep blue.png", "wool deep blue.png", "wool deep blue.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_SKY_BLUE = new Block(139, "Wool Sky Blue", new BlockTexture("wool sky blue.png", "wool sky blue.png", "wool sky blue.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_DARK_GREEN = new Block(140, "Wool Dark Green", new BlockTexture("wool dark green.png", "wool dark green.png", "wool dark green.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_GREEN = new Block(141, "Wool Green", new BlockTexture("wool green.png", "wool green.png", "wool green.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_GREY = new Block(142, "Wool Grey", new BlockTexture("wool grey.png", "wool grey.png", "wool grey.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BRAIN_CORAL_BLOCK = new Block(143, "Brain Coral Block", new BlockTexture("brain coral.png", "brain coral.png", "brain coral.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_DIAMOND_BLOCK = new Block(144, "Diamond Block", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_IRON_BLOCK = new Block(145, "Iron Block", new BlockTexture("iron.png", "iron.png", "iron.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POTATOES_PLANT = new Block(146, "Potatoes hidden<PLANT>", new BlockTexture("potatoes plant.png", "potatoes plant.png", "potatoes plant.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_HONEYCOMB_BLOCK_STAIRS = new Block(147, "Honeycomb Block Stairs", new BlockTexture("honeycomb.png", "honeycomb.png", "honeycomb.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_OAK_FENCE = new Block(148, "Oak Fence", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BIRCH_FENCE = new Block(149, "Birch Fence", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BUBBLE_CORAL_BLOCK = new Block(150, "Bubble Coral Block", new BlockTexture("bubble coral.png", "bubble coral.png", "bubble coral.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BUBBLE_CORAL = new Block(151, "Bubble Coral", new BlockTexture("bubble coral.png", "bubble coral.png", "bubble coral.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_BUBBLE_CORAL_FAN = new Block(152, "Bubble Coral Fan", new BlockTexture("bubble coral fan.png", "bubble coral fan.png", "bubble coral fan.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_JUNGLE_GRASS = new Block(153, "Jungle Grass", new BlockTexture("jungle grass.png", "dirt.png", "jungle grass side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LILY_PAD = new Block(154, "Lily Pad", new BlockTexture("lily pad.png", "lily pad.png", "lily pad.png"), true, true, RenderType.FLOOR);
    public static final Block BLOCK_TRACK = new Block(155, "Track", new BlockTexture("track.png", "track.png", "track.png"), true, false, RenderType.FLOOR);
    public static final Block BLOCK_WOOL_MAGENTA = new Block(156, "Wool Magenta", new BlockTexture("wool magenta.png", "wool magenta.png", "wool magenta.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WOOL_BLACK = new Block(157, "Wool Black", new BlockTexture("wool black.png", "wool black.png", "wool black.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GRANITE_BRICK_STAIRS = new Block(158, "Granite Brick Stairs", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CEMENT = new Block(159, "Cement", new BlockTexture("cement.png", "cement.png", "cement.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_OAK_SAPLING = new Block(160, "Oak Sapling", new BlockTexture("oak sapling.png", "oak sapling.png", "oak sapling.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_BIRCH_SAPLING = new Block(161, "Birch Sapling", new BlockTexture("birch sapling.png", "birch sapling.png", "birch sapling.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_WHEAT_SEEDS = new Block(162, "Wheat Seeds", new BlockTexture("wheat seeds.png", "wheat seeds.png", "wheat seeds.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CARROT_SEEDS = new Block(163, "Carrot Seeds", new BlockTexture("carrot seeds.png", "carrot seeds.png", "carrot seeds.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_POTATO_SEEDS = new Block(165, "Potato Seeds", new BlockTexture("carrot seeds.png", "carrot seeds.png", "carrot seeds.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_A1 = new Block(166, "A1 hidden", new BlockTexture("a1.png", "a1.png", "a1.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_A2 = new Block(167, "A2 hidden", new BlockTexture("a2.png", "a2.png", "a2.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_B1 = new Block(168, "B1 hidden", new BlockTexture("b1.png", "b1.png", "b1.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_B2 = new Block(169, "B2 hidden", new BlockTexture("b2.png", "b2.png", "b2.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_B3 = new Block(170, "B3 hidden", new BlockTexture("b3.png", "b3.png", "b3.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_B4 = new Block(171, "B4 hidden", new BlockTexture("b4.png", "b4.png", "b4.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_B5 = new Block(172, "B5 hidden", new BlockTexture("b5.png", "b5.png", "b5.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_B6 = new Block(173, "B6 hidden", new BlockTexture("b6.png", "b6.png", "b6.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_ELECTRIC_LIGHT = new Block(174, "Electric Light", new BlockTexture("electric light.png", "electric light.png", "electric light.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE = new Block(175, "Red Palisade Sandstone", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PHANTOM_SANDSTONE = new Block(176, "Phantom Sandstone", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone side.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PALISADE_SANDSTONE = new Block(177, "Palisade Sandstone", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GLOW_ROCK = new Block(178, "Glow Rock", new BlockTexture("glow rock.png", "glow rock.png", "glow rock.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_STAINED_GLASS = new Block(179, "Black-Stained Glass", new BlockTexture("black stained glass.png", "black stained glass.png", "black stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_STAINED_GLASS = new Block(180, "Blue-Stained Glass", new BlockTexture("blue stained glass.png", "blue stained glass.png", "blue stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BROWN_STAINED_GLASS = new Block(181, "Brown-Stained Glass", new BlockTexture("brown stained glass.png", "brown stained glass.png", "brown stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CYAN_STAINED_GLASS = new Block(182, "Cyan-Stained Glass", new BlockTexture("cyan stained glass.png", "cyan stained glass.png", "cyan stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREY_STAINED_GLASS = new Block(183, "Grey-Stained Glass", new BlockTexture("grey stained glass.png", "grey stained glass.png", "grey stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_STAINED_GLASS = new Block(184, "Green-Stained Glass", new BlockTexture("green stained glass.png", "green stained glass.png", "green stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_GLASS = new Block(185, "Light Blue-Stained Glass", new BlockTexture("light blue stained glass.png", "light blue stained glass.png", "light blue stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_GREY_STAINED_GLASS = new Block(186, "Light Grey-Stained Glass", new BlockTexture("light grey stained glass.png", "light grey stained glass.png", "light grey stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIME_STAINED_GLASS = new Block(187, "Lime-Stained Glass", new BlockTexture("lime stained glass.png", "lime stained glass.png", "lime stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_STAINED_GLASS = new Block(188, "Magenta-Stained Glass", new BlockTexture("magenta stained glass.png", "magenta stained glass.png", "magenta stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_STAINED_GLASS = new Block(189, "Orange-Stained Glass", new BlockTexture("orange stained glass.png", "orange stained glass.png", "orange stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_STAINED_GLASS = new Block(190, "Pink-Stained Glass", new BlockTexture("pink stained glass.png", "pink stained glass.png", "pink stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_STAINED_GLASS = new Block(191, "Purple-Stained Glass", new BlockTexture("purple stained glass.png", "purple stained glass.png", "purple stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RED_STAINED_GLASS = new Block(192, "Red-Stained Glass", new BlockTexture("red stained glass.png", "red stained glass.png", "red stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WHITE_STAINED_GLASS = new Block(193, "White-Stained Glass", new BlockTexture("white stained glass.png", "white stained glass.png", "white stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_STAINED_GLASS = new Block(194, "Yellow-Stained Glass", new BlockTexture("yellow stained glass.png", "yellow stained glass.png", "yellow stained glass.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_DARK_OAK_FENCE = new Block(195, "Dark Oak Fence", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BIRCH_PLANKS_STAIRS = new Block(196, "Birch Planks Stairs", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_OAK_PLANKS_STAIRS = new Block(197, "Oak Planks Stairs", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_DARK_OAK_PLANKS_STAIRS = new Block(198, "Dark Oak Planks Stairs", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_HONEYCOMB_BLOCK_SLAB = new Block(199, "Honeycomb Block Slab", new BlockTexture("honeycomb.png", "honeycomb.png", "honeycomb.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_JUNGLE_GRASS_PLANT = new Block(200, "Jungle Grass Plant", new BlockTexture("jungle grass plant.png", "jungle grass plant.png", "jungle grass plant.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_PRISMARINE_BRICK_STAIRS = new Block(201, "Prismarine Brick Stairs", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_SANDSTONE_STAIRS = new Block(202, "Sandstone Stairs", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone side.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CAVE_VINES_FLAT = new Block(203, "Cave Vines Flat", new BlockTexture("cave vines flat.png", "cave vines flat.png", "cave vines flat.png"), false, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_POLISHED_DIORITE_STAIRS = new Block(204, "Polished Diorite Stairs", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BIRCH_PLANKS_SLAB = new Block(205, "Birch Planks Slab", new BlockTexture("bookshelf.png", "bookshelf.png", "bookshelf.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_OAK_PLANKS_SLAB = new Block(206, "Oak Planks Slab", new BlockTexture("oak planks.png", "oak planks.png", "oak planks.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_DARK_OAK_PLANKS_SLAB = new Block(207, "Dark Oak Planks Slab", new BlockTexture("spruce planks.png", "spruce planks.png", "spruce planks.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BAMBOO_WOOD_STAIRS = new Block(208, "Bamboo Wood Stairs", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_STONE_BRICK_SLAB = new Block(209, "Stone Brick Slab", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RED_SANDSTONE_SLAB = new Block(210, "Red Sandstone Slab", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_SANDSTONE_SLAB = new Block(211, "Sandstone Slab", new BlockTexture("sandstone.png", "sandstone bottom.png", "sandstone side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_DRY_GRASS = new Block(212, "Dry Grass", new BlockTexture("dry grass.png", "dirt.png", "dry grass side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POLISHED_ANDESITE_SLAB = new Block(213, "Polished Andesite Slab", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_IRON_LADDER = new Block(214, "Iron Ladder", new BlockTexture("iron ladder.png", "iron ladder.png", "iron ladder.png"), false, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_RED_VINES_FLAT = new Block(215, "Red Vines Flat", new BlockTexture("red vines flat.png", "red vines flat.png", "red vines flat.png"), true, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_YELLOW_CANDLE = new Block(216, "Yellow Candle", new BlockTexture("yellow candle.png", "yellow candle.png", "yellow candle.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CAVE_VINES = new Block(217, "Cave Vines", new BlockTexture("cave vines flat.png", "cave vines flat.png", "cave vines flat.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_GREEN_CANDLE = new Block(218, "Green Candle", new BlockTexture("green candle.png", "green candle.png", "green candle.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_GRANITE = new Block(219, "Granite", new BlockTexture("granite.png", "granite.png", "granite.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_CANDLE = new Block(220, "Blue Candle", new BlockTexture("blue candle.png", "blue candle.png", "blue candle.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_RED_VINES = new Block(221, "Red Vines", new BlockTexture("red vines flat.png", "red vines flat.png", "red vines flat.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_FLAT_VINES = new Block(222, "Flat Vines", new BlockTexture("vines.png", "vines.png", "vines.png"), true, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_DARK_OAK_LADDER = new Block(223, "Dark Oak Ladder", new BlockTexture("dark oak ladder.png", "dark oak ladder.png", "dark oak ladder.png"), false, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_DRY_GRASS_PLANT = new Block(224, "Dry Grass Plant", new BlockTexture("dry grass plant.png", "dry grass plant.png", "dry grass plant.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_ACACIA_LEAVES = new Block(225, "Acacia Leaves", new BlockTexture("acacia leaves.png", "acacia leaves.png", "acacia leaves.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ACACIA_LOG = new Block(226, "Acacia Log", new BlockTexture("acacia log.png", "acacia log.png", "acacia log side.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_FIRE_CORAL_FAN = new Block(227, "Fire Coral Fan", new BlockTexture("fire coral fan.png", "fire coral fan.png", "fire coral fan.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_LAPIS_LAZUL_ORE = new Block(228, "Lapis Lazul Ore", new BlockTexture("lapis lazul ore.png", "lapis lazul ore.png", "lapis lazul ore.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LAPIS_LAZUL_BLOCK = new Block(229, "Lapis Lazul Block", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ACACIA_SAPLING = new Block(230, "Acacia Sapling", new BlockTexture("acacia sapling.png", "acacia sapling.png", "acacia sapling.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_IRON_ORE = new Block(231, "Iron Ore", new BlockTexture("iron ore.png", "iron ore.png", "iron ore.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BRAIN_CORAL_FAN = new Block(232, "Brain Coral Fan", new BlockTexture("brain coral fan.png", "brain coral fan.png", "brain coral fan.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_WHITE_ROSE = new Block(233, "White Rose", new BlockTexture("white rose.png", "white rose.png", "white rose.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_GOLD_ORE = new Block(234, "Gold Ore", new BlockTexture("gold ore.png", "gold ore.png", "gold ore.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_HORN_CORAL = new Block(235, "Horn Coral", new BlockTexture("horn coral.png", "horn coral.png", "horn coral.png"), true, false, RenderType.SPRITE);
    public static final Block BLOCK_RED_ROSE = new Block(236, "Red Rose", new BlockTexture("red rose.png", "red rose.png", "red rose.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_EMERALD_ORE = new Block(237, "Emerald Ore", new BlockTexture("emerald ore.png", "emerald ore.png", "emerald ore.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_EMERALD_BLOCK = new Block(238, "Emerald Block", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_EYE_SUSAN = new Block(239, "Black-Eye Susan", new BlockTexture("black eye susan.png", "black eye susan.png", "black eye susan.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_ORANGE_TULIP = new Block(240, "Orange Tulip", new BlockTexture("orange tulip.png", "orange tulip.png", "orange tulip.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_DEAD_BUSH = new Block(241, "Dead Bush", new BlockTexture("dead bush.png", "dead bush.png", "dead bush.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_HAY_BAIL = new Block(242, "Hay Bail", new BlockTexture("hay bail.png", "hay bail.png", "hay bail side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_POLISHED_ANDESITE_STAIRS = new Block(243, "Polished Andesite Stairs", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_POLISHED_DIORITE_SLAB = new Block(244, "Polished Diorite Slab", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CURVED_TRACK = new Block(245, "Curved Track hidden", new BlockTexture("curved track.png", "curved track.png", "curved track.png"), true, false, RenderType.FLOOR);
    public static final Block BLOCK_BEETS = new Block(246, "Beets hidden", new BlockTexture("beets.png", "beets.png", "beets.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_BEETROOT_SEEDS = new Block(247, "Beetroot Seeds", new BlockTexture("carrot seeds.png", "carrot seeds.png", "carrot seeds.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_BAMBOO_LADDER = new Block(248, "Bamboo Ladder", new BlockTexture("bamboo ladder.png", "bamboo ladder.png", "bamboo ladder.png"), false, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_ACACIA_FENCE = new Block(249, "Acacia Fence", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_ACACIA_PLANKS_STAIRS = new Block(250, "Acacia Planks Stairs", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_ACACIA_PLANKS_SLAB = new Block(251, "Acacia Planks Slab", new BlockTexture("acacia planks.png", "acacia planks.png", "acacia planks.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RAISED_TRACK = new Block(252, "Raised Track hidden", new BlockTexture("track.png", "track.png", "track.png"), true, false, RenderType.TRACK);
    public static final Block BLOCK_SEA_GRASS = new Block(253, "Sea Grass", new BlockTexture("sea grass.png", "sea grass.png", "sea grass.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_BLUE_STAINED_WOOD = new Block(254, "Blue Stained Wood", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_RUBY_CRYSTAL = new Block(255, "Ruby Crystal", new BlockTexture("ruby crystal.png", "ruby crystal.png", "ruby crystal.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_JADE_CRYSTAL = new Block(256, "Jade Crystal", new BlockTexture("jade crystal.png", "jade crystal.png", "jade crystal.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_AQUAMARINE_CRYSTAL = new Block(257, "Aquamarine Crystal", new BlockTexture("aquamarine crystal.png", "aquamarine crystal.png", "aquamarine crystal.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_BAMBOO_WOOD_SLAB = new Block(258, "Bamboo Wood Slab", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RED_SANDSTONE_STAIRS = new Block(259, "Red Sandstone Stairs", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone side.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_STONE_BRICK_STAIRS = new Block(260, "Stone Brick Stairs", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_STONE_BRICK_FENCE = new Block(262, "Stone Brick Fence", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BRICK_STAIRS = new Block(263, "Brick Stairs", new BlockTexture("brick.png", "brick.png", "brick.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BRICK_SLAB = new Block(264, "Brick Slab", new BlockTexture("brick.png", "brick.png", "brick.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_SNOW_BLOCK = new Block(265, "Snow Block", new BlockTexture("snow.png", "snow.png", "snow.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_COBBLESTONE_STAIRS = new Block(266, "Cobblestone Stairs", new BlockTexture("cobblestone.png", "cobblestone.png", "cobblestone.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_COBBLESTONE_SLAB = new Block(267, "Cobblestone Slab", new BlockTexture("cobblestone.png", "cobblestone.png", "cobblestone.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PALISADE_STONE_STAIRS = new Block(268, "Palisade Stone Stairs", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone side.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PALISADE_STONE_SLAB = new Block(269, "Palisade Stone Slab", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PALISADE_STONE_FENCE = new Block(270, "Palisade Stone Fence", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone side.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_PALISADE_STONE_2_STAIRS = new Block(271, "Palisade Stone 2 Stairs", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PALISADE_STONE_2_SLAB = new Block(272, "Palisade Stone 2 Slab", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PALISADE_STONE_2_FENCE = new Block(273, "Palisade Stone 2 Fence", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_POLISHED_DIORITE_FENCE = new Block(274, "Polished Diorite Fence", new BlockTexture("polished diorite.png", "polished diorite.png", "polished diorite.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_POLISHED_ANDESITE_FENCE = new Block(275, "Polished Andesite Fence", new BlockTexture("polished andesite.png", "polished andesite.png", "polished andesite.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_CRACKED_STONE_STAIRS = new Block(276, "Cracked Stone Stairs", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CRACKED_STONE_SLAB = new Block(277, "Cracked Stone Slab", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CRACKED_STONE_FENCE = new Block(278, "Cracked Stone Fence", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_STONE_WITH_VINES_STAIRS = new Block(279, "Stone with Vines Stairs", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_STONE_WITH_VINES_SLAB = new Block(280, "Stone with Vines Slab", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_STONE_WITH_VINES_FENCE = new Block(281, "Stone with Vines Fence", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BURGUNDY_BRICK_STAIRS = new Block(282, "Burgundy Brick Stairs", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BURGUNDY_BRICK_SLAB = new Block(283, "Burgundy Brick Slab", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BURGUNDY_BRICK_FENCE = new Block(284, "Burgundy Brick Fence", new BlockTexture("burgundy brick.png", "burgundy brick.png", "burgundy brick.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_SWITCH_JUNCTION = new Block(285, "Switch Junction", new BlockTexture("switch junction.png", "switch junction.png", "switch junction.png"), true, false, RenderType.FLOOR);
    public static final Block BLOCK_TRACK_STOP = new Block(286, "Track Stop", new BlockTexture("track stop.png", "track stop.png", "track stop.png"), true, false, RenderType.FLOOR);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_STAIRS = new Block(287, "Red Palisade Sandstone Stairs", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone side.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_SLAB = new Block(288, "Red Palisade Sandstone Slab", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_FENCE = new Block(289, "Red Palisade Sandstone Fence", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone side.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_PALISADE_SANDSTONE_STAIRS = new Block(290, "Palisade Sandstone Stairs", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone side.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PALISADE_SANDSTONE_SLAB = new Block(291, "Palisade Sandstone Slab", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PALISADE_SANDSTONE_FENCE = new Block(292, "Palisade Sandstone Fence", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone side.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_WOOL_STAIRS = new Block(293, "Wool Stairs", new BlockTexture("wool.png", "wool.png", "wool.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_SLAB = new Block(294, "Wool Slab", new BlockTexture("wool.png", "wool.png", "wool.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_GREY_STAIRS = new Block(295, "Wool Grey Stairs", new BlockTexture("wool grey.png", "wool grey.png", "wool grey.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_GREY_SLAB = new Block(296, "Wool Grey Slab", new BlockTexture("wool grey.png", "wool grey.png", "wool grey.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_RED_STAIRS = new Block(297, "Wool Red Stairs", new BlockTexture("wool red.png", "wool red.png", "wool red.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_RED_SLAB = new Block(298, "Wool Red Slab", new BlockTexture("wool red.png", "wool red.png", "wool red.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_PINK_STAIRS = new Block(299, "Wool Pink Stairs", new BlockTexture("wool pink.png", "wool pink.png", "wool pink.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_PINK_SLAB = new Block(300, "Wool Pink Slab", new BlockTexture("wool pink.png", "wool pink.png", "wool pink.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_ORANGE_STAIRS = new Block(301, "Wool Orange Stairs", new BlockTexture("wool orange.png", "wool orange.png", "wool orange.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_ORANGE_SLAB = new Block(302, "Wool Orange Slab", new BlockTexture("wool orange.png", "wool orange.png", "wool orange.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_YELLOW_STAIRS = new Block(303, "Wool Yellow Stairs", new BlockTexture("wool yellow.png", "wool yellow.png", "wool yellow.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_YELLOW_SLAB = new Block(304, "Wool Yellow Slab", new BlockTexture("wool yellow.png", "wool yellow.png", "wool yellow.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_GREEN_STAIRS = new Block(305, "Wool Green Stairs", new BlockTexture("wool green.png", "wool green.png", "wool green.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_GREEN_SLAB = new Block(306, "Wool Green Slab", new BlockTexture("wool green.png", "wool green.png", "wool green.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_DARK_GREEN_STAIRS = new Block(307, "Wool Dark Green Stairs", new BlockTexture("wool dark green.png", "wool dark green.png", "wool dark green.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_DARK_GREEN_SLAB = new Block(308, "Wool Dark Green Slab", new BlockTexture("wool dark green.png", "wool dark green.png", "wool dark green.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_TURQUOISE_STAIRS = new Block(309, "Wool Turquoise Stairs", new BlockTexture("wool turquoise.png", "wool turquoise.png", "wool turquoise.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_TURQUOISE_SLAB = new Block(310, "Wool Turquoise Slab", new BlockTexture("wool turquoise.png", "wool turquoise.png", "wool turquoise.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_DEEP_BLUE_STAIRS = new Block(311, "Wool Deep Blue Stairs", new BlockTexture("wool deep blue.png", "wool deep blue.png", "wool deep blue.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_DEEP_BLUE_SLAB = new Block(312, "Wool Deep Blue Slab", new BlockTexture("wool deep blue.png", "wool deep blue.png", "wool deep blue.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_SKY_BLUE_STAIRS = new Block(313, "Wool Sky Blue Stairs", new BlockTexture("wool sky blue.png", "wool sky blue.png", "wool sky blue.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_SKY_BLUE_SLAB = new Block(314, "Wool Sky Blue Slab", new BlockTexture("wool sky blue.png", "wool sky blue.png", "wool sky blue.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_BROWN_STAIRS = new Block(315, "Wool Brown Stairs", new BlockTexture("wool brown.png", "wool brown.png", "wool brown.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_BROWN_SLAB = new Block(316, "Wool Brown Slab", new BlockTexture("wool brown.png", "wool brown.png", "wool brown.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_PURPLE_STAIRS = new Block(317, "Wool Purple Stairs", new BlockTexture("wool purple.png", "wool purple.png", "wool purple.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_PURPLE_SLAB = new Block(318, "Wool Purple Slab", new BlockTexture("wool purple.png", "wool purple.png", "wool purple.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_MAGENTA_STAIRS = new Block(319, "Wool Magenta Stairs", new BlockTexture("wool magenta.png", "wool magenta.png", "wool magenta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_MAGENTA_SLAB = new Block(320, "Wool Magenta Slab", new BlockTexture("wool magenta.png", "wool magenta.png", "wool magenta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WOOL_BLACK_STAIRS = new Block(321, "Wool Black Stairs", new BlockTexture("wool black.png", "wool black.png", "wool black.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WOOL_BLACK_SLAB = new Block(322, "Wool Black Slab", new BlockTexture("wool black.png", "wool black.png", "wool black.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_YELLOW_CONCRETE_STAIRS = new Block(323, "Yellow Concrete Stairs", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_CONCRETE_SLAB = new Block(324, "Yellow Concrete Slab", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_YELLOW_CONCRETE_FENCE = new Block(325, "Yellow Concrete Fence", new BlockTexture("yellow concrete.png", "yellow concrete.png", "yellow concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BLACK_CONCRETE_STAIRS = new Block(326, "Black Concrete Stairs", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLACK_CONCRETE_SLAB = new Block(327, "Black Concrete Slab", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLACK_CONCRETE_FENCE = new Block(328, "Black Concrete Fence", new BlockTexture("black concrete.png", "black concrete.png", "black concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BLUE_CONCRETE_STAIRS = new Block(329, "Blue Concrete Stairs", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLUE_CONCRETE_SLAB = new Block(330, "Blue Concrete Slab", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLUE_CONCRETE_FENCE = new Block(331, "Blue Concrete Fence", new BlockTexture("blue concrete.png", "blue concrete.png", "blue concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BROWN_CONCRETE_STAIRS = new Block(332, "Brown Concrete Stairs", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BROWN_CONCRETE_SLAB = new Block(333, "Brown Concrete Slab", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BROWN_CONCRETE_FENCE = new Block(334, "Brown Concrete Fence", new BlockTexture("brown concrete.png", "brown concrete.png", "brown concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_CYAN_CONCRETE_STAIRS = new Block(335, "Cyan Concrete Stairs", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CYAN_CONCRETE_SLAB = new Block(336, "Cyan Concrete Slab", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CYAN_CONCRETE_FENCE = new Block(337, "Cyan Concrete Fence", new BlockTexture("cyan concrete.png", "cyan concrete.png", "cyan concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_GREY_CONCRETE_STAIRS = new Block(338, "Grey Concrete Stairs", new BlockTexture("grey concrete.png", "grey concrete.png", "grey concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREY_CONCRETE_SLAB = new Block(339, "Grey Concrete Slab", new BlockTexture("grey concrete.png", "grey concrete.png", "grey concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_CONCRETE_FENCE = new Block(340, "Grey Concrete Fence", new BlockTexture("grey concrete.png", "grey concrete.png", "grey concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_GREEN_CONCRETE_STAIRS = new Block(341, "Green Concrete Stairs", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREEN_CONCRETE_SLAB = new Block(342, "Green Concrete Slab", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREEN_CONCRETE_FENCE = new Block(343, "Green Concrete Fence", new BlockTexture("green concrete.png", "green concrete.png", "green concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE_STAIRS = new Block(344, "Light Blue Concrete Stairs", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE_SLAB = new Block(345, "Light Blue Concrete Slab", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIGHT_BLUE_CONCRETE_FENCE = new Block(346, "Light Blue Concrete Fence", new BlockTexture("light blue concrete.png", "light blue concrete.png", "light blue concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_LIGHT_GREY_CONCRETE_STAIRS = new Block(347, "Light Grey Concrete Stairs", new BlockTexture("light grey concrete.png", "light grey concrete.png", "light grey concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_GREY_CONCRETE_SLAB = new Block(348, "Light Grey Concrete Slab", new BlockTexture("light grey concrete.png", "light grey concrete.png", "light grey concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIGHT_GREY_CONCRETE_FENCE = new Block(349, "Light Grey Concrete Fence", new BlockTexture("light grey concrete.png", "light grey concrete.png", "light grey concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_LIME_CONCRETE_STAIRS = new Block(350, "Lime Concrete Stairs", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIME_CONCRETE_SLAB = new Block(351, "Lime Concrete Slab", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIME_CONCRETE_FENCE = new Block(352, "Lime Concrete Fence", new BlockTexture("lime concrete.png", "lime concrete.png", "lime concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_MAGENTA_CONCRETE_STAIRS = new Block(353, "Magenta Concrete Stairs", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_CONCRETE_SLAB = new Block(354, "Magenta Concrete Slab", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_CONCRETE_FENCE = new Block(355, "Magenta Concrete Fence", new BlockTexture("magenta concrete.png", "magenta concrete.png", "magenta concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_ORANGE_CONCRETE_STAIRS = new Block(356, "Orange Concrete Stairs", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_CONCRETE_SLAB = new Block(357, "Orange Concrete Slab", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_ORANGE_CONCRETE_FENCE = new Block(358, "Orange Concrete Fence", new BlockTexture("orange concrete.png", "orange concrete.png", "orange concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_PINK_CONCRETE_STAIRS = new Block(359, "Pink Concrete Stairs", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PINK_CONCRETE_SLAB = new Block(360, "Pink Concrete Slab", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PINK_CONCRETE_FENCE = new Block(361, "Pink Concrete Fence", new BlockTexture("pink concrete.png", "pink concrete.png", "pink concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_PURPLE_CONCRETE_STAIRS = new Block(362, "Purple Concrete Stairs", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_CONCRETE_SLAB = new Block(363, "Purple Concrete Slab", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PURPLE_CONCRETE_FENCE = new Block(364, "Purple Concrete Fence", new BlockTexture("purple concrete.png", "purple concrete.png", "purple concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_RED_CONCRETE_STAIRS = new Block(365, "Red Concrete Stairs", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_RED_CONCRETE_SLAB = new Block(366, "Red Concrete Slab", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RED_CONCRETE_FENCE = new Block(367, "Red Concrete Fence", new BlockTexture("red concrete.png", "red concrete.png", "red concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_WHITE_CONCRETE_STAIRS = new Block(368, "White Concrete Stairs", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WHITE_CONCRETE_SLAB = new Block(369, "White Concrete Slab", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WHITE_CONCRETE_FENCE = new Block(370, "White Concrete Fence", new BlockTexture("white concrete.png", "white concrete.png", "white concrete.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_YELLOW_GLAZED_TERACOTTA_STAIRS = new Block(371, "Yellow Glazed Teracotta Stairs", new BlockTexture("yellow glazed teracotta.png", "yellow glazed teracotta.png", "yellow glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_GLAZED_TERACOTTA_SLAB = new Block(372, "Yellow Glazed Teracotta Slab", new BlockTexture("yellow glazed teracotta.png", "yellow glazed teracotta.png", "yellow glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLACK_GLAZED_TERACOTTA_STAIRS = new Block(373, "Black Glazed Teracotta Stairs", new BlockTexture("black glazed teracotta.png", "black glazed teracotta.png", "black glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLACK_GLAZED_TERACOTTA_SLAB = new Block(374, "Black Glazed Teracotta Slab", new BlockTexture("black glazed teracotta.png", "black glazed teracotta.png", "black glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLUE_GLAZED_TERACOTTA_STAIRS = new Block(375, "Blue Glazed Teracotta Stairs", new BlockTexture("blue glazed teracotta.png", "blue glazed teracotta.png", "blue glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLUE_GLAZED_TERACOTTA_SLAB = new Block(376, "Blue Glazed Teracotta Slab", new BlockTexture("blue glazed teracotta.png", "blue glazed teracotta.png", "blue glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BROWN_GLAZED_TERACOTTA_STAIRS = new Block(377, "Brown Glazed Teracotta Stairs", new BlockTexture("brown glazed teracotta.png", "brown glazed teracotta.png", "brown glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BROWN_GLAZED_TERACOTTA_SLAB = new Block(378, "Brown Glazed Teracotta Slab", new BlockTexture("brown glazed teracotta.png", "brown glazed teracotta.png", "brown glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CYAN_GLAZED_TERACOTTA_STAIRS = new Block(379, "Cyan Glazed Teracotta Stairs", new BlockTexture("cyan glazed teracotta.png", "cyan glazed teracotta.png", "cyan glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CYAN_GLAZED_TERACOTTA_SLAB = new Block(380, "Cyan Glazed Teracotta Slab", new BlockTexture("cyan glazed teracotta.png", "cyan glazed teracotta.png", "cyan glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_GLAZED_TERACOTTA_STAIRS = new Block(381, "Grey Glazed Teracotta Stairs", new BlockTexture("grey glazed teracotta.png", "grey glazed teracotta.png", "grey glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREY_GLAZED_TERACOTTA_SLAB = new Block(382, "Grey Glazed Teracotta Slab", new BlockTexture("grey glazed teracotta.png", "grey glazed teracotta.png", "grey glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREEN_GLAZED_TERACOTTA_STAIRS = new Block(383, "Green Glazed Teracotta Stairs", new BlockTexture("green glazed teracotta.png", "green glazed teracotta.png", "green glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREEN_GLAZED_TERACOTTA_SLAB = new Block(384, "Green Glazed Teracotta Slab", new BlockTexture("green glazed teracotta.png", "green glazed teracotta.png", "green glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_STAIRS = new Block(385, "Light Blue Glazed Teracotta Stairs", new BlockTexture("light blue glazed teracotta.png", "light blue glazed teracotta.png", "light blue glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_BLUE_GLAZED_TERACOTTA_SLAB = new Block(386, "Light Blue Glazed Teracotta Slab", new BlockTexture("light blue glazed teracotta.png", "light blue glazed teracotta.png", "light blue glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIGHT_GREY_GLAZED_TERACOTTA_STAIRS = new Block(387, "Light Grey Glazed Teracotta Stairs", new BlockTexture("light grey glazed teracotta.png", "light grey glazed teracotta.png", "light grey glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_GREY_GLAZED_TERACOTTA_SLAB = new Block(388, "Light Grey Glazed Teracotta Slab", new BlockTexture("light grey glazed teracotta.png", "light grey glazed teracotta.png", "light grey glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIME_GLAZED_TERACOTTA_STAIRS = new Block(389, "Lime Glazed Teracotta Stairs", new BlockTexture("lime glazed teracotta.png", "lime glazed teracotta.png", "lime glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIME_GLAZED_TERACOTTA_SLAB = new Block(390, "Lime Glazed Teracotta Slab", new BlockTexture("lime glazed teracotta.png", "lime glazed teracotta.png", "lime glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_GLAZED_TERACOTTA_STAIRS = new Block(391, "Magenta Glazed Teracotta Stairs", new BlockTexture("magenta glazed teracotta.png", "magenta glazed teracotta.png", "magenta glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_GLAZED_TERACOTTA_SLAB = new Block(392, "Magenta Glazed Teracotta Slab", new BlockTexture("magenta glazed teracotta.png", "magenta glazed teracotta.png", "magenta glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_ORANGE_GLAZED_TERACOTTA_STAIRS = new Block(393, "Orange Glazed Teracotta Stairs", new BlockTexture("orange glazed teracotta.png", "orange glazed teracotta.png", "orange glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_GLAZED_TERACOTTA_SLAB = new Block(394, "Orange Glazed Teracotta Slab", new BlockTexture("orange glazed teracotta.png", "orange glazed teracotta.png", "orange glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PINK_GLAZED_TERACOTTA_STAIRS = new Block(395, "Pink Glazed Teracotta Stairs", new BlockTexture("pink glazed teracotta.png", "pink glazed teracotta.png", "pink glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PINK_GLAZED_TERACOTTA_SLAB = new Block(396, "Pink Glazed Teracotta Slab", new BlockTexture("pink glazed teracotta.png", "pink glazed teracotta.png", "pink glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PURPLE_GLAZED_TERACOTTA_STAIRS = new Block(397, "Purple Glazed Teracotta Stairs", new BlockTexture("purple glazed teracotta.png", "purple glazed teracotta.png", "purple glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_GLAZED_TERACOTTA_SLAB = new Block(398, "Purple Glazed Teracotta Slab", new BlockTexture("purple glazed teracotta.png", "purple glazed teracotta.png", "purple glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RED_GLAZED_TERACOTTA_STAIRS = new Block(399, "Red Glazed Teracotta Stairs", new BlockTexture("red glazed teracotta.png", "red glazed teracotta.png", "red glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_RED_GLAZED_TERACOTTA_SLAB = new Block(400, "Red Glazed Teracotta Slab", new BlockTexture("red glazed teracotta.png", "red glazed teracotta.png", "red glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WHITE_GLAZED_TERACOTTA_STAIRS = new Block(401, "White Glazed Teracotta Stairs", new BlockTexture("white glazed teracotta.png", "white glazed teracotta.png", "white glazed teracotta.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WHITE_GLAZED_TERACOTTA_SLAB = new Block(402, "White Glazed Teracotta Slab", new BlockTexture("white glazed teracotta.png", "white glazed teracotta.png", "white glazed teracotta.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_OAK_LADDER = new Block(403, "Oak Ladder", new BlockTexture("oak ladder.png", "oak ladder.png", "oak ladder.png"), false, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_MINECART_ROAD_BLOCK = new Block(404, "Minecart Road Block", new BlockTexture("minecart road.png", "minecart road side.png", "minecart road side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_STONE = new Block(405, "Stone", new BlockTexture("stone.png", "stone.png", "stone.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BAMBOO_WOOD_FENCE = new Block(406, "Bamboo Wood Fence", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_CROISSANT = new Block(407, "Croissant", new BlockTexture("croissant.png", "croissant.png", "croissant.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_MINECART_ROAD_SLAB = new Block(408, "Minecart Road Slab", new BlockTexture("minecart road.png", "minecart road side.png", "minecart road side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PRISMARINE_BRICKS = new Block(409, "Prismarine Bricks", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_DARK_PRISMARINE_BRICKS = new Block(410, "Dark Prismarine Bricks", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GLASS_STAIRS = new Block(411, "Glass Stairs", new BlockTexture("glass.png", "glass.png", "glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GLASS_SLAB = new Block(412, "Glass Slab", new BlockTexture("glass.png", "glass.png", "glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLUE_TORCH = new Block(413, "Blue Torch", new BlockTexture("blue torch.png", "blue torch.png", "blue torch.png"), (b) -> {
        b.opaque = false;
        b.solid = false;
        b.type = RenderType.TORCH;
        b.setIcon("blue_torch.png");
    });
    public static final Block BLOCK_CEMENT_STAIRS = new Block(414, "Cement Stairs", new BlockTexture("cement.png", "cement.png", "cement.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CEMENT_FENCE = new Block(415, "Cement Fence", new BlockTexture("cement.png", "cement.png", "cement.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_CEMENT_SLAB = new Block(416, "Cement Slab", new BlockTexture("cement.png", "cement.png", "cement.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_OBSIDIAN_SLAB = new Block(417, "Obsidian Slab", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_OBSIDIAN_FENCE = new Block(418, "Obsidian Fence", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_LAPIS_LAZUL_STAIRS = new Block(419, "Lapis Lazul Stairs", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LAPIS_LAZUL_BLOCK_SLAB = new Block(420, "Lapis Lazul Block Slab", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LAPIS_LAZUL_FENCE = new Block(421, "Lapis Lazul Fence", new BlockTexture("lapis lazul.png", "lapis lazul.png", "lapis lazul.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_IRON_STAIRS = new Block(422, "Iron Stairs", new BlockTexture("iron.png", "iron.png", "iron.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_IRON_BLOCK_SLAB = new Block(423, "Iron Block Slab", new BlockTexture("iron.png", "iron.png", "iron.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_IRON_FENCE = new Block(424, "Iron Fence", new BlockTexture("iron.png", "iron.png", "iron.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_GOLD_STAIRS = new Block(425, "Gold Stairs", new BlockTexture("gold.png", "gold.png", "gold.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GOLD_BLOCK_SLAB = new Block(426, "Gold Block Slab", new BlockTexture("gold.png", "gold.png", "gold.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GOLD_FENCE = new Block(427, "Gold Fence", new BlockTexture("gold.png", "gold.png", "gold.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_EMERALD_STAIRS = new Block(428, "Emerald Stairs", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_EMERALD_BLOCK_SLAB = new Block(429, "Emerald Block Slab", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_EMERALD_FENCE = new Block(430, "Emerald Fence", new BlockTexture("emerald.png", "emerald.png", "emerald.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_DIAMOND_STAIRS = new Block(431, "Diamond Stairs", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_DIAMOND_BLOCK_SLAB = new Block(432, "Diamond Block Slab", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_DIAMOND_FENCE = new Block(433, "Diamond Fence", new BlockTexture("diamond.png", "diamond.png", "diamond.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_CROSSWALK_PAINT = new Block(434, "Crosswalk Paint", new BlockTexture("crosswalk paint.png", "crosswalk paint.png", "crosswalk paint.png"), false, false, RenderType.FLOOR);
    public static final Block BLOCK_DARK_PRISMARINE_BRICK_STAIRS = new Block(435, "Dark Prismarine Brick Stairs", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PRISMARINE_BRICK_SLAB = new Block(436, "Prismarine Brick Slab", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_OBSIDIAN_STAIRS = new Block(437, "Obsidian Stairs", new BlockTexture("obsidian.png", "obsidian.png", "obsidian.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PRISMARINE_BRICK_FENCE = new Block(438, "Prismarine Brick Fence", new BlockTexture("prismarine brick.png", "prismarine brick.png", "prismarine brick.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_RED_SANDSTONE_PILLAR = new Block(439, "Red Sandstone Pillar", new BlockTexture("red sandstone.png", "red sandstone bottom.png", "red sandstone side.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_STONE_BRICK_PILLAR = new Block(440, "Stone Brick Pillar", new BlockTexture("stone brick.png", "stone brick.png", "stone brick.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_SANDSTONE_PILLAR = new Block(441, "Palisade Sandstone Pillar", new BlockTexture("palisade sandstone.png", "palisade sandstone.png", "palisade sandstone side.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_STONE_PILLAR = new Block(442, "Palisade Stone Pillar", new BlockTexture("palisade stone.png", "palisade stone.png", "palisade stone side.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PALISADE_STONE_2_PILLAR = new Block(443, "Palisade Stone 2 Pillar", new BlockTexture("palisade stone 2.png", "palisade stone 2.png", "palisade stone 2.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_CRACKED_STONE_PILLAR = new Block(444, "Cracked Stone Pillar", new BlockTexture("cracked stone.png", "cracked stone.png", "cracked stone.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_STONE_WITH_VINES_PILLAR = new Block(445, "Stone with Vines Pillar", new BlockTexture("stone with vines.png", "stone with vines.png", "stone with vines.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_FLAT_DRAGON_VINES = new Block(446, "Flat Dragon Vines", new BlockTexture("flat dragon vines.png", "flat dragon vines.png", "flat dragon vines.png"), true, false, RenderType.WALL_ITEM);
    public static final Block BLOCK_DRAGON_VINES = new Block(447, "Dragon Vines", new BlockTexture("flat dragon vines.png", "flat dragon vines.png", "flat dragon vines.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_RED_PALISADE_SANDSTONE_PILLAR = new Block(448, "Red Palisade Sandstone Pillar", new BlockTexture("red palisade sandstone.png", "red palisade sandstone.png", "red palisade sandstone side.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_MARBLE_PILLAR_BLOCK = new Block(449, "Marble Pillar Block", new BlockTexture("marble.png", "marble.png", "marble side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_QUARTZ_PILLAR = new Block(450, "Quartz Pillar", new BlockTexture("quartz.png", "quartz.png", "quartz side.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_MARBLE_PILLAR = new Block(451, "Marble Pillar", new BlockTexture("marble.png", "marble.png", "marble side.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_FARMLAND = new Block(452, "Farmland hidden", new BlockTexture("farmland.png", "dirt.png", "dirt.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ROAD_MARKINGS = new Block(453, "Road Markings", new BlockTexture("road markings.png", "road markings.png", "road markings.png"), false, false, RenderType.FLOOR);
    public static final Block BLOCK_GRANITE_BRICK_PILLAR = new Block(454, "Granite Brick Pillar", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_GRANITE_BRICK_SLAB = new Block(455, "Granite Brick Slab", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GRANITE_BRICK_FENCE = new Block(456, "Granite Brick Fence", new BlockTexture("granite brick.png", "granite brick.png", "granite brick.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_CAMPFIRE = new Block(457, "Campfire", new BlockTexture("campfire.png", "campfire.png", "campfire.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_DARK_PRISMARINE_BRICK_SLAB = new Block(458, "Dark Prismarine Brick Slab", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_DARK_PRISMARINE_BRICK_FENCE = new Block(459, "Dark Prismarine Brick Fence", new BlockTexture("dark prismarine bricks.png", "dark prismarine bricks.png", "dark prismarine bricks.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_ENGRAVED_SANDSTONE = new Block(460, "Engraved Sandstone", new BlockTexture("sandstone.png", "sandstone.png", "engraved sandstone side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ENGRAVED_RED_SANDSTONE = new Block(461, "Engraved Red Sandstone", new BlockTexture("red sandstone.png", "red sandstone.png", "engraved red sandstone side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_MARBLE_TILE = new Block(462, "Orange Marble Tile", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CHECKERBOARD_CHISELED_MARBLE = new Block(463, "Checkerboard Chiseled Marble", new BlockTexture("checkerboard chiseled marble.png", "checkerboard chiseled marble.png", "checkerboard chiseled marble.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CHISELED_MARBLE = new Block(464, "Chiseled Marble", new BlockTexture("chiseled marble.png", "chiseled marble.png", "chiseled marble.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CHISELED_QUARTZ = new Block(465, "Chiseled Quartz", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MARBLE_TILE = new Block(466, "Marble Tile", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_MARBLE_TILE = new Block(467, "Blue Marble Tile", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_MARBLE_TILE = new Block(468, "Green Marble Tile", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_MARBLE_TILE_SLAB = new Block(469, "Orange Marble Tile Slab", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_MARBLE_TILE = new Block(470, "Grey Marble Tile", new BlockTexture("grey marble tile.png", "grey marble tile.png", "grey marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_MARBLE_TILE_STAIRS = new Block(471, "Orange Marble Tile Stairs", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREEN_MARBLE_TILE_SLAB = new Block(472, "Green Marble Tile Slab", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_YELLOW_STAINED_GLASS_STAIRS = new Block(473, "Yellow-Stained Glass Stairs", new BlockTexture("yellow stained glass.png", "yellow stained glass.png", "yellow stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_STAINED_GLASS_SLAB = new Block(474, "Yellow-Stained Glass Slab", new BlockTexture("yellow stained glass.png", "yellow stained glass.png", "yellow stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLACK_STAINED_GLASS_STAIRS = new Block(475, "Black-Stained Glass Stairs", new BlockTexture("black stained glass.png", "black stained glass.png", "black stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLACK_STAINED_GLASS_SLAB = new Block(476, "Black-Stained Glass Slab", new BlockTexture("black stained glass.png", "black stained glass.png", "black stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLUE_STAINED_GLASS_STAIRS = new Block(477, "Blue-Stained Glass Stairs", new BlockTexture("blue stained glass.png", "blue stained glass.png", "blue stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLUE_STAINED_GLASS_SLAB = new Block(478, "Blue-Stained Glass Slab", new BlockTexture("blue stained glass.png", "blue stained glass.png", "blue stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BROWN_STAINED_GLASS_STAIRS = new Block(479, "Brown-Stained Glass Stairs", new BlockTexture("brown stained glass.png", "brown stained glass.png", "brown stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BROWN_STAINED_GLASS_SLAB = new Block(480, "Brown-Stained Glass Slab", new BlockTexture("brown stained glass.png", "brown stained glass.png", "brown stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CYAN_STAINED_GLASS_STAIRS = new Block(481, "Cyan-Stained Glass Stairs", new BlockTexture("cyan stained glass.png", "cyan stained glass.png", "cyan stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CYAN_STAINED_GLASS_SLAB = new Block(482, "Cyan-Stained Glass Slab", new BlockTexture("cyan stained glass.png", "cyan stained glass.png", "cyan stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_STAINED_GLASS_STAIRS = new Block(483, "Grey-Stained Glass Stairs", new BlockTexture("grey stained glass.png", "grey stained glass.png", "grey stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREY_STAINED_GLASS_SLAB = new Block(484, "Grey-Stained Glass Slab", new BlockTexture("grey stained glass.png", "grey stained glass.png", "grey stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREEN_STAINED_GLASS_STAIRS = new Block(485, "Green-Stained Glass Stairs", new BlockTexture("green stained glass.png", "green stained glass.png", "green stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREEN_STAINED_GLASS_SLAB = new Block(486, "Green-Stained Glass Slab", new BlockTexture("green stained glass.png", "green stained glass.png", "green stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_GLASS_STAIRS = new Block(487, "Light Blue-Stained Glass Stairs", new BlockTexture("light blue stained glass.png", "light blue stained glass.png", "light blue stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_GLASS_SLAB = new Block(488, "Light Blue-Stained Glass Slab", new BlockTexture("light blue stained glass.png", "light blue stained glass.png", "light blue stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIGHT_GREY_STAINED_GLASS_STAIRS = new Block(489, "Light Grey-Stained Glass Stairs", new BlockTexture("light grey stained glass.png", "light grey stained glass.png", "light grey stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_GREY_STAINED_GLASS_SLAB = new Block(490, "Light Grey-Stained Glass Slab", new BlockTexture("light grey stained glass.png", "light grey stained glass.png", "light grey stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIME_STAINED_GLASS_STAIRS = new Block(491, "Lime-Stained Glass Stairs", new BlockTexture("lime stained glass.png", "lime stained glass.png", "lime stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIME_STAINED_GLASS_SLAB = new Block(492, "Lime-Stained Glass Slab", new BlockTexture("lime stained glass.png", "lime stained glass.png", "lime stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_STAINED_GLASS_STAIRS = new Block(493, "Magenta-Stained Glass Stairs", new BlockTexture("magenta stained glass.png", "magenta stained glass.png", "magenta stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_STAINED_GLASS_SLAB = new Block(494, "Magenta-Stained Glass Slab", new BlockTexture("magenta stained glass.png", "magenta stained glass.png", "magenta stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_ORANGE_STAINED_GLASS_STAIRS = new Block(495, "Orange-Stained Glass Stairs", new BlockTexture("orange stained glass.png", "orange stained glass.png", "orange stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_STAINED_GLASS_SLAB = new Block(496, "Orange-Stained Glass Slab", new BlockTexture("orange stained glass.png", "orange stained glass.png", "orange stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PINK_STAINED_GLASS_STAIRS = new Block(497, "Pink-Stained Glass Stairs", new BlockTexture("pink stained glass.png", "pink stained glass.png", "pink stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PINK_STAINED_GLASS_SLAB = new Block(498, "Pink-Stained Glass Slab", new BlockTexture("pink stained glass.png", "pink stained glass.png", "pink stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PURPLE_STAINED_GLASS_STAIRS = new Block(499, "Purple-Stained Glass Stairs", new BlockTexture("purple stained glass.png", "purple stained glass.png", "purple stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_STAINED_GLASS_SLAB = new Block(500, "Purple-Stained Glass Slab", new BlockTexture("purple stained glass.png", "purple stained glass.png", "purple stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RED_STAINED_GLASS_STAIRS = new Block(501, "Red-Stained Glass Stairs", new BlockTexture("red stained glass.png", "red stained glass.png", "red stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_RED_STAINED_GLASS_SLAB = new Block(502, "Red-Stained Glass Slab", new BlockTexture("red stained glass.png", "red stained glass.png", "red stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WHITE_STAINED_GLASS_STAIRS = new Block(503, "White-Stained Glass Stairs", new BlockTexture("white stained glass.png", "white stained glass.png", "white stained glass.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WHITE_STAINED_GLASS_SLAB = new Block(504, "White-Stained Glass Slab", new BlockTexture("white stained glass.png", "white stained glass.png", "white stained glass.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CHECKERBOARD_CHISELED_MARBLE_STAIRS = new Block(505, "Checkerboard Chiseled Marble Stairs", new BlockTexture("checkerboard chiseled marble.png", "checkerboard chiseled marble.png", "checkerboard chiseled marble.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CHECKERBOARD_CHISELED_MARBLE_SLAB = new Block(506, "Checkerboard Chiseled Marble Slab", new BlockTexture("checkerboard chiseled marble.png", "checkerboard chiseled marble.png", "checkerboard chiseled marble.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CHISELED_MARBLE_STAIRS = new Block(507, "Chiseled Marble Stairs", new BlockTexture("chiseled marble.png", "chiseled marble.png", "chiseled marble.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CHISELED_MARBLE_SLAB = new Block(508, "Chiseled Marble Slab", new BlockTexture("chiseled marble.png", "chiseled marble.png", "chiseled marble.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MARBLE_TILE_STAIRS = new Block(509, "Marble Tile Stairs", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MARBLE_TILE_SLAB = new Block(510, "Marble Tile Slab", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_MARBLE_TILE_STAIRS = new Block(511, "Grey Marble Tile Stairs", new BlockTexture("grey marble tile.png", "grey marble tile.png", "grey marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREY_MARBLE_TILE_SLAB = new Block(512, "Grey Marble Tile Slab", new BlockTexture("grey marble tile.png", "grey marble tile.png", "grey marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLUE_MARBLE_TILE_STAIRS = new Block(513, "Blue Marble Tile Stairs", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLUE_MARBLE_TILE_SLAB = new Block(514, "Blue Marble Tile Slab", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREEN_MARBLE_TILE_STAIRS = new Block(515, "Green Marble Tile Stairs", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CHISELED_QUARTZ_STAIRS = new Block(516, "Chiseled Quartz Stairs", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CHISELED_QUARTZ_SLAB = new Block(517, "Chiseled Quartz Slab", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CHISELED_QUARTZ_PILLAR = new Block(518, "Chiseled Quartz Pillar", new BlockTexture("chiseled quartz.png", "chiseled quartz.png", "chiseled quartz.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_GREY_MARBLE_TILE_PILLAR = new Block(519, "Grey Marble Tile Pillar", new BlockTexture("grey marble tile.png", "grey marble tile.png", "grey marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BLUE_MARBLE_TILE_PILLAR = new Block(520, "Blue Marble Tile Pillar", new BlockTexture("blue marble tile.png", "blue marble tile.png", "blue marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_GREEN_MARBLE_TILE_PILLAR = new Block(521, "Green Marble Tile Pillar", new BlockTexture("green marble tile.png", "green marble tile.png", "green marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_ORANGE_MARBLE_TILE_PILLAR = new Block(522, "Orange Marble Tile Pillar", new BlockTexture("orange marble tile.png", "orange marble tile.png", "orange marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_MARBLE_TILE_PILLAR = new Block(523, "Marble Tile Pillar", new BlockTexture("marble tile.png", "marble tile.png", "marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_LAMP = new Block(524, "Lamp", new BlockTexture("lamp.png", "lamp.png", "lamp.png"), false, false, RenderType.LAMP);
    public static final Block BLOCK_BLUE_LAMP = new Block(525, "Blue Lamp", new BlockTexture("blue lamp.png", "blue lamp.png", "blue lamp.png"), false, false, RenderType.LAMP);
    public static final Block BLOCK_GLASS_PANE = new Block(526, "Glass Pane", new BlockTexture("glass.png", "glass.png", "glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_YELLOW_STAINED_GLASS_PANE = new Block(527, "Yellow-Stained Glass Pane", new BlockTexture("yellow stained glass.png", "yellow stained glass.png", "yellow stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_BLACK_STAINED_GLASS_PANE = new Block(528, "Black-Stained Glass Pane", new BlockTexture("black stained glass.png", "black stained glass.png", "black stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_BLUE_STAINED_GLASS_PANE = new Block(529, "Blue-Stained Glass Pane", new BlockTexture("blue stained glass.png", "blue stained glass.png", "blue stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_BROWN_STAINED_GLASS_PANE = new Block(530, "Brown-Stained Glass Pane", new BlockTexture("brown stained glass.png", "brown stained glass.png", "brown stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_CYAN_STAINED_GLASS_PANE = new Block(531, "Cyan-Stained Glass Pane", new BlockTexture("cyan stained glass.png", "cyan stained glass.png", "cyan stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_GREY_STAINED_GLASS_PANE = new Block(532, "Grey-Stained Glass Pane", new BlockTexture("grey stained glass.png", "grey stained glass.png", "grey stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_GREEN_STAINED_GLASS_PANE = new Block(533, "Green-Stained Glass Pane", new BlockTexture("green stained glass.png", "green stained glass.png", "green stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_GLASS_PANE = new Block(534, "Light Blue-Stained Glass Pane", new BlockTexture("light blue stained glass.png", "light blue stained glass.png", "light blue stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_LIGHT_GREY_STAINED_GLASS_PANE = new Block(535, "Light Grey-Stained Glass Pane", new BlockTexture("light grey stained glass.png", "light grey stained glass.png", "light grey stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_LIME_STAINED_GLASS_PANE = new Block(536, "Lime-Stained Glass Pane", new BlockTexture("lime stained glass.png", "lime stained glass.png", "lime stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_MAGENTA_STAINED_GLASS_PANE = new Block(537, "Magenta-Stained Glass Pane", new BlockTexture("magenta stained glass.png", "magenta stained glass.png", "magenta stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_ORANGE_STAINED_GLASS_PANE = new Block(538, "Orange-Stained Glass Pane", new BlockTexture("orange stained glass.png", "orange stained glass.png", "orange stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_PINK_STAINED_GLASS_PANE = new Block(539, "Pink-Stained Glass Pane", new BlockTexture("pink stained glass.png", "pink stained glass.png", "pink stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_PURPLE_STAINED_GLASS_PANE = new Block(540, "Purple-Stained Glass Pane", new BlockTexture("purple stained glass.png", "purple stained glass.png", "purple stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_RED_STAINED_GLASS_PANE = new Block(541, "Red-Stained Glass Pane", new BlockTexture("red stained glass.png", "red stained glass.png", "red stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_WHITE_STAINED_GLASS_PANE = new Block(542, "White-Stained Glass Pane", new BlockTexture("white stained glass.png", "white stained glass.png", "white stained glass.png"), true, false, RenderType.PANE);
    public static final Block BLOCK_START_BOUNDARY_BLOCK = new Block(543, "Start Boundary Block", new BlockTexture("start boundary.png", "start boundary.png", "start boundary side.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTE_BLOCK = new Block(544, "Paste Block", new BlockTexture("paste.png", "paste.png", "paste.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE = new Block(545, "Yellow Chiseled Marble Tile", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SUNFLOWER = new Block(546, "Sunflower hidden", new BlockTexture("sunflower.png", "sunflower bottom.png", "sunflower side.png"), false, false, RenderType.SUNFLOWER_HEAD);
    public static final Block BLOCK_SUNFLOWER_STALK = new Block(547, "Sunflower Stalk hidden", new BlockTexture("sunflower stalk.png", "sunflower stalk.png", "sunflower stalk.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_MEGA_TNT = new Block(548, "Mega TNT", new BlockTexture("tnt.png", "tnt bottom.png", "mega tnt side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SUNFLOWER_SEEDS = new Block(549, "Sunflower Seeds", new BlockTexture("carrot seeds.png", "carrot seeds.png", "carrot seeds.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CROSSTRACK = new Block(550, "CrossTrack", new BlockTexture("crosstrack.png", "crosstrack.png", "crosstrack.png"), true, false, RenderType.FLOOR);
    public static final Block BLOCK_ADDITIVE_PASTE_BLOCK = new Block(551, "Additive Paste Block", new BlockTexture("additive paste.png", "additive paste.png", "additive paste.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTE_ROTATE_BLOCK = new Block(552, "Paste Rotate Block", new BlockTexture("paste rotate.png", "paste rotate.png", "paste rotate side.png"), false, false, RenderType.SLAB);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE = new Block(553, "Black Chiseled Marble Tile", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE = new Block(554, "Blue Chiseled Marble Tile", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE = new Block(555, "Brown Chiseled Marble Tile", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE = new Block(556, "Cyan Chiseled Marble Tile", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREY_CHISELED_MARBLE_TILE = new Block(557, "Grey Chiseled Marble Tile", new BlockTexture("grey chiseled marble tile.png", "grey chiseled marble tile.png", "grey chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE = new Block(558, "Green Chiseled Marble Tile", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE = new Block(559, "Pastel-Blue Chiseled Marble Tile", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE = new Block(560, "Pastel-Green Chiseled Marble Tile", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE = new Block(561, "Magenta Chiseled Marble Tile", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE = new Block(562, "Orange Chiseled Marble Tile", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE = new Block(563, "Pink Chiseled Marble Tile", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE = new Block(564, "Purple Chiseled Marble Tile", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE = new Block(565, "Burgundy Chiseled Marble Tile", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BAMBOO_BLOCK = new Block(566, "Bamboo Block", new BlockTexture("bamboo.png", "bamboo.png", "bamboo side.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE = new Block(567, "Pastel-Red Chiseled Marble Tile", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_MARBLE_TILE = new Block(568, "Yellow Marble Tile", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_MARBLE_TILE = new Block(569, "Black Marble Tile", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ICE_BLOCK = new Block(570, "Ice Block", new BlockTexture("ice.png", "ice.png", "ice.png"), true, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BROWN_MARBLE_TILE = new Block(571, "Brown Marble Tile", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CYAN_MARBLE_TILE = new Block(572, "Cyan Marble Tile", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BAMBOO_WOOD = new Block(573, "Bamboo Wood", new BlockTexture("bamboo wood.png", "bamboo wood.png", "bamboo wood.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_BOTTLE = new Block(574, "Bottle", new BlockTexture("bottle.png", "bottle.png", "bottle.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE = new Block(575, "Pastel-Blue Marble Tile", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE = new Block(576, "Pastel-Green Marble Tile", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE = new Block(577, "Magenta Marble Tile", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_CUP = new Block(578, "Cup", new BlockTexture("cup.png", "cup.png", "cup.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_PINK_MARBLE_TILE = new Block(579, "Pink Marble Tile", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_MARBLE_TILE = new Block(580, "Purple Marble Tile", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE = new Block(581, "Burgundy Marble Tile", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE = new Block(582, "Pastel-Red Marble Tile", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WINE_GLASS = new Block(583, "Wine Glass", new BlockTexture("wine glass.png", "wine glass.png", "wine glass.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_YELLOW_STAINED_WOOD = new Block(584, "Yellow Stained Wood", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_BLACK_STAINED_WOOD = new Block(585, "Black Stained Wood", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_YELLOW_MARBLE_TILE_STAIRS = new Block(586, "Yellow Marble Tile Stairs", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_MARBLE_TILE_SLAB = new Block(587, "Yellow Marble Tile Slab", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_YELLOW_MARBLE_TILE_PILLAR = new Block(588, "Yellow Marble Tile Pillar", new BlockTexture("yellow marble tile.png", "yellow marble tile.png", "yellow marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BLACK_MARBLE_TILE_STAIRS = new Block(589, "Black Marble Tile Stairs", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLACK_MARBLE_TILE_SLAB = new Block(590, "Black Marble Tile Slab", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLACK_MARBLE_TILE_PILLAR = new Block(591, "Black Marble Tile Pillar", new BlockTexture("black marble tile.png", "black marble tile.png", "black marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BREAD = new Block(592, "Bread", new BlockTexture("bread.png", "bread.png", "bread.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_CYAN_STAINED_WOOD = new Block(593, "Cyan Stained Wood", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE_PILLAR = new Block(594, "Pink Chiseled Marble Tile Pillar", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BROWN_MARBLE_TILE_STAIRS = new Block(595, "Brown Marble Tile Stairs", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BROWN_MARBLE_TILE_SLAB = new Block(596, "Brown Marble Tile Slab", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BROWN_MARBLE_TILE_PILLAR = new Block(597, "Brown Marble Tile Pillar", new BlockTexture("brown marble tile.png", "brown marble tile.png", "brown marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_CYAN_MARBLE_TILE_STAIRS = new Block(598, "Cyan Marble Tile Stairs", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CYAN_MARBLE_TILE_SLAB = new Block(599, "Cyan Marble Tile Slab", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CYAN_MARBLE_TILE_PILLAR = new Block(600, "Cyan Marble Tile Pillar", new BlockTexture("cyan marble tile.png", "cyan marble tile.png", "cyan marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE_STAIRS = new Block(601, "Purple Chiseled Marble Tile Stairs", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE_SLAB = new Block(602, "Purple Chiseled Marble Tile Slab", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PURPLE_CHISELED_MARBLE_TILE_PILLAR = new Block(603, "Purple Chiseled Marble Tile Pillar", new BlockTexture("purple chiseled marble tile.png", "purple chiseled marble tile.png", "purple chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_STAIRS = new Block(604, "Burgundy Chiseled Marble Tile Stairs", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_SLAB = new Block(605, "Burgundy Chiseled Marble Tile Slab", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BURGUNDY_CHISELED_MARBLE_TILE_PILLAR = new Block(606, "Burgundy Chiseled Marble Tile Pillar", new BlockTexture("burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png", "burgundy chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE_STAIRS = new Block(607, "Pastel-Blue Marble Tile Stairs", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE_SLAB = new Block(608, "Pastel-Blue Marble Tile Slab", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PASTEL_BLUE_MARBLE_TILE_PILLAR = new Block(609, "Pastel-Blue Marble Tile Pillar", new BlockTexture("pastel blue marble tile.png", "pastel blue marble tile.png", "pastel blue marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE_STAIRS = new Block(610, "Pastel-Green Marble Tile Stairs", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE_SLAB = new Block(611, "Pastel-Green Marble Tile Slab", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PASTEL_GREEN_MARBLE_TILE_PILLAR = new Block(612, "Pastel-Green Marble Tile Pillar", new BlockTexture("pastel green marble tile.png", "pastel green marble tile.png", "pastel green marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE_STAIRS = new Block(613, "Magenta Marble Tile Stairs", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE_SLAB = new Block(614, "Magenta Marble Tile Slab", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_MARBLE_TILE_PILLAR = new Block(615, "Magenta Marble Tile Pillar", new BlockTexture("magenta marble tile.png", "magenta marble tile.png", "magenta marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_STAIRS = new Block(616, "Pastel-Red Chiseled Marble Tile Stairs", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_SLAB = new Block(617, "Pastel-Red Chiseled Marble Tile Slab", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PASTEL_RED_CHISELED_MARBLE_TILE_PILLAR = new Block(618, "Pastel-Red Chiseled Marble Tile Pillar", new BlockTexture("pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png", "pastel red chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PINK_MARBLE_TILE_STAIRS = new Block(619, "Pink Marble Tile Stairs", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PINK_MARBLE_TILE_SLAB = new Block(620, "Pink Marble Tile Slab", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PINK_MARBLE_TILE_PILLAR = new Block(621, "Pink Marble Tile Pillar", new BlockTexture("pink marble tile.png", "pink marble tile.png", "pink marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PURPLE_MARBLE_TILE_STAIRS = new Block(622, "Purple Marble Tile Stairs", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_MARBLE_TILE_SLAB = new Block(623, "Purple Marble Tile Slab", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PURPLE_MARBLE_TILE_PILLAR = new Block(624, "Purple Marble Tile Pillar", new BlockTexture("purple marble tile.png", "purple marble tile.png", "purple marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE_STAIRS = new Block(625, "Burgundy Marble Tile Stairs", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE_SLAB = new Block(626, "Burgundy Marble Tile Slab", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BURGUNDY_MARBLE_TILE_PILLAR = new Block(627, "Burgundy Marble Tile Pillar", new BlockTexture("burgundy marble tile.png", "burgundy marble tile.png", "burgundy marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE_STAIRS = new Block(628, "Pastel-Red Marble Tile Stairs", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE_SLAB = new Block(629, "Pastel-Red Marble Tile Slab", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PASTEL_RED_MARBLE_TILE_PILLAR = new Block(630, "Pastel-Red Marble Tile Pillar", new BlockTexture("pastel red marble tile.png", "pastel red marble tile.png", "pastel red marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE_STAIRS = new Block(631, "Yellow Chiseled Marble Tile Stairs", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE_SLAB = new Block(632, "Yellow Chiseled Marble Tile Slab", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_YELLOW_CHISELED_MARBLE_TILE_PILLAR = new Block(633, "Yellow Chiseled Marble Tile Pillar", new BlockTexture("yellow chiseled marble tile.png", "yellow chiseled marble tile.png", "yellow chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE_STAIRS = new Block(634, "Black Chiseled Marble Tile Stairs", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE_SLAB = new Block(635, "Black Chiseled Marble Tile Slab", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLACK_CHISELED_MARBLE_TILE_PILLAR = new Block(636, "Black Chiseled Marble Tile Pillar", new BlockTexture("black chiseled marble tile.png", "black chiseled marble tile.png", "black chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE_STAIRS = new Block(637, "Blue Chiseled Marble Tile Stairs", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE_SLAB = new Block(638, "Blue Chiseled Marble Tile Slab", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLUE_CHISELED_MARBLE_TILE_PILLAR = new Block(639, "Blue Chiseled Marble Tile Pillar", new BlockTexture("blue chiseled marble tile.png", "blue chiseled marble tile.png", "blue chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE_STAIRS = new Block(640, "Brown Chiseled Marble Tile Stairs", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE_SLAB = new Block(641, "Brown Chiseled Marble Tile Slab", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BROWN_CHISELED_MARBLE_TILE_PILLAR = new Block(642, "Brown Chiseled Marble Tile Pillar", new BlockTexture("brown chiseled marble tile.png", "brown chiseled marble tile.png", "brown chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE_STAIRS = new Block(643, "Cyan Chiseled Marble Tile Stairs", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE_SLAB = new Block(644, "Cyan Chiseled Marble Tile Slab", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CYAN_CHISELED_MARBLE_TILE_PILLAR = new Block(645, "Cyan Chiseled Marble Tile Pillar", new BlockTexture("cyan chiseled marble tile.png", "cyan chiseled marble tile.png", "cyan chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_GREY_CHISELED_MARBLE_TILE_STAIRS = new Block(646, "Grey Chiseled Marble Tile Stairs", new BlockTexture("grey chiseled marble tile.png", "grey chiseled marble tile.png", "grey chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREY_CHISELED_MARBLE_TILE_SLAB = new Block(647, "Grey Chiseled Marble Tile Slab", new BlockTexture("grey chiseled marble tile.png", "grey chiseled marble tile.png", "grey chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_CHISELED_MARBLE_TILE_PILLAR = new Block(648, "Grey Chiseled Marble Tile Pillar", new BlockTexture("grey chiseled marble tile.png", "grey chiseled marble tile.png", "grey chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE_STAIRS = new Block(649, "Green Chiseled Marble Tile Stairs", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE_SLAB = new Block(650, "Green Chiseled Marble Tile Slab", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREEN_CHISELED_MARBLE_TILE_PILLAR = new Block(651, "Green Chiseled Marble Tile Pillar", new BlockTexture("green chiseled marble tile.png", "green chiseled marble tile.png", "green chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_STAIRS = new Block(652, "Pastel-Blue Chiseled Marble Tile Stairs", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_SLAB = new Block(653, "Pastel-Blue Chiseled Marble Tile Slab", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PASTEL_BLUE_CHISELED_MARBLE_TILE_PILLAR = new Block(654, "Pastel-Blue Chiseled Marble Tile Pillar", new BlockTexture("pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png", "pastel blue chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_STAIRS = new Block(655, "Pastel-Green Chiseled Marble Tile Stairs", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_SLAB = new Block(656, "Pastel-Green Chiseled Marble Tile Slab", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PASTEL_GREEN_CHISELED_MARBLE_TILE_PILLAR = new Block(657, "Pastel-Green Chiseled Marble Tile Pillar", new BlockTexture("pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png", "pastel green chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE_STAIRS = new Block(658, "Magenta Chiseled Marble Tile Stairs", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE_SLAB = new Block(659, "Magenta Chiseled Marble Tile Slab", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_CHISELED_MARBLE_TILE_PILLAR = new Block(660, "Magenta Chiseled Marble Tile Pillar", new BlockTexture("magenta chiseled marble tile.png", "magenta chiseled marble tile.png", "magenta chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE_STAIRS = new Block(661, "Orange Chiseled Marble Tile Stairs", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE_SLAB = new Block(662, "Orange Chiseled Marble Tile Slab", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_ORANGE_CHISELED_MARBLE_TILE_PILLAR = new Block(663, "Orange Chiseled Marble Tile Pillar", new BlockTexture("orange chiseled marble tile.png", "orange chiseled marble tile.png", "orange chiseled marble tile.png"), true, false, RenderType.PILLAR);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE_STAIRS = new Block(664, "Pink Chiseled Marble Tile Stairs", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PINK_CHISELED_MARBLE_TILE_SLAB = new Block(665, "Pink Chiseled Marble Tile Slab", new BlockTexture("pink chiseled marble tile.png", "pink chiseled marble tile.png", "pink chiseled marble tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_STAINED_WOOD = new Block(666, "Grey Stained Wood", new BlockTexture("grey stained wood.png", "grey stained wood.png", "grey stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_GREEN_STAINED_WOOD = new Block(667, "Green Stained Wood", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD = new Block(668, "Light Blue Stained Wood", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_LIME_STAINED_WOOD = new Block(669, "Lime Stained Wood", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD = new Block(670, "Magenta Stained Wood", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ORANGE_STAINED_WOOD = new Block(671, "Orange Stained Wood", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PINK_STAINED_WOOD = new Block(672, "Pink Stained Wood", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PURPLE_STAINED_WOOD = new Block(673, "Purple Stained Wood", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_WHITE_SPACE_TILE_STAIRS = new Block(674, "White Space Tile Stairs", new BlockTexture("white space tile.png", "white space tile.png", "white space tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WHITE_STAINED_WOOD = new Block(675, "White Stained Wood", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ENGRAVED_SANDSTONE_2 = new Block(676, "Engraved Sandstone 2", new BlockTexture("sandstone.png", "sandstone.png", "engraved sandstone 2 side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_ENGRAVED_RED_SANDSTONE_2 = new Block(677, "Engraved Red Sandstone 2", new BlockTexture("red sandstone.png", "red sandstone.png", "engraved red sandstone 2 side.png"), true, true, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_PEONY_BUSH = new Block(678, "Peony Bush", new BlockTexture("peony bush.png", "peony bush.png", "peony bush.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_ICE_BLOCK_STAIRS = new Block(679, "Ice Block Stairs", new BlockTexture("ice.png", "ice.png", "ice.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_ICE_BLOCK_SLAB = new Block(680, "Ice Block Slab", new BlockTexture("ice.png", "ice.png", "ice.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_SOLAR_PANEL = new Block(681, "Solar Panel", new BlockTexture("solar l.png", "solar l side.png", "solar l side.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD_STAIRS = new Block(682, "Mosaic Bamboo Wood Stairs", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD_SLAB = new Block(683, "Mosaic Bamboo Wood Slab", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MOSAIC_BAMBOO_WOOD_FENCE = new Block(684, "Mosaic Bamboo Wood Fence", new BlockTexture("mosaic bamboo wood.png", "mosaic bamboo wood.png", "mosaic bamboo wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_YELLOW_STAINED_WOOD_STAIRS = new Block(685, "Yellow Stained Wood Stairs", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_YELLOW_STAINED_WOOD_SLAB = new Block(686, "Yellow Stained Wood Slab", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_YELLOW_STAINED_WOOD_FENCE = new Block(687, "Yellow Stained Wood Fence", new BlockTexture("yellow stained wood.png", "yellow stained wood.png", "yellow stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BLACK_STAINED_WOOD_STAIRS = new Block(688, "Black Stained Wood Stairs", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLACK_STAINED_WOOD_SLAB = new Block(689, "Black Stained Wood Slab", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLACK_STAINED_WOOD_FENCE = new Block(690, "Black Stained Wood Fence", new BlockTexture("black stained wood.png", "black stained wood.png", "black stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_BLUE_STAINED_WOOD_STAIRS = new Block(691, "Blue Stained Wood Stairs", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_BLUE_STAINED_WOOD_SLAB = new Block(692, "Blue Stained Wood Slab", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_BLUE_STAINED_WOOD_FENCE = new Block(693, "Blue Stained Wood Fence", new BlockTexture("blue stained wood.png", "blue stained wood.png", "blue stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_CYAN_STAINED_WOOD_STAIRS = new Block(694, "Cyan Stained Wood Stairs", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_CYAN_STAINED_WOOD_SLAB = new Block(695, "Cyan Stained Wood Slab", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_CYAN_STAINED_WOOD_FENCE = new Block(696, "Cyan Stained Wood Fence", new BlockTexture("cyan stained wood.png", "cyan stained wood.png", "cyan stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_GREY_STAINED_WOOD_STAIRS = new Block(697, "Grey Stained Wood Stairs", new BlockTexture("grey stained wood.png", "grey stained wood.png", "grey stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREY_STAINED_WOOD_SLAB = new Block(698, "Grey Stained Wood Slab", new BlockTexture("grey stained wood.png", "grey stained wood.png", "grey stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREY_STAINED_WOOD_FENCE = new Block(699, "Grey Stained Wood Fence", new BlockTexture("grey stained wood.png", "grey stained wood.png", "grey stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_GREEN_STAINED_WOOD_STAIRS = new Block(700, "Green Stained Wood Stairs", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GREEN_STAINED_WOOD_SLAB = new Block(701, "Green Stained Wood Slab", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GREEN_STAINED_WOOD_FENCE = new Block(702, "Green Stained Wood Fence", new BlockTexture("green stained wood.png", "green stained wood.png", "green stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD_STAIRS = new Block(703, "Light Blue Stained Wood Stairs", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD_SLAB = new Block(704, "Light Blue Stained Wood Slab", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIGHT_BLUE_STAINED_WOOD_FENCE = new Block(705, "Light Blue Stained Wood Fence", new BlockTexture("light blue stained wood.png", "light blue stained wood.png", "light blue stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_LIME_STAINED_WOOD_STAIRS = new Block(706, "Lime Stained Wood Stairs", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_LIME_STAINED_WOOD_SLAB = new Block(707, "Lime Stained Wood Slab", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_LIME_STAINED_WOOD_FENCE = new Block(708, "Lime Stained Wood Fence", new BlockTexture("lime stained wood.png", "lime stained wood.png", "lime stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD_STAIRS = new Block(709, "Magenta Stained Wood Stairs", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD_SLAB = new Block(710, "Magenta Stained Wood Slab", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_MAGENTA_STAINED_WOOD_FENCE = new Block(711, "Magenta Stained Wood Fence", new BlockTexture("magenta stained wood.png", "magenta stained wood.png", "magenta stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_ORANGE_STAINED_WOOD_STAIRS = new Block(712, "Orange Stained Wood Stairs", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_ORANGE_STAINED_WOOD_SLAB = new Block(713, "Orange Stained Wood Slab", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_ORANGE_STAINED_WOOD_FENCE = new Block(714, "Orange Stained Wood Fence", new BlockTexture("orange stained wood.png", "orange stained wood.png", "orange stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_PINK_STAINED_WOOD_STAIRS = new Block(715, "Pink Stained Wood Stairs", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PINK_STAINED_WOOD_SLAB = new Block(716, "Pink Stained Wood Slab", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PINK_STAINED_WOOD_FENCE = new Block(717, "Pink Stained Wood Fence", new BlockTexture("pink stained wood.png", "pink stained wood.png", "pink stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_PURPLE_STAINED_WOOD_STAIRS = new Block(718, "Purple Stained Wood Stairs", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_PURPLE_STAINED_WOOD_SLAB = new Block(719, "Purple Stained Wood Slab", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_PURPLE_STAINED_WOOD_FENCE = new Block(720, "Purple Stained Wood Fence", new BlockTexture("purple stained wood.png", "purple stained wood.png", "purple stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_RED_STAINED_WOOD_STAIRS = new Block(721, "Red Stained Wood Stairs", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_RED_STAINED_WOOD_SLAB = new Block(722, "Red Stained Wood Slab", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_RED_STAINED_WOOD_FENCE = new Block(723, "Red Stained Wood Fence", new BlockTexture("red stained wood.png", "red stained wood.png", "red stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_WHITE_STAINED_WOOD_STAIRS = new Block(724, "White Stained Wood Stairs", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_WHITE_STAINED_WOOD_SLAB = new Block(725, "White Stained Wood Slab", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_WHITE_STAINED_WOOD_FENCE = new Block(726, "White Stained Wood Fence", new BlockTexture("white stained wood.png", "white stained wood.png", "white stained wood.png"), true, false, RenderType.FENCE);
    public static final Block BLOCK_WHITE_SPACE_TILE = new Block(727, "White Space Tile", new BlockTexture("white space tile.png", "white space tile.png", "white space tile.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_GRAY_SPACE_TILE = new Block(728, "Gray Space Tile", new BlockTexture("gray space tile.png", "gray space tile.png", "gray space tile.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_WHITE_SPACE_TILE_SLAB = new Block(729, "White Space Tile Slab", new BlockTexture("white space tile.png", "white space tile.png", "white space tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_GRAY_SPACE_TILE_STAIRS = new Block(730, "Gray Space Tile Stairs", new BlockTexture("gray space tile.png", "gray space tile.png", "gray space tile.png"), true, false, RenderType.STAIRS);
    public static final Block BLOCK_GRAY_SPACE_TILE_SLAB = new Block(731, "Gray Space Tile Slab", new BlockTexture("gray space tile.png", "gray space tile.png", "gray space tile.png"), true, false, RenderType.SLAB);
    public static final Block BLOCK_SPRUCE_LOG = new Block(732, "Spruce Log", new BlockTexture("spruce log.png", "spruce log.png", "spruce log side.png"), true, true, RenderType.ORIENTABLE_BLOCK);
    public static final Block BLOCK_SPRUCE_LEAVES = new Block(733, "Spruce Leaves", new BlockTexture("spruce leaves.png", "spruce leaves.png", "spruce leaves.png"), false, false, BlockList.DEFAULT_BLOCK_TYPE_ID);
    public static final Block BLOCK_SPRUCE_SAPLING = new Block(734, "Spruce Sapling", new BlockTexture("spruce sapling.png", "spruce sapling.png", "spruce sapling.png"), false, false, RenderType.SPRITE);
    public static final Block BLOCK_PASTE_VERTEX_BLOCK = new Block(735, "Paste Vertex Block", new BlockTexture("paste rotate.png", "paste rotate.png", "paste vertex side.png"), false, false, RenderType.SLAB);
    public static final Block BLOCK_MERGE_TRACK = new Block(736, "Merge Track", new BlockTexture("merge track.png", "merge track.png", "merge track.png"), true, false, RenderType.FLOOR);


}
