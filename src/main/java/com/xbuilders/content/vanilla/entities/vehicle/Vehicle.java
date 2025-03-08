/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.entities.vehicle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.client.visuals.gameScene.rendering.entity.EntityMesh;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Vehicle extends Entity {

    public static final String JSON_TEXTURE = "v_t";

    public static class Vehicle_staticData {
        public final EntityMesh body;
        public final HashMap<String, Integer> textures;

        public Vehicle_staticData(String bodyMesh, String texturesDir) throws IOException {
            body = new EntityMesh();

            body.loadFromOBJ(resourceLoader.getResourceAsStream(bodyMesh));

            //Generate textures
            List<String> textureRes = resourceLoader.listResourceFiles(texturesDir);
            this.textures = new HashMap<>();
            for (String textureResource: textureRes) {
                String textureKey = resourceLoader.getName(textureResource).replace(".png", "");
                int textureID = Objects.requireNonNull(
                        TextureUtils.loadTextureFromResource(textureResource, false)).id;
                this.textures.put(textureKey, textureID);
            }
        }
    }

    /**
     * Resets the range of the degree back to 0-360
     */
    public float normalizeRotation(float rotationYDeg) {
        return rotationYDeg % 360;
    }

    /**
     * @return the client_distToPlayer
     */
    public float get3DDistToPlayer() {
        return distToPlayer;
    }

    public boolean isOnRoad() {
        int x = Math.round(worldPosition.x);
        int y = Math.round(worldPosition.y);
        int z = Math.round(worldPosition.z);
        short b1 = Server.world.getBlockID(x, y, z);
        short b2 = Server.world.getBlockID(x, y + 1, z);

        return b1 == (Blocks.BLOCK_MINECART_ROAD_BLOCK) || b1 == (Blocks.BLOCK_MINECART_ROAD_SLAB)
                || b2 == (Blocks.BLOCK_MINECART_ROAD_BLOCK) || b2 == (Blocks.BLOCK_MINECART_ROAD_SLAB);
    }

    private float rotationYDeg = 0;
    private boolean collisionEnabled = true;

    public boolean jumpWithSideCollision = false;
    public PositionHandler posHandler;
    ClientWindow window;
    UserControlledPlayer player;


    public Vehicle(ClientWindow window, long uniqueIdentifier) {
        super(uniqueIdentifier);
        this.window = window;
        this.player = GameScene.userPlayer;
    }

    /**
     * @return the collisionEnabled
     */
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    /**
     * @param collisionEnabled the collisionEnabled to setBlock
     */
    public void setCollisionEnabled(boolean collisionEnabled) {
        this.collisionEnabled = collisionEnabled;
    }

    public float getAngleToPlayer() {
        UserControlledPlayer userControlledPlayer = GameScene.userPlayer;
        return MathUtils.getAngleOfPoints(worldPosition.x, worldPosition.z,
                userControlledPlayer.worldPosition.x,
                userControlledPlayer.worldPosition.z);
    }

    public abstract void vehicle_draw();

    /**
     * @return the entity actually moved
     */
    public abstract boolean vehicle_move();

    private long lastJumpMS = 0;

    public void server_update() {
        aabb.update();
        if (vehicle_move()) {
            posHandler.update();
            if (
                    (Math.abs(posHandler.collisionHandler.collisionData.block_penPerAxes.x) > 0
                            || Math.abs(posHandler.collisionHandler.collisionData.block_penPerAxes.z) > 0)

                            && jumpWithSideCollision) {
                if (System.currentTimeMillis() - lastJumpMS > 200 && posHandler.onGround) {
                    posHandler.jump();
                    lastJumpMS = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public final void client_draw() {
        if (inFrustum) {
            vehicle_draw();
        }
    }

    public void entityMoveEvent() {
        markAsModifiedByUser();
        vehicle_entityMoveEvent();
    }

    public abstract void vehicle_entityMoveEvent();

    public UserControlledPlayer getPlayer() {
        return GameScene.userPlayer;
    }


    public void goForward(float amount) {
        Vector2f vec = MathUtils.getCircumferencePoint(-getRotationYDeg(), amount);
        worldPosition.add(vec.x, 0, vec.y);
        multiplayerProps.markStateChanged();
    }

    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        posHandler = new PositionHandler(window, Server.world, aabb, player.aabb);
    }


    public void serializeStateData(Output output, Kryo kryo) {
        kryo.writeObject(output, getRotationYDeg());
    }

    public void loadStateData(Input input, Kryo kryo) {
        rotationYDeg = kryo.readObject(input, Float.class);
    }

    public float getRotationYDeg() {
        return rotationYDeg;
    }

    public void setRotationYDeg(float rotationYDeg) {
        this.rotationYDeg = rotationYDeg;
        multiplayerProps.markStateChanged();
    }
}
