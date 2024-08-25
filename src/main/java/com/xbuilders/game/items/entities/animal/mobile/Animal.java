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
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.BaseWindow;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class Animal extends Entity {

    private Limb limbs;
    public PositionHandler pos;
    public final BaseWindow window;
    public final Player player;
    public Consumer<Float> goForwardCallback;
    public boolean freezeMode = false;

    public Limb[] getLimbs() {
        return limbs.limbs;
    }

    public void drawLimbs(Matrix4f startingMatrix) {
        limbs.draw(startingMatrix);
    }

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
        Item heldItem = Main.game.getSelectedItem();
        return heldItem != null && heldItem.equals(MyGame.TOOL_ANIMAL_FEED);
    }

    public void eatAnimalFeed() {
    }

    public Animal(BaseWindow window) {
        this.window = window;
        random = new AnimalRandom();
        this.player = GameScene.player;
        limbs = new Limb(Entity.shader, modelMatrix);
    }

    //TODO: Implement these methods and make animal creation as simple as possible
//    public abstract void move();
//    public abstract void animal_draw();
//
//    public final void draw() {
//        if(move()) pos.update();
//        if (inFrustum || playerIsRidingThis()) animal_draw();
//    }

    @Override
    public final void initializeOnDraw(byte[] bytes) {
        pos = new PositionHandler(window, GameScene.world, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);
        random.setSeed(getIdentifier());
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
                GameScene.player.worldPosition.x,
                GameScene.player.worldPosition.z) + MathUtils.HALF_PI);
    }

    public void goForward(float amount) {
        amount *= window.smoothFrameDeltaSec * 50;
        if (freezeMode) return;
        Vector2f vec = TrigUtils.getCircumferencePoint(-getRotationYDeg(), amount);
        worldPosition.add(vec.x, 0, vec.y);
        if (goForwardCallback != null) goForwardCallback.accept(amount);
        multiplayerProps.markStateChanged();
    }
}
