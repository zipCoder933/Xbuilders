package com.xbuilders.engine.server.multiplayer;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.server.players.pipeline.BlockHistory;
import com.xbuilders.engine.common.bytes.ByteUtils;
import com.xbuilders.engine.common.ErrorHandler;
import com.xbuilders.engine.common.network.testing.server.NetworkSocket;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.wcc.WCCi;
import org.joml.Vector3i;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static com.xbuilders.engine.server.world.chunk.saving.ChunkSavingLoadingUtils.BLOCK_DATA_MAX_BYTES;
import static com.xbuilders.engine.common.bytes.ByteUtils.bytesToShort;
import static com.xbuilders.engine.common.bytes.ByteUtils.shortToBytes;

public class MultiplayerPendingBlockChanges {

    public final ConcurrentHashMap<Vector3i, BlockHistory> blockChanges = new ConcurrentHashMap<>();

    public long lastRangeChange;

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



    public static boolean changeWithinReach(Player player, Vector3i worldPos) {
        return player.isWithinReach(worldPos.x, worldPos.y, worldPos.z);
    }

    public static boolean changeCanBeLoaded(Player player, Vector3i worldPos) {
        if (player.isWithinReach(worldPos.x, worldPos.y, worldPos.z)) {
            Vector3i chunkPos = new Vector3i();
            WCCi.getChunkAtWorldPos(chunkPos, worldPos.x, worldPos.y, worldPos.z);
            Chunk chunk = LocalClient.world.getChunk(chunkPos);
            return chunk != null && chunk.gen_Complete();
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
        baos.write(new byte[]{GameServer.VOXELS_UPDATED});
        baos.write(ByteUtils.intToBytes(worldPos.x));
        baos.write(ByteUtils.intToBytes(worldPos.y));
        baos.write(ByteUtils.intToBytes(worldPos.z));
        baos.write(ByteUtils.shortToBytes(change.newBlock.id));
        writeBlockData(change.newBlockData, baos);
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
            if (receivedData[start.get()] == GameServer.VOXELS_UPDATED) {


                //XYZ coordinates
                int x = ByteUtils.bytesToInt(receivedData[start.get() + 1], receivedData[start.get() + 2], receivedData[start.get() + 3], receivedData[start.get() + 4]);
                int y = ByteUtils.bytesToInt(receivedData[start.get() + 5], receivedData[start.get() + 6], receivedData[start.get() + 7], receivedData[start.get() + 8]);
                int z = ByteUtils.bytesToInt(receivedData[start.get() + 9], receivedData[start.get() + 10], receivedData[start.get() + 11], receivedData[start.get() + 12]);

                //Block ID
                int newBlock = ByteUtils.bytesToShort(receivedData[start.get() + 13], receivedData[start.get() + 14]);
                BlockHistory blockHistory = new BlockHistory(Registrys.getBlock((short) newBlock));
                blockHistory.fromNetwork = true;
                start.set(start.get() + 15);

                //Block data
                blockHistory.newBlockData = readBlockData(receivedData, start);
                blockHistory.updateBlockData = true;

                //Add the block to the list
                Vector3i position = new Vector3i(x, y, z);
                newEvent.accept(position, blockHistory);
            }
        }
    }


    public static void writeBlockData(BlockData data, OutputStream out) throws IOException {
        if (data == null) {
            out.write(new byte[]{0, 0});//Just write 0 for the length
            return;
        }

        if (data.size() > BLOCK_DATA_MAX_BYTES) {
            ErrorHandler.report(new Throwable("Block data too large: " + data.size()));
            out.write(new byte[]{0, 0});//Just write 0 for the length
            return;
        }
        //First write the length of the block data as an unsigned short
        out.write(shortToBytes(data.size() & 0xffff));

        //Then write the bytes
        byte[] bytes = data.toByteArray();
        out.write(bytes);
    }

    public static BlockData readBlockData(byte[] bytes, AtomicInteger start) {
        //Get the length from unsigned short to int
        int length = bytesToShort(bytes[start.get()], bytes[start.get() + 1]) & 0xffff;
        start.set(start.get() + 2);

        try {
            //Read the bytes
            byte[] data = new byte[length];
            System.arraycopy(bytes, start.get(), data, 0, length);
            start.set(start.get() + length);
            return new BlockData(data);
        } catch (IndexOutOfBoundsException e) {
            ErrorHandler.log(e);
            return null; //Catch the error just to be safe
        }
    }

    public void clear() {
        blockChanges.clear();
        changeEvent();
    }

    protected void changeEvent() {
    }
}
