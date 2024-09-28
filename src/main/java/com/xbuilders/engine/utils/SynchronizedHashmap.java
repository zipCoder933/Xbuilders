package com.xbuilders.engine.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedHashmap<K, V> extends HashMap<K, V> implements Map<K, V> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public final Lock readLock = readWriteLock.readLock();
    public final Lock writeLock = readWriteLock.writeLock();

    //put
    public V put(K key, V value) {
        writeLock.lock();
        try {
            return super.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    //clear
    public void clear() {
        writeLock.lock();
        try {
            super.clear();
        } finally {
            writeLock.unlock();
        }
    }
}
