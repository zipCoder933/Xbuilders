/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.world.World;
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


    //static Constants
    public static final boolean DRAW_ENTITY_BOX = false;
    public static final boolean DRAW_COLLISION_CANDIDATES = false;
    public static final float BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS = 2;
    public static final float ENTITY_COLLISION_CANDIDATE_CHECK_RADIUS = 10;
    public static final float DEFAULT_GRAVITY = 0.42f;
    public static final float DEFAULT_TERMINAL_VELOCITY = 0.6f;
    public static final float MIN_JUMP_GRAVITY = DEFAULT_GRAVITY / 2;
    public static final float DEFAULT_COAST = 0.6f;

    //Variables
    protected final Vector3f velocity = new Vector3f();
    private boolean frozen = false;
    private boolean gravityEnabled;
    public boolean onGround;
    public final BaseWindow window;


    public float surfaceCoasting = 0.75f;
    public float surfaceFriction = 0f;


    public float gravity = DEFAULT_GRAVITY;
    public float terminalVelocity = DEFAULT_TERMINAL_VELOCITY;
    public boolean collisionsEnabled = true;
    public final Box renderedBox;
    public final EntityAABB aabb;
    public final CollisionHandler collisionHandler;
    public float stepHeight = 0.6f;
    float frameDeltaSec;

    public PositionHandler(BaseWindow window, World world,
                           EntityAABB thisAABB,
                           EntityAABB UserPlayerAABB,
                           List<Player> playerList) {

        this.window = window;
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

            //Set the coast and friction
            if (!onGround || !gravityEnabled || !collisionsEnabled || collisionHandler.floorBlock == BlockList.BLOCK_AIR) {
                surfaceCoasting = DEFAULT_COAST;
                surfaceFriction = 0;
            } else {
                surfaceCoasting = collisionHandler.floorBlock.surfaceCoast;
                surfaceFriction = collisionHandler.floorBlock.surfaceFriction;
            }

            if (surfaceFriction > 0) {//Apply friction
                velocity.x *= 1 - surfaceFriction;
                velocity.z *= 1 - surfaceFriction;
            }

            //Handle Y velocity
            if (collisionsEnabled && isGravityEnabled()) {
                double fallSpeed = (gravity * frameDeltaSec);
                //TODO: Cap the number of times we can update PositionHandler (10fps) (The movement is jittery when running against walls, if we try to limit that here)
                if (window.getMsPerFrame() < 10)
                    fallSpeed /= 4; //For some reason, we need to fall slower if the MPF is too low
                this.velocity.y += fallSpeed;
            } else {
                velocity.y *= 0.75f; //Vertical coasting
            }
            if (velocity.y > -0.00001f) {
                onGround = true;
            } else if (velocity.y > terminalVelocity * frameDeltaSec) {
                velocity.y = terminalVelocity * frameDeltaSec;
            }

            //Calculate new AABB
            aabb.box.setX(aabb.box.min.x + velocity.x);
            aabb.box.setY(aabb.box.min.y + velocity.y);
            aabb.box.setZ(aabb.box.min.z + velocity.z);

            //Apply coasting
            velocity.x *= surfaceCoasting;
            velocity.z *= surfaceCoasting;
        }
        if (collisionsEnabled) {
            collisionHandler.resolveCollisions(GameScene.projection, GameScene.view);
        }

        //calculate new world position
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

    public void addVelocity(float x, float y, float z) {
        this.velocity.add(x * frameDeltaSec, y * frameDeltaSec, z * frameDeltaSec);
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity.set(x, y, z);
    }

    public void setVelocity(float x, float z) {
        this.velocity.x = x;
        this.velocity.z = z;
    }
}
