//package com.xbuilders.game.items.entities.vehicle;
//
//import com.xbuilders.engine.items.EntityLink;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.player.UserControlledPlayer;
//import com.xbuilders.engine.rendering.entity.EntityMesh;
//import com.xbuilders.engine.utils.ResourceUtils;
//import com.xbuilders.engine.utils.math.MathUtils;
//import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
//import org.joml.Vector3f;
//import org.joml.Vector3i;
//
//import javax.imageio.ImageIO;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * @author zipCoder933
// */
//public abstract class MinecartEntityLink extends EntityLink {
//
//    public MinecartEntityLink(int id, String name, String textureFile) {
//        super(id, name);
//        setSupplier(() -> new Minecart());
//        this.textureFile = textureFile;
//    }
//
//    public String textureFile;
//    public EntityMesh texture;
//    public EntityMesh model;
//
//    public class Minecart extends Vehicle {
//
//        public Minecart() {
//            super(new Vector3f(1, 0.8f, 1), true);
//
//            setFrustumSphereRadius(1.5f);
//        }
//
//        @Override
//        public boolean move() {
//            int x = Math.round(worldPosition.x);
//            int y = Math.round(worldPosition.y);
//            int z = Math.round(worldPosition.z);
//
//            if (playerIsRidingThis()) {
//                if (onTrack) {
//                    moveWithTrack(new Vector3i(x, y, z));
//                } else {
//                    freeMove();
//                }
//                return true;
//            }
//            return get3DDistToPlayer() < getPointerHandler().getSettingsFile().playerRayMaxDistance;
//        }
//
//        @Override
//        public void onDestructionInitiated() {
//        }
//
//        @Override
//        public void onDestructionCancel() {
//        }
//
//        float rotationYCurve;
//
//        @Override
//        public void renderMob(PGraphics g) {
////            g.shape(model);
//            model.drawImpl(g);
//        }
//
//        @Override
//        public void draw(PGraphics g) {
//            rotationYCurve = (float) MathUtils.curve(rotationYCurve, rotationYDeg, 0.25f);
//            modelMatrix.translate(renderOffset.x, renderOffset.y, renderOffset.z);
//            modelMatrix.rotateY((float) (rotationYCurve * (Math.PI / 180)));
//            if (riseInertaState == -1) {
//                if (forwardBackDir == -1) {
//                    modelMatrix.rotateX(-0.4f);
//                } else {
//                    modelMatrix.rotateX(0.4f);
//                }
//            } else if (riseInertaState == 1) {
//                if (forwardBackDir == -1) {
//                    modelMatrix.rotateX(0.4f);
//                } else {
//                    modelMatrix.rotateX(-0.4f);
//                }
//            }
//            sendModelMatrixToShader();
//            renderMob(g);
//        }
//
//        private boolean alignToNearestTrack() {
//            Vector3i currentTrackPiece = MinecartUtils.getNearestTrackPiece(this);
//            if (currentTrackPiece != null) {
//                BlockData trackData = getPointerHandler().getWorld().getBlockData(currentTrackPiece.x, currentTrackPiece.y, currentTrackPiece.z);
//                BlockOrientation orientation = BlockOrientation.getBlockOrientation(trackData);
//                if (orientation.getXZ() == 0 || orientation.getXZ() == 2) {
//                    worldPosition.x = currentTrackPiece.x;
//                    this.rotationYDeg = (float) 0;
//                } else if (orientation.getXZ() == 1 || orientation.getXZ() == 3) {
//                    worldPosition.z = currentTrackPiece.z;
//                    this.rotationYDeg = (float) 90;
//                }
//                rotationYCurve = rotationYDeg;
//                return true;
//            }
//            return false;
//        }
//
//        @Override
//        public void onDestroyClickEvent() {
//            destroy(true);
//        }
//
//        private void freeMove() {
//            riseInertaState = 0;
//            posHandler.setGravityEnabled(true);
//            float rotateSpeed = 0;
//            float targetSpeed = 0;
//
//            if (isOnRoad()) {
//                rotateSpeed = 0.5f;
//                if (getPlayer().forwardKeyPressed()) {
//                    targetSpeed = 0.15f;
//                    rotateSpeed = 2.0f;
//                } else if (getPlayer().backKeyPressed()) {
//                    targetSpeed = -0.08f;
//                    rotateSpeed = 1.0f;
//                }
//                speedCurve = (float) MathUtils.curve(speedCurve, targetSpeed, 0.03f);
//            } else {
//                rotateSpeed = 1.5f;
//                if (getPlayer().forwardKeyPressed()) {
//                    targetSpeed = 0.015f;
//                } else if (getPlayer().backKeyPressed()) {
//                    targetSpeed = -0.01f;
//                }
//                speedCurve = (float) MathUtils.curve(speedCurve, targetSpeed, 0.2f);
//            }
//
//            if (getPlayer().leftKeyPressed()) {
//                float rotationY1 = rotationYDeg + rotateSpeed;
//                this.rotationYDeg = rotationY1;
//            } else if (getPlayer().rightKeyPressed()) {
//                float rotationY1 = rotationYDeg - rotateSpeed;
//                this.rotationYDeg = rotationY1;
//            }
//            rotationYDeg = normalizeRotation(rotationYDeg);
//            rotationYCurve = rotationYDeg;
//            goForward(speedCurve);
//        }
//
//        public float snapDegreeTo90(int degree) {
//            float roundedDegree = Math.round(degree / 90.0) * 90; // this will give you 90
//            // Return the snapped degree
//            return roundedDegree;
//        }
//
//        @Override
//        public boolean onClickEvent() {
//            UserControlledPlayer userControlledPlayer = getPointerHandler().getPlayer();
//            if (userControlledPlayer.positionLock == null) {
//                getPointerHandler().getPlayer().setPositionLock(new PositionLock(this, -0.7f));
//                forwardBackDir = 0;
//                MinecartUtils.resetKeyEvent();
//                onTrack = alignToNearestTrack();
//                if (onTrack) {
//                    getPointerHandler().getGame().alert("Press the forward and backward keys to toggle minecart direction.");
//                } else {
//                    getPointerHandler().getGame().alert("Use WASD or arrow keys to navigate on minecart roads");
//                }
//            }
//            return false;
//        }
//
//        float speedCurve;
//        int forwardBackDir = 0;
//        Vector3i lastTrack, rotPos;
//        private boolean rotationEnabled = false;
//        boolean onTrack = false;
//        int riseInertaState = 0;
//        long riseInertaChangeMS = 0;
//
//        /**
//         * @param position the rotated to set
//         */
//        private void disableRotation(Vector3i position) {
//            rotPos = position;
//            this.rotationEnabled = false;
//        }
//
//        public void enableRotation() {
//            this.rotationEnabled = true;
//        }
//
//        private void moveWithTrack(Vector3i position) {
//            this.forwardBackDir = MinecartUtils.assignForwardOrBackward(this, forwardBackDir);//0=stop,-1=back,1=go
//            float speed = forwardBackDir > 0 ? 0.15f : -0.15f;
//            posHandler.setGravityEnabled(true);
//            Block b = getPointerHandler().getWorld().getBlock(position);
//            Block bup = getPointerHandler().getWorld().getBlock(position.x, position.y - 1, position.z);
//            Block bdown = getPointerHandler().getWorld().getBlock(position.x, position.y + 1, position.z);
//
//            if (forwardBackDir == 0) {
//                if (b == GameItems.SWITCH_JUNCTION) {
//                    if (getPointerHandler().getPlayer().leftKeyPressed()) {
//                        if (MinecartUtils.switchJunctionKeyEvent) {
//                            float rotationY1 = rotationYDeg + 90;
//                            this.rotationYDeg = rotationY1;
//                            MinecartUtils.switchJunctionKeyEvent = false;
//                        }
//                    } else if (getPointerHandler().getPlayer().rightKeyPressed()) {
//                        if (MinecartUtils.switchJunctionKeyEvent) {
//                            float rotationY1 = rotationYDeg - 90;
//                            this.rotationYDeg = rotationY1;
//                            MinecartUtils.switchJunctionKeyEvent = false;
//                        }
//                    } else {
//                        MinecartUtils.switchJunctionKeyEvent = true;
//                    }
//                }
//            } else {
//                if (b == GameItems.SWITCH_JUNCTION) {
//                    stop(position);
//                    goForward(speed);
//                } else if (b == GameItems.STOP_TRACK) {
//                    stop(position);
//                    goForward(speed);
//                } else if (b == GameItems.CURVED_TRACK) {
//                    if (rotationEnabled) {
//                        worldPosition.x = position.x;
//                        worldPosition.z = position.z;
//
//                        if (MinecartUtils.leftCurvedPath(lastTrack, position)) {
//                            float rotationY1 = rotationYDeg + 90;
//                            this.rotationYDeg = rotationY1;
//                        } else {
//                            float rotationY1 = rotationYDeg - 90;
//                            this.rotationYDeg = rotationY1;
//                        }
//                        disableRotation(position);
//                    }
//                    goForward(speed);
//                } else if (b == GameItems.MERGE_TRACK) {
//                    if (rotationEnabled) {
//                        worldPosition.x = position.x;
//                        worldPosition.z = position.z;
//
//                        if (MinecartUtils.mergeTrackLeftCurvedPath(lastTrack, position)) {
//                            float rotationY1 = rotationYDeg + 90;
//                            this.rotationYDeg = rotationY1;
//                        } else {
//                            float rotationY1 = rotationYDeg - 90;
//                            this.rotationYDeg = rotationY1;
//                        }
//                        disableRotation(position);
//                    }
//                    goForward(speed);
//                } else if (b == GameItems.CROSS_TRACK) {
//                    enableRotation();
//                    goForward(speed);
//                } else {
//                    enableRotation();
//                    if (riseInertaState <= 0 && b == GameItems.RAISED_TRACK) {
//                        posHandler.setGravityEnabled(false);
//                        worldPosition.y -= 0.07f;
//                        goForward(0.07f * forwardBackDir);
//                        riseInertaState = -1;
//                        riseInertaChangeMS = System.currentTimeMillis();
//                    } else if (riseInertaState >= 0 && bdown == GameItems.RAISED_TRACK) {
//                        posHandler.setGravityEnabled(false);
//                        worldPosition.y += 0.08f;
//                        goForward(0.07f * forwardBackDir);
//                        riseInertaState = 1;
//                        riseInertaChangeMS = System.currentTimeMillis();
//                    } else {
//                        if (System.currentTimeMillis() - riseInertaChangeMS > 200) {
//                            riseInertaState = 0;
//                        }
//                        BlockOrientation orientation = BlockOrientation.getBlockOrientation(
//                                getPointerHandler().getWorld().getBlockData(position.x, position.y, position.z));
//                        if (orientation != null) {
//                            if (orientation.getXZ() == 0 || orientation.getXZ() == 2) {
//                                worldPosition.x = position.x;
//                            } else if (orientation.getXZ() == 1 || orientation.getXZ() == 3) {
//                                worldPosition.z = position.z;
//                            }
//                        }
//                        goForward(speed);
//
//                        if ((b.isSolid() || bdown.isSolid()) && MinecartUtils.getNearestTrackPiece(this) == null) {
//                            onTrack = false;
//                        }
//                    }
//                }
//
//                if (rotationEnabled == false && !position.equals(rotPos)) {
//                    rotPos = position;
//                    enableRotation();
//                }
//            }
//
//            if (lastTrack == null
//                    || position.x != lastTrack.x || position.z != lastTrack.z) {
//                lastTrack = position;
//            }
//        }
//
//        @Override
//        public void toBytes(XBFilterOutputStream fout) throws IOException {
//        }
//
//        @Override
//        public void initialize(byte[] bytes, boolean setByUser) {
//            if (model == null) {
//                try {
//                    texture = new PImage(ImageIO.read(ResourceUtils.resource("items\\entities\\minecart\\" + textureFile)));
//                    model = getPointerHandler().getApplet().loadShape(ResourceUtils.resourcePath("items\\entities\\minecart\\minecart.obj"));
//                    model.setTexture(texture);
//                } catch (IOException ex) {
//                    Logger.getLogger(BirchTrapdoorLink.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            if (setByUser) {
//                alignToNearestTrack();
//            }
//        }
//
//        private void stop(Vector3i position) {
//            if (rotationEnabled) {
//                forwardBackDir = 0;
//                disableRotation(position);
//            }
//        }
//
//    }
//}
