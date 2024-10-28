package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class MultiplayerPendingBlockChanges {

    public final ConcurrentHashMap<Vector3i, BlockHistory> blockChanges = new ConcurrentHashMap<>();

    public long lastRangeChange;
    public long allChangesUpdate;
    NetworkSocket socket;
    Player player;

    public MultiplayerPendingBlockChanges(NetworkSocket socket, Player player) {
        this.socket = socket;
        this.player = player;
    }


    public boolean periodicRangeSendCheck(int updateInterval) {
        if (System.currentTimeMillis() - lastRangeChange > updateInterval
                && anyChangesWithinReach()) {
            lastRangeChange = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public boolean periodicSendAllCheck(int interval) {
        if (System.currentTimeMillis() - allChangesUpdate > interval
                && !blockChanges.isEmpty()) {
            allChangesUpdate = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public static boolean changeWithinReach(Player player, Vector3i worldPos) {
        return player.isWithinReach(worldPos.x, worldPos.y, worldPos.z);
    }

    public static boolean changeCanBeLoaded(Player player, Vector3i worldPos) {
        if (player.isWithinReach(worldPos.x, worldPos.y, worldPos.z)) {
            Vector3i chunkPos = new Vector3i();
            WCCi.getChunkAtWorldPos(chunkPos, worldPos.x, worldPos.y, worldPos.z);
            Chunk chunk = GameScene.world.getChunk(chunkPos);
            if (chunk != null && chunk.gen_Complete()) return true;
        }
        return false;
    }


    public void addBlockChange(Vector3i worldPos, Block block, BlockData data) {
        addBlockChange(worldPos, new BlockHistory(block, data));
    }

    public void addBlockChange(Vector3i worldPos, BlockHistory change) {
        blockChanges.put(worldPos, change);
        changeEvent();
    }

    public boolean anyChangesWithinReach() {
        if (!blockChanges.isEmpty()) {
            Iterator<Map.Entry<Vector3i, BlockHistory>> iterator
                    = blockChanges.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Vector3i, BlockHistory> entry = iterator.next();
                Vector3i worldPos = entry.getKey();
                if (changeWithinReach(player, worldPos)) {
                    return true;
                }
            }
        }
        return false;
    }


    public void blockChangeRecord(OutputStream baos, Vector3i worldPos, BlockHistory change) throws IOException {
        baos.write(new byte[]{GameServer.VOXEL_BLOCK_CHANGE});
        baos.write(ByteUtils.intToBytes(worldPos.x));
        baos.write(ByteUtils.intToBytes(worldPos.y));
        baos.write(ByteUtils.intToBytes(worldPos.z));
        baos.write(ByteUtils.shortToBytes(change.newBlock.id));
        ChunkSavingLoadingUtils.writeBlockData(change.newBlockData, baos);
    }

    public int sendAllChanges() {
        if (!blockChanges.isEmpty()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (Map.Entry<Vector3i, BlockHistory> entry : blockChanges.entrySet()) {
                    Vector3i worldPos = entry.getKey();
                    BlockHistory change = entry.getValue();
                    blockChangeRecord(baos, worldPos, change);
                }
                baos.close();

                lastRangeChange = System.currentTimeMillis();
                byte byteList[] = baos.toByteArray();
                socket.sendData(byteList);

                int changesToBeSent = blockChanges.size();
                clear();
                return changesToBeSent;
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
        }
        return 0;
    }

    public int sendNearBlockChanges() {
        if (blockChanges.isEmpty()) return 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int changesToBeSent = 0;

        try {
            Iterator<Map.Entry<Vector3i, BlockHistory>> iterator = blockChanges.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Vector3i, BlockHistory> entry = iterator.next();
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                if (changeWithinReach(player, worldPos)) {
                    blockChangeRecord(baos, worldPos, change);
                    iterator.remove(); // Remove it so we don't send it again
                    changeEvent();
                    changesToBeSent++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            baos.close();
            if (changesToBeSent > 0) {
                lastRangeChange = System.currentTimeMillis();
                byte byteList[] = baos.toByteArray();
                socket.sendData(byteList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return changesToBeSent;
    }


    public static void readBlockChange(byte[] receivedData, BiConsumer<Vector3i, BlockHistory> newEvent) {
        //Split the recievedData by the newline byte
        AtomicInteger start = new AtomicInteger(0);
        while (start.get() < receivedData.length) {
            if (receivedData[start.get()] == GameServer.VOXEL_BLOCK_CHANGE) {


                //XYZ coordinates
                int x = ByteUtils.bytesToInt(receivedData[start.get() + 1], receivedData[start.get() + 2], receivedData[start.get() + 3], receivedData[start.get() + 4]);
                int y = ByteUtils.bytesToInt(receivedData[start.get() + 5], receivedData[start.get() + 6], receivedData[start.get() + 7], receivedData[start.get() + 8]);
                int z = ByteUtils.bytesToInt(receivedData[start.get() + 9], receivedData[start.get() + 10], receivedData[start.get() + 11], receivedData[start.get() + 12]);

                //Block ID
                int newBlock = ByteUtils.bytesToShort(receivedData[start.get() + 13], receivedData[start.get() + 14]);
                BlockHistory blockHistory = new BlockHistory(ItemList.getBlock((short) newBlock));
                blockHistory.fromNetwork = true;
                start.set(start.get() + 15);

                //Block data
                blockHistory.newBlockData = ChunkSavingLoadingUtils.readBlockData(receivedData, start);
                blockHistory.updateBlockData = true;

                //Add the block to the list
                Vector3i position = new Vector3i(x, y, z);
                newEvent.accept(position, blockHistory);
            }
        }
    }

    public void clear() {
        blockChanges.clear();
        changeEvent();
    }

    protected void changeEvent() {
    }
}
