package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import com.xbuilders.engine.world.wcc.WCCf;
import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//This WILL be another class for 2 reasons
//1) It is less complicated this way
//2) the blockPipeline does not need to know anything aout syncing local entities
public class PendingEntityChanges {
    HashSet<Entity> entityCreation = new HashSet<>();
    HashSet<Entity> entityDeletion = new HashSet<>();
    HashSet<Entity> entityUpdate = new HashSet<>();

    public long rangeChangesUpdate;
    public long allChangesUpdate;
    NetworkSocket socket;
    Player player;

    //Each list gets its own read and write lock
    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected final Lock readLock = readWriteLock.readLock();
    protected final Lock writeLock = readWriteLock.writeLock();


    public PendingEntityChanges(NetworkSocket socket, Player player) {
        this.socket = socket;
        this.player = player;
    }

    public static boolean changeWithinReach(Player userPlayer, Vector3f currentPos) {
        return userPlayer.isWithinReach(
                currentPos.x,
                currentPos.y,
                currentPos.z);
    }

    @FunctionalInterface
    public interface ReadConsumer {
        void accept(int mode, EntityLink entityLink, long identifier, Vector3f currentPos, byte[] data);
    }

    public int sendNearEntityChanges() {
        if (entityCreation.isEmpty() && entityDeletion.isEmpty() && entityUpdate.isEmpty()) return 0;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HashSet<Entity> deletionCopy;
            HashSet<Entity> creationCopy;
            HashSet<Entity> updateCopy;

            writeLock.lock();
            try { //Make a copy of the change list first
                deletionCopy = new HashSet<>(entityDeletion);
                creationCopy = new HashSet<>(entityCreation);
                updateCopy = new HashSet<>(entityUpdate);
            } finally {
                writeLock.unlock();
            }

            int createChanges = 0;
            int deleteChanges = 0;
            int updateChanges = 0;

            //Deletion list
            Iterator<Entity> iterator = deletionCopy.iterator();
            while (iterator.hasNext()) {
                Entity entity = iterator.next();
                if (changeWithinReach(player, entity.worldPosition)) {
                    entityChangeRecord(baos, GameServer.ENTITY_DELETED, entity);
                    createChanges++;
                    entityDeletion.remove(entity); //Remove it from the original list
                }
            }

            //Creation list
            iterator = creationCopy.iterator();
            while (iterator.hasNext()) {
                Entity entity = iterator.next();
                if (changeWithinReach(player, entity.worldPosition)) {
                    entityChangeRecord(baos, GameServer.ENTITY_CREATED, entity);
                    deleteChanges++;
                    entityCreation.remove(entity);
                }
            }

            //Update list
            iterator = updateCopy.iterator();
            while (iterator.hasNext()) {
                Entity entity = iterator.next();
                if (changeWithinReach(player, entity.worldPosition)) {
                    entityChangeRecord(baos, GameServer.ENTITY_UPDATED, entity);
                    updateChanges++;
                    iterator.remove();
                }
            }

            baos.close();

            if (createChanges > 0 || deleteChanges > 0 || updateChanges > 0) {
                rangeChangesUpdate = System.currentTimeMillis();
                byte byteList[] = baos.toByteArray();
                socket.sendData(byteList);
            }
            return createChanges + deleteChanges + updateChanges;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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
                && (entityCreation.size() > 0 || entityDeletion.size() > 0 || entityUpdate.size() > 0)) {
            allChangesUpdate = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    private boolean anyChangesWithinReach() {
        readLock.lock();
        try {
            return !entityCreation.isEmpty() || !entityDeletion.isEmpty() || !entityUpdate.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    public static void readEntityChange(byte[] receivedData, ReadConsumer newEvent) {
        //Split the recievedData by the newline byte
        AtomicInteger start = new AtomicInteger(0);
        while (start.get() < receivedData.length) {
            if (receivedData[start.get()] == GameServer.ENTITY_CREATED ||
                    receivedData[start.get()] == GameServer.ENTITY_DELETED ||
                    receivedData[start.get()] == GameServer.ENTITY_UPDATED) {
                int mode = receivedData[start.get()];
                start.set(start.get() + 1);

                //last XYZ coordinates
                long identifier = ByteUtils.bytesToLong(receivedData, start);

                //Current XYZ coordinates
                float x = ByteUtils.bytesToFloat(receivedData[start.get()], receivedData[start.get() + 1], receivedData[start.get() + 2], receivedData[start.get() + 3]);
                float y = ByteUtils.bytesToFloat(receivedData[start.get() + 4], receivedData[start.get() + 5], receivedData[start.get() + 6], receivedData[start.get() + 7]);
                float z = ByteUtils.bytesToFloat(receivedData[start.get() + 8], receivedData[start.get() + 9], receivedData[start.get() + 10], receivedData[start.get() + 11]);
                Vector3f currentPos = new Vector3f(x, y, z);
                start.set(start.get() + 12);

                //Entity ID
                int blockID = ByteUtils.bytesToShort(receivedData[start.get()], receivedData[start.get() + 1]);
                EntityLink entity = ItemList.getEntity((short) blockID);
                start.set(start.get() + 2);

                //Block data
                byte[] data = ChunkSavingLoadingUtils.readEntityData(receivedData, start);

                //Add the block to the list
                newEvent.accept(mode, entity, identifier, currentPos, data);
            }
        }
    }

    public void addEntityChange(Entity entity, int mode) {
        writeLock.lock();
        try {
            if (mode == GameServer.ENTITY_CREATED) entityCreation.add(entity);
            else if (mode == GameServer.ENTITY_DELETED) entityDeletion.add(entity);
            else if (mode == GameServer.ENTITY_UPDATED) entityUpdate.add(entity);
            changeEvent();
        } finally {
            writeLock.unlock();
        }
    }

    public boolean sendChange(Entity entity, byte operation) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            entityChangeRecord(baos, operation, entity);
            baos.close();

            byte byteList[] = baos.toByteArray();
            if (changeWithinReach(player, entity.worldPosition)) {
                System.out.println("Entity sent: " + Arrays.toString(byteList));
                socket.sendData(byteList);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void entityChangeRecord(OutputStream baos, byte entityOperation, Entity entity) throws IOException {
        baos.write(new byte[]{entityOperation});

        //Send identifier
        ByteUtils.writeLong(baos, entity.getIdentifier());

        //Send current position
        baos.write(ByteUtils.floatToBytes(entity.worldPosition.x));
        baos.write(ByteUtils.floatToBytes(entity.worldPosition.y));
        baos.write(ByteUtils.floatToBytes(entity.worldPosition.z));

        //Send entity ID
        baos.write(ByteUtils.shortToBytes(entity.link.id));

        //Send entity byte data (state or entity initialisation)
        byte[] data = null;
        if (entityOperation == GameServer.ENTITY_UPDATED) {
            data = entity.stateToBytes();
        } else if (entityOperation == GameServer.ENTITY_CREATED) {
            data = entity.toBytes();
        }
        ChunkSavingLoadingUtils.writeEntityData(data, baos);
    }


    public void clear() {
        readLock.lock();
        try {
            entityCreation.clear();
            entityDeletion.clear();
            entityUpdate.clear();
            changeEvent();
        } finally {
            readLock.unlock();
        }
    }

    protected void changeEvent() {
    }
}
