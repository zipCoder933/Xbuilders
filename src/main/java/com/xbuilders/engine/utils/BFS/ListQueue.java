/*
 * Click nbfs://nbhost/SystemFileSystem/BlockNodeemplates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/BlockNodeemplates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.BFS;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author zipCoder933
 * @param <BlockNode>
 */
public class ListQueue<T> extends ArrayList<T> {

    public ListQueue() {
        super();
    }

    public ListQueue(ListQueue<T> lq) {
        super(lq);
    }

    public void add(ArrayList<T> startingNodes) {
        for (T startNode : startingNodes) {
            add(startNode);
        }
    }

    public void add(T... startingNodes) {
        for (T startNode : startingNodes) {
            add(startNode);
        }
    }

    public boolean containsNodes() {
        return !this.isEmpty();
    }

    public void addAtBeginning(T node) {
        //Dont mark as already explored. That is supposed to be done by the explore() algorithm
        this.add(0, node);
    }

    public void addNodes(T... nodes) {
        //Dont mark as already explored. That is supposed to be done by the explore() algorithm
        for (T node : nodes) {
            this.add(node);
        }
    }

    public void addNodes(HashSet<T> nodes) {
        for (T node : nodes) {
            this.add(node);
        }
    }

    public void addNodes(ArrayList<T> nodes) {
        //Dont mark as already explored. That is supposed to be done by the explore() algorithm
        for (T node : nodes) {
            this.add(node);
        }
    }

    public T getAndRemove(int i) {
        T node = this.get(i);
        this.remove(i);
        return node;
    }

}
