/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.entities.vehicle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.content.vanilla.items.Blocks;
import org.joml.Vector2f;

import java.io.IOException;

public abstract class Vehicle extends Entity {

    public static final String JSON_TEXTURE = "v_t";

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
        short b1 = GameScene.world.getBlockID(x, y, z);
        short b2 = GameScene.world.getBlockID(x, y + 1, z);

        return b1 == (Blocks.BLOCK_MINECART_ROAD_BLOCK) || b1 == (Blocks.BLOCK_MINECART_ROAD_SLAB)
                || b2 == (Blocks.BLOCK_MINECART_ROAD_BLOCK) || b2 == (Blocks.BLOCK_MINECART_ROAD_SLAB);
    }

    private float rotationYDeg = 0;
    private boolean collisionEnabled = true;

    public boolean jumpWithSideCollision = false;
    public PositionHandler posHandler;
    MainWindow window;
    UserControlledPlayer player;


    public Vehicle(int id, MainWindow window, long uniqueIdentifier) {
        super(id, uniqueIdentifier);
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

    @Override
    public final void draw() {
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
        posHandler = new PositionHandler(window, GameScene.world, aabb, player.aabb);
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
