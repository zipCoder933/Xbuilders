package com.xbuilders.engine.game.multiplayer;

import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.game.model.items.entity.Entity;
import com.xbuilders.engine.game.model.items.entity.EntitySupplier;
import com.xbuilders.engine.game.model.players.Player;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.game.model.world.chunk.saving.ChunkSavingLoadingUtils;
import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//This WILL be another class for 2 reasons
//1) It is less complicated this way
//2) the blockPipeline does not need to know anything aout syncing local entities
public class MultiplayerPendingEntityChanges {


    final ConcurrentHashMap.KeySetView<Entity, Boolean> entityCreation = ConcurrentHashMap.newKeySet();
    final ConcurrentHashMap.KeySetView<Entity, Boolean> entityDeletion = ConcurrentHashMap.newKeySet();
    final ConcurrentHashMap.KeySetView<Entity, Boolean> entityUpdate = ConcurrentHashMap.newKeySet();

    public long rangeChangesUpdate;
    NetworkSocket socket;
    Player player;

    public MultiplayerPendingEntityChanges(NetworkSocket socket, Player player) {
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
        void accept(int mode, EntitySupplier entityLink, long identifier,
                    Vector3f currentPos, byte[] data, boolean isControlledByAnotherPlayer);
    }

    public int sendNearEntityChanges() {
        if (entityCreation.isEmpty() && entityDeletion.isEmpty() && entityUpdate.isEmpty()) return 0;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HashSet<Entity> deletionCopy = new HashSet<>(entityDeletion);
            HashSet<Entity> creationCopy = new HashSet<>(entityCreation);
            HashSet<Entity> updateCopy = new HashSet<>(entityUpdate);


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

    private boolean anyChangesWithinReach() {
        return !entityCreation.isEmpty() || !entityDeletion.isEmpty() || !entityUpdate.isEmpty();
    }

    public void entityChangeRecord(OutputStream baos, byte entityOperation, Entity entity) throws IOException {
        baos.write(new byte[]{entityOperation});
        baos.write(new byte[]{(byte) (entity.multiplayerProps.controlledByUs() ? 1 : 0)});
        entity.multiplayerProps.controlMode = false;

        //Send identifier
        ByteUtils.writeLong(baos, entity.getUniqueIdentifier());

        //Send current position
        baos.write(ByteUtils.floatToBytes(entity.worldPosition.x));
        baos.write(ByteUtils.floatToBytes(entity.worldPosition.y));
        baos.write(ByteUtils.floatToBytes(entity.worldPosition.z));

        //Send entity ID
        baos.write(ByteUtils.shortToBytes(entity.id));

        //Send entity byte data (state or entity initialisation)
        byte[] data = null;
        if (entityOperation == GameServer.ENTITY_UPDATED) {
            data = entity.entityState_toBytes();
        } else if (entityOperation == GameServer.ENTITY_CREATED) {
            data = entity.toBytes();
        }
        ChunkSavingLoadingUtils.writeEntityData(data, baos);
    }


    public static void readEntityChange(byte[] receivedData, ReadConsumer newEvent) {
        //Split the recievedData by the newline byte
        AtomicInteger start = new AtomicInteger(0);
        while (start.get() < receivedData.length) {
            if (receivedData[start.get()] == GameServer.ENTITY_CREATED ||
                    receivedData[start.get()] == GameServer.ENTITY_DELETED ||
                    receivedData[start.get()] == GameServer.ENTITY_UPDATED) {

                int mode = receivedData[start.get()];
                boolean controlledByAnotherPlayer = receivedData[start.get() + 1] == 1;
                start.set(start.get() + 2);


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
                EntitySupplier entity = Registrys.getEntity((short) blockID);
                start.set(start.get() + 2);

                //Block data
                byte[] data = ChunkSavingLoadingUtils.readEntityData(receivedData, start);

                //Add the block to the list
                newEvent.accept(mode, entity, identifier, currentPos, data, controlledByAnotherPlayer);
            }
        }
    }

    public void addEntityChange(Entity entity, byte operation, boolean sendImmediately) {
        if (sendImmediately && sendInstantChange(entity, operation)) return;

        if (operation == GameServer.ENTITY_CREATED) entityCreation.add(entity);
        else if (operation == GameServer.ENTITY_DELETED) entityDeletion.add(entity);
        else if (operation == GameServer.ENTITY_UPDATED) entityUpdate.add(entity);
        changeEvent();
    }

    private boolean sendInstantChange(Entity entity, byte operation) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            entityChangeRecord(baos, operation, entity);
            baos.close();
            if (changeWithinReach(player, entity.worldPosition)) {
                socket.sendData(baos.toByteArray());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void clear() {
        entityCreation.clear();
        entityDeletion.clear();
        entityUpdate.clear();
        changeEvent();
    }

    protected void changeEvent() {
    }
}
