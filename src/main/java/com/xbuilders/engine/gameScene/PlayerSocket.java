package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import org.joml.Vector3i;

import java.util.HashMap;

public class PlayerSocket extends NetworkSocket {
    public Player player;
    public HashMap<Vector3i, BlockHistory> changes = new HashMap<>();

    public float distToPlayer(Player player2) {
     return   this.player.worldPosition.distance(player2.worldPosition);
    }
}
