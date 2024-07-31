package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import org.joml.Vector3i;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PendingEntityChanges {

    HashSet<Entity> entityCreation = new HashSet<>();
    HashSet<Entity> entityDeletion = new HashSet<>();
    HashSet<Entity> entityUpdate = new HashSet<>();

    public long rangeChangesUpdate;
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


    public static boolean changeWithinReach(Player player, Vector3i worldPos) {
        return player.isWithinReach(worldPos.x, worldPos.y, worldPos.z);
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


    public void entityChangeRecord(OutputStream baos, byte entityOperation, Entity entity) throws IOException {
        baos.write(new byte[]{entityOperation});
        baos.write(ByteUtils.floatToBytes(entity.multiplayerProperties.lastPosition.x));
        baos.write(ByteUtils.floatToBytes(entity.multiplayerProperties.lastPosition.y));
        baos.write(ByteUtils.floatToBytes(entity.multiplayerProperties.lastPosition.z));
        baos.write(ByteUtils.shortToBytes(entity.link.id));

        byte[] data = null;

        if (entityOperation == GameServer.ENTITY_UPDATED) {
            data = entity.stateToBytes();
        } else if (entityOperation == GameServer.ENTITY_CREATED) {
            data = entity.toBytes();
        }
        ChunkSavingLoadingUtils.writeEntity(data, baos);
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
