/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.rendering.wireframeBox.Box;
import com.xbuilders.engine.world.World;
import com.xbuilders.window.BaseWindow;

import java.util.List;

import org.joml.Matrix4f;
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

    public Vector3f velocity;
    private boolean frozen = false;
    private boolean gravityEnabled;
    public boolean onGround;
    public final BaseWindow window;
    public final float friction = 0.75f;
    public boolean collisionsEnabled = true;
    public Box renderedBox;
    EntityAABB aabb;
    public CollisionHandler collisionHandler;
    public float stepHeight = 0.6f;

    //Constants
    public final boolean DRAW_ENTITY_BOX = false;
    final static boolean DRAW_COLLISION_CANDIDATES = false;
    final static float BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS = 2;
    final static float ENTITY_COLLISION_CANDIDATE_CHECK_RADIUS = 10;

    static final float GRAVITY = 0.0005f;


    public PositionHandler(World chunks, BaseWindow window,
                           EntityAABB thisAABB,
                           EntityAABB userControlledPlayerAABB,
                           List<Player> playerList) {

        this.window = window;
        this.velocity = new Vector3f(0f, 0f, 0f);
        frozen = false;
        gravityEnabled = true;
        renderedBox = new Box();

        this.aabb = thisAABB;

        collisionsEnabled = true;
        renderedBox.setLineWidth(15);

        collisionHandler = new CollisionHandler(chunks, this, thisAABB,
                userControlledPlayerAABB, playerList);
    }

    protected static Matrix4f sprojection, sview;

    public void update(Matrix4f projection, Matrix4f view) {
        if (sprojection == null) {
            sprojection = projection;
            sview = view;
        }
        aabb.update(); //Update the aabb first
        if (DRAW_ENTITY_BOX) {
            renderedBox.setLineWidth(2);
            renderedBox.setColor(new Vector4f(1, 0, 0, 1));
            renderedBox.set(aabb.box);
            renderedBox.draw(projection, view);
        }

        if (!isFrozen()) {
            velocity.x *= friction;
            velocity.z *= friction;
            if (collisionsEnabled && isGravityEnabled()) {
                this.velocity.sub(0, (float) (GRAVITY * window.getMsPerFrame()), 0);
            } else {
                velocity.y *= friction;
            }
            if (velocity.y < 0.00001f) {
                onGround = true;
            }


            aabb.box.setX(aabb.box.min.x + velocity.x);
            aabb.box.setY(aabb.box.min.y - velocity.y);
            aabb.box.setZ(aabb.box.min.z + velocity.z);
        }
        if (collisionsEnabled) {
            collisionHandler.resolveCollisions(projection, view);
        }
        aabb.worldPosition.x = aabb.box.min.x - aabb.offset.x;
        aabb.worldPosition.y = aabb.box.min.y - aabb.offset.y;
        aabb.worldPosition.z = aabb.box.min.z - aabb.offset.z;
    }


    public final void jump() {
        if (onGround && isGravityEnabled()) {
            this.velocity.y += GRAVITY * 20.0f * window.getMsPerFrame();
            onGround = false;
        }
    }

}
