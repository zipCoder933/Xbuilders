package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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
                tickEvent();
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
                //System.out.println("Chunk " + chunk.position + " distToPlayer " + chunk.distToPlayer+" simulation distance "+MainWindow.settings.internal_simulationDistance.value);
                if (chunk.distToPlayer < MainWindow.settings.internal_simulationDistance.value + Chunk.WIDTH) {
                    chunksUpdated += (chunk.tick() ? 1 : 0);
                }
            }
            System.out.println("Tick " + chunksUpdated + " chunks");
        }
    }

    public void stopGameEvent() {
        timer.cancel();
        timer = null;
    }
}
