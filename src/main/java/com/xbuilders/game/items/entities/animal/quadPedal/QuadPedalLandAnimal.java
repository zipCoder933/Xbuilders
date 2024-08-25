package com.xbuilders.game.items.entities.animal.quadPedal;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.PositionLock;
import com.xbuilders.game.items.entities.animal.mobile.AnimalAction;
import com.xbuilders.game.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.BaseWindow;

public class QuadPedalLandAnimal<T extends QuadPedalLandAnimalLink> extends LandAnimal {

    T link;

    public QuadPedalLandAnimal(BaseWindow window) {
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

    private float legMovement = 0;
    public float SCALE = 0.6f;
    protected float legXSpacing = 0.32f * SCALE;
    protected float legZSpacing = 0.9f * SCALE;
    protected float legYSpacing = -1.3f * SCALE;
    public final PositionLock lock = new PositionLock(this, -1);


    public void animal_move() {
        if (playerIsRidingThis()) {
            float rotSpeed = 0.5f;
            if (GameScene.player.forwardKeyPressed()) {
                goForward(0.2f, true);
                rotSpeed = 3;
                currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 1000);
            } else if (allowVoluntaryMovement()) super.animal_move();

            if (GameScene.player.leftKeyPressed()) {
                setRotationYDeg(getRotationYDeg() - rotSpeed);
            } else if (GameScene.player.rightKeyPressed()) {
                setRotationYDeg(getRotationYDeg() + rotSpeed);
            }
        } else if (allowVoluntaryMovement() && inFrustum) super.animal_move();
    }

    @Override
    public void animal_drawBody() {
        shader.bind();
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);

        if (currentAction.type == AnimalAction.ActionType.IDLE
                && link.sitting != null
                && currentAction.duration > 1000) {
            drawSitting();
        } else {
            drawBody();
            //Z is the directon of the horse
            link.legs.draw(modelMatrix, shader, legXSpacing, legYSpacing, legZSpacing, legMovement);
            link.legs.draw(modelMatrix, shader, legXSpacing, legYSpacing, -legZSpacing, legMovement);
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
                    setRotationYDeg((float) Math.toDegrees(getYDirectionToPlayer()));
                }
                currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 10 * 1000);
            }
        }
        multiplayerProps.markStateChanged();
        return true;
    }
}