package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.game.WaterPropagation;

import java.util.ArrayList;
import java.util.List;

public class LivePropagationHandler extends Thread{
    List<LivePropagationTask> tasks = new ArrayList<>();


    public void addTask(WaterPropagation waterPropagation) {
        tasks.add(waterPropagation);
    }

    public LivePropagationHandler() {

    }

//    public void update(){
//        for(int i = 0; i < tasks.size(); i++){
//            LivePropagationTask task = tasks.get(i);
//            if(System.currentTimeMillis() - task.lastUpdate > task.updateIntervalMS){
//                task.lastUpdate = System.currentTimeMillis();
//                tasks.get(i).update();
//            }
//        }
//    }

    public void startGame(WorldInfo world) {
    }

    public void endGame() {
    }

}
