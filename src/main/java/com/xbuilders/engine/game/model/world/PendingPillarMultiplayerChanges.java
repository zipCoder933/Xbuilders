package com.xbuilders.engine.game.model.world;

import com.xbuilders.engine.game.model.players.pipeline.BlockHistory;
import org.joml.Vector3i;

import java.util.HashMap;

public class PendingPillarMultiplayerChanges {
    public final HashMap<Vector3i, BlockHistory> blockChanges = new HashMap<>();

    public int totalSize() {
        return blockChanges.size();
    }
}
