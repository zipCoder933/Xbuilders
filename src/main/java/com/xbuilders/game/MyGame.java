/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.builtinMechanics.fire.FirePropagation;
import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.*;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.player.SkinLink;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.json.JsonManager;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.game.UI.Hotbar;
import com.xbuilders.game.UI.Inventory;
import com.xbuilders.game.blockTools.BlockTools;
import com.xbuilders.game.items.blocks.RenderType;
import com.xbuilders.game.items.blocks.type.*;
import com.xbuilders.game.items.entities.Banner;
import com.xbuilders.game.items.entities.animal.*;
import com.xbuilders.game.items.entities.animal.fish.FishALink;
import com.xbuilders.game.items.entities.animal.fish.FishBLink;
import com.xbuilders.game.items.entities.animal.landAndWater.BeaverEntityLink;
import com.xbuilders.game.items.entities.animal.landAndWater.TurtleEntityLink;
import com.xbuilders.game.items.entities.animal.quadPedal.DogLink;
import com.xbuilders.game.items.entities.animal.quadPedal.HorseLink;
import com.xbuilders.game.items.entities.animal.quadPedal.MuleLink;
import com.xbuilders.game.items.entities.vehicle.Boat;
import com.xbuilders.game.items.entities.vehicle.Minecart;
import com.xbuilders.game.items.tools.*;
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xbuilders.engine.items.ItemUtils.getAllJsonBlocks;
import static com.xbuilders.engine.ui.gameScene.GameUI.printKeyConsumption;

/**
 * @author zipCoder933
 */
public class MyGame extends Game {

    public MyGame(MainWindow window) {
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
//        commandHelp.put("send", "send clipboard");
        return commandHelp;
    }

    @Override
    public String handleCommand(String[] parts) {
//        if (parts[0].equals("send")) {
//            PasteTool.clipboard.toBytes();
//            return "Clipboard: ";
//        }
        return null;
    }

    public boolean drawCursor(CursorRay cursorRay) {
        return blockTools.getSelectedTool().drawCursor(cursorRay, GameScene.projection, GameScene.view);
    }

