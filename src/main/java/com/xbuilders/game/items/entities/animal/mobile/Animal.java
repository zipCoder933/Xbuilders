/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.game.MyGame;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class Animal extends Entity {

    private static final float ONE_SIXTEENTH = (float) 1 / 16;
    public Limb[] limbs;
    public final PositionHandler pos;
    public final MainWindow window;
    public final Player player;
    public Consumer<Float> goForwardCallback;
    public boolean freezeMode = false;

    public boolean allowVoluntaryMovement() {
        return !multiplayerProps.controlledByAnotherPlayer;
    }

    private float rotationYDeg;
    public final AnimalRandom random;


    public boolean isPendingDestruction() {
        return false;
    }

    public void tameAnimal() {

    }

    public void facePlayer() {
        setRotationYDeg((float) Math.toDegrees(getYDirectionToPlayer()));
    }

    public boolean isTamed() {
        return false;
    }

    public final byte[] stateToBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(ByteUtils.floatToBytes(getRotationYDeg()));
            random.writeState(baos);
            animal_writeState(baos);
            baos.close();//releases the baos to prevent memory leaks and promote efficiency
            return baos.toByteArray();  //toByteArray() already calls flush()
        } catch (IOException e) {
        }
        return null;
    }

    public void animal_writeState(ByteArrayOutputStream baos) throws IOException {
    }

    public void animal_readState(byte[] state, AtomicInteger start) {
    }

    public void loadState(byte[] state) {
        AtomicInteger start = new AtomicInteger(0);
        rotationYDeg = (ByteUtils.bytesToFloat(state, start));
        random.readState(state, start);
        animal_readState(state, start);
    }


    public boolean playerHasAnimalFeed() {
        Item heldItem = MainWindow.game.getSelectedItem();
        return heldItem != null && heldItem.equals(MyGame.TOOL_ANIMAL_FEED);
    }

    public void eatAnimalFeed() {
    }

    public Animal(MainWindow window) {
        this.window = window;
        random = new AnimalRandom();
        this.player = GameScene.player;
        this.pos = new PositionHandler(window, GameScene.world, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);
        random.setSeed(getIdentifier());
    }

    public abstract void animal_move();

    public abstract void animal_drawBody();


    public final void draw() {
        if (allowVoluntaryMovement() && !freezeMode) animal_move();
        if (inFrustum || playerIsRidingThis()) {
            //Model matrix is our parent (body) matrix
            modelMatrix.rotateY((float) Math.toRadians(getRotationYDeg()));
            animal_drawBody();

            //Draw our limbs
            if (limbs != null) for (int i = 0; i < limbs.length; i++) limbs[i].inner_draw_limb(modelMatrix);
        }
        if (updatePosHandler || inFrustum || playerIsRidingThis()) {
            updatePosHandler = false;
            pos.update(); //Update if 1) it's been moved 2) we're in frustum 3) we're the player's animal
        }
    }

    private boolean updatePosHandler = false;

    public final void entityMoveEvent() {//Called after update position
        updatePosHandler = true;
    }

    @Override
    public final void initializeOnDraw(byte[] bytes) {

    }

    public float getRotationYDeg() {
        return rotationYDeg;
    }

    public void setRotationYDeg(float rotationYDeg) {
        multiplayerProps.markStateChanged();
        this.rotationYDeg = rotationYDeg;
    }


    /**
     * @return the angle in radians
     */
    public float getYDirectionToPlayer() {
        return (float) (-MathUtils.calcRotationAngle(
                worldPosition.x, worldPosition.z,
                GameScene.player.worldPosition.x, GameScene.player.worldPosition.z)
                + MathUtils.HALF_PI);
    }


    public void goForward(float amount, boolean jump) {
        amount *= window.smoothFrameDeltaSec * 50;
        if (freezeMode) return;
        Vector2f vec = TrigUtils.getCircumferencePoint(-getRotationYDeg(), amount);
        worldPosition.add(vec.x, 0, vec.y);
        if (goForwardCallback != null) goForwardCallback.accept(amount);
        if (jump) jumpIfColliding(400, false);
        multiplayerProps.markStateChanged();
    }

    private long lastJumpTime;

    private void jumpIfColliding(int interval /*ms*/, boolean jumpOverEntities) {
        Vector3f pen = pos.collisionHandler.collisionData.block_penPerAxes;
        if (jumpOverEntities) pen = pos.collisionHandler.collisionData.totalPenPerAxes;

        if (Math.abs(pen.x) > 0.02
                || Math.abs(pen.z) > 0.02) {
            if (System.currentTimeMillis() - lastJumpTime > interval) {
                lastJumpTime = System.currentTimeMillis();
                pos.jump();
            }
        }
    }
}
