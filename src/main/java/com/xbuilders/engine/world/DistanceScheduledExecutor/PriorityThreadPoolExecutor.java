package com.xbuilders.engine.world.DistanceScheduledExecutor;

import java.util.concurrent.*;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public PriorityThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize,// the number of threads
                corePoolSize, // the maximum number of threads
                0, TimeUnit.MILLISECONDS,//Time to wait before terminating an idle thread
                new PriorityBlockingQueue<Runnable>(corePoolSize, new PriorityFutureComparator()));
    }


    public PriorityThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize,// the number of threads
                corePoolSize, // the maximum number of threads
                0, TimeUnit.MILLISECONDS,//Time to wait before terminating an idle thread
                new PriorityBlockingQueue<Runnable>(corePoolSize, new PriorityFutureComparator()),//The PriorityBlockingQueue used can impact the task execution
                threadFactory);
    }


    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and the {@linkplain ThreadPoolExecutor.AbortPolicy
     * default rejected execution handler}.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param threadFactory   the factory to use when the executor
     *                        creates a new thread
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code threadFactory} is null
     */
    public PriorityThreadPoolExecutor(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      TimeUnit unit,
                                      ThreadFactory threadFactory) {
        super(corePoolSize,// the number of threads
                maximumPoolSize, // the maximum number of threads
                keepAliveTime, unit,//Time to wait before terminating an idle thread
                new PriorityBlockingQueue<Runnable>(corePoolSize, new PriorityFutureComparator()),//The PriorityBlockingQueue used can impact the task execution
                threadFactory);
    }


    /*
     * ABOUT THE INITIAL CAPACITY OF THE PRIORITY_BLOCKING_QUEUE
     * Setting the initial capacity of the PriorityBlockingQueue to the value of the ThreadPoolExecutor's maximumPoolSize
     * can be a reasonable approach, especially if you expect the number of tasks in the queue to be close to the
     * maximum number of tasks that the ThreadPoolExecutor will be executing concurrently. However, keep in mind that
     * the initial capacity is just a hint to the implementation and the queue will still dynamically resize if needed.
     * It's important to monitor the behavior of the application and adjust the initial capacity as needed based on
     * actual usage patterns.
     * */


    /**
     * @param corePoolSize             the number of threads that are allowed to be in the pool
     * @param maximumPoolSize          the maximum number of threads
     * @param keepAliveTime            the time to wait before terminating an idle thread
     * @param unit                     the time unit for the keepAliveTime
     * @param threadFactory            the factory used to create new threads
     * @param blockingQueueInitialSize the initial capacity of the PriorityBlockingQueue
     */
    public PriorityThreadPoolExecutor(int corePoolSize,
                                      int maximumPoolSize,
                                      int blockingQueueInitialSize,
                                      long keepAliveTime,
                                      TimeUnit unit,
                                      ThreadFactory threadFactory) {
        super(corePoolSize, // the number of threads
                maximumPoolSize, // the maximum number of threads
                keepAliveTime, unit,//Time to wait before terminating an idle thread
                new PriorityBlockingQueue<Runnable>(blockingQueueInitialSize, new PriorityFutureComparator()),
                threadFactory);
    }
//</editor-fold>

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
        return new PriorityFuture<T>(newTaskFor, ((PriorityCallable) callable).priority);
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        RunnableFuture<T> newTaskFor = super.newTaskFor(runnable, value);
        return new PriorityFuture<T>(newTaskFor, ((PriorityRunnable) runnable).priority);
    }

    public void cancelAllTasks() {
        cancelAllTasks(this);
    }

    public static void cancelAllTasks(ThreadPoolExecutor executor) {
        for (Runnable scheduledTask : executor.getQueue()) {
            // Cast to access the Future method cancel
            Future<?> future = (Future<?>) scheduledTask;
            // Cancel scheduled but not started tasks
            future.cancel(false);

            //Wait for the task to finish
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
            }
        }

    }


    public <T> Future<T> submit(float priority, Callable<T> task) {
        return super.submit(new PriorityCallable(priority) {
            @Override
            public T call() throws Exception {
                return task.call();
            }
        });
    }

    public Future<?> submit(float priority, Runnable task) {
        return super.submit(new PriorityRunnable(priority) {
            @Override
            public void run() {
                task.run();
            }
        });
    }
}
