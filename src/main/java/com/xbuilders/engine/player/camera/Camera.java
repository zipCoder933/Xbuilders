package com.xbuilders.engine.player.camera;

import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.player.raycasting.Ray;
import com.xbuilders.engine.player.raycasting.RayCasting;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.rendering.wireframeBox.Box;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.window.BaseWindow;

import java.awt.*;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class Camera {

    public final Vector2i simplifiedPanTilt;
    public final Vector3f right, look, target, position;
    public final Vector3f cursorRaycastLook = new Vector3f();
    public final Vector3f cameraRaycastLook = new Vector3f();
    public final Vector3f cameraForward = new Vector3f();
    public int cursorRayDist = 1000;//Max distance for front ray
    public final int maxCursorRayDist = 1000;//Max distance for front ray for cursor raycaster
    private float tilt, pan, normalizedPan;
    public final Ray cursorRay, cameraViewRay;
    private float thirdPersonDist = 0;
    public final static FrustumCullingTester frustum = new FrustumCullingTester();
    public static final double HALF_PI = Math.PI / 2;
    public static final double TWO_PI = Math.PI * 2;
    public final static Vector3f up = new Vector3f(0f, -1f, 0f);
    private final float sensitivity = 0.15f;
    private Point mouse;
    private final IntBuffer windowX, windowY;
    private Robot robot;
    private final UserControlledPlayer player;
    private final BaseWindow window;
    private final Matrix4f view, projection;
    private final World world;
    public boolean cursorRayHitAllBlocks = false;

    public boolean cursorRayHitTarget() {
        return cursorRayHitAllBlocks || cursorRay.hitTarget;
    }

    private void calculateCameraOrientation() {
        normalizedPan = (float) ((float) pan / MathUtils.TWO_PI);
        if (normalizedPan < 0) {
            normalizedPan += ((int) -normalizedPan) + 1;
        } else {
            normalizedPan -= (int) normalizedPan;
        }
        simplifiedPanTilt.x = Math.round(normalizedPan * 4);
        if (simplifiedPanTilt.x == 4) {
            simplifiedPanTilt.x = 0;
        }
        simplifiedPanTilt.y = Math.round(tilt);
        //Add 1 to simplifiedPanTilt.x but make it still wrap around to 0
        switch (simplifiedPanTilt.x) {
            case 0 -> simplifiedPanTilt.x = 1;
            case 1 -> simplifiedPanTilt.x = 2;
            case 2 -> simplifiedPanTilt.x = 3;
            default -> simplifiedPanTilt.x = 0;
        }
    }

    public BlockData simplifiedPanTiltAsBlockData(BlockData data) {
        if (data.size() != 2) {
            data.setSize(2);
        }
        data.set(0, (byte) simplifiedPanTilt.x);
        data.set(1, (byte) simplifiedPanTilt.y);
        return data;
    }

    /**
     * @return the thirdPersonDist
     */
    public float getThirdPersonDist() {
        return thirdPersonDist;
    }

    /**
     * @param thirdPersonDist the thirdPersonDist to set
     */
    public void setThirdPersonDist(float thirdPersonDist) {
        this.thirdPersonDist = MathUtils.clamp(thirdPersonDist, -20, 20);
    }

    public void cycleToNextView(int dist) {
        if (getThirdPersonDist() == 0) {
            setThirdPersonDist(dist);
        } else if (getThirdPersonDist() > 0) {
            setThirdPersonDist(-dist);
        } else {
            setThirdPersonDist(0);
        }
    }


    public Camera(UserControlledPlayer player,
                  BaseWindow window,
                  Matrix4f view, Matrix4f projection,
                  World world) {

        this.view = view;
        this.projection = projection;
        this.world = world;
        this.window = window;
        this.player = player;
        windowX = MemoryUtil.memAllocInt(1);
        windowY = MemoryUtil.memAllocInt(1);
        simplifiedPanTilt = new Vector2i();
        cursorRay = new Ray();
        cameraViewRay = new Ray();

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            ErrorHandler.handleFatalError(ex);
        }

        target = new Vector3f();
        position = new Vector3f();
        right = new Vector3f(1f, 0f, 0f);
        look = new Vector3f(0f, 0.5f, 1f);

        pan = 0;
        tilt = 0f;

        cursor = new Box();
        cursor.set(0, 0, 0, 1, 1, 1);
        cursor.setColor(new Vector4f(1, 1, 1, 1));
        // frustum.setCamInternals(game.cameraFOV, game.getCameraRatio(),
        // game.cameraNearDist, game.cameraFarDist);
    }

    public void hideMouse() {
        GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }

    public void showMouse() {
        GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    public void update(boolean holdMouse) {
        if (holdMouse) {

            hideMouse();
            mouse = MouseInfo.getPointerInfo().getLocation();
            window.getWindowPos(windowX, windowY);

            int x = windowX.get(0);
            int y = windowY.get(0);
            int w = window.getWidth();
            int h = window.getHeight();

            int middleX = w / 2 + x;
            int middleY = h / 2 + y;

            int deltaX = mouse.x - middleX;
            int deltaY = mouse.y - middleY;

            // The window worldPosition is a little off, could be being multiplied by some factor
            robot.mouseMove(middleX, middleY); // target mouse


            if (getThirdPersonDist() > 0) {
                pan += MathUtils.map(deltaX, 0, w, 0, TWO_PI) * sensitivity;
            } else {
                pan -= MathUtils.map(deltaX, 0, w, 0, TWO_PI) * sensitivity;
            }

            tilt += MathUtils.map(deltaY, 0, h, 0, Math.PI) * sensitivity;
            tilt = (float) MathUtils.clamp(tilt, -Math.PI / 2.01f, Math.PI / 2.01f);
            if (tilt == HALF_PI) {
                tilt += 0.001f;
            }
            calculateCameraOrientation();

            //Update the camera position
            if (getThirdPersonDist() == 0) {
                cameraForward.set(Math.cos(pan), 0, Math.sin(pan));
                right.set(Math.cos(pan - Math.PI / 2), 0, Math.sin(pan - Math.PI / 2));
            } else {
                cameraForward.set(-Math.cos(pan), 0, -Math.sin(pan));
                right.set(-Math.cos(pan - Math.PI / 2), 0, -Math.sin(pan - Math.PI / 2));
            }

            look.set(Math.cos(pan), Math.tan(tilt), Math.sin(pan));
            right.normalize();
            look.normalize();
            cameraForward.normalize();
            target.set(player.worldPosition);
            position.set(player.worldPosition);

            if (getThirdPersonDist() == 0) {
                target.add(look);
                cursorRaycastLook.set(look);
            } else {
                float thirdPersonDist2 = Math.abs(thirdPersonDist);
                if (getThirdPersonDist() > 0) {
                    cameraRaycastLook.set(look);
                } else {
                    cameraRaycastLook.set(0).sub(look);
                }
                RayCasting.traceSimpleRay(cameraViewRay, position, cameraRaycastLook, (int) thirdPersonDist2 + 1,
                        ((block, forbiddenBlock) -> {
                            Block block2 = ItemList.getBlock(block);
                            return block != BlockList.BLOCK_AIR.id &&
                                    block != forbiddenBlock
                                    && (block2.solid || block2.opaque);
                        })
                        , GameScene.world);
                look.mul(MathUtils.clamp(cameraViewRay.distanceTraveled, 2, thirdPersonDist2) - 1);


                if (getThirdPersonDist() > 0) {
                    position.add(look);
                    cursorRaycastLook.set(0).sub(look);
                } else {
                    position.sub(look);
                    cursorRaycastLook.set(0).add(look);
                }
            }

            if (cursorRayHitAllBlocks) cursorRayDist = MathUtils.clamp(cursorRayDist, 1, maxCursorRayDist);
            else cursorRayDist = maxCursorRayDist;
            RayCasting.traceComplexRay(cursorRay, position, cursorRaycastLook, cursorRayDist,
                    ((block, forbiddenBlock) -> {
                        if (cursorRayHitAllBlocks) {
                            return block != forbiddenBlock;
                        } else return block != BlockList.BLOCK_AIR.id &&
                                block != forbiddenBlock;
                    }),
                    ((entity) -> {
//                        if (player.positionLock != null) {
//                            return entity != player.positionLock.entity;
//                        }
                        return true;
                    }),
                    world);

            view.identity().lookAt(position, target, up);

        } else showMouse();

        //We must update the frustum AFTER we update the camera
        frustum.update(projection, view);
    }

    Box cursor;

    public void drawRay() {
        if (cursorRay.hitTarget || cursorRayHitAllBlocks) {
            if (cursorRay.entity != null) {
                cursor.set(cursorRay.entity.aabb.box);
                cursor.draw(projection, view);
            } else if (cursorRay.cursorBoxes == null) {
                cursor.set(
                        (int) cursorRay.getHitPosition().x,
                        (int) cursorRay.getHitPosition().y,
                        (int) cursorRay.getHitPosition().z,
                        1, 1, 1);
                cursor.draw(projection, view);
            } else {
                for (AABB box : cursorRay.cursorBoxes) {
                    cursor.set(box);
                    cursor.draw(projection, view);
                }
            }
        }
    }

    public String toString() {
        return "Camera: pan\\tilt:(" + MiscUtils.printVector(player.camera.simplifiedPanTilt) + "), thirdPersonDist:" + thirdPersonDist + " cursorHitAll:" + cursorRayHitAllBlocks;
    }
}
