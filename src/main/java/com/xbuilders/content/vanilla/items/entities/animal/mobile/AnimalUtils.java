package com.xbuilders.content.vanilla.items.entities.animal.mobile;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.entity.Entity;
import com.xbuilders.engine.utils.math.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class AnimalUtils {
    final static Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);

    private static float calculateYaw(Vector3f cameraPosition, Vector3f objectPosition) {
        // Calculate the direction from the object to the camera
        Vector3f direction = new Vector3f(cameraPosition).sub(objectPosition).normalize();
        // Calculate the yaw angle (rotation around the Y-axis)
        return (float) Math.atan2(direction.x, direction.z);
    }

    public static void rotateToFacePlayer(Matrix4f matrix) {
        Vector3f entityHeadPos = MatrixUtils.getPositionFromMatrix(matrix);
        Vector3f playerHeadPos = GameScene.player.camera.position;

        //If the head twists more than 1.7 radians, then don't rotate
        //if (Math.abs(calculateYaw(playerHeadPos, entityHeadPos)) < 1.7f) {
            MatrixUtils.removeRotation(matrix);
            MatrixUtils.rotateToFaceCamera(matrix,
                    playerHeadPos, entityHeadPos,
                    upVector);
        //}
    }

    public static boolean inWater(Entity entity) {
        if (GameScene.world.getBlock(
                (int) entity.worldPosition.x,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z
        ).isLiquid()
                || GameScene.world.getBlock(
                (int) entity.worldPosition.x - 1,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z
        ).isLiquid()
                || GameScene.world.getBlock(
                (int) entity.worldPosition.x + 1,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z
        ).isLiquid()
                || GameScene.world.getBlock(
                (int) entity.worldPosition.x,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z - 1
        ).isLiquid()) {
            return true;
        }
        return (GameScene.world.getBlock(
                (int) entity.worldPosition.x,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z + 1
        ).isLiquid());
    }
}
