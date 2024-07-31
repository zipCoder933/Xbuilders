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

//This WILL be another class for 2 reasons
//1) It is less complicated this way
//2) the blockPipeline does not need to know anything aout syncing local entities
public class Local_PendingEntityChanges extends PendingEntityChanges {

    public Local_PendingEntityChanges(Player player) {
        super(null, player);
    }
}
