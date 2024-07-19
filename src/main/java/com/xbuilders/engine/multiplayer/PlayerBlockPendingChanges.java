package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.world.chunk.BlockData;
import org.joml.Vector3i;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

public class PlayerBlockPendingChanges {
    HashMap<Vector3i, BlockHistory> record = new HashMap<>();
    public long lastChangeUpdate;
    NetworkSocket socket;
    Player player;

    public PlayerBlockPendingChanges(NetworkSocket socket, Player player) {
        this.socket = socket;
        this.player = player;
        lastChangeUpdate = System.currentTimeMillis();
    }


    public boolean periodicSendCheck(int updateInterval) {
        if (System.currentTimeMillis() - lastChangeUpdate > updateInterval
                && anyChangesWithinReach()) {
            lastChangeUpdate = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public boolean periodicHostSendCheck() {
        if (System.currentTimeMillis() - lastChangeUpdate > 30 * 1000
                && !record.isEmpty()) {
            lastChangeUpdate = System.currentTimeMillis();
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

    public void addBlockChange(Vector3i worldPos, Block block, BlockData data) {
        addBlockChange(worldPos, new BlockHistory(block, data));
    }

    public void addBlockChange(Vector3i worldPos, BlockHistory history) {
        writeLock.lock();
        try {
            record.put(worldPos, history);
            changeEvent();
        } finally {
            writeLock.unlock();
        }
    }

    public boolean anyChangesWithinReach() {
        readLock.lock();
        try {
            Iterator<Map.Entry<Vector3i, BlockHistory>> iterator
                    = record.entrySet().iterator();
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
        if (this.record.isEmpty()) return 0;
        //Make a copy of the change list first
        HashMap<Vector3i, BlockHistory> copy = new HashMap<>(this.record);
        for (Map.Entry<Vector3i, BlockHistory> entry : copy.entrySet()) {
            Vector3i worldPos = entry.getKey();
            BlockHistory change = entry.getValue();
            if (changeWithinReach(player, worldPos)) {

                changes.accept(worldPos, change);

                this.record.remove(entry.getKey());//Remove it so we don't send it again
                changeEvent();
                changesToBeSent++;
            }
        }

        return changesToBeSent;
    }

    public void record(OutputStream baos, Vector3i worldPos, BlockHistory change) throws IOException {
        baos.write(new byte[]{GameServer.VOXEL_BLOCK_CHANGE});
        baos.write(ByteUtils.intToBytes(worldPos.x));
        baos.write(ByteUtils.intToBytes(worldPos.y));
        baos.write(ByteUtils.intToBytes(worldPos.z));
        baos.write(ByteUtils.shortToBytes(change.currentBlock.id));
        if (change.data != null) baos.write(change.data.toByteArray());
    }

    public int sendAllChangesToPlayer() {
        if (record.isEmpty()) return 0;
        readLock.lock();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (Map.Entry<Vector3i, BlockHistory> entry : record.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                record(baos, worldPos, change);
            }
            baos.close();

            lastChangeUpdate = System.currentTimeMillis();
            byte byteList[] = baos.toByteArray();
            socket.sendData(byteList);

            int changesToBeSent = record.size();
            clear();

            return changesToBeSent;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

        return 0;
    }

    public int sendApplicableBlockChangesToPlayer() {
        if (record.isEmpty()) return 0;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            HashMap<Vector3i, BlockHistory> copy;

            writeLock.lock();
            try { //Make a copy of the change list first
                copy = new HashMap<>(record);
            } finally {
                writeLock.unlock();
            }

            int changesToBeSent = 0;

            for (Map.Entry<Vector3i, BlockHistory> entry : copy.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                if (changeWithinReach(player, worldPos)) {
                    record(baos, worldPos, change);
                    record.remove(entry.getKey());//Remove it so we don't send it again
                    changeEvent();
                    changesToBeSent++;
                }
            }
            baos.close();

            if (changesToBeSent > 0) {
                lastChangeUpdate = System.currentTimeMillis();
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
        int i = 0;
        while (i < receivedData.length) {
            if (receivedData[i] == GameServer.VOXEL_BLOCK_CHANGE) {
                int x = ByteUtils.bytesToInt(receivedData[i + 1], receivedData[i + 2], receivedData[i + 3], receivedData[i + 4]);
                int y = ByteUtils.bytesToInt(receivedData[i + 5], receivedData[i + 6], receivedData[i + 7], receivedData[i + 8]);
                int z = ByteUtils.bytesToInt(receivedData[i + 9], receivedData[i + 10], receivedData[i + 11], receivedData[i + 12]);
                int blockID = ByteUtils.bytesToShort(receivedData[i + 13], receivedData[i + 14]);
                i += 15;
                List<Byte> data = new ArrayList<>();
                while (i < receivedData.length && receivedData[i] != GameServer.VOXEL_BLOCK_CHANGE) {
                    data.add(receivedData[i]);
                    i++;
                }

                BlockHistory blockHistory = new BlockHistory();
                blockHistory.currentBlock = ItemList.getBlock((short) blockID);
                blockHistory.fromNetwork = true;
                blockHistory.data = new BlockData(data);

                Vector3i position = new Vector3i(x, y, z);
                newEvent.accept(position, blockHistory);
            }
        }
    }

    public void clear() {
        readLock.lock();
        try {
            record.clear();
            changeEvent();
        } finally {
            readLock.unlock();
        }
    }

    protected void changeEvent() {
    }
}
