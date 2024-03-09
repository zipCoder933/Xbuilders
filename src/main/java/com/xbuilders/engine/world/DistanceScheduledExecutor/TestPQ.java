package com.xbuilders.engine.world.DistanceScheduledExecutor;

import java.util.concurrent.*;

class TestPQ {


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int nThreads = 2;
        int qInitialSize = 10;

        PriorityThreadPoolExecutor exec = new PriorityThreadPoolExecutor(nThreads);

//        ExecutorService exec = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
//                new PriorityBlockingQueue<Runnable>(qInitialSize, new PriorityFutureComparator())) {
//
//            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
//                RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
//                return new PriorityFuture<T>(newTaskFor, ((PriorityThreadPoolExecutor.PriorityCallable) callable).priority);
//            }
//
//            protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
//                RunnableFuture<T> newTaskFor = super.newTaskFor(runnable, value);
//                return new PriorityFuture<T>(newTaskFor, ((PriorityThreadPoolExecutor.PriorityRunnable) runnable).priority);
//            }
//        };

        for (int i = 0; i < 20; i++) {
            float priority = (float) (Math.random() * 100);

            exec.submit(priority, () -> {
                System.out.println("Hello world " + priority);
//                Thread.sleep(100);
                return "Hello world";
            });

//            System.out.println("Scheduling: " + priority);
//            PriorityCallable job =
//                    new PriorityCallable<String>(priority) {
//                        @Override
//                        public String call() throws Exception {
//                            System.out.println("Hello world " + priority);
//                            return "Hello world";
//                        }
//                    };
//            exec.submit(job);
        }
    }
}