/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import com.xbuilders.engine.world.wcc.WCCf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public abstract class Entity {

    protected ArrayList<Byte> loadBytes;
    public EntityLink link;
    public EntityAABB aabb;
    public final WCCf chunkPosition;
    public final Vector3f worldPosition;
    boolean destroyMode = false;
    Chunk chunk;
    public float frustumSphereRadius = 1;
    protected boolean isNew;
    public boolean inFrustum;
    public float distToPlayer;

    public Entity() {
        aabb = new EntityAABB();
        worldPosition = aabb.worldPosition;
        chunkPosition = new WCCf();
        isNew = true;
    }

    //We will only bring this back if the entity is taking too long to load things that dont need the GLFW context.
    public abstract void initialize(ArrayList<Byte> bytes);

    public void updatePosition() {
        aabb.update();
        chunkPosition.set(worldPosition);
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
