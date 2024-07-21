package com.xbuilders.game;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.chunk.BlockData;
import org.joml.Vector3i;

import java.util.ArrayList;

public class LavaPropagation extends WaterPropagation {

    public LavaPropagation() {
        updateIntervalMS = 900;
        maxFlow = 6;
        interestedBlock = MyGame.BLOCK_LAVA;
    }
}
