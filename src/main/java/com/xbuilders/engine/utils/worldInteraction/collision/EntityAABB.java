/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.World;
import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public class EntityAABB {

    public EntityAABB() {
        box = new AABB();
        offset = new Vector3f();
        size = new Vector3f();
        worldPosition = new Vector3f();
    }

    /**
     * Clamps world position and updates aabb box
     */
    public void update() {
        //Clamp world position
        worldPosition.x = MathUtils.clamp(worldPosition.x, World.WORLD_SIZE_NEG_X, World.WORLD_SIZE_POS_X);
        if(worldPosition.y > World.WORLD_BOTTOM_Y) {
            worldPosition.y = World.WORLD_BOTTOM_Y;
        }
        worldPosition.z = MathUtils.clamp(worldPosition.z, World.WORLD_SIZE_NEG_Z, World.WORLD_SIZE_POS_Z);
        box.setPosAndSize(
                worldPosition.x + offset.x,
                worldPosition.y + offset.y,
                worldPosition.z + offset.z,
                size.x, size.y, size.z);
    }

    public final Vector3f worldPosition;
    public final AABB box;
    public final Vector3f offset;
    public final Vector3f size;
}
