package com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor;


public abstract class PriorityRunnable implements Runnable {
    public final long priority;

    public PriorityRunnable(float priority) {
        this.priority = (long) priority;
    }

    public PriorityRunnable(long priority) {
        this.priority = priority;
    }

    public PriorityRunnable(int priority) {
        this.priority = (long) priority;
    }
}