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
import com.xbuilders.engine.items.loot.LootTableRegistry;
import com.xbuilders.engine.items.recipes.crafting.CraftingRecipe;
import com.xbuilders.engine.items.recipes.RecipeRegistry;
import com.xbuilders.engine.items.recipes.crafting.CraftingRecipes;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.data.WorldData;
import com.xbuilders.game.vanilla.items.Recipes;
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
import com.xbuilders.game.vanilla.terrain.defaultTerrain.DefaultTerrain;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Predicate;

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
        return blockTools.clickEvent(ray, isCreationMode);
    }


    public boolean releaseMouse() {
        return blockTools.releaseMouse();
    }


    //Builtin menus

    public BlockTools blockTools;


    //Custom menus
    public BarrelUI barrelUI;
    public CraftingUI craftingUI;
    public SmeltingUI smeltingUI;
    public UI_Inventory inventoryUI;
    public UI_RecipeIndex recipeIndexUI;

    public GameMenus gameMenus = new GameMenus();

    @Override
    public void uiInit(NkContext ctx, GameUI gameUI) {

        blockTools = new BlockTools(ctx, window, GameScene.player.camera.cursorRay);

        //Menus
        barrelUI = new BarrelUI(ctx, window);
        craftingUI = new CraftingUI(ctx, window);
        smeltingUI = new SmeltingUI(ctx, window);
        recipeIndexUI = new UI_RecipeIndex(ctx, Registrys.items.getList(), window);
        inventoryUI = new UI_Inventory(ctx, Registrys.items.getList(), window, GameUI.hotbar);

        gameMenus.menus.add(barrelUI);
        gameMenus.menus.add(craftingUI);
        gameMenus.menus.add(smeltingUI);
        gameMenus.menus.add(recipeIndexUI);
        gameMenus.menus.add(inventoryUI);
    }


    @Override
    public boolean uiDraw(MemoryStack stack) {
        if (gameMenus.draw(stack)) {
            return true;
        } else {
            blockTools.draw(stack);
        }
        return false;
    }


    @Override
    public boolean uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (gameMenus.mouseScrollEvent(scroll, xoffset, yoffset)) {
            return true;
        } else if (blockTools.mouseScrollEvent(scroll, xoffset, yoffset)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        //Menus
        if (gameMenus.keyEvent(key, scancode, action, mods)) {
            return true;
        } else if (blockTools.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(blockTools.getClass());
            return true;
        } else {
            //Wake keys
            if (action == GLFW.GLFW_RELEASE && key == UI_RecipeIndex.KEY_OPEN_RECIPE_INDEX) {
                recipeIndexUI.setOpen(!recipeIndexUI.isOpen());
                return true;
            } else if (action == GLFW.GLFW_RELEASE && key == UI_Inventory.KEY_OPEN_INVENTORY) {
                inventoryUI.setOpen(!inventoryUI.isOpen());
                return true;
            }
        }
        return false;
    }

    public void gameModeChangedEvent(GameMode gameMode) {
        GameScene.player.camera.cursorRay.disableBoundaryMode();
        blockTools.reset();
    }

    @Override
    public boolean uiMouseButtonEvent(int button, int action, int mods) {
        if (gameMenus.mouseButtonEvent(button, action, mods)) {
            return true;
        } else if (blockTools.UIMouseButtonEvent(button, action, mods)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean menusAreOpen() {
        return blockTools.isOpen() || gameMenus.isOpen();
    }

    WorldData currentWorld;

    @Override
    public void startGameEvent(WorldData worldInfo) {
        this.currentWorld = worldInfo;
    }

    @Override
    public void stopGameEvent() {
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
        for (File jsonFile : Objects.requireNonNull(ResourceUtils.resource("items/loot").listFiles())) {
            LootTableRegistry.blockLootTables.loadFromFile(jsonFile);
        }

        //Load Recipes
        Recipes.loadRecipes();

        Blocks.editBlocks(window);
        Items.editItems(window);

        //propagations
        gameScene.livePropagationHandler.addTask(new WaterPropagation());
        gameScene.livePropagationHandler.addTask(new LavaPropagation());
//        gameScene.livePropagationHandler.addTask(new GrassPropagation());
//        new FirePropagation(gameScene.livePropagationHandler);


        //Add terrains;
        terrainsList.add(new DefaultTerrain());
        terrainsList.add(new FlatTerrain());
        if (window.devMode) terrainsList.add(new DevTerrain());
    }


}
