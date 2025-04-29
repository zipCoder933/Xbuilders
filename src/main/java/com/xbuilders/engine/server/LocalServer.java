/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.xbuilders.Main;
import com.xbuilders.engine.Server;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.common.utils.ErrorHandler;
import com.xbuilders.engine.common.utils.bytes.ByteUtils;
import com.xbuilders.engine.common.json.JsonManager;
import com.xbuilders.engine.common.network.ServerBase;
import com.xbuilders.engine.common.commands.CommandRegistry;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.EntityRegistry;
import com.xbuilders.engine.server.entity.EntitySupplier;
import com.xbuilders.engine.server.entity.ItemDrop;
import com.xbuilders.engine.server.item.ItemStack;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.server.players.pipeline.BlockEventPipeline;
import com.xbuilders.engine.server.players.pipeline.BlockHistory;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.wcc.WCCf;
import com.xbuilders.engine.server.world.wcc.WCCi;
import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LocalServer extends Server {
    public LivePropagationHandler livePropagationHandler;
    private final World world;
    private final Game game;
    public final BlockEventPipeline eventPipeline;
    public final LogicThread tickThread;
    public final CommandRegistry commands = new CommandRegistry();
    ServerBase endpoint;
    private boolean alive = true;

    public LocalServer(Game game, World world, UserControlledPlayer player) {
        this.game = game;
        this.world = world;
        eventPipeline = new BlockEventPipeline(world, player);
        livePropagationHandler = new LivePropagationHandler();
        tickThread = new LogicThread(this);
    }

    public void run() {
        livePropagationHandler.startGameEvent(world.data);
        eventPipeline.startGameEvent(world.data);
        tickThread.startGameEvent();

        //Setup the game
        game.setupServer(this);

        while (alive) {
            eventPipeline.update();
        }
    }


    public void stop() throws IOException {
        alive = false;
        if (world.data != null) world.stopGameEvent();
        tickThread.stopGameEvent();
        eventPipeline.stopGameEvent();
        livePropagationHandler.stopGameEvent();
    }

    //Game Mode =======================================================================================================


    public Difficulty getDifficulty() {
        return world.data.data.difficulty;
    }

    public void setDifficulty(Difficulty difficulty) throws IOException {
        if (difficulty == null) difficulty = Difficulty.NORMAL;
        world.data.data.difficulty = difficulty;
        world.data.save();
        Main.getClient().consoleOut("Difficulty changed to: " + getDifficulty());
    }

    public GameMode getGameMode() {
        return world.data.data.gameMode;
    }

    public void setGameMode(GameMode gameMode) throws IOException {
        if (gameMode == null) gameMode = GameMode.ADVENTURE;
        world.data.data.gameMode = gameMode;
        world.data.save();
        Main.getClient().consoleOut("Game mode changed to: " + getGameMode());
    }


    //Permissions =======================================================================================================
    private boolean isOperator;

    public boolean isOperator() {
        return isOperator;
    }

    public boolean setOperator(boolean isOperator2) {
        if (ownsGame()) return false;
        else {
            isOperator = isOperator2;
            Main.getClient().consoleOut("Operator privileges have been " + (isOperator ? "granted" : "revoked"));
        }
        return true;
    }

    public boolean ownsGame() {
        return false;
    }


    // Getters
    public int getLightLevel(int worldX, int worldY, int worldZ) {
        WCCi wcc = new WCCi().set(worldX, worldY, worldZ);
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            int sun = chunk.data.getSun(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
            int torch = chunk.data.getTorch(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
            sun = (int) Math.min(sun, GameScene.background.getLightness() * 15);
            return Math.max(sun, torch);
        }
        return 0;
    }

    //Set block ===============================================================================

    public void setBlock(short block, int worldX, int worldY, int worldZ) {
        setBlock(block, new WCCi().set(worldX, worldY, worldZ));
    }

    public void setBlock(short block, BlockData data, int worldX, int worldY, int worldZ) {
        setBlock(block, data, new WCCi().set(worldX, worldY, worldZ));
    }

    public void setBlockData(BlockData data, int worldX, int worldY, int worldZ) {
        setBlockData(data, new WCCi().set(worldX, worldY, worldZ));
    }

    public void setBlock(short newBlock, BlockData blockData, WCCi wcc) {
        if (!World.inYBounds((wcc.chunk.y * Chunk.WIDTH) + wcc.chunkVoxel.y)) return;
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            //Get the previous block
            short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);

            //we need to set the block because some algorithms want to check to see if the block has changed immediately
            chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, newBlock); //Important
            chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockData); //Important

            BlockHistory history = new BlockHistory(previousBlock, newBlock);
            history.updateBlockData = true;
            history.newBlockData = blockData;
            eventPipeline.addEvent(wcc, history);
        }
    }

    public void setBlock(short newBlock, WCCi wcc) {
        if (!World.inYBounds((wcc.chunk.y * Chunk.WIDTH) + wcc.chunkVoxel.y)) return;
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            //Get the previous block
            short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);

            //we need to set the block because some algorithms want to check to see if the block has changed immediately
            chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, newBlock); //Important

            BlockHistory history = new BlockHistory(previousBlock, newBlock);
            eventPipeline.addEvent(wcc, history);
        }
    }

    public void setBlockData(BlockData blockData, WCCi wcc) {
        if (!World.inYBounds((wcc.chunk.y * Chunk.WIDTH) + wcc.chunkVoxel.y)) return;
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            //Get the previous block
            short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);

            //we need to set the block because some algorithms want to check to see if the block has changed immediately
            chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockData); //Important

            BlockHistory history = new BlockHistory(previousBlock, previousBlock);
            history.updateBlockData = true;
            history.newBlockData = blockData;
            eventPipeline.addEvent(wcc, history);
        }
    }

    //Set entity =================================================================================
    public Entity placeItemDrop(Vector3f position, ItemStack item, boolean droppedFromPlayer) {
        Entity e = placeEntity(EntityRegistry.ENTITY_ITEM_DROP, position, null);

        ItemDrop drop = (ItemDrop) e;
        drop.definitionData.stack = item;
        drop.definitionData.droppedFromPlayer = droppedFromPlayer;
        return e;
    }

    /**
     * @param entity    the entity to place
     * @param w         the world position to place the entity
     * @param simpleGen the JSON to inject into the entity
     * @return the entity that was placed
     */
    public Entity placeEntity(EntitySupplier entity, Vector3f w, JsonManager.SimpleJsonGenerator simpleGen) {
        byte[] entityBytes = null;
        if (simpleGen != null) {
            //Write the entity input as binary data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator generator = Entity.smileFactory.createGenerator(baos)) {
                generator.writeStartObject(); // Start root object
                simpleGen.write(generator);
                generator.writeEndObject(); // End root object
                generator.close();
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
            entityBytes = baos.toByteArray();
        }

        WCCf wcc = new WCCf();
        wcc.set(w);
        Chunk chunk = world.chunks.get(wcc.chunk);
        if (chunk != null) {
            chunk.markAsModified();
            Entity e = chunk.entities.placeNew(w, entity, entityBytes);
            e.sendMultiplayer = true;//Tells the chunkEntitySet to send the entity to the clients
            return e;
        }
        return null;
    }


    public void setTimeOfDay(double v) {

        System.out.println("Setting time of day to " + v);
        GameScene.background.setTimeOfDay(v);
        byte[] timeFloat = ByteUtils.floatToBytes((float) v);
        //  server.sendToAllClients(new byte[]{GameServer.SET_TIME, timeFloat[0], timeFloat[1], timeFloat[2], timeFloat[3]});

    }

    public float getTimeOfDay() {
        return GameScene.background.getTimeOfDay();
    }


    public void playerJoinEvent(Player client) {
        Main.getClient().consoleOut("A new player has joined: " + client);
        System.out.println("JOIN EVENT: " + client.getName());
        System.out.println("Players: " + world.players);
        world.players.add(client);
        System.out.println("Players: " + world.players);
    }

    public void playerLeaveEvent(Player player) {
        Main.getClient().consoleOut(player.getName() + " has left");
        world.players.remove(player);

        if (player.isHost) {
            ClientWindow.popupMessage.message(
                    "Host has left the game",
                    "Last ping from host " + player.getSecSinceLastPing() + "s ago");
        }
    }


    private GameMode lastGameMode;


}
