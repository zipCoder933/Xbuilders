package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.world.chunk.BlockData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Player socket is a model of the other player.
 * The changes are changes that WE have made but are sending to the other player
 */
public class PlayerSocket extends NetworkSocket {
    public Player player;
    public boolean wasWithinReach = false;
//    public int playerChunkDistance; //So far this feature is not used
    private long lastChangeUpdate;
    HashMap<Vector3i, BlockHistory> changesForPlayer = new HashMap<>();


    public void addBlockChange(Vector3i worldPos, Block block, BlockData data) {
        changesForPlayer.put(worldPos, new BlockHistory(block, data));
    }

    public boolean anyChangesWithinReach() {
        for (Map.Entry<Vector3i, BlockHistory> entry : changesForPlayer.entrySet()) {
            Vector3i worldPos = entry.getKey();
            if (isWithinReach(worldPos.x, worldPos.y, worldPos.z)) {
                return true;
            }
        }
        return false;
    }

    public int sendApplicableBlockChangesToPlayer() {
        if (changesForPlayer.isEmpty()) return 0;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //Make a copy of the change list first
            HashMap<Vector3i, BlockHistory> copy = new HashMap<>(changesForPlayer);
            int changesToBeSent = 0;

            for (Map.Entry<Vector3i, BlockHistory> entry : copy.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                if (isWithinReach(worldPos.x, worldPos.y, worldPos.z)) {
                    baos.write(new byte[]{GameServer.VOXEL_BLOCK_CHANGE});
                    baos.write(ByteUtils.intToBytes(worldPos.x));
                    baos.write(ByteUtils.intToBytes(worldPos.y));
                    baos.write(ByteUtils.intToBytes(worldPos.z));
                    baos.write(ByteUtils.shortToBytes(change.currentBlock.id));
                    if (change.data != null) baos.write(change.data.toByteArray());
                    changesForPlayer.remove(entry.getKey());//Remove it so we don't send it again
                    changesToBeSent++;
                }
            }
            baos.close();

            if (changesToBeSent > 0) {
                lastChangeUpdate = System.currentTimeMillis();
                byte byteList[] = baos.toByteArray();
                sendData(byteList);
            }
            return changesToBeSent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isWithinReach(float worldX, float worldY, float worldZ) {
        return player.worldPosition.distance(worldX, worldY, worldZ) < GameScene.world.getViewDistance();
    }

    public boolean isWithinReach(Player otherPlayer) {
        return this.player.worldPosition.distance(otherPlayer.worldPosition) < GameScene.world.getViewDistance();
    }


    public String getName() {
        if (player != null && player.name != null) {
            return player.name;
        }
        return "\"" + getHostAddress() + "\"";
    }

    @Override
    public String toString() {
        return "PlayerSocket(" + getName() + '}';
    }


    public void update(UserControlledPlayer user, Matrix4f projection, Matrix4f view) {
        boolean inRange = isWithinReach(user);
        if (inRange) {
            player.update(projection, view);
        }

        if (System.currentTimeMillis() - lastChangeUpdate > 1000
                && anyChangesWithinReach()) { //We have to send changes every so often if the changes are out of range
            lastChangeUpdate = System.currentTimeMillis();
            int changes = sendApplicableBlockChangesToPlayer();
            System.out.println("\tSent " + changes + " changes");
        }

        if (inRange != wasWithinReach) {
            System.out.println("Player " + getName() + " " + (inRange ? "in" : "out") + " reach");
            wasWithinReach = inRange;
        }
    }
}
