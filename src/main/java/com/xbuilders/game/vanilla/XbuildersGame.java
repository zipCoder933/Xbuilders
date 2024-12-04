/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.builtinMechanics.fire.FirePropagation;
import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.*;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.loot.LootTableRegistry;
import com.xbuilders.engine.items.recipes.CraftingRecipe;
import com.xbuilders.engine.items.recipes.RecipeRegistry;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.ui.gameScene.UI_Hotbar;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.data.WorldData;
import com.xbuilders.game.vanilla.ui.*;
import com.xbuilders.game.vanilla.blockTools.BlockTools;
import com.xbuilders.game.vanilla.items.Blocks;
import com.xbuilders.game.vanilla.items.Entities;
import com.xbuilders.game.vanilla.items.Items;
import com.xbuilders.game.vanilla.items.blocks.RenderType;
import com.xbuilders.game.vanilla.items.blocks.type.*;
import com.xbuilders.game.vanilla.propagation.*;
import com.xbuilders.game.vanilla.skins.FoxSkin;
import com.xbuilders.game.vanilla.terrain.DevTerrain;
import com.xbuilders.game.vanilla.terrain.FlatTerrain;
import com.xbuilders.game.vanilla.terrain.complexTerrain.ComplexTerrain;
import com.xbuilders.game.vanilla.terrain.defaultTerrain.DefaultTerrain;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.xbuilders.engine.ui.gameScene.GameUI.printKeyConsumption;

/**
 * @author zipCoder933
 */
public class XbuildersGame extends Game {


    public XbuildersGame(MainWindow window) {
        super(window);

        //add skins
        availableSkins.put(0, (p) -> new FoxSkin(p, "red"));
        availableSkins.put(1, (p) -> new FoxSkin(p, "yellow"));
        availableSkins.put(2, (p) -> new FoxSkin(p, "blue"));
        availableSkins.put(3, (p) -> new FoxSkin(p, "green"));
        availableSkins.put(4, (p) -> new FoxSkin(p, "magenta"));


        //Add terrains;
        terrainsList.add(new DefaultTerrain());
        terrainsList.add(new FlatTerrain());
        if (window.devMode) terrainsList.add(new DevTerrain());
        if (window.settings.internal_experimentalFeatures) {
            terrainsList.add(new ComplexTerrain());
        }
    }

    public HashMap<String, String> getCommandHelp() {
        HashMap<String, String> commandHelp = new HashMap<>();
        return commandHelp;
    }

    @Override
    public String handleCommand(String[] parts) {
        return null;
    }

    public boolean drawCursor(CursorRay cursorRay) {
        if (GameScene.getGameMode() != GameMode.FREEPLAY) return false;
        return blockTools.getSelectedTool().drawCursor(cursorRay, GameScene.projection, GameScene.view);
    }

    public boolean clickEvent(final CursorRay ray, boolean isCreationMode) {
        return blockTools.clickEvent(GameScene.player.getSelectedItem(), ray, isCreationMode);
    }


    public boolean releaseMouse() {
        return blockTools.releaseMouse();
    }


    //Builtin menus
    public UI_Inventory inventory;

    public BlockTools blockTools;


    //Custom menus
    public BarrelUI barrelUI;
    public CraftingUI craftingUI;
    public GameMenus gameMenus = new GameMenus();

    @Override
    public void uiInit(NkContext ctx, GameUI gameUI) {
        inventory = new UI_Inventory(ctx, Registrys.items.getList(), window, GameUI.hotbar);
        blockTools = new BlockTools(ctx, window, GameScene.player.camera.cursorRay);

        //Menus
        barrelUI = new BarrelUI(ctx, window);
        craftingUI = new CraftingUI(ctx, window);
        gameMenus.menus.add(barrelUI);
        gameMenus.menus.add(craftingUI);
    }


    @Override
    public boolean uiDraw(MemoryStack stack) {
        if (inventory.isOpen()) {
            inventory.draw(stack);
            return true;
        } else if (gameMenus.draw(stack)) {
            return true;
        } else {
            blockTools.draw(stack);
        }
        return false;
    }


