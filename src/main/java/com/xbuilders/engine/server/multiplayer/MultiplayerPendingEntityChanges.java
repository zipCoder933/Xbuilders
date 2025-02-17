package com.xbuilders.engine.server.multiplayer;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.EntitySupplier;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.utils.bytes.SimpleKyro;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            Output output = new Output(baos);
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
                    entityChangeRecord(output, GameServer.ENTITY_DELETED, entity);
                    createChanges++;
                    entityDeletion.remove(entity); //Remove it from the original list
                }
            }

            //Creation list
            iterator = creationCopy.iterator();
            while (iterator.hasNext()) {
                Entity entity = iterator.next();
                if (changeWithinReach(player, entity.worldPosition)) {
                    entityChangeRecord(output, GameServer.ENTITY_CREATED, entity);
                    deleteChanges++;
                    entityCreation.remove(entity);
                }
            }

            //Update list
            iterator = updateCopy.iterator();
            while (iterator.hasNext()) {
                Entity entity = iterator.next();
                if (changeWithinReach(player, entity.worldPosition)) {
                    entityChangeRecord(output, GameServer.ENTITY_UPDATED, entity);
                    updateChanges++;
                    iterator.remove();
                }
            }
            output.close();

            if (createChanges > 0 || deleteChanges > 0 || updateChanges > 0) {
                rangeChangesUpdate = System.currentTimeMillis();
                byte byteList[] = baos.toByteArray();
                //System.out.println("Sending " + byteList.length + " bytes");
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

    static final SimpleKyro simpleKyro = new SimpleKyro();

    public void entityChangeRecord(Output out, byte entityOperation, Entity entity) throws IOException {
        //Output out, Kryo kryo,
        out.writeByte(entityOperation);
        out.writeBoolean(entity.multiplayerProps.controlledByUs());
        entity.multiplayerProps.controlMode = false;

        //Send identifier
        out.writeLong(entity.getUniqueIdentifier());

        //Send current position
        out.writeFloat(entity.worldPosition.x);
        out.writeFloat(entity.worldPosition.y);
        out.writeFloat(entity.worldPosition.z);

        //Send entity ID
        out.writeString(entity.getId());

        //Send entity byte data (state or entity initialisation)
        byte[] data = null;
        if (entityOperation == GameServer.ENTITY_UPDATED) {
            data = entity.serializeStateData();
        } else if (entityOperation == GameServer.ENTITY_CREATED) {
            data = entity.serializeDefinitionData();
        }
        if (data == null) data = new byte[0];
        simpleKyro.writeByteArray(out, data);
    }


    public static void readEntityChange(Input input, ReadConsumer newEvent) throws IOException {
        //Split the recievedData by the newline byte

        while (input.available() > 0) {
            byte mode = input.readByte();
            if (mode == GameServer.ENTITY_CREATED ||
                    mode == GameServer.ENTITY_DELETED ||
                    mode == GameServer.ENTITY_UPDATED) {

                //Controlled by another player
                boolean controlledByAnotherPlayer = input.readBoolean();
                //last XYZ coordinates
                long identifier = input.readLong();

                //Current XYZ coordinates
                float x = input.readFloat();
                float y = input.readFloat();
                float z = input.readFloat();
                Vector3f currentPos = new Vector3f(x, y, z);

                //Entity ID
                String blockID = input.readString();
                EntitySupplier entity = Registrys.getEntity(blockID);

                //Block data
                byte[] data = simpleKyro.readByteArray(input);

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
            Output output = new Output(baos);
            entityChangeRecord(output, operation, entity);
            output.close();
            if (changeWithinReach(player, entity.worldPosition)) {
                byte[] data = baos.toByteArray();
                //System.out.println("Sending " + data.length + " bytes");
                socket.sendData(data);
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
