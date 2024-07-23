/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.world.World;
import com.xbuilders.game.Main;
import com.xbuilders.window.BaseWindow;

import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author zipCoder933
 */
public class PositionHandler {

    public boolean isFrozen() {
        return frozen && collisionsEnabled;
    }

    /**
     * @param frozen the frozen to setPosAndSize
     */
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    /**
     * @return the gravityEnabled
     */
    public boolean isGravityEnabled() {
        return gravityEnabled;
    }

    /**
     * @param gravityEnabled the gravityEnabled to setPosAndSize
     */
    public void setGravityEnabled(boolean gravityEnabled) {
        this.gravityEnabled = gravityEnabled;
    }

    public void setFallMedium(float gravity, float terminalVelocity) {
        this.gravity = gravity;
        this.terminalVelocity = Math.min(terminalVelocity, DEFAULT_TERMINAL_VELOCITY);
    }

    public void resetFallMedium() {
        this.gravity = DEFAULT_GRAVITY;
        this.terminalVelocity = DEFAULT_TERMINAL_VELOCITY;
    }


    //Constants
    public final boolean DRAW_ENTITY_BOX = false;
    final static boolean DRAW_COLLISION_CANDIDATES = false;
    final static float BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS = 2;
    final static float ENTITY_COLLISION_CANDIDATE_CHECK_RADIUS = 10;
    public static final float DEFAULT_GRAVITY = 0.42f;
    public static final float DEFAULT_TERMINAL_VELOCITY = 0.6f;
    final float MIN_JUMP_GRAVITY = DEFAULT_GRAVITY / 2;

    //Variables
    public Vector3f velocity;
    private boolean frozen = false;
    private boolean gravityEnabled;
    public boolean onGround;
    public final BaseWindow window;
    public final float friction = 0.75f;
    public float gravity = DEFAULT_GRAVITY;
    public float terminalVelocity = DEFAULT_TERMINAL_VELOCITY;
    public boolean collisionsEnabled = true;
    public Box renderedBox;
    EntityAABB aabb;
    public CollisionHandler collisionHandler;
    public float stepHeight = 0.6f;

    long lastUpdate;
    float frameDeltaSec;

    public PositionHandler(BaseWindow window, World world,
                           EntityAABB thisAABB,
                           EntityAABB UserPlayerAABB,
                           List<Player> playerList) {

        this.window = window;
        this.velocity = new Vector3f(0f, 0f, 0f);
        frozen = false;
        gravityEnabled = true;
        renderedBox = new Box();

        this.aabb = thisAABB;

        collisionsEnabled = true;
        renderedBox.setLineWidth(15);

        collisionHandler = new CollisionHandler(world, this, thisAABB,
                UserPlayerAABB, playerList);
    }


    public void update() {
        frameDeltaSec = window.smoothFrameDeltaSec;

        aabb.update(); //Update the aabb first
        if (DRAW_ENTITY_BOX) {
            renderedBox.setLineWidth(2);
            renderedBox.setColor(new Vector4f(1, 0, 0, 1));
            renderedBox.set(aabb.box);
            renderedBox.draw(GameScene.projection, GameScene.view);
        }

        if (!isFrozen()) {
            velocity.x *= friction;
            velocity.z *= friction;

            if (collisionsEnabled && isGravityEnabled()) {
                double fallSpeed = (gravity * frameDeltaSec);

                //TODO: Cap the number of times we can update PositionHandler (10fps) (The movement is jittery when running against walls, if we try to limit that here)
                if (window.getMsPerFrame() < 10)
                    fallSpeed /= 4; //For some reason, we need to fall slower if the MPF is too low
                this.velocity.y += fallSpeed;
            } else {
                velocity.y *= friction;
            }

            if (velocity.y > -0.00001f) {
                onGround = true;
            } else if (velocity.y > terminalVelocity * frameDeltaSec) {
                velocity.y = terminalVelocity * frameDeltaSec;
            }


            aabb.box.setX(aabb.box.min.x + velocity.x);
            aabb.box.setY(aabb.box.min.y + velocity.y);
            aabb.box.setZ(aabb.box.min.z + velocity.z);
        }
        if (collisionsEnabled) {
            collisionHandler.resolveCollisions(GameScene.projection, GameScene.view);
        }
        aabb.worldPosition.x = aabb.box.min.x - aabb.offset.x;
        aabb.worldPosition.y = aabb.box.min.y - aabb.offset.y;
        aabb.worldPosition.z = aabb.box.min.z - aabb.offset.z;
        aabb.clamp(false);
    }


    public final void jump() {
        if (onGround && isGravityEnabled()) {
            double jumpHeight = (Math.max(MIN_JUMP_GRAVITY, gravity) * 18 * frameDeltaSec);
            this.velocity.y -= jumpHeight;
            onGround = false;
        }
    }

    public void addVelocity(int x, float y, int z) {
        this.velocity.add(x * frameDeltaSec, y * frameDeltaSec, z * frameDeltaSec);
    }
}
