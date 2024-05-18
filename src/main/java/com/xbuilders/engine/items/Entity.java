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
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import com.xbuilders.engine.world.wcc.WCCf;

import java.io.IOException;
import java.util.ArrayList;

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

    static {
        if (shader == null) {
            try {
                shader = new EntityShader();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void getLightForPosition() {
        Chunk chunk = GameScene.world.getChunk(chunkPosition.chunk);
        sunValue = (float) chunk.data.getSun(
                (int) Math.floor(chunkPosition.chunkVoxel.x),
                (int) Math.floor(chunkPosition.chunkVoxel.y),
                (int) Math.floor(chunkPosition.chunkVoxel.z)) / 15;
        torchValue = (float) chunk.data.getTorch(
                (int) Math.floor(chunkPosition.chunkVoxel.x),
                (int) Math.floor(chunkPosition.chunkVoxel.y),
                (int) Math.floor(chunkPosition.chunkVoxel.z)) / 15;
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
