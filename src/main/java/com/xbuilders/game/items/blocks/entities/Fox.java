/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.entities;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.rendering.entity.EntityShader;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.game.items.blocks.entities.mobile.AnimalAction;
import com.xbuilders.game.items.blocks.entities.mobile.AnimalRandom;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.utils.obj.OBJ;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * @author zipCoder933
 */
public class Fox extends Entity {

    //    Box box;
    PositionHandler pos;
    BaseWindow window;
    Player player;
    static EntityMesh body;
    static EntityShader bodyShader;
    MVP mvp;
    Matrix4f bodyMatrix;
    static int texture;
    float activity = 0.5f;

    public void setActivity(float activity) {
        this.activity = MathUtils.clamp(activity, 0, 1);
    }

    public Fox(BaseWindow window, Player player) {
        aabb.size.set(0.5f, 0.8f, 0.5f);
        aabb.offset.set(-(aabb.size.x / 2), 0, -(aabb.size.z / 2));
        aabb.update();
        this.window = window;
        this.player = player;
        bodyMatrix = new Matrix4f();
        frustumSphereRadius = 2;
    }

    int time = 0;
    private float maxSpeed = 0.17f;

    @Override
    public void initialize(ArrayList<Byte> bytes) {
//        box = new Box();
//        box.setColor(new Vector4f(1, 0, 1, 1));
//        box.setLineWidth(5);
        pos = new PositionHandler(GameScene.world, window, aabb, player.aabb, GameScene.otherPlayers);
        pos.setGravityEnabled(true);

        mvp = new MVP();

        /**
         * We only need 1 model. We can reuse the same model for each entity to
         * save vram!
         */
        if (body == null) {
            try {
                bodyShader = new EntityShader();
                body = new EntityMesh();

                OBJ loadModel = OBJLoader.loadModel(ResourceUtils.resource("items\\entity\\animal\\fox\\body.obj"));
                texture = TextureUtils.loadTexture(
                        ResourceUtils.RESOURCE_DIR.getAbsolutePath() + "\\items\\entity\\animal\\fox\\red.png", false).id;
                body.loadFromOBJ(loadModel);
                body.setTextureID(texture);
            } catch (IOException ex) {
                ErrorHandler.handleFatalError(ex);
            }
        }
    }

    Vector2f direction = new Vector2f();
    double yRotDegrees;
    AnimalRandom random = new AnimalRandom();
    AnimalAction action = null;

    public void move() {
        time += 1;
        if (action == null || action.pastDuration()) {
            if (action == null) {
                action = newRandomAction(null);
            } else {
                action = newRandomAction(action.type);
            }
        }

        if (null != action.type) {
            switch (action.type) {
                case TURN:
                    yRotDegrees = (yRotDegrees + action.velocity);
                    break;
                case WALK:
                    goForward(action.velocity);
                    break;
                default:
            }
        }
    }

    public AnimalAction newRandomAction(Enum lastAction) {
        AnimalAction.ActionType actionType = AnimalAction.getRandomActionType(random.getRandom(),
                AnimalAction.ActionType.IDLE, AnimalAction.ActionType.WALK, AnimalAction.ActionType.TURN);
        AnimalAction action = new AnimalAction(actionType);

        if (lastAction != null) {
            AnimalAction.ActionType lastAction2 = (AnimalAction.ActionType) lastAction;
            if (actionType == lastAction2) {
                switch (lastAction2) {
                    case IDLE:
                        actionType = AnimalAction.ActionType.TURN;
                        break;
                    case TURN:
                        actionType = AnimalAction.ActionType.WALK;
                        break;
                    case FOLLOW:
                        actionType = AnimalAction.ActionType.TURN;
                        break;
                    default:
                        break;
                }
            }
        }

        long actionDuration = 0;

        /*if (distToPlayer < 5 && playerHasAnimalFeed()) {
            actionType = AnimalAction.ActionType.FOLLOW;
            tameAnimal();
            actionDuration = getRandom().nextLong(4000, 25000);
            actionVelocity = getMaxSpeed() / 2;
        } else*/
        if (actionType == AnimalAction.ActionType.TURN) {
            actionDuration = 50 + (random.nextInt(100));
            float rotationAction = MathUtils.clamp(activity, 0.2f, 1);
            action.velocity = random.noise(10) > 0 ? rotationAction * 10 : rotationAction * -10;
            if (!inFrustum) {
                action.velocity *= 1.5;
            }
        } else if (actionType == AnimalAction.ActionType.WALK) {
            actionDuration = 600 + (random.nextInt(500) - 250);
            action.velocity = random.nextFloat(0.01f, maxSpeed);

            if (activity < 0.66 && random.nextBoolean()) {
                action.velocity /= 3;
            }
            if (!inFrustum) {
                action.velocity *= 1.5;
            }
        } else {
            if (activity > 0.8) {
                actionDuration = random.nextInt(-40, 200);
                if (actionDuration < 0) {
                    actionDuration = 0;
                }
            } else {
                actionDuration = random.nextInt(50, 400);
            }
            actionDuration *= MathUtils.map(activity, 0, 1, 5, 1);
            if (random.noise(10) > MathUtils.map(activity, 0, 1, -0.2f, 0.3f)) {
                actionDuration *= MathUtils.map(activity, 0, 1, 10, 3);
            }
        }
        action.duration = actionDuration;
        return action;
    }


    public void goForward(float amount) {
        Vector2f vec = TrigUtils.getCircumferencePoint(-yRotDegrees, amount);
        worldPosition.add(vec.x, 0, vec.y);
    }

    @Override
    public void draw(Matrix4f projection, Matrix4f view) {
        if (inFrustum) {
            move();


//        box.setToAABB(projection, view, aabb.box);
//        box.draw();
            bodyShader.bind();
            float rotationRadians = (float) Math.toRadians(yRotDegrees);
            bodyMatrix.identity().translate(worldPosition).rotateY(rotationRadians);

            //This just has to happen because the OBJ model is upside down
            bodyMatrix.rotateZ((float) Math.PI).translate(0,-0.8f,0);//.rotateLocalY(rotationRadians);

            mvp.update(projection, view, bodyMatrix);
            mvp.sendToShader(bodyShader.getID(), bodyShader.mvpUniform);
            body.draw(false);

            pos.update(projection, view);
        }
    }


}
