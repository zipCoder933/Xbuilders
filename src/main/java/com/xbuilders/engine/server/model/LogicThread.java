package com.xbuilders.engine.server.model;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.world.chunk.Chunk;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class LogicThread {
    Timer timer;
    /**
     * The tick rate is constant, we only shorten the time between ticks if the game is running slow.
     * random tick amont (how fast plants grow, etc) is controlled by how many blocks are updated per tick.
     */
    final int TICK_RATE_MS = 100;
    final int CHUNK_RANDOM_TICK_RATE = 10;
    private long lastTickTime = 0;
    private int ticks = 0;


    /**
     * CHUNK TICKING
     * A chunk is ticked every game tick
     * - random ticking can occur less often but the random distribution of ticks should be uniform
     */
    public void startGameEvent() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    tickEvent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000, TICK_RATE_MS);
    }

//    public void update() {
//        if (System.currentTimeMillis() - lastTickTime > TICK_RATE_MS) {
//            lastTickTime = System.currentTimeMillis();//We set the last tick here to keep the time between ticks constant
//            tickEvent();
//        }
//    }


    public void tickEvent() {
        ticks++;
        if (ticks % CHUNK_RANDOM_TICK_RATE == 0) {
            int chunksUpdated = 0;
            Iterator<Chunk> iterator = GameScene.world.chunks.values().iterator();
            while (iterator.hasNext()) {
                Chunk chunk = iterator.next();
                int simDistance = MainWindow.settings.internal_simulationDistance.value;

                int spawnDistance = (int) Math.min(Chunk.WIDTH * 2, MainWindow.settings.internal_simulationDistance.value * 0.6f);
                spawnDistance = Math.min(MainWindow.settings.video_entityDistance.value, spawnDistance);//Spawn distance is the distance at which entities are spawned
                //System.out.println("Chunk " + chunk.client_distToPlayer + " " + simDistance + " " + spawnDistance);

                if (chunk.client_distToPlayer < simDistance) {
                    boolean spawnEntities = false;//chunk.client_distToPlayer < spawnDistance
                    chunksUpdated += (chunk.tick(spawnEntities) ? 1 : 0);
                }
            }
            //System.out.println("Tick " + chunksUpdated + " chunks");
        }
    }

    public void stopGameEvent() {
        timer.cancel();
        timer = null;
    }
}
