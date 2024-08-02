package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.PositionLock;
import com.xbuilders.game.items.entities.animal.mobile.AnimalAction;
import com.xbuilders.game.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.render.MVP;

public class QuadPedalEntity<T extends QuadPedalLandAnimalLink> extends LandAnimal {

    final MVP bodyMatrix = new MVP();

    T link;

    public QuadPedalEntity(BaseWindow window) {
        super(window);
        aabb.setOffsetAndSize(1f, 1.5f, 1f, true);
//            freezeMode = true;
        frustumSphereRadius = 2;
    }


    public void animalInit(T link, byte[] bytes) {
        this.link = link;
        goForwardCallback = amount -> {
            legMovement += amount;
        };
    }

    private long lastJumpTime = 0;
    private float legMovement = 0;
    public float SCALE = 0.6f;
    protected float legXSpacing = 0.32f * SCALE;
    protected float legZSpacing = 0.9f * SCALE;
    protected float legYSpacing = -1.3f * SCALE;
    public final PositionLock lock = new PositionLock(this, -1);


    @Override
    public void draw() {
        if (inFrustum) {


            if (playerIsRidingThis()) {
                float rotSpeed = 0.5f;
                if (GameScene.player.forwardKeyPressed()) {
                    goForward(0.2f);
                    rotSpeed = 3;
                    currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 1000);
                } else move();

                if (GameScene.player.leftKeyPressed()) {
                    rotationYDeg -= rotSpeed;
                } else if (GameScene.player.rightKeyPressed()) {
                    rotationYDeg += rotSpeed;
                }
            } else move();


            shader.bind();
            float rotationRadians = (float) Math.toRadians(rotationYDeg);
            bodyMatrix.identity().translate(worldPosition).rotateY(rotationRadians);

            bodyMatrix.update();
            bodyMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);

            if (currentAction.type == AnimalAction.ActionType.IDLE
                    && link.sitting != null
                    && currentAction.duration > 1000) {
                drawSitting();
            } else {
                drawBody();
                //Z is the directon of the horse
                link.legs.draw(bodyMatrix, shader, legXSpacing, legYSpacing, legZSpacing, legMovement);
                link.legs.draw(bodyMatrix, shader, legXSpacing, legYSpacing, -legZSpacing, legMovement);
            }


            pos.update();
            if ((Math.abs(pos.collisionHandler.collisionData.penPerAxes.x) > 0.02
                    || Math.abs(pos.collisionHandler.collisionData.penPerAxes.z) > 0.02)
                    && !pos.collisionHandler.collisionData.sideCollisionIsEntity) {
                if (System.currentTimeMillis() - lastJumpTime > 500) {
                    lastJumpTime = System.currentTimeMillis();
                    pos.jump();
                }
            }
        }
    }

    protected void drawBody() {
        link.body.draw(false);
    }

    protected void drawSitting() {
        link.sitting.draw(false);
    }

    /**
     * //        rotationAnimation = (float) MathUtils.linearTravel(rotationAnimation, target % MathUtils.TWO_PI,  0.5f);
     */


    @Override
    public boolean run_ClickEvent() {
        if (link.rideable) {
            GameScene.player.positionLock = lock;
        } else {
            if (currentAction.type == AnimalAction.ActionType.IDLE) {
                currentAction = new AnimalAction(AnimalAction.ActionType.OTHER, 10);
            } else {
                if (distToPlayer < 5) {
                    rotationYDeg = (float) Math.toDegrees(getDirectionToPlayer());
                }
                currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 10 * 1000);
            }
        }
        return true;
    }
}