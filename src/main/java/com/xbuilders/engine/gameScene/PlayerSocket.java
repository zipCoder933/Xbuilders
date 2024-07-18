package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import org.joml.Vector3i;

import java.util.HashMap;

public class PlayerSocket extends NetworkSocket {
    public Player player;
    public int playerChunkDistance;
    public HashMap<Vector3i, BlockHistory> changes = new HashMap<>();

    public float distToPlayer(Player player2) {
        return this.player.worldPosition.distance(player2.worldPosition);
    }

    public boolean isWithinReach(Player player2){
        return distToPlayer(player2) < playerChunkDistance;
    }


    public String getName() {
        if (player != null && player.name != null) {
            return player.name;
        }
        return "\""+getHostAddress()+"\"";
    }

    @Override
    public String toString() {
        return "PlayerSocket(" + getName() + '}';
    }
}