    @Override
    public boolean uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (inventory.isOpen()) {
            return inventory.mouseScrollEvent(scroll, xoffset, yoffset);
        } else if (blockTools.mouseScrollEvent(scroll, xoffset, yoffset)) {
        }
        return false;
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (inventory.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(inventory.getClass());
            return true;
        } else if (gameMenus.keyEvent(key, scancode, action, mods)) {
            return true;
        } else if (blockTools.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(blockTools.getClass());
            return true;
        }
        return false;
    }

    public void event_gameModeChanged(GameMode gameMode) {
        GameScene.player.camera.cursorRay.disableBoundaryMode();
        blockTools.reset();
    }

    @Override
    public boolean uiMouseButtonEvent(int button, int action, int mods) {
        if (inventory.isOpen() && inventory.mouseButtonEvent(button, action, mods)) {
            return true;
        } else if (gameMenus.mouseButtonEvent(button, action, mods)) {
            return true;
        } else if (blockTools.UIMouseButtonEvent(button, action, mods)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean menusAreOpen() {
        return inventory.isOpen() || blockTools.isOpen() || gameMenus.isOpen();
    }

    WorldData currentWorld;

    @Override
    public void startGame(WorldData worldInfo) {
        this.currentWorld = worldInfo;
    }

    @Override
    public void setup(GameScene gameScene) throws Exception {
        //Add block types FIRST. We need them to be able to setup blocks properly
        Registrys.blocks.addBlockType("sprite", RenderType.SPRITE, new SpriteRenderer());
        Registrys.blocks.addBlockType("floor", RenderType.FLOOR, new FloorItemRenderer());
        Registrys.blocks.addBlockType("orientable", RenderType.ORIENTABLE_BLOCK, new OrientableBlockRenderer());
        Registrys.blocks.addBlockType("slab", RenderType.SLAB, new SlabRenderer());
        Registrys.blocks.addBlockType("stairs", RenderType.STAIRS, new StairsRenderer());
        Registrys.blocks.addBlockType("fence", RenderType.FENCE, new FenceRenderer());
        Registrys.blocks.addBlockType("wall", RenderType.WALL_ITEM, new WallItemRenderer());
        Registrys.blocks.addBlockType("lamp", RenderType.LAMP, new LampRenderer());
        Registrys.blocks.addBlockType("pane", RenderType.PANE, new PaneRenderer());
        Registrys.blocks.addBlockType("track", RenderType.RAISED_TRACK, new RaisedTrackRenderer());
        Registrys.blocks.addBlockType("torch", RenderType.TORCH, new TorchRenderer());
        Registrys.blocks.addBlockType("pillar", RenderType.PILLAR, new PillarRenderer());
        Registrys.blocks.addBlockType("trapdoor", RenderType.TRAPDOOR, new TrapdoorRenderer());
        Registrys.blocks.addBlockType("fence gate", RenderType.FENCE_GATE, new FenceGateRenderer());
        Registrys.blocks.addBlockType("door half", RenderType.DOOR_HALF, new DoorHalfRenderer());


        System.out.println("Initializing items...");
        ArrayList<Block> blockList = Blocks.starup_getBlocks();
        ArrayList<EntitySupplier> entityList = Entities.startup_getEntities(window);
        ArrayList<Item> itemList = Items.startup_getItems();

        Registrys.initialize(blockList, entityList, itemList);

        //Load Loot
        LootTableRegistry.blockLootTables.loadFromFile(ResourceUtils.resource("items/loot/block.json"));


        //Load recipes
        RecipeRegistry.craftingRecipes.loadFromFile(ResourceUtils.resource("items/recipes/crafting/variants.json"));
        RecipeRegistry.craftingRecipes.loadFromFile(ResourceUtils.resource("items/recipes/crafting/tools.json"));

//        synthesizeLootAndRecipes(itemList, blockList);


        RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                "#wood", "#wood", "#wood",
                null, "xbuilders:stick", null,
                null, "xbuilders:stick", null,
                "xbuilders:wooden_pickaxe", 1));

        RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                null, "#wood", null,
                null, "xbuilders:stick", null,
                null, "xbuilders:stick", null,
                "xbuilders:wooden_shovel", 1));

        RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                "#wood", "#wood", null,
                "#wood", "xbuilders:stick", null,
                null, "xbuilders:stick", null,
                "xbuilders:wooden_axe", 1));

        RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                "#wood", "#wood", null,
                null, "xbuilders:stick", null,
                null, "xbuilders:stick", null,
                "xbuilders:hoe", 1));

        Blocks.editBlocks(window);

        gameScene.livePropagationHandler.addTask(new WaterPropagation());
        gameScene.livePropagationHandler.addTask(new LavaPropagation());
        gameScene.livePropagationHandler.addTask(new GrassPropagation());
        new FirePropagation(gameScene.livePropagationHandler);
    }


    /**
     * For temporary purposes
     *
     * @param itemList
     */
    private void synthesizeBlockVariantRecipes(ArrayList<Item> itemList) throws IOException {
        System.out.println("Synthesizing block variants...");
        //Write recipes
        for (Item item : itemList) {
            Block block = item.getBlock();
            if (block == null) continue;
//            if (RecipeRegistry.craftingRecipes.getFromOutput(item.id) != null) continue;//If there is already a recipe, skip it
            Item blockVariant = Items.getBlockVariant(item, BlockRegistry.DEFAULT_BLOCK_TYPE_ID, RenderType.ORIENTABLE_BLOCK);
            if (blockVariant == null) continue;

            if (block.renderType == RenderType.SLAB) {
                RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                        null, null, null,
                        null, null, null,
                        blockVariant.id, blockVariant.id, blockVariant.id,
                        item.id, 6));
            } else if (block.renderType == RenderType.STAIRS) {
                RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                        null, null, blockVariant.id,
                        null, blockVariant.id, blockVariant.id,
                        blockVariant.id, blockVariant.id, blockVariant.id,
                        item.id, 6));
            } else if (block.renderType == RenderType.FENCE) {
                RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                        null, null, null,
                        blockVariant.id, "xbuilders:stick", blockVariant.id,
                        blockVariant.id, "xbuilders:stick", blockVariant.id,
                        item.id, 10));
            } else if (block.renderType == RenderType.FENCE_GATE) {
                RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                        null, null, null,
                        "xbuilders:stick", blockVariant.id, "xbuilders:stick",
                        "xbuilders:stick", blockVariant.id, "xbuilders:stick",
                        item.id, 4));
            } else if (block.renderType == RenderType.PILLAR) {
                RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                        null, blockVariant.id, null,
                        null, blockVariant.id, null,
                        null, blockVariant.id, null,
                        item.id, 4));
            } else if (block.renderType == RenderType.PANE) {
                RecipeRegistry.craftingRecipes.add(new CraftingRecipe(
                        blockVariant.id, null, blockVariant.id,
                        blockVariant.id, null, blockVariant.id,
                        blockVariant.id, null, blockVariant.id,
                        item.id, 12));
            }
        }
        RecipeRegistry.craftingRecipes.writeToFile(ResourceUtils.resource("items/recipes/variants.json"));
    }


}
