/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.rendering.entity.EntityShader;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import com.xbuilders.engine.world.wcc.WCCf;

import java.io.IOException;
import java.util.ArrayList;

import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.window.render.MVP;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public abstract class Entity {

    /**
     * We are choosing to have 1 shader for all entities
     * Just want to make sure we keep the uniforms down so we dont have to update so many every frame
     * https://stackoverflow.com/questions/69664014/should-every-object-have-its-own-shader
     */
    public static EntityShader shader;


    private void getLightForPosition() {
        Chunk chunk = GameScene.world.getChunk(chunkPosition.chunk);
        byte light = (byte) 0b11110000;
        if (chunk != null) {
            light = chunk.data.getPackedLight(
                    (int) Math.floor(chunkPosition.chunkVoxel.x),
                    (int) Math.floor(chunkPosition.chunkVoxel.y),
                    (int) Math.floor(chunkPosition.chunkVoxel.z));

            for (int i = 1; i < 4; i++) {
                if (light == 0) {
                    WCCi wcc = new WCCi();
                    wcc.set((int) Math.floor(worldPosition.x),
                            (int) Math.floor(worldPosition.y - i),
                            (int) Math.floor(worldPosition.z));
                    chunk = GameScene.world.getChunk(wcc.chunk);
                    if (chunk != null) {
                        light = chunk.data.getPackedLight(
                                wcc.chunkVoxel.x,
                                wcc.chunkVoxel.y,
                                wcc.chunkVoxel.z);
                    }
                } else break;
            }

            //Unpack light
//            light = ChunkVoxels.unpackLight(light);

        }
        sunValue = 1;
        torchValue = 1;
//
//        sunValue = ( float)  /15;
//        torchValue = (float) chunk.data.getTorch(
//                (int) Math.floor(chunkPosition.chunkVoxel.x),
//                (int) Math.floor(chunkPosition.chunkVoxel.y),
//                (int) Math.floor(chunkPosition.chunkVoxel.z)) / 15;
    }

    private float sunValue;
    private float torchValue;

    protected ArrayList<Byte> loadBytes;
    public EntityLink link;
    public EntityAABB aabb;
    public final WCCf chunkPosition;
    public final Vector3f worldPosition;
    private final Vector3f prevWorldPosition;//KEEP PRIVATE
    public final MVP mvp = new MVP();
    boolean destroyMode = false;
    Chunk chunk;
    public float frustumSphereRadius = 1; //Each entity has a sphere that is used for frustum culling. This defines its radius.
    protected boolean needsInitialization;
    public boolean inFrustum; //This value is automatically set by the frustum culling tester
    public float distToPlayer;

    public Entity() {
        aabb = new EntityAABB();
        worldPosition = aabb.worldPosition;
        prevWorldPosition = new Vector3f();
        chunkPosition = new WCCf();
        needsInitialization = true;
    }

    /**
     * Private entity drawing method, used to do things before and after the entity is drawn
     */
    protected void hidden_drawEntity(Matrix4f projection, Matrix4f view) {
        if (shader == null) {
            try {
                shader = new EntityShader();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (inFrustum) {
            shader.loadFloat(shader.sunUniform, sunValue);
            shader.loadFloat(shader.torchUniform, torchValue);
            mvp.sendToShader(shader.getID(), shader.mvpUniform);
        }
        draw(projection, view);
    }

    protected void hidden_entityInitialize(ArrayList<Byte> loadBytes) {
        getLightForPosition();
        initialize(loadBytes);
    }


    //We will only bring this back if the entity is taking too long to load things that dont need the GLFW context.
    public abstract void initialize(ArrayList<Byte> bytes);

    public void updatePosition() {
        aabb.update();
        chunkPosition.set(worldPosition);

        if (!worldPosition.equals(prevWorldPosition)) { //If the entity has moved
            getLightForPosition();
            prevWorldPosition.set(worldPosition);
        }
    }


    public abstract void draw(Matrix4f projection, Matrix4f view);

    public void toBytes(XBFilterOutputStream fout) throws IOException {
    }


    @Override
    public String toString() {
        return "Entity{" + "position=" + MiscUtils.printVector(worldPosition) + ", chunk=" + chunk + '}';
    }

    public void destroy() {
        destroyMode = true;
    }


}
