/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.entity;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.multiplayer.GameServer;
import com.xbuilders.engine.player.camera.FrustumCullingTester;
import com.xbuilders.engine.rendering.entity.EntityShader;
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

    public static final int MAX_ENTITY_DIST = 100;

    public boolean chunkUpdatedMesh;
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

    public Entity placeNew(Vector3f worldPos, EntityLink entity, byte[] data) {
        Entity e = entity.makeNew(thisChunk, worldPos.x, worldPos.y, worldPos.z, data);
        list.add(e);
        return e;
    }

    public Entity placeNew(Vector3i worldPos, EntityLink entity, byte[] data) {
        Entity e = entity.makeNew(thisChunk, worldPos.x, worldPos.y, worldPos.z, null);
        list.add(e);
        return e;
    }

    public static void startDraw(Matrix4f projection, Matrix4f view) {
        if (Entity.shader == null) {//Unless another entity uses a different shader, we only need to bind once
            Entity.shader = new EntityShader();
        }
        Entity.shader.bind();
        Entity.shader.updateProjectionViewMatrix(projection, view);
    }

    public void draw(FrustumCullingTester frustum, Vector3f playerPos) {


        for (int i = list.size() - 1; i >= 0; i--) {
            Entity e = list.get(i);
            if (e.destroyMode) {
                GameScene.server.addEntityChange(e, GameServer.ENTITY_DELETED, true);
                list.remove(i);
            } else {
                if (e.needsInitialization) {//Initialize entity on the main thread
                    e.link.initializeEntity(e, e.loadBytes); //Sometimes the entity link has static variables, this is an attempt to fix that
                    e.needsInitialization = false;
                    e.updatePosition();

                    //We have to send the entity after it has been initialized
                    if (e.sendMultiplayer) GameScene.server.addEntityChange(e, GameServer.ENTITY_CREATED, true);
                    e.loadBytes = null; //Do this last
                }
                e.inFrustum = frustum.isSphereInside(e.worldPosition, e.frustumSphereRadius);//Sphere boundary checks are faster than AABB
                e.distToPlayer = e.worldPosition.distance(playerPos);


                if (e.distToPlayer < MAX_ENTITY_DIST) e.hidden_drawEntity();

                if (chunkUpdatedMesh) {
                    e.hidden_entityOnChunkMeshChanged();
                }
                boolean hasMoved = e.updatePosition();
                e.multiplayerProps.sendState();
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
        chunkUpdatedMesh = false;
    }

}
