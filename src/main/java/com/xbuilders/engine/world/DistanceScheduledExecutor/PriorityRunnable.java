package com.xbuilders.engine.world.DistanceScheduledExecutor;


public abstract class PriorityRunnable implements Runnable {
    public final float priority;

    public PriorityRunnable(float priority) {
        this.priority = priority;
    }
}