/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.BaseWindow;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class Animal extends Entity {


    public PositionHandler pos;
    public final BaseWindow window;
    public final Player player;
    public double yRotDegrees;
    public final AnimalRandom random = new AnimalRandom();
    public Consumer<Float> goForwardCallback;
    public boolean freezeMode = false;


    public boolean playerHasAnimalFeed() {
        Item heldItem = Main.game.getSelectedItem();
        return heldItem != null && heldItem.equals(MyGame.TOOL_ANIMAL_FEED);
    }


    public void goForward(float amount) {
        amount *= window.smoothFrameDeltaSec * 50;
        if (freezeMode) return;
        Vector2f vec = TrigUtils.getCircumferencePoint(-yRotDegrees, amount);
        worldPosition.add(vec.x, 0, vec.y);
        if (goForwardCallback != null) goForwardCallback.accept(amount);
    }

    public Animal(BaseWindow window) {
        this.window = window;
        this.player = GameScene.player;
    }

    @Override
    public final void initializeOnDraw(ArrayList<Byte> bytes) {
        // box = new Box();
        // box.setColor(new Vector4f(1, 0, 1, 1));
        // box.setLineWidth(5);
        pos = new PositionHandler(window, GameScene.world, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);
    }
}
