///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.entities.vehicle;
//
//import com.xbuilders.engine.gameScene.GameScene;
//import com.xbuilders.engine.items.EntityLink;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.player.PositionLock;
//import com.xbuilders.engine.player.UserControlledPlayer;
//import com.xbuilders.engine.rendering.entity.EntityMesh;
//import com.xbuilders.engine.utils.ResourceUtils;
//import com.xbuilders.engine.utils.math.MathUtils;
//import com.xbuilders.engine.world.chunk.BlockData;
//import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
//import com.xbuilders.game.Main;
//import com.xbuilders.game.MyGame;
//import org.joml.Vector3f;
//import org.joml.Vector3i;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * @author zipCoder933
// */
//public abstract class MinecartEntityLink extends EntityLink {
//
//    public MinecartEntityLink(int id, String name, String textureFile) {
//        super(id, name);
//        supplier = (() -> new Minecart());
//        initializationCallback = (entity) -> {
//            if (model == null) {
//                model = new EntityMesh();
//                try {
//                    model.loadFromOBJ(ResourceUtils.resource("items\\entities\\minecart\\minecart.obj"));
//                    model.setTexture(ResourceUtils.resource("items\\entities\\minecart\\" + textureFile));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };
//    }
//
//    public EntityMesh model;
//
//    public class Minecart extends Vehicle {
//
//        public Minecart() {
//            super(new Vector3f(1, 0.8f, 1), true);
//
//            frustumSphereRadius = (1.5f);
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
//        public boolean run_ClickEvent() {
//            UserControlledPlayer userControlledPlayer = GameScene.player;
//            if (userControlledPlayer.positionLock == null) {
//                GameScene.player.positionLock = (new PositionLock(this, -0.7f));
//                forwardBackDir = 0;
//                MinecartUtils.resetKeyEvent();
//                onTrack = alignToNearestTrack();
//                if (onTrack) {
//                    GameScene.alert("Press the forward and backward keys to toggle minecart direction.");
//                } else {
//                    GameScene.alert("Use WASD or arrow keys to navigate on minecart roads");
//                }
//            }
//            return false;
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
//        public void vehicle_draw() {
//            model.updateModelMatrix(modelMatrix);
//            model.draw();
//        }
//
//        @Override
//        public boolean vehicle_move() {
//            return false;
//        }
//
//        @Override
//        public void draw() {
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
//            renderMob();
//        }
//
//        @Override
//        public void vehicle_entityMoveEvent() {
//
//        }
//
//        @Override
//        public void vehicle_initializeOnDraw(ArrayList<Byte> bytes) {
//                alignToNearestTrack();
//        }
//
//        private boolean alignToNearestTrack() {
//            Vector3i currentTrackPiece = MinecartUtils.getNearestTrackPiece(this);
//            if (currentTrackPiece != null) {
//                BlockData trackData = GameScene.world.getBlockData(currentTrackPiece.x, currentTrackPiece.y, currentTrackPiece.z);
//                if (trackData.get(0) == 0 || trackData.get(0) == 2) {
//                    worldPosition.x = currentTrackPiece.x;
//                    this.rotationYDeg = (float) 0;
//                } else if (trackData.get(0) == 1 || trackData.get(0) == 3) {
//                    worldPosition.z = currentTrackPiece.z;
//                    this.rotationYDeg = (float) 90;
//                }
//                rotationYCurve = rotationYDeg;
//                return true;
//            }
//            return false;
//        }
//
//
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
//                } else if (getPlayer().backwardKeyPressed()) {
//                    targetSpeed = -0.08f;
//                    rotateSpeed = 1.0f;
//                }
//                speedCurve = (float) MathUtils.curve(speedCurve, targetSpeed, 0.03f);
//            } else {
//                rotateSpeed = 1.5f;
//                if (getPlayer().forwardKeyPressed()) {
//                    targetSpeed = 0.015f;
//                } else if (getPlayer().backwardKeyPressed()) {
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
//
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
//            Block b = GameScene.world.getBlock(position.x, position.y, position.z);
//            Block bup = GameScene.world.getBlock(position.x, position.y - 1, position.z);
//            Block bdown = GameScene.world.getBlock(position.x, position.y + 1, position.z);
//
//            if (forwardBackDir == 0) {
//                if (b.id == MyGame.BLOCK_SWITCH_JUNCTION) {
//                    if (GameScene.player.leftKeyPressed()) {
//                        if (MinecartUtils.switchJunctionKeyEvent) {
//                            float rotationY1 = rotationYDeg + 90;
//                            this.rotationYDeg = rotationY1;
//                            MinecartUtils.switchJunctionKeyEvent = false;
//                        }
//                    } else if (GameScene.player.rightKeyPressed()) {
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
//                if (b.id == MyGame.BLOCK_SWITCH_JUNCTION) {
//                    stop(position);
//                    goForward(speed);
//                } else if (b.id == MyGame.BLOCK_TRACK_STOP) {
//                    stop(position);
//                    goForward(speed);
//                } else if (b.id == MyGame.BLOCK_CURVED_TRACK) {
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
//                } else if (b.id == MyGame.BLOCK_MERGE_TRACK) {
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
//                } else if (b.id == MyGame.BLOCK_CROSSTRACK) {
//                    enableRotation();
//                    goForward(speed);
//                } else {
//                    enableRotation();
//                    if (riseInertaState <= 0 && b.id == MyGame.BLOCK_RAISED_TRACK) {
//                        posHandler.setGravityEnabled(false);
//                        worldPosition.y -= 0.07f;
//                        goForward(0.07f * forwardBackDir);
//                        riseInertaState = -1;
//                        riseInertaChangeMS = System.currentTimeMillis();
//                    } else if (riseInertaState >= 0 && bdown.id == MyGame.BLOCK_RAISED_TRACK) {
//                        posHandler.setGravityEnabled(false);
//                        worldPosition.y += 0.08f;
//                        goForward(0.07f * forwardBackDir);
//                        riseInertaState = 1;
//                        riseInertaChangeMS = System.currentTimeMillis();
//                    } else {
//                        if (System.currentTimeMillis() - riseInertaChangeMS > 200) {
//                            riseInertaState = 0;
//                        }
//                        BlockData orientation = GameScene.world.getBlockData(position.x, position.y, position.z);
//                        if (orientation != null) {
//                            if (orientation.get(0) == 0 || orientation.get(0) == 2) {
//                                worldPosition.x = position.x;
//                            } else if (orientation.get(0) == 1 || orientation.get(0) == 3) {
//                                worldPosition.z = position.z;
//                            }
//                        }
//                        goForward(speed);
//
//                        if ((b.solid || bdown.solid) && MinecartUtils.getNearestTrackPiece(this) == null) {
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
//        private void stop(Vector3i position) {
//            if (rotationEnabled) {
//                forwardBackDir = 0;
//                disableRotation(position);
//            }
//        }
//
//    }
//}
