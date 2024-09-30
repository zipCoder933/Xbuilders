//package com.xbuilders.engine.multiplayer;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//public class SynchronizedHashmap<K, V> extends HashMap<K, V> implements Map<K, V> {
//    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
//
//    /**
//     * IMPORTANT:
//     * If you attempt to lock the write lock while already holding the read lock, you'll encounter a deadlock. Here's what happens:
//     *
//     * Deadlock: The thread will block indefinitely, waiting for the write lock.
//     * Lock upgrade prevention: ReentrantReadWriteLock doesn't support upgrading from a read lock to a write lock.
//     * Here's an example to illustrate:
//     *
//     * ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
//     * rwLock.readLock().lock();
//     * try {
//     *     // This will cause a deadlock
//     *     rwLock.writeLock().lock();
//     *     try {
//     *         // This code will never be reached
//     *     } finally {
//     *         rwLock.writeLock().unlock();
//     *     }
//     * } finally {
//     *     rwLock.readLock().unlock();
//     * }
//     *
//     * To avoid this:
//     * - Release the read lock before acquiring the write lock.
//     * - Design your code to separate read and write operations clearly.
//     * - Consider using a ReentrantReadWriteLock.WriteLock.lockInterruptibly() if you need to handle potential deadlocks.
//     * - Always be cautious when working with multiple locks to prevent deadlock situations.
//     */
//    public final Lock readLock = readWriteLock.readLock();
//    public final Lock writeLock = readWriteLock.writeLock();
//}
