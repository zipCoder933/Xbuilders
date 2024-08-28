package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.player.pipeline.BlockHistory;
import org.joml.Vector3i;

import java.util.HashSet;

public abstract class LivePropagationTask {
    public int updateIntervalMS = 100;
    protected long lastUpdate = 0;

    /**
     *
     * @param worldPos
     * @param history
     * @return true if the node was added
     */
    public abstract boolean addNode(Vector3i worldPos, BlockHistory history);


    public LivePropagationTask() {
    }

    public abstract void update();
//        Iterator<Vector3i> iterator = blocksOfInterest.iterator();
//        while (iterator.hasNext()) {
//            Vector3i item = iterator.next();
////            if (item.equals("item2")) {
////                iterator.remove();
////            }
//        }
}
