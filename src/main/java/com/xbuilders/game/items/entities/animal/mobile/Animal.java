/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.rendering.entity.EntityShader;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.window.BaseWindow;

import org.joml.Vector2f;

import java.io.IOException;
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


    public void setSize(float sizeX, float sizeY, float sizeZ, boolean alignToGround) {
        aabb.size.set(sizeX, sizeY, sizeZ);
        if (alignToGround) aabb.offset.set(-(aabb.size.x / 2), -aabb.size.y, -(aabb.size.z / 2));
        else aabb.offset.set(-(aabb.size.x / 2), -(aabb.size.y / 2), -(aabb.size.z / 2));
        aabb.update();
    }

    public void goForward(float amount) {
        if(freezeMode) return;
        Vector2f vec = TrigUtils.getCircumferencePoint(-yRotDegrees, amount);
        worldPosition.add(vec.x, 0, vec.y);
        if (goForwardCallback != null) goForwardCallback.accept(amount);
    }

    public Animal(BaseWindow window) {
        this.window = window;
        this.player = GameScene.player;
    }

    @Override
    public final void initialize(ArrayList<Byte> bytes)  {
        // box = new Box();
        // box.setColor(new Vector4f(1, 0, 1, 1));
        // box.setLineWidth(5);
        pos = new PositionHandler(GameScene.world, window, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);
    }
}
