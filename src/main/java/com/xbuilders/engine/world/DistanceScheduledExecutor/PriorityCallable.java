package com.xbuilders.engine.world.DistanceScheduledExecutor;

import java.util.concurrent.Callable;

public abstract class PriorityCallable<T> implements Callable<T> {
    public final float priority;

    public PriorityCallable(float priority) {
        this.priority = priority;
    }
}

//public interface PriorityCallable<V> extends Callable<V> {
//    public float getPriority();
//    /**
//     * Computes a result, or throws an exception if unable to do so.
//     *
//     * @return computed result
//     * @throws Exception if unable to compute a result
//     */
//    V call() throws Exception;
//}
