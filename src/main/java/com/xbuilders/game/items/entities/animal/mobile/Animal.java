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
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.BaseWindow;

import org.joml.Vector2f;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

public abstract class Animal extends Entity {


    public PositionHandler pos;
    public final BaseWindow window;
    public final Player player;
    public Consumer<Float> goForwardCallback;
    public boolean freezeMode = false;

    public float rotationYDeg;
    public final AnimalRandom random;

    public byte[] stateToBytes() {
        byte[] rotationBytes = ByteUtils.floatToBytes(rotationYDeg);
        byte[] seedBytes = ByteUtils.intToBytes(random.getSeed());
        return new byte[]{
                rotationBytes[0], rotationBytes[1], rotationBytes[2], rotationBytes[3],
                seedBytes[0], seedBytes[1], seedBytes[2], seedBytes[3]};
    }

    public void loadState(byte[] state) {
        if (state.length != 8) return;
        rotationYDeg = ByteUtils.bytesToFloat(state[0], state[1], state[2], state[3]);

        int newSeed = ByteUtils.bytesToInt(state[4], state[5], state[6], state[7]);
        if(random.getSeed() != newSeed) random.setSeed(newSeed);
    }

    float lastRotation = 0;
    public boolean hasStateChanged() {
        if(rotationYDeg != lastRotation) {
            lastRotation = rotationYDeg;
            return true;
        } else {
            return false;
        }
    }


    public boolean playerHasAnimalFeed() {
        Item heldItem = Main.game.getSelectedItem();
        return heldItem != null && heldItem.equals(MyGame.TOOL_ANIMAL_FEED);
    }


    public void goForward(float amount) {
        amount *= window.smoothFrameDeltaSec * 50;
        if (freezeMode) return;
        Vector2f vec = TrigUtils.getCircumferencePoint(-rotationYDeg, amount);
        worldPosition.add(vec.x, 0, vec.y);
        if (goForwardCallback != null) goForwardCallback.accept(amount);
    }

    public Animal(BaseWindow window) {
        this.window = window;
        random = new AnimalRandom((int) (Math.random() * Integer.MAX_VALUE));
        this.player = GameScene.player;
    }

    @Override
    public final void initializeOnDraw(byte[] bytes) {
        // box = new Box();
        // box.setColor(new Vector4f(1, 0, 1, 1));
        // box.setLineWidth(5);
        pos = new PositionHandler(window, GameScene.world, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);
    }
}
