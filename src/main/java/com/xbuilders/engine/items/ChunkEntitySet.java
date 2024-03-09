/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.camera.FrustumCullingTester;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author zipCoder933
 */
public class ChunkEntitySet {

    Chunk thisChunk;
    //    FrustumCullingTester frustum;
    public final ArrayList<Entity> list;

    public ChunkEntitySet(Chunk aThis) {
        this.thisChunk = aThis;
//        this.frustum = frustum;
        list = new ArrayList<>();
    }

    public void clear() {
        list.clear();
    }

    public void placeNew(Vector3i worldPos, EntityLink entity) {
        Entity e = entity.makeNew(thisChunk, worldPos.x, worldPos.y, worldPos.z, null);
//        System.out.println("Making new entity: " + e.toString());
        list.add(e);
    }

    public void draw(Matrix4f projection, Matrix4f view, FrustumCullingTester frustum, Vector3f playerPos) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Entity e = list.get(i);
            if (e.destroyMode) {
                list.remove(i);
            } else {
                if (e.isNew) {//Initialize entity on the main thread
                    e.initialize(e.loadBytes);
                    e.loadBytes = null;
                    e.isNew = false;
                    e.updatePosition();
                }
                e.inFrustum = frustum.isSphereInside(e.worldPosition, e.frustumSphereRadius);
                e.distToPlayer = e.worldPosition.distance(playerPos);
                e.draw(projection, view);
                e.updatePosition();


                if (!e.chunkPosition.chunk.equals(e.chunk.position)) { //Switch chunks
                    Chunk toChunk = GameScene.world.chunks.get(e.chunkPosition.chunk);
                    if (toChunk != null) {
//                        System.out.println("SWITCHING FROM " + MiscUtils.printVector(e.chunkPosition.chunk) + " TO " + toChunk);
//                        e.renderThisFrame = false;
                        e.chunk = toChunk;
                        toChunk.entities.list.add(e);
                        list.remove(i);
                    }
                }
            }
        }
    }

}
