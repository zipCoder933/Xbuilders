/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world;

import com.xbuilders.engine.world.chunk.Chunk;
import java.util.Comparator;

/**
 *
 * @author zipCoder933
 */
class SortByDistance implements Comparator<Chunk> {

    @Override
    public int compare(Chunk chunk1, Chunk chunk2) {
        return (int) chunk1.position.distance(chunk2.position);
    }

}