    public boolean clickEvent(final CursorRay ray, boolean isCreationMode) {
        Item item = getSelectedItem();
        Block block = BlockRegistry.BLOCK_AIR;
        if (item != null && item.block != null) block = item.block;
        return blockTools.getSelectedTool().setBlock(block, ray, isCreationMode);
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
    Hotbar hotbar;
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
            Logger.getLogger(MyGame.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public boolean uiMouseButtonEvent(int button, int action, int mods) {
        if (inventory.isOpen() && inventory.mouseButtonEvent(button, action, mods)) {
            return true;
        } else if (blockTools.mouseButtonEvent(button, action, mods)) {
            return true;
        } else {
            return hotbar.mouseButtonEvent(button, action, mods);
        }
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
        if (f.exists()) {
            gameInfo = json.gson.fromJson(new FileReader(f), GameInfo.class);
            if (gameInfo == null) {
                gameInfo = new GameInfo();
            }
        } else {
            System.out.println("Making new game info");
            gameInfo = new GameInfo();
        }
    }

    @Override
    public void saveState() {
        if (gameInfo != null) {
            //Write variables
            gameInfo.timeOfDay = GameScene.background.getTimeOfDay();

            //Save game info
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
        return block.renderType != RenderType.SPRITE;
    }


    //<editor-fold defaultstate="collapsed" desc="Blocks and items">
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


    public static final Item TOOL_ANIMAL_FEED = new AnimalFeed();

    @Override
    public void initialize(GameScene gameScene) throws Exception {
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

        ArrayList<Block> blockList = getAllJsonBlocks(ResourceUtils.resource("\\items\\blocks\\json"));


        EntitySupplier[] entityArray = new EntitySupplier[]{
                //Banners
                new EntitySupplier(82, "Red Banner", () -> new Banner(82)),
                new EntitySupplier(81, "Orange Banner", () -> new Banner(81)),
                new EntitySupplier(72, "Yellow Banner", () -> new Banner(72)),
                new EntitySupplier(75, "Lime Banner", () -> new Banner(75)),
                new EntitySupplier(78, "Green Banner", () -> new Banner(78)),
                new EntitySupplier(73, "Blue Banner", () -> new Banner(73)),
                new EntitySupplier(77, "Gray Banner", () -> new Banner(77)),
                new EntitySupplier(76, "Pink Banner", () -> new Banner(76)),
                new EntitySupplier(84, "Purple Banner", () -> new Banner(84)),
                new EntitySupplier(74, "White Banner", () -> new Banner(74)),
                new EntitySupplier(83, "Xbuilders Banner", () -> new Banner(83)),
                new EntitySupplier(80, "Regal Banner", () -> new Banner(80)),
                new EntitySupplier(79, "Royal Banner", () -> new Banner(79)),


                //Boats
                new EntitySupplier(92, "Oak Boat", () -> new Boat(92, window)),
                new EntitySupplier(95, "Dark Oak Boat", () -> new Boat(95, window)),
                new EntitySupplier(96, "Spruce Boat", () -> new Boat(96, window)),
                new EntitySupplier(94, "Acacia Boat", () -> new Boat(94, window)),
                new EntitySupplier(93, "Jungle Boat", () -> new Boat(93, window)),
                new EntitySupplier(97, "Birch Boat", () -> new Boat(97, window)),

                //Minecarts
                new EntitySupplier(103, "Blue Minecart", () -> new Minecart(103, window)),
                new EntitySupplier(99, "Charcoal Minecart", () -> new Minecart(99, window)),
                new EntitySupplier(104, "Cyan Minecart", () -> new Minecart(104, window)),
                new EntitySupplier(101, "Green Minecart", () -> new Minecart(101, window)),
                new EntitySupplier(98, "Iron Minecart", () -> new Minecart(98, window)),
                new EntitySupplier(100, "Red Minecart", () -> new Minecart(100, window)),
                new EntitySupplier(102, "Yellow Minecart", () -> new Minecart(102, window)),

                //Foxes
                new FoxLink(window, 56, "Fox"),
//                new FoxLink(window, 56, "Red Fox", "red.png"),
//                new FoxLink(window, 1, "Gray Fox", "gray.png"),
//                new FoxLink(window, 2, "White Fox", "white.png"),
                //Cats
                new CatLink(window, 3, "Cat"),
//                new CatLink(window, 3, "Black Cat", "black.png"),
//                new CatLink(window, 4, "British Shorthair Cat", "british_shorthair.png"),
//                new CatLink(window, 5, "Calico Cat", "calico.png"),
//                new CatLink(window, 6, "Calico Cat", "calico2.png"),
//                new CatLink(window, 7, "Jellie Cat", "jellie.png"),
//                new CatLink(window, 8, "Ocelot", "ocelot.png"),
//                new CatLink(window, 9, "Persian Cat", "persian.png"),
//                new CatLink(window, 10, "Ragdoll Cat", "ragdoll.png"),
//                new CatLink(window, 11, "Red Cat", "red.png"),
//                new CatLink(window, 12, "Siamese Cat", "siamese.png"),
//                new CatLink(window, 13, "Tabby Cat", "tabby.png"),
//                new CatLink(window, 14, "White Cat", "white.png"),
                //Rabbits
                new RabbitLink(window, 15, "Rabbit"),
//                new RabbitLink(window, 15, "Black Rabbit", "black.png"),
//                new RabbitLink(window, 16, "White Rabbit", "white.png"),
//                new RabbitLink(window, 17, "Brown Rabbit", "brown.png"),
//                new RabbitLink(window, 18, "Caerbannog Rabbit", "caerbannog.png"),
//                new RabbitLink(window, 19, "Gold Rabbit", "gold.png"),
//                new RabbitLink(window, 20, "Salt Rabbit", "salt.png"),
//                new RabbitLink(window, 21, "Toast Rabbit", "toast.png"),
//                new RabbitLink(window, 22, "White Splotched Rabbit", "white_splotched.png"),

                //Horses
                new HorseLink(window, 23, "Horse"),
//                new HorseLink(window, 23, "Black Horse", "black.png"),
//                new HorseLink(window, 24, "Brown Horse", "brown.png"),
//                new HorseLink(window, 25, "Chestnut Horse", "chestnut.png"),
//                new HorseLink(window, 26, "Creamy Horse", "creamy.png"),
//                new HorseLink(window, 27, "Dark Brown Horse", "darkbrown.png"),
//                new HorseLink(window, 28, "White Horse", "white.png"),
//                new HorseLink(window, 29, "Gray Horse", "gray.png"),
                //Mules
                new MuleLink(window, 30, "Mule"), //TODO: Separate this so that it is 2 items, (Do this after instantiation of items)
//                new MuleLink(window, 30, "Mule", "mule.png"),
//                new MuleLink(window, 31, "Donkey", "donkey.png"),

                //Dogs
                new DogLink(window, 32, "Dog"),
//                new DogLink(window, 32, "Black Dog", "black.png"),
//                new DogLink(window, 33, "Brown Dog", "brown.png"),
//                new DogLink(window, 34, "Gold Dog", "gold.png"),
//                new DogLink(window, 35, "White Dog", "white.png"),

                //Turtles
                new TurtleEntityLink(window, 105, "Sea Turtle"),
//                new TurtleEntityLink(window, 105, "Green Sea Turtle", "big_sea_turtle.png"),
//                new TurtleEntityLink(window, 106, "Yellow Sea Turtle", "yellow_turtle.png"),

                //Beavers
                new BeaverEntityLink(window, 200, "Beaver"),

                //Fish
                new FishALink(window, 41, "Butterfly Fish"),
                new FishBLink(window, 42, "Angler Fish"),

//                new FishBLink(window, 50, "Angel Fish", "angel.png"),
//                new FishBLink(window, 51, "Blue Ring Angelfish", "blue_ring_angel.png"),
//                new FishBLink(window, 52, "Copperband Butterfly", "copperband_butterfly.png"),
//                new FishBLink(window, 53, "Gold Butterfly Fish", "gold.png"),
//                new FishBLink(window, 54, "Ornate Butterfly Fish", "ornate_butterfly.png"),
//                new FishBLink(window, 36, "Red Butterfly Fish", "red_butterfly.png"),
//                new FishBLink(window, 37, "Regal Tang Fish", "regal_tang.png"),
//                new FishBLink(window, 38, "Striped Butterfly Fish", "striped_butterfly.png"),
//                new FishBLink(window, 49, "Yellow Angelfish", "yellow_angel.png"),
//                new FishBLink(window, 48, "Gray Glitterfish", "gray_glitter.png"),
//                new FishALink(window, 41, "Bicolor Angelfish", "bicolor_angel.png"),
//                new FishALink(window, 40, "Clown Fish", "clown.png"),
//                new FishALink(window, 43, "Clown Loach", "clown_loach.png"),
//                new FishALink(window, 44, "Cotton Candy Betta", "cotton_candy_betta.png"),
//                new FishALink(window, 45, "Damsel Fish", "damsel.png"),
//                new FishALink(window, 46, "Emporer Angelfish", "emporer_angel.png"),
//                new FishALink(window, 39, "Orange Green Betta", "orange_green_betta.png"),
//                new FishALink(window, 47, "Royal Gramma Fish", "royal_gramma.png"),
//                new FishALink(window, 42, "Salamander Betta", "salamander_betta.png"),
//                new FishALink(window, 55, "Tri-Band Betta", "tri_band_betta.png"),
        };

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(new Saddle());
        itemList.add(new Hoe());
        itemList.add(new Flashlight());
        itemList.add(new Camera());
        itemList.add(TOOL_ANIMAL_FEED);


        ArrayList<EntitySupplier> entityList = new ArrayList<>();
        entityList.addAll(Arrays.asList(entityArray));

        itemList.addAll(ItemUtils.getItemsFromBlocks(blockList, entityList));

        //Reassign blocks
        HashMap<Short, Block> reassignments = new HashMap<>();
        Blocks.reassignBlocks(reassignments);
        for (int i = 0; i < blockList.size(); i++) { //Check to ensure the blocks ID remains the same
            if (reassignments.containsKey(blockList.get(i).id)) {
                System.out.println("Reassigned Block " + blockList.get(i).toString());
                short originalID = blockList.get(i).id;
                blockList.set(i, reassignments.get(blockList.get(i).id));
                if (blockList.get(i).id != originalID)
                    throw new RuntimeException("Reassigned Block ID " + originalID + " changed to " + blockList.get(i).id);
            }
        }

        //Set items AFTER setting block types
        Registrys.setAllItems(blockList, entityList, itemList);

        Blocks.editBlocks(window);


        gameScene.livePropagationHandler.addTask(new WaterPropagation());
        gameScene.livePropagationHandler.addTask(new LavaPropagation());
        gameScene.livePropagationHandler.addTask(new GrassPropagation());
        new FirePropagation(gameScene.livePropagationHandler);
    }


}
