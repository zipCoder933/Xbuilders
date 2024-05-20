package com.xbuilders.game.items.entities.animal;

import com.xbuilders.game.items.entities.animal.mobile.AnimalAction;
import com.xbuilders.game.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.render.MVP;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class QuadPedalEntity<T extends QuadPedalLandAnimalLink> extends LandAnimal {

    MVP mvp;
    final Matrix4f bodyMatrix = new Matrix4f();

    T link;

    public QuadPedalEntity(BaseWindow window) {
        super(window);
        setSize(1f, 1.5f, 1f, true);
//            freezeMode = true;
        frustumSphereRadius = 2;
    }


    public void animalInit(T link, ArrayList<Byte> bytes) {
        this.link = link;
        mvp = new MVP();
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

    @Override
    public void draw(Matrix4f projection, Matrix4f view) {
        if (inFrustum) {
            move();
            shader.bind();
            float rotationRadians = (float) Math.toRadians(yRotDegrees);
            bodyMatrix.identity().translate(worldPosition).rotateY(rotationRadians);

            mvp.update(projection, view, bodyMatrix);
            mvp.sendToShader(shader.getID(), shader.mvpUniform);

            if (currentAction.type == AnimalAction.ActionType.IDLE
                    && link.sitting != null
                    && currentAction.duration > 1000) {
                drawSitting();
            } else {
                drawBody();
                //Z is the directon of the horse
                link.legs.draw(projection, view, bodyMatrix, shader, legXSpacing, legYSpacing, legZSpacing, legMovement);
                link.legs.draw(projection, view, bodyMatrix, shader, legXSpacing, legYSpacing, -legZSpacing, legMovement);
            }


            pos.update(projection, view);
            if (Math.abs(pos.collisionHandler.collisionData.penPerAxes.x) > 0.02
                    || Math.abs(pos.collisionHandler.collisionData.penPerAxes.z) > 0.02) {
                if (System.currentTimeMillis() - lastJumpTime > 2000) {
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


    @Override
    public boolean run_ClickEvent() {
        if (link.rideable) {

        } else {
            if (currentAction.type == AnimalAction.ActionType.IDLE) {
                currentAction = new AnimalAction(AnimalAction.ActionType.OTHER, 10);
            } else currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 10 * 1000);
        }
        return false;
    }
}