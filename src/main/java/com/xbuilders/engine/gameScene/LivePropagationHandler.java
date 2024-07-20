package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.game.WaterPropagation;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class LivePropagationHandler extends Thread {
    List<LivePropagationTask> tasks = new ArrayList<>();


    public void addTask(WaterPropagation waterPropagation) {
        tasks.add(waterPropagation);
    }

    public void addNode(Vector3i pos, short block) {
        for (int i = 0; i < tasks.size(); i++) {
            LivePropagationTask task = tasks.get(i);
            if (task.interestedBlock == block)
                tasks.get(i).nodes.add(pos);
        }
    }

    public LivePropagationHandler() {

    }

    public void update() {
        for (int i = 0; i < tasks.size(); i++) {
            LivePropagationTask task = tasks.get(i);
            if (System.currentTimeMillis() - task.lastUpdate > task.updateIntervalMS) {
                task.lastUpdate = System.currentTimeMillis();
                tasks.get(i).update();
            }
        }
    }

    public void startGame(WorldInfo world) {
    }

    public void endGame() {
    }

}
