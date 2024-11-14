/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.builtinMechanics.fire.FirePropagation;
import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameProperties;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.*;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.player.SkinLink;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.json.JsonManager;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.game.UI.Hotbar;
import com.xbuilders.game.UI.Inventory;
import com.xbuilders.game.blockTools.BlockTools;
import com.xbuilders.game.items.Blocks;
import com.xbuilders.game.items.Entities;
import com.xbuilders.game.items.Items;
import com.xbuilders.game.items.blocks.RenderType;
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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xbuilders.engine.ui.gameScene.GameUI.printKeyConsumption;

/**
 * @author zipCoder933
 */
public class XbuildersGameProps extends GameProperties {


    public XbuildersGameProps(MainWindow window) {
        super(window);

        //add skins
        availableSkins.put(0, new SkinLink((p) -> new FoxSkin(p, "red")));
        availableSkins.put(1, new SkinLink((p) -> new FoxSkin(p, "yellow")));
        availableSkins.put(2, new SkinLink((p) -> new FoxSkin(p, "blue")));
        availableSkins.put(3, new SkinLink((p) -> new FoxSkin(p, "green")));
        availableSkins.put(4, new SkinLink((p) -> new FoxSkin(p, "magenta")));
        json = new JsonManager();


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

    public static class GameInfo {
        public final Item[] playerBackpack;
        public double timeOfDay;

        public GameInfo() {
            playerBackpack = new Item[33];
        }
    }


    public boolean releaseMouse() {
        return blockTools.releaseMouse();
    }


    JsonManager json;
    GameInfo gameInfo;
    Inventory inventory;
    public Hotbar hotbar;
    BlockTools blockTools;

    @Override
    public Item getSelectedItem() {
        return hotbar.getSelectedItem();
    }


    @Override
    public void uiDraw(MemoryStack stack) {
        if (inventory.isOpen()) {
            inventory.draw(stack);
        } else {
            blockTools.draw(stack);
            hotbar.draw(stack);
        }
    }

    @Override
    public void uiInit(NkContext ctx, GameUI gameUI) {
        try {
            hotbar = new Hotbar(ctx, window);
            inventory = new Inventory(ctx, Registrys.items.getList(), window, hotbar);
            blockTools = new BlockTools(ctx, window, GameScene.player.camera.cursorRay);

        } catch (IOException ex) {
            Logger.getLogger(XbuildersGameProps.class.getName()).log(Level.SEVERE, null, ex);
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
        } else if (blockTools.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(blockTools.getClass());
            return true;
        } else if (hotbar.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(hotbar.getClass());
            return true;
        }
        return false;
    }

    public void gameModeChanged(GameMode gameMode) {
        GameScene.player.camera.cursorRay.disableBoundaryMode();
        blockTools.reset();
    }

    @Override
    public boolean uiMouseButtonEvent(int button, int action, int mods) {
        if (inventory.isOpen() && inventory.mouseButtonEvent(button, action, mods)) {
            return true;
        } else if (blockTools.UIMouseButtonEvent(button, action, mods)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean menusAreOpen() {
        return inventory.isOpen() || blockTools.isOpen();
    }

    WorldInfo currentWorld;

    @Override
    public void startGame(WorldInfo worldInfo) {
        this.currentWorld = worldInfo;
        try {
            loadState();
            if (GameScene.server.isHosting() || !GameScene.server.isPlayingMultiplayer())
                GameScene.setTimeOfDay(gameInfo.timeOfDay);

        } catch (IOException ex) {
            ErrorHandler.report(ex);
        }

        inventory.setPlayerInfo(gameInfo);
        hotbar.setPlayerInfo(gameInfo);
    }

    private void loadState() throws FileNotFoundException {
        File f = new File(currentWorld.getDirectory() + "\\game.json");
//        if (f.exists()) {
//            gameInfo = json.gson_itemAdapter.fromJson(new FileReader(f), GameInfo.class);
//            if (gameInfo == null) {
//                gameInfo = new GameInfo();
//            }
//        } else {
        System.out.println("Making new game info");
        gameInfo = new GameInfo();
//        }
    }

    @Override
    public void saveState() {
        if (gameInfo != null) {
            //Write variables
            gameInfo.timeOfDay = GameScene.background.getTimeOfDay();

            //Save game info
            File f = new File(currentWorld.getDirectory() + "\\game.json");
            try (FileWriter writer = new FileWriter(f)) {
                json.gson_itemAdapter.toJson(gameInfo, writer);
            } catch (IOException ex) {
                Logger.getLogger(XbuildersGameProps.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean includeBlockIcon(Block block) {
        return block.renderType != RenderType.SPRITE;
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

        Blocks.editBlocks(window);

        gameScene.livePropagationHandler.addTask(new WaterPropagation());
        gameScene.livePropagationHandler.addTask(new LavaPropagation());
        gameScene.livePropagationHandler.addTask(new GrassPropagation());
        new FirePropagation(gameScene.livePropagationHandler);
    }


}
