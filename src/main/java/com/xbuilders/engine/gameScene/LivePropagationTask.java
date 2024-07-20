package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import org.joml.Vector3i;

import java.util.HashSet;

public abstract class LivePropagationTask {
    public int updateIntervalMS = 100;
    protected long lastUpdate = 0;

    public short interestedBlock = BlockList.BLOCK_AIR.id;
    public HashSet<Vector3i> nodes = new HashSet<>();


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
