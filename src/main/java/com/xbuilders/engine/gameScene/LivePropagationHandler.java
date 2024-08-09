package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.world.WorldInfo;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class LivePropagationHandler extends Thread {
    List<LivePropagationTask> tasks = new ArrayList<>();


    public void addTask(LivePropagationTask waterPropagation) {
        tasks.add(waterPropagation);
    }

    public void addNode(Vector3i pos, BlockHistory hist) {
        for (int i = 0; i < tasks.size(); i++) {
            LivePropagationTask task = tasks.get(i);
            if (task.isInterestedInBlock(hist))
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
