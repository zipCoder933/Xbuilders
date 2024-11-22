/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.builtinMechanics.fire.FirePropagation;
import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.*;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.loot.Loot;
import com.xbuilders.engine.items.loot.LootList;
import com.xbuilders.engine.items.loot.LootTables;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.data.WorldData;
import com.xbuilders.game.UI.GameMenus;
import com.xbuilders.game.UI.UI_Hotbar;
import com.xbuilders.game.UI.UI_Inventory;
import com.xbuilders.game.blockTools.BlockTools;
import com.xbuilders.game.items.Blocks;
import com.xbuilders.game.items.Entities;
import com.xbuilders.game.items.Items;
import com.xbuilders.game.items.blocks.RenderType;
import com.xbuilders.game.items.blocks.barrel.BarrelUI;
import com.xbuilders.game.items.blocks.type.*;
import com.xbuilders.game.propagation.*;
import com.xbuilders.game.skins.FoxSkin;
import com.xbuilders.game.terrain.DevTerrain;
import com.xbuilders.game.terrain.FlatTerrain;
import com.xbuilders.game.terrain.complexTerrain.ComplexTerrain;
import com.xbuilders.game.terrain.defaultTerrain.DefaultTerrain;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

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
        return blockTools.clickEvent(getSelectedItem(), ray, isCreationMode);
    }


    public boolean releaseMouse() {
        return blockTools.releaseMouse();
    }


    //Builtin menus
    public UI_Inventory inventory;
    public UI_Hotbar hotbar;
    public BlockTools blockTools;

    //Custom menus
    public BarrelUI barrel;
    public GameMenus gameMenus = new GameMenus();

    @Override
    public void uiInit(NkContext ctx, GameUI gameUI) {
        hotbar = new UI_Hotbar(ctx, window);
        inventory = new UI_Inventory(ctx, Registrys.items.getList(), window, hotbar);
        blockTools = new BlockTools(ctx, window, GameScene.player.camera.cursorRay);
        barrel = new BarrelUI(ctx, window);
        gameMenus.menus.add(barrel);
    }

    @Override
    public ItemStack getSelectedItem() {
        return hotbar.getSelectedItem();
    }


    @Override
    public void uiDraw(MemoryStack stack) {
        if (inventory.isOpen()) {
            inventory.draw(stack);
        } else if (gameMenus.draw(stack)) {
        } else {
            blockTools.draw(stack);
            hotbar.draw(stack);
        }
    }


    @Override
    public boolean uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (inventory.isOpen()) {
            inventory.mouseScrollEvent(scroll, xoffset, yoffset);
        } else {
            if (!blockTools.mouseScrollEvent(scroll, xoffset, yoffset)) {
                hotbar.mouseScrollEvent(scroll, xoffset, yoffset);
            }
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
        } else if (hotbar.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(hotbar.getClass());
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

        LootTables.loadBlockLootTable(ResourceUtils.resource("items/loot/block.json"));
//        LootTables.blockLootTables.put(Blocks.BLOCK_SAND, new LootList(
//                new Loot(() -> new ItemStack("xbuilders:sand"), 1.0f, 3),
//                new Loot(() -> new ItemStack("xbuilders:bamboo"), 0.05f, 1)
//        ));
//        for(Item item :Registrys.items.getList()) {
//            if(item.getBlock() !=null){
//                LootTables.blockLootTables.put(item.getBlock().alias, new LootList(
//                        new Loot(() -> new ItemStack(item), 1.0f, 1)
//                ));
//            }
//        }
//        LootTables.writeLootTableToJson(LootTables.blockLootTables, ResourceUtils.resource("items/loot/block.json"));

        Blocks.editBlocks(window);

        gameScene.livePropagationHandler.addTask(new WaterPropagation());
        gameScene.livePropagationHandler.addTask(new LavaPropagation());
        gameScene.livePropagationHandler.addTask(new GrassPropagation());
        new FirePropagation(gameScene.livePropagationHandler);
    }


}
