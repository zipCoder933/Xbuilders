///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.entities.mobile;
//
//import com.xbuilders.engine.VoxelGame;
//import com.xbuilders.engine.player.UserControlledPlayer;
//import com.xbuilders.engine.utils.math.MathUtils;
//import com.xbuilders.engine.utils.math.TrigUtils;
//import com.xbuilders.engine.utils.random.RandomUtils;
//import org.joml.Vector3f;
//import processing.core.PGraphics;
//
//import static com.xbuilders.game.items.entities.mobile.AnimalAction.ActionType.*;
//
///**
// * @author zipCoder933
// */
//public abstract class LandAnimal extends Animal {
//
//    /**
//     * @return the travelSpeed
//     */
//    public float getTravelSpeed() {
//        return travelSpeed;
//    }
//
//
//    /**
//     * @return the player
//     */
//    public UserControlledPlayer getPlayer() {
//        return player;
//    }
//
//    /**
//     * @return the travelSpeed
//     */
//    public float getWalkAmt() {
//        return getTravelSpeed();
//    }
//
//    /**
//     * @return the action
//     */
//    public AnimalAction getAction() {
//        return action;
//    }
//
//    /**
//     * @param action the action to set
//     */
//    public void setAction(AnimalAction action) {
//        this.action = action;
//    }
//
//    private float maxSpeed = 0.17f;
//    private float activity = 0.8f;
//    private float actionVelocity;
//    private float travelSpeed = 0;
//
//    public void walkForward(float speed) {
//        travelSpeed = speed;
//        goForward(speed);
//    }
//
//    private UserControlledPlayer player;
//
//    public LandAnimal(Vector3f aabbSize, float animalRadius) {
//        super(aabbSize, true, animalRadius);
//        this.player = VoxelGame.getGame().player;
//        setNeedsConstantSaving(true);
//    }
//
//    /**
//     * @return the actionVelocity
//     */
//    public float getActionVelocity() {
//        return actionVelocity;
//    }
//
//    /**
//     * @return the activity
//     */
//    public float getActivity() {
//        return activity;
//    }
//
//    private AnimalAction action;
//
//    /**
//     * @return the maxSpeed
//     */
//    public float getMaxSpeed() {
//        return maxSpeed;
//    }
//
//    /**
//     * @param maxSpeed the maxSpeed to set
//     */
//    public void setMaxSpeed(float maxSpeed) {
//        this.maxSpeed = maxSpeed;
//    }
//
//    /**
//     * @param activity the activity to set
//     */
//    public void setActivity(float activity) {
//        this.activity = MathUtils.clamp(activity, 0, 1);
//    }
//
//    @Override
//    public void animalClicked() {
//    }
//
//    @Override
//    public void onDestructionInitiated() {
//        setAction(new AnimalAction(AnimalAction.ActionType.WALK, 10000));
//        actionVelocity = maxSpeed * 0.8f;
//    }
//
//    @Override
//    public void onDestructionCancel() {
//        getPosHandler().jump();
//        setAction(new AnimalAction(AnimalAction.ActionType.IDLE, 2000));
//    }
//
//    public abstract void renderAnimal(PGraphics g);
//
//    public AnimalAction newRandomAction(Enum lastAction) {
//        AnimalAction.ActionType actionType = AnimalAction.getRandomActionType(getRandom().getRandom(),
//                AnimalAction.ActionType.IDLE, AnimalAction.ActionType.WALK, AnimalAction.ActionType.TURN);
//
//        if (lastAction != null) {
//            AnimalAction.ActionType lastAction2 = (AnimalAction.ActionType) lastAction;
//            if (actionType == lastAction2) {
//                switch (lastAction2) {
//                    case IDLE:
//                        actionType = AnimalAction.ActionType.TURN;
//                        break;
//                    case TURN:
//                        actionType = AnimalAction.ActionType.WALK;
//                        break;
//                    case FOLLOW:
//                        actionType = AnimalAction.ActionType.TURN;
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//
//        long actionDuration = 0;
//
//        if (get3DDistToPlayer() < 5 && playerHasAnimalFeed()) {
//            actionType = AnimalAction.ActionType.FOLLOW;
//            tameAnimal();
//            actionDuration = getRandom().nextLong(4000, 25000);
//            actionVelocity = getMaxSpeed() / 2;
//        } else if (actionType == AnimalAction.ActionType.TURN) {
//            actionDuration = 50 + (getRandom().nextInt(100));
//            setVelocityOfTurnAction();
//        } else if (actionType == AnimalAction.ActionType.WALK) {
//            actionDuration = 600 + (getRandom().nextInt(500) - 250);
//            calculateWalkVelocity();
//        } else {
//            if (activity > 0.8) {
//                actionDuration = RandomUtils.randInt(-40, 200);
//                if (actionDuration < 0) {
//                    actionDuration = 0;
//                }
//            } else {
//                actionDuration = RandomUtils.randInt(50, 400);
//            }
//            actionDuration *= MathUtils.map(getActivity(), 0, 1, 5, 1);
//            if (getRandom().noise(10) > MathUtils.map(getActivity(), 0, 1, -0.2f, 0.3f)) {
//                actionDuration *= MathUtils.map(getActivity(), 0, 1, 10, 3);
//            }
//        }
//        return new AnimalAction(actionType, actionDuration);
//    }
//
//    float dist2;
//    float speedCurve;
//
//    @Override
//    public boolean move() {
//        if (playerIsRidingThis()) {
//            setAlwaysInFrustum(true);
//            float rotateSpeed = 0.2f;
//            float targetSpeed = 0;
//            if (getPlayer().forwardKeyPressed()) {
//                rotateSpeed = 2.5f;
//                targetSpeed = getMaxSpeed();
//            } else {
//                float n = getRandom().noise(3);
//                if (Math.abs(n) > 0.2) {
//                    addToRotationY(n > 0 ? 1.5f : -1.5f);
//                }
//            }
//            if (getPlayer().leftKeyPressed()) {
//                setRotationY(getRotationY() + rotateSpeed);
//            } else if (getPlayer().rightKeyPressed()) {
//                setRotationY(getRotationY() - rotateSpeed);
//            }
//            speedCurve = (float) MathUtils.curve(speedCurve, targetSpeed, 0.03f);
//            walkForward(speedCurve);
//        } else {
//            speedCurve = 0;
//            setAlwaysInFrustum(false);
//            if (getAction() == null || getAction().pastDuration()) {
//                if (getAction() == null) {
//                    setAction(newRandomAction(null));
//                } else {
//                    setAction(newRandomAction(getAction().getType()));
//                }
//            }
//
//            if (null != getAction().getType()) {
//                switch (getAction().getType()) {
//                    case FOLLOW:
//                        if (getAction().getTimeSinceCreatedMS() < 50 || getPointerHandler().getApplet().frameCount % 25 == 0) {
//                            UserControlledPlayer userControlledPlayer = VoxelGame.getGame().player;
//                            UserControlledPlayer userControlledPlayer1 = VoxelGame.getGame().player;
//                            dist2 = (float) MathUtils.dist(worldPosition.x, worldPosition.z,
//                                    userControlledPlayer1.worldPos.x, userControlledPlayer.worldPos.z);
//                        }
//                        if (get3DDistToPlayer() < 15 && playerHasAnimalFeed()) {
//                            if (getAction().getTimeSinceCreatedMS() > 500
//                                    && dist2 > 4
//                                    && getRandom().noise(4) > -0.5f) {
//                                walkForward(getActionVelocity());
//                                setRotationY(getRotationY() + getRandom().noise(2f, -3, 3));
//                            } else {
//                                travelSpeed = 0;
//                                if (dist2 < 4) {
//                                    eatAnimalFeed();
//                                }
//                            }
//                            float distAngle = TrigUtils.getSignedAngleDistance(getAngleToPlayer(), getRotationY());
//                            if (Math.abs(distAngle) > 170) {
//                                setRotationY(distAngle);
//                            } else {
//                                setRotationY(getRotationY() + distAngle * 0.08f);
//                            }
//                        } else {
//                            action = new AnimalAction(AnimalAction.ActionType.IDLE, 500);
//                        }
//                        break;
//                    case TURN:
//                        setRotationY(getRotationY() + getActionVelocity());
//                        travelSpeed = 0;
//                        break;
//                    case WALK:
//                        if (isPendingDestruction() && getRandom().nextFloat() > 0.6) {
//                            setRotationY(getRotationY() + getRandom().nextFloat() * 2);
//                        }
//                        walkForward(getActionVelocity());
//                        break;
//                    default:
//                        travelSpeed = 0;
//                }
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public boolean moveWhenOutOfFrustum() {
//        if (getAction() != null && getAction().getType() == AnimalAction.ActionType.FOLLOW) {
//            return false;
//        } else {
//            walkForward(maxSpeed);
//            travelSpeed = 0;
//            return true;
//        }
//    }
//
//    public long lastJumpMS;
//    public boolean jumpWithSideCollision = true;
//    private int jumpIndex = 0;
//    private int turnDirectionJump = 0;
//
//    @Override
//    public void postProcessMovement() {
//        if (jumpWithSideCollision &&
//                getPosHandler().collisionHandler.collisionData.sideCollision) {
//            if (jumpIndex > 0 && !playerIsRidingThis()) { //If the animal has at least jumped once, turn the animal around so it's not just bumping into walls
//                setRotationY(getRotationY() + turnDirectionJump);
//            }
//            if (System.currentTimeMillis() - lastJumpMS > 400) {
//                jumpIndex++;
//                getPosHandler().jump();
//                lastJumpMS = System.currentTimeMillis();
//            }
//        } else {
//            turnDirectionJump = getRandom().nextBoolean() ? 10 : -10;
//            jumpIndex = 0;
//        }
//    }
//
//    private void calculateWalkVelocity() {
//        if (getRandom().getSeed() > SEED_MAXIMUM * 0.7) {
//            actionVelocity = getRandom().nextFloat(0.01f, getMaxSpeed() * 0.6f);
//        } else {
//            actionVelocity = getRandom().nextFloat(0.01f, getMaxSpeed());
//        }
//        if (activity < 0.66 && getRandom().nextBoolean()) {
//            actionVelocity /= 3;
//        }
//        if (!inFrustum) {
//            actionVelocity *= 1.5;
//        }
//    }
//
//    private void setVelocityOfTurnAction() {
//        float rotationAction = MathUtils.clamp(getActivity(), 0.2f, 1);
//        actionVelocity = getRandom().noise(10) > 0 ? rotationAction * 10 : rotationAction * -10;
//        if (!inFrustum) {
//            actionVelocity *= 1.5;
//        }
//    }
//}
