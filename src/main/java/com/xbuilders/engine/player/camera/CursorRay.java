package com.xbuilders.engine.player.camera;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.player.raycasting.Ray;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.rendering.wireframeBox.Box;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CursorRay {

    public boolean hitTarget() {
        return cursorRayHitAllBlocks || cursorRay.hitTarget;
    }

    public Vector3i getHitPos() {
        return cursorRay.getHitPositionAsInt();
    }

    public Vector3i getHitPosPlusNormal() {
        return cursorRay.getHitPosPlusNormal();
    }

    public Vector3i getHitNormalAsInt() {
        return cursorRay.getHitNormalAsInt();
    }

    public Entity getEntity() {
        return cursorRay.entity;
    }

    public CursorRay(Camera camera) {
        this.camera = camera;
        cursorBox = new Box();
        cursorBox.setPosAndSize(0, 0, 0, 1, 1, 1);
        cursorBox.setColor(new Vector4f(0, 0, 0, 1));
        cursorBox.setLineWidth(2);
        cursorRay = new Ray();
    }

    public final Camera camera;
    public final Box cursorBox;
    protected final Ray cursorRay;
    public boolean cursorRayHitAllBlocks = false;


    //Boundary mode:
    private boolean useBoundary = false;
    private boolean boundary_setStartNode = false;
    public boolean boundary_useHitPos = false;
    private BiConsumer<AABB, Boolean> boundaryConsumer;
    private final Vector3i boundary_startNode = new Vector3i();
    private final Vector3i boundary_endNode = new Vector3i();
    private final AABB boundary_aabb = new AABB();

    public void enableBoundaryMode(BiConsumer<AABB, Boolean> createBoundaryConsumer) {
        useBoundary = true;
        boundary_setStartNode = false;
        this.boundaryConsumer = createBoundaryConsumer;
    }

    public void disableBoundaryMode() {
        useBoundary = false;
        boundaryConsumer = null;
    }

    public boolean createClickEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (useBoundary) {
                    boundaryClickEvent(true);
                    return false;
                }
                return true; //If we want to permit the click event to continue
            }
        }
        return false;
    }

    public boolean destroyClickEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (useBoundary) {
                    boundaryClickEvent(false);
                    return false;
                }
                return true; //If we want to permit the click event to continue
            }
        }
        return false;
    }

    private void boundaryClickEvent(boolean create) {
        if (!boundary_setStartNode) {
            setBoundaryNode(boundary_startNode);
            boundary_setStartNode = true;
        } else {
            if (boundaryConsumer != null) boundaryConsumer.accept(boundary_aabb, create);
            makeAABBFrom2Points(boundary_startNode, boundary_endNode, boundary_aabb);
            boundary_setStartNode = false;
        }
    }

    private void makeAABBFrom2Points(Vector3i start, Vector3i end, AABB aabb) {
        int minX = Math.min(start.x, end.x);
        int minY = Math.min(start.y, end.y);
        int minZ = Math.min(start.z, end.z);
        int maxX = Math.max(start.x, end.x);
        int maxY = Math.max(start.y, end.y);
        int maxZ = Math.max(start.z, end.z);
        aabb.setPosAndSize(
                minX,
                minY,
                minZ,
                (int) MathUtils.dist(minX, maxX + 1),
                (int) MathUtils.dist(minY, maxY + 1),
                (int) MathUtils.dist(minZ, maxZ + 1));
    }

    private void setBoundaryNode(Vector3i node) {
        if (boundary_useHitPos) node.set(getHitPos());
        else node.set(getHitPosPlusNormal());
    }


    public void drawRay() {
        if (cursorRay.hitTarget || cursorRayHitAllBlocks) {
            if (useBoundary) {
                if (!boundary_setStartNode) {
                    setBoundaryNode(boundary_startNode);
                    boundary_aabb.setPosAndSize(boundary_startNode.x, boundary_startNode.y, boundary_startNode.z, 1, 1, 1);
                } else {
                    setBoundaryNode(boundary_endNode);
                    makeAABBFrom2Points(boundary_startNode, boundary_endNode, boundary_aabb);
                }

                cursorBox.set(boundary_aabb);
                cursorBox.draw(camera.projection, camera.view);

            } else if (cursorRay.entity != null) {
                cursorBox.set(cursorRay.entity.aabb.box);
                cursorBox.draw(camera.projection, camera.view);
            } else if (cursorRay.cursorBoxes == null) {
                cursorBox.setPosAndSize(
                        (int) cursorRay.getHitPosition().x,
                        (int) cursorRay.getHitPosition().y,
                        (int) cursorRay.getHitPosition().z,
                        1, 1, 1);
                cursorBox.draw(camera.projection, camera.view);
            } else {
                for (AABB box : cursorRay.cursorBoxes) {
                    cursorBox.set(box);
                    cursorBox.draw(camera.projection, camera.view);
                }
            }
        }
    }

}
