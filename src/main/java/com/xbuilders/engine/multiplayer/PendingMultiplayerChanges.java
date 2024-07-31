package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

//    public void read() {
//        readLock.lock();
//        try {
//// Perform read operations
//        } finally {
//            readLock.unlock();
//        }
//    }
//
//    public void write() {
//        writeLock.lock();
//        try {
//// Perform write operations
//        } finally {
//            writeLock.unlock();
//        }
//    }

public class PendingMultiplayerChanges {
    HashMap<Vector3i, BlockHistory> blockChanges = new HashMap<>();

    HashMap<Vector3f, Entity> entityCreation = new HashMap<>();

    public long rangeChangesUpdate;
    public long allChangesUpdate;
    NetworkSocket socket;
    Player player;

    public PendingMultiplayerChanges(NetworkSocket socket, Player player) {
        this.socket = socket;
        this.player = player;
    }


    public boolean periodicRangeSendCheck(int updateInterval) {
        if (System.currentTimeMillis() - rangeChangesUpdate > updateInterval
                && anyChangesWithinReach()) {
            rangeChangesUpdate = System.currentTimeMillis();
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

    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected final Lock readLock = readWriteLock.readLock();
    protected final Lock writeLock = readWriteLock.writeLock();


    public void addEntityChange(Entity entity, int mode) {
        writeLock.lock();
        try {
            if (mode == GameServer.ENTITY_CREATED) entityCreation.put(entity.worldPosition, entity);
            changeEvent();
        } finally {
            writeLock.unlock();
        }
    }

    public void addBlockChange(Vector3i worldPos, Block block, BlockData data) {
        addBlockChange(worldPos, new BlockHistory(block, data));
    }

    public void addBlockChange(Vector3i worldPos, BlockHistory history) {
        writeLock.lock();
        try {
            blockChanges.put(worldPos, history);
            changeEvent();
        } finally {
            writeLock.unlock();
        }
    }

    public boolean anyChangesWithinReach() {
        readLock.lock();
        try {
            Iterator<Map.Entry<Vector3i, BlockHistory>> iterator
                    = blockChanges.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Vector3i, BlockHistory> entry = iterator.next();
                Vector3i worldPos = entry.getKey();
                if (changeWithinReach(player, worldPos)) {
                    return true;
                }
            }
            return false;
        } finally {
            readLock.unlock();
        }
    }

    public int dumpChanges(BiConsumer<Vector3i, BlockHistory> changes) {
        int changesToBeSent = 0;
        if (this.blockChanges.isEmpty()) return 0;
        //Make a copy of the change list first
        HashMap<Vector3i, BlockHistory> copy = new HashMap<>(this.blockChanges);
        for (Map.Entry<Vector3i, BlockHistory> entry : copy.entrySet()) {
            Vector3i worldPos = entry.getKey();
            BlockHistory change = entry.getValue();
            if (changeWithinReach(player, worldPos)) {

                changes.accept(worldPos, change);

                this.blockChanges.remove(entry.getKey());//Remove it so we don't send it again
                changeEvent();
                changesToBeSent++;
            }
        }

        return changesToBeSent;
    }

    public void blockChangeRecord(OutputStream baos, Vector3i worldPos, BlockHistory change) throws IOException {
        if (change.currentBlock == null) return;

        baos.write(new byte[]{GameServer.VOXEL_BLOCK_CHANGE});
        baos.write(ByteUtils.intToBytes(worldPos.x));
        baos.write(ByteUtils.intToBytes(worldPos.y));
        baos.write(ByteUtils.intToBytes(worldPos.z));
        baos.write(ByteUtils.shortToBytes(change.currentBlock.id));
        ChunkSavingLoadingUtils.writeBlockData(change.data, baos);
    }

    public void entityChangeRecord(OutputStream baos, byte entityOperation, Entity entity) throws IOException {
        baos.write(new byte[]{entityOperation});
        baos.write(ByteUtils.floatToBytes(entity.lastPosition.x));
        baos.write(ByteUtils.floatToBytes(entity.lastPosition.y));
        baos.write(ByteUtils.floatToBytes(entity.lastPosition.z));
        baos.write(ByteUtils.shortToBytes(entity.link.id));
        if (entityOperation == GameServer.ENTITY_UPDATED) {
            entity.writeState(baos);
        } else if (entityOperation == GameServer.ENTITY_CREATED) entity.toBytes();
    }

    public int sendAllChanges() {
        if (blockChanges.isEmpty()) return 0;
        readLock.lock();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (Map.Entry<Vector3i, BlockHistory> entry : blockChanges.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                blockChangeRecord(baos, worldPos, change);
            }
            baos.close();

            rangeChangesUpdate = System.currentTimeMillis();
            byte byteList[] = baos.toByteArray();
            socket.sendData(byteList);

            int changesToBeSent = blockChanges.size();
            clear();

            return changesToBeSent;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

        return 0;
    }

    public int sendNearBlockChanges() {
        if (blockChanges.isEmpty()) return 0;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            HashMap<Vector3i, BlockHistory> copy;

            writeLock.lock();
            try { //Make a copy of the change list first
                copy = new HashMap<>(blockChanges);
            } finally {
                writeLock.unlock();
            }

            int changesToBeSent = 0;

            for (Map.Entry<Vector3i, BlockHistory> entry : copy.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                if (changeWithinReach(player, worldPos)) {
                    blockChangeRecord(baos, worldPos, change);
                    blockChanges.remove(entry.getKey());//Remove it so we don't send it again
                    changeEvent();
                    changesToBeSent++;
                }
            }
            baos.close();

            if (changesToBeSent > 0) {
                rangeChangesUpdate = System.currentTimeMillis();
                byte byteList[] = baos.toByteArray();
                socket.sendData(byteList);
            }
            return changesToBeSent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void readBlockChange(byte[] receivedData, BiConsumer<Vector3i, BlockHistory> newEvent) {
        //Split the recievedData by the newline byte
        AtomicInteger start = new AtomicInteger(0);
        while (start.get() < receivedData.length) {
            if (receivedData[start.get()] == GameServer.VOXEL_BLOCK_CHANGE) {

                BlockHistory blockHistory = new BlockHistory();
                blockHistory.fromNetwork = true;

                //XYZ coordinates
                int x = ByteUtils.bytesToInt(receivedData[start.get() + 1], receivedData[start.get() + 2], receivedData[start.get() + 3], receivedData[start.get() + 4]);
                int y = ByteUtils.bytesToInt(receivedData[start.get() + 5], receivedData[start.get() + 6], receivedData[start.get() + 7], receivedData[start.get() + 8]);
                int z = ByteUtils.bytesToInt(receivedData[start.get() + 9], receivedData[start.get() + 10], receivedData[start.get() + 11], receivedData[start.get() + 12]);

                //Block ID
                int blockID = ByteUtils.bytesToShort(receivedData[start.get() + 13], receivedData[start.get() + 14]);
                blockHistory.currentBlock = ItemList.getBlock((short) blockID);
                start.set(start.get() + 15);

                //Block data
                blockHistory.data = ChunkSavingLoadingUtils.readBlockData(receivedData, start);
                blockHistory.updateBlockData = true;

                //Add the block to the list
                Vector3i position = new Vector3i(x, y, z);
                newEvent.accept(position, blockHistory);
            }
        }
    }

    public void clear() {
        readLock.lock();
        try {
            blockChanges.clear();
            changeEvent();
        } finally {
            readLock.unlock();
        }
    }

    protected void changeEvent() {
    }
}
