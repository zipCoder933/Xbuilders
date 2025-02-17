package com.xbuilders.content.vanilla.items.entities.animal.fish;

import com.fasterxml.jackson.core.JsonGenerator;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.entity.EntitySupplier;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.content.vanilla.items.Blocks;
import com.xbuilders.content.vanilla.items.entities.animal.mobile.Animal;
import org.joml.Vector2f;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public abstract class FishAnimal<ActionEnum> extends Animal {

    public FishAnimal( long uniqueIdentifier, ClientWindow window) {
        super( uniqueIdentifier, window);
        pos.aabb.setOffsetAndSize(.5f, .5f, .5f, false);
        lastInWater = System.currentTimeMillis();
        inWater = inWater();
    }

    public int textureIndex;

    @Override
    public void serializeDefinitionData(JsonGenerator generator) throws IOException {
        super.serializeDefinitionData(generator);
        generator.writeNumberField(JSON_SPECIES, textureIndex);
    }

    @Override
    public void initSupplier(EntitySupplier entitySupplier) {
        super.initSupplier(entitySupplier);
        entitySupplier.spawnCondition = (x, y, z) -> {
            if (Server.world.getBlockID(x, y, z) == Blocks.BLOCK_WATER) return true;
            return false;
        };
        entitySupplier.isAutonomous = true;
    }

    public abstract void renderFish();


    /**
     * @return the maxSpeed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * @param maxSpeed the maxSpeed to set
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    private float maxSpeed = 0.17f;
    private float rotationVelocity;

    private long lastActionChange;
    private long lastInWater;

    float forwardVelocity = 0;
    boolean inWater = false;
    float yVelocity;
    long actionDuration;

    public boolean inWater() {
        inWater = Server.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y),
                (int) Math.floor(worldPosition.z)).isLiquid();
        if (inWater) lastInWater = System.currentTimeMillis();
        return inWater;
    }

    @Override
    public void animal_move() {
        if (ClientWindow.frameCount % 5 == 0) {
            inWater = inWater();
        }

        if (System.currentTimeMillis() - lastActionChange > actionDuration) { //Change Action
            rotationVelocity = random.noise(2) * 2;
            forwardVelocity = (float) MathUtils.mapAndClamp(random.noise(4), -0.5f, 1, 0.01f, getMaxSpeed());
            yVelocity = random.noise(1, -0.06f, 0.05f);
            actionDuration = random.nextLong(50, 2000);
            lastActionChange = System.currentTimeMillis();
        }

        if (inWater) { //In Water
            pos.setGravityEnabled(false);
            if (isPendingDestruction()) {
                setRotationYDeg(getRotationYDeg() + rotationVelocity / 3);
            } else if (distToPlayer < 10 && playerHasAnimalFeed()) {
                float playerY = GameScene.userPlayer.worldPosition.y;
                float playerPos = playerY + random.noise(1, -2, 2);
                worldPosition.y = (float) MathUtils.curve(worldPosition.y, playerPos, 0.05f);
                facePlayer();
                if (distToPlayer < 3) {
                    tamed = true;
                    eatAnimalFeed();
                }
            } else {
                setRotationYDeg(getRotationYDeg() + rotationVelocity);
            }
            worldPosition.y += yVelocity;

            if ( //Rotate the fish if we hit something
                    Math.abs(pos.collisionHandler.collisionData.block_penPerAxes.x) > 0.02
                            || Math.abs(pos.collisionHandler.collisionData.block_penPerAxes.z) > 0.02) {
                setRotationYDeg(getRotationYDeg() + rotationVelocity * 20);
            }

        } else { //Out of water
            yVelocity = 0.02f;
            worldPosition.y += yVelocity;
            pos.setGravityEnabled(true);
            forwardVelocity = 0.01f;
            setRotationYDeg(getRotationYDeg() + random.noise(20) * 30);

            if (System.currentTimeMillis() - lastJumpMS > 300) {
                pos.jump();
                lastJumpMS = System.currentTimeMillis();
            }
        }


        if (isPendingDestruction() && inWater) {
            Vector2f vec = MathUtils.getCircumferencePoint(-getRotationYDeg(), maxSpeed * 0.7f);
            worldPosition.add(vec.x, 0, vec.y);
        } else {
            Vector2f vec = MathUtils.getCircumferencePoint(-getRotationYDeg(), forwardVelocity);
            worldPosition.add(vec.x, 0, vec.y);
        }

        if (System.currentTimeMillis() - lastInWater > 10000) {
            destroy();
        }

        pos.update();
    }


    public boolean playerIsInSameMediumAsFish() {
        return inWater == GameScene.userPlayer.getBlockAtCameraPos().isLiquid();
    }

    @Override
    public final void animal_drawBody() {
        if (inFrustum) {
            if (inWater) { //Wiggle if we are in water
                float waggle = (float) (Math.sin(ClientWindow.frameCount / 2) * MathUtils.mapAndClamp(forwardVelocity, 0, maxSpeed, 0, 0.25f));
                modelMatrix.rotateY(waggle).translate(waggle * -0.1f, 0, 0);
            }
            modelMatrix.updateAndSendToShader(shader.getID(), shader.uniform_modelMatrix);
            renderFish();
        }
    }

    long lastJumpMS;

}