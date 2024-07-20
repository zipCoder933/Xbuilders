package com.xbuilders.engine.gameScene;

import com.xbuilders.game.Main;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public abstract class LivePropagationTask {
    public int updateIntervalMS = 100;
    protected long lastUpdate = 0;
    HashSet<Vector3i> blocksOfInterest = new HashSet<>();

    public void addBlockOfInterest(Vector3i worldPos) {
        blocksOfInterest.add(worldPos);
    }

    public LivePropagationTask() {
    }

    public void update() {
//        Iterator<Vector3i> iterator = blocksOfInterest.iterator();
//        while (iterator.hasNext()) {
//            Vector3i item = iterator.next();
////            if (item.equals("item2")) {
////                iterator.remove();
////            }
//        }
    }
}
