/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.BFS;

import java.util.HashSet;

/**
 * Ensures that there is only one type of each node in the queue.
 *
 * @author zipCoder933
 * @param <T>
 */
public class HashQueue<T> extends HashSet<T>{

    public HashQueue() {
       super();
    }
    
    public HashQueue(HashQueue queue2) {
        super(queue2);
    }

    /**
     * Gets an element from the hashset. Hashsets are unordered, however it
     * should return the first element added
     *
     * @return the node
     */
    public T get() {
        return this.stream().findFirst().get();
    }

    /**
     * Removes an element from the hashset. Hashsets are unordered, however it
     * should remove the first element added
     *
     */
    public void remove() {
        T node = this.stream().findFirst().get();
        this.remove(node);
    }

    /**
     * Gets an element from the hashset. Hashsets are unordered, however it
     * should return and remove the first element added
     *
     * @return the node
     */
    public T getAndRemove() {
        T node = this.stream().findFirst().get();
        this.remove(node);
        return node;
    }
}
