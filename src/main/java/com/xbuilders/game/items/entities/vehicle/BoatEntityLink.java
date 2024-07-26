/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.vehicle;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.PositionLock;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import com.xbuilders.window.BaseWindow;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author zipCoder933
 */
public class BoatEntityLink extends EntityLink {

    public BoatEntityLink(BaseWindow window, int id, String name, String texturePath, String iconPath) {
        super(id, name);
        supplier = () -> new Boat(window);
        setIcon(iconPath);

        initializationCallback = (EntityLink) -> {
            if (model == null) {
                model = new EntityMesh();
                try {
                    model.loadFromOBJ(ResourceUtils.resource("items\\entity\\boat\\boat.obj"));
                    model.setTexture(ResourceUtils.resource("items\\entity\\boat\\textures\\" + texturePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

    }

    public EntityMesh model;

    class Boat extends Vehicle {

        public Boat(BaseWindow window) {
            super(window);
            frustumSphereRadius = (1.5f);
            aabb.setOffsetAndSize(1.5f, 1f, 1.5f, true);
        }

        public boolean isInWater() {
            int wx = (int) worldPosition.x;
            int wy = (int) worldPosition.y;
            int wz = (int) worldPosition.z;
            boolean belowBLockLiquid = GameScene.world.getBlock(wx, wy + 1, wz).isLiquid()
                    && worldPosition.y > wy + 0.85f;//We dont have to stand by strict block coordinates

            isInWater = GameScene.world.getBlock(wx, wy, wz).isLiquid()
                    || GameScene.world.getBlock(wx, wy - 1, wz).isLiquid()
                    || belowBLockLiquid;

            return isInWater;
        }

        boolean isInWater;

        @Override
        public void vehicle_draw() {
            modelMatrix.rotateY((float) (rotationYDeg * (Math.PI / 180)));
            modelMatrix.update();
            modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);

            model.draw(false);
        }

        boolean rise;

        @Override
        public boolean vehicle_move() {
            int wx = (int) worldPosition.x;
            int wy = (int) worldPosition.y;
            int wz = (int) worldPosition.z;
            isInWater();

            if (playerIsRidingThis()) {
                if (isInWater) {
                    if (GameScene.world.getBlock(wx, wy - 1, wz).isLiquid()) {
                        rise = true;
                    }
                } else {
                    worldPosition.y -= 0.03;
                    rise = false;
                }

                float rotateSpeed = 0.5f;
                float targetSpeed = 0;
                if (getPlayer().forwardKeyPressed()) {
                    if (isInWater) {
                        targetSpeed = 0.15f;
                        rotateSpeed = 2.0f;
                    } else {
                        rotateSpeed = 0.5f;
                        targetSpeed = 0.03f;
                        speedCurve = targetSpeed;
                    }
                } else if (getPlayer().backwardKeyPressed()) {
                    if (isInWater) {
                        targetSpeed = -0.08f;
                        rotateSpeed = 1.0f;
                    } else {
                        rotateSpeed = 0.5f;
                        targetSpeed = -0.01f;
                        speedCurve = targetSpeed;
                    }
                }

                if (rise) {
                    worldPosition.y -= 0.01;
                }

                if (getPlayer().leftKeyPressed()) {
                    float rotationY1 = rotationYDeg + rotateSpeed;
                    this.rotationYDeg = rotationY1;
                } else if (getPlayer().rightKeyPressed()) {
                    float rotationY1 = rotationYDeg - rotateSpeed;
                    this.rotationYDeg = rotationY1;
                }
                speedCurve = (float) MathUtils.curve(speedCurve, targetSpeed, 0.03f);
                goForward(speedCurve);
            } else if (isInWater) {
                speedCurve = (float) MathUtils.curve(speedCurve, 0, 0.03f);
                goForward(speedCurve / 2);
            }
            return get3DDistToPlayer() < 50;
        }

        @Override
        public void vehicle_entityMoveEvent() {
            posHandler.setGravityEnabled(isInWater() ? false : true);
        }


        @Override
        public void vehicle_initializeOnDraw(byte[] bytes) {
            posHandler.setGravityEnabled(isInWater() ? false : true);
        }


        float speedCurve;

        @Override
        public void onDestructionInitiated() {
        }

        @Override
        public void onDestructionCancel() {
        }

        @Override
        public boolean run_ClickEvent() {
            GameScene.player.positionLock = new PositionLock(this, 0);
            return true;
        }


        public void toBytes(XBFilterOutputStream fout) throws IOException {
        }

    }
}
