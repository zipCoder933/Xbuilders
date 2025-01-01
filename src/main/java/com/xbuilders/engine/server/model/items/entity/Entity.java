/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.model.items.entity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.multiplayer.EntityMultiplayerInfo;
import com.xbuilders.engine.server.multiplayer.GameServer;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityShader;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityShader_ArrayTexture;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import com.xbuilders.engine.server.model.world.chunk.ChunkVoxels;
import com.xbuilders.engine.server.model.world.chunk.Chunk;
import com.xbuilders.engine.server.model.world.wcc.WCCf;

import com.xbuilders.engine.server.model.world.wcc.WCCi;
import com.xbuilders.window.render.MVP;
import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static EntityShader_ArrayTexture arrayTextureShader;
    public boolean sendMultiplayer;

    public boolean playerIsRidingThis() {
        return GameScene.player.positionLock != null && GameScene.player.positionLock.entity == this;
    }

    private void getLightForPosition() {
        Chunk chunk = GameScene.world.getChunk(chunkPosition.chunk);
        byte light = (byte) 0b11110000;

        if (chunk != null) {
            light = chunk.data.getPackedLight(
                    (int) Math.floor(chunkPosition.chunkVoxel.x),
                    (int) Math.floor(chunkPosition.chunkVoxel.y),
                    (int) Math.floor(chunkPosition.chunkVoxel.z));

            for (int i = 1; i < 3; i++) { //Go up, if the block is in an opaque block
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
        }

        //Unpack light
        sunValue = (float) ChunkVoxels.getSun(light) / 15;
        torchValue = (float) ChunkVoxels.getTorch(light) / 15;
    }

    public float sunValue;
    public float torchValue;
    public boolean spawnedNaturally = false;
    protected byte[] loadBytes;
    public EntitySupplier link;
    protected final long uniqueIdentifier;


    //Position
    public EntityAABB aabb;
    public final WCCf chunkPosition;
    public final Vector3f worldPosition;
    public final EntityMultiplayerInfo multiplayerProps;
    private final Vector3f prevWorldPosition;//KEEP PRIVATE

    //Model view projection
    public final MVP modelMatrix = new MVP();

    private boolean destroyMode = false;
    Chunk chunk;
    public float frustumSphereRadius = 1; //Each entity has a sphere that is used for frustum culling. This defines its radius.
    protected boolean needsInitialization;
    public boolean inFrustum; //This value is automatically set by the frustum culling tester
    public float distToPlayer;
    public final short id;
    public final String alias;
    private static final SecureRandom entityIdentifierGenerator = new SecureRandom();

    public Entity(int id, long uniqueIdentifier) {
        this.id = (short) id;
        this.alias = null;
        sendMultiplayer = false;
        aabb = new EntityAABB();
        worldPosition = aabb.worldPosition;
        prevWorldPosition = new Vector3f();
        chunkPosition = new WCCf();
        needsInitialization = true;
        multiplayerProps = new EntityMultiplayerInfo(this);

        if (uniqueIdentifier == 0)
            this.uniqueIdentifier = entityIdentifierGenerator.nextLong(); //Auto generate the identifier
        else
            this.uniqueIdentifier = uniqueIdentifier;
    }


    /**
     * Private entity drawing method, used to do things before and after the entity is drawn
     */
    protected void hidden_drawEntity() {
        if (shader == null) {
            shader = new EntityShader();
        }
        if (arrayTextureShader == null) {
            arrayTextureShader = new EntityShader_ArrayTexture();
        }
        if (inFrustum) {
            modelMatrix.identity().translate(worldPosition);//Model matrix is already in world position
            shader.setSunAndTorch(sunValue, torchValue);
        }
        draw();
    }

    public final static Kryo kyro = new Kryo();

    /**
     * Used when the entity doesnt have a implemented serialize() method
     *
     * @return
     */
    public byte[] getLoadedBytes() {
        return loadBytes;
    }

    /**
     * Used as another layer of abstraction, if the entity has no data, we return the loaded bytes
     *
     * @param e
     * @return
     * @throws IOException
     */
    public final static byte[] serializeEntityDefinitionData(Entity e) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        e.serialize(output, Entity.kyro);
        output.close();
        byte[] entityBytes = baos.toByteArray();
        System.out.println("Entity bytes: " + Arrays.toString(entityBytes));

        //If the entity has no data, we return the loaded bytes
        if (entityBytes.length == 0 && e.getLoadedBytes() != null) {
            return e.getLoadedBytes();
        } else return entityBytes;
    }

    protected final void hidden_initializeEntity() {
        try {
            getLightForPosition();
            if (loadBytes == null) loadBytes = new byte[0];
            Input input = new Input(loadBytes);
            load(input, kyro);
        } catch (Exception e) {
            ErrorHandler.log(e);
            destroy();
        }

        needsInitialization = false;
        updatePosition();

        //We have to send the entity after it has been initialized
        if (sendMultiplayer) GameScene.server.addEntityChange(this, GameServer.ENTITY_CREATED, true);
    }

    /**
     * Initializes and optionally deserializes the entity
     */
    public void load(Input input, Kryo kyro) throws IOException {
    }

    /**
     * serializes the entity
     */
    public void serialize(Output output, Kryo kyro) throws IOException {
    }


    /**
     * Used for multiplayer / model, live entity state
     *
     * @return
     */
    public byte[] entityState_write() {
        return null;
    }

    /**
     * Used for multiplayer / model, live entity state
     *
     * @param state
     * @param start
     */
    public void entityState_read(byte[] state, AtomicInteger start) {
    }

    //This method will be called when a chunk is saved (or removed)
    public final boolean updatePosition() {
        aabb.update(true);//IF the entity goes outside of a chunk, it will not be reassigned to another chunk and it will dissapear when moved too far
        chunkPosition.set(worldPosition);

        boolean hasMoved = !worldPosition.equals(prevWorldPosition);
        if (hasMoved) { //If the entity has moved
            getLightForPosition();
            prevWorldPosition.set(worldPosition);
            entityMoveEvent();
        }
        return hasMoved;
    }

    public void entityMoveEvent() {
    }

    public void markAsModifiedByUser() {
        chunk.markAsModified();
    }

    protected void hidden_entityOnChunkMeshChanged() {
        getLightForPosition();
    }


    public abstract void draw();


    @Override
    public String toString() {
        return "Entity{id=" + Long.toHexString(getUniqueIdentifier()) + '}';
    }

    public void destroy() {
        destroyMode = true;
    }

    public boolean isDestroyMode() {
        return destroyMode;
    }

    /**
     * Used as a convenience method to initialize the entity supplier
     * Only called once
     *
     * @param entitySupplier
     */
    public void initSupplier(EntitySupplier entitySupplier) {
    }

    /**
     * @return if we want to permit the click event to continue
     */
    public boolean run_ClickEvent() {
        return false;
    }

    public long getUniqueIdentifier() {
        return uniqueIdentifier;
    }

}
