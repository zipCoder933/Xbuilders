/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package com.xbuilders.game.items.entities.mobile;

import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.rendering.entity.EntityShader;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import com.xbuilders.engine.world.wcc.WCCf;
import com.xbuilders.game.Main;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.utils.obj.OBJ;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.texture.TextureUtils;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Animal extends Entity {

    public PositionHandler pos;
    public final BaseWindow window;
    public final Player player;
    public double yRotDegrees;
  public final  AnimalRandom random = new AnimalRandom();

    public void goForward(float amount) {
        Vector2f vec = TrigUtils.getCircumferencePoint(-yRotDegrees, amount);
        worldPosition.add(vec.x, 0, vec.y);
    }

    public Animal(BaseWindow window, Player player) {
        this.window = window;
        this.player = player;
    }

    @Override
    public final void initialize(ArrayList<Byte> bytes) {
        // box = new Box();
        // box.setColor(new Vector4f(1, 0, 1, 1));
        // box.setLineWidth(5);
        pos = new PositionHandler(GameScene.world, window, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);

        animalInit(bytes);
    }

    protected abstract void animalInit(ArrayList<Byte> bytes);

}
