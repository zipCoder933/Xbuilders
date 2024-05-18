/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.camera.FrustumCullingTester;
import com.xbuilders.engine.utils.math.MathUtils;
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
                if (e.needsInitialization) {//Initialize entity on the main thread
                    e.link.initializeEntity(e, e.loadBytes); //Sometimes the entity link has static variables, this is an attempt to fix that
                    e.loadBytes = null;
                    e.needsInitialization = false;
                    e.updatePosition();
                }
                e.inFrustum = frustum.isSphereInside(e.worldPosition, e.frustumSphereRadius);
                e.distToPlayer = e.worldPosition.distance(playerPos);
                e.hidden_drawEntity(projection, view);
                e.updatePosition();


                if (!e.chunkPosition.chunk.equals(e.chunk.position)) { //Switch chunks
                    Chunk toChunk = GameScene.world.chunks.get(e.chunkPosition.chunk);
                    if (toChunk != null && toChunk.gen_Complete()) {
                        //If the chunk exists, AND it's generated, add the entity to the new chunk
//                        System.out.println("SWITCHING FROM " + MiscUtils.printVector(e.chunkPosition.chunk) + " TO " + toChunk);
//                        e.renderThisFrame = false;
                        e.chunk = toChunk;
                        toChunk.entities.list.add(e);
                        list.remove(i);
                    } else {
                        //Otherwise, clamp entity position to the existing chunk
                        e.worldPosition.set(
                                MathUtils.clamp(e.worldPosition.x, e.chunk.position.x, e.chunk.position.x + Chunk.WIDTH - 1),
                                MathUtils.clamp(e.worldPosition.y, e.chunk.position.y, e.chunk.position.y + Chunk.HEIGHT - 1),
                                MathUtils.clamp(e.worldPosition.z, e.chunk.position.z, e.chunk.position.z + Chunk.WIDTH - 1));
                        e.updatePosition();
                    }
                }
            }
        }
    }

}
