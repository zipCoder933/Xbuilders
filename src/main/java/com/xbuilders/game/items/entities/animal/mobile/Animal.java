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
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class Animal extends Entity {


    public PositionHandler pos;
    public final BaseWindow window;
    public final Player player;
    public Consumer<Float> goForwardCallback;
    public boolean freezeMode = false;

    public float rotationYDeg;
    public final AnimalRandom random;

    public final byte[] stateToBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(ByteUtils.floatToBytes(rotationYDeg));
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
        rotationYDeg = ByteUtils.bytesToFloat(state, start);
        random.readState(state, start);
        animal_readState(state, start);
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
        random = new AnimalRandom();
        this.player = GameScene.player;
    }

    @Override
    public final void initializeOnDraw(byte[] bytes) {
        // box = new Box();
        // box.setColor(new Vector4f(1, 0, 1, 1));
        // box.setLineWidth(5);
        pos = new PositionHandler(window, GameScene.world, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);
        random.setSeed(getIdentifier());
    }
}
