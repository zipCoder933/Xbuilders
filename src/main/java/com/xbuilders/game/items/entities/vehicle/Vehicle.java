/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.vehicle;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.BaseWindow;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

public abstract class Vehicle extends Entity {

    /**
     * Resets the range of the degree back to 0-360
     */
    public float normalizeRotation(float rotationYDeg) {
        return rotationYDeg % 360;
    }

    /**
     * @return the distToPlayer
     */
    public float get3DDistToPlayer() {
        return distToPlayer;
    }

    public boolean isOnRoad() {
        int x = Math.round(worldPosition.x);
        int y = Math.round(worldPosition.y);
        int z = Math.round(worldPosition.z);
        Block b1 = GameScene.world.getBlock(x, y, z);
        Block b2 = GameScene.world.getBlock(x, y + 1, z);

        return b1.equals(MyGame.BLOCK_MINECART_ROAD_BLOCK) || b1.equals(MyGame.BLOCK_MINECART_ROAD_SLAB)
                || b2.equals(MyGame.BLOCK_MINECART_ROAD_BLOCK) || b2.equals(MyGame.BLOCK_MINECART_ROAD_SLAB);
    }

    public float rotationYDeg = 0;
    private boolean collisionEnabled = true;
    public Vector3f renderOffset;
    public boolean jumpWithSideCollision = false;
    public PositionHandler posHandler;
    BaseWindow window;
    UserControlledPlayer player;


    public Vehicle(BaseWindow window) {
        super();
        this.window = window;
        this.player = GameScene.player;
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
        UserControlledPlayer userControlledPlayer = GameScene.player;
        return TrigUtils.getAngleOfPoints(worldPosition.x, worldPosition.z,
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
    public void draw() {
        aabb.update();
        if (vehicle_move()) {
            posHandler.update(GameScene.projection, GameScene.view);
            if (posHandler.collisionHandler.collisionData.sideCollision
                    && !posHandler.collisionHandler.collisionData.sideCollisionIsEntity
                    && jumpWithSideCollision) {
                if (System.currentTimeMillis() - lastJumpMS > 200 && posHandler.onGround) {
                    posHandler.jump();
                    lastJumpMS = System.currentTimeMillis();
                }
            }
        }

        if (inFrustum) {
            mvp_modelMatrix.rotateY((float) (rotationYDeg * (Math.PI / 180)));
            mvp.update(mvp_modelMatrix);
            mvp.sendToShader(shader.getID(), shader.uniform_modelMatrix);
            vehicle_draw();
        }
    }

    public void entityMoveEvent() {
        markAsModifiedByUser();
        vehicle_entityMoveEvent();
    }

    public abstract void vehicle_entityMoveEvent();

    @Override
    public String toString() {
        return "mob \"" + this.link.name + "\" (hash=" + this.hashCode() + ")";
    }

    public UserControlledPlayer getPlayer() {
        return GameScene.player;
    }

    public void goForward(float amount, float rotationDegrees) {
        Vector2f vec = TrigUtils.getCircumferencePoint(-rotationDegrees, amount);
        worldPosition.add(vec.x, 0, vec.y);
    }

    public void goForward(float amount) {
        Vector2f vec = TrigUtils.getCircumferencePoint(-rotationYDeg, amount);
        worldPosition.add(vec.x, 0, vec.y);
    }

    @Override
    public final void initializeOnDraw(ArrayList<Byte> bytes) {
        // box = new Box();
        // box.setColor(new Vector4f(1, 0, 1, 1));
        // box.setLineWidth(5);
        posHandler = new PositionHandler(window, GameScene.world, aabb, player.aabb, GameScene.otherPlayers);
        vehicle_initializeOnDraw(bytes);
    }

    public abstract void vehicle_initializeOnDraw(ArrayList<Byte> bytes);

    public abstract void onDestructionInitiated();

    public abstract void onDestructionCancel();

}