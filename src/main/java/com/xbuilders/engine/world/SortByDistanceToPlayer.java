/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world;

import com.xbuilders.engine.world.chunk.Chunk;
import org.joml.Vector3f;

import java.util.Comparator;

/**
 * @author zipCoder933
 */
class SortByDistanceToPlayer implements Comparator<Chunk> {

    Vector3f playerPos;

    public SortByDistanceToPlayer(Vector3f playerPos) {
        this.playerPos = playerPos;
    }

    @Override
    public int compare(Chunk chunk, Chunk chunk1) {
        float d1 = playerPos.distance(chunk.position.x, chunk.position.y, chunk.position.z);
        float d2 = playerPos.distance(chunk1.position.x, chunk1.position.y, chunk1.position.z);
        return Float.compare(d1, d2);
    }

}
