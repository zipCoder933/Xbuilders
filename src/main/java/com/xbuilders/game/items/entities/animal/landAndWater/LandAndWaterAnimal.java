/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.landAndWater;

import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.game.Main;
import com.xbuilders.game.items.entities.animal.mobile.Animal;
import com.xbuilders.game.items.entities.animal.mobile.AnimalAction;
import com.xbuilders.game.items.entities.animal.quadPedal.QuadPedalLandAnimalLink;
import com.xbuilders.window.BaseWindow;
import org.joml.Vector2f;

import static com.xbuilders.game.items.entities.animal.mobile.AnimalAction.ActionType.*;

/**
 * @author zipCoder933
 */
public abstract class LandAndWaterAnimal extends Animal {

    private float maxSpeed = 0.10f;
    private float activity = 0.8f;
    private float actionVelocity;
    private AnimalAction action;
    private boolean inWater;
    private float walkAmt = 0;
    float yVelocity, rotationVelocity, forwardVelocity;

    public LandAndWaterAnimal(BaseWindow window) {
        super(window);
    }

    /**
     * @return the inWater
     */
    public boolean isInWater() {
        return inWater;
    }

    /**
     * @return the walkAmt
     */
    public float getWalkAmt() {
        return walkAmt;
    }

    /**
     * @return the action
     */
    public AnimalAction getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(AnimalAction action) {
        this.action = action;
    }


    /**
     * @return the actionVelocity
     */
    public float getActionVelocity() {
        return actionVelocity;
    }

    /**
     * @return the activity
     */
    public float getActivity() {
        return activity;
    }

    /**
     * @return the maxSpeed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * @param maxSpeed the maxSpeed to set
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * @param activity the activity to set
     */
    public void setActivity(float activity) {
        this.activity = MathUtils.clamp(activity, 0, 1);
    }


    public AnimalAction newRandomAction(Enum lastAction) {
        random.nextInt(AnimalAction.ActionType.values().length - 1);
        AnimalAction.ActionType actionType = AnimalAction.getRandomActionType(random,
                AnimalAction.ActionType.IDLE,
                WALK,
                TURN);

        if (lastAction != null) {
            AnimalAction.ActionType lastAction2 = (AnimalAction.ActionType) lastAction;
            if (actionType == lastAction2) {
                switch (lastAction2) {
                    case IDLE:
                        actionType = TURN;
                        break;
                    case TURN:
                        actionType = WALK;
                        break;
                    case FOLLOW:
                        actionType = TURN;
                        break;
                    default:
                        break;
                }
            }
        }

        long actionDuration = 0;

        if (distToPlayer < 5 && playerHasAnimalFeed()) {
            actionType = FOLLOW;
            tameAnimal();
            actionDuration = random.nextLong(4000, 25000);
            actionVelocity = getMaxSpeed() / 2;
        } else if (actionType == TURN) {
            actionDuration = 50 + (random.nextInt(100));
            setVelocityOfTurnAction();
        } else if (actionType == WALK) {
            actionDuration = 600 + (random.nextInt(500) - 250);
            calculateWalkVelocity();
        } else {
            if (activity > 0.8) {
                actionDuration = random.nextInt(-40, 200);
                if (actionDuration < 0) {
                    actionDuration = 0;
                }
            } else {
                actionDuration = random.nextInt(50, 400);
            }
            actionDuration *= MathUtils.map(getActivity(), 0, 1, 5, 1);
            if (random.noise(10) > MathUtils.map(getActivity(), 0, 1, -0.2f, 0.3f)) {
                actionDuration *= MathUtils.map(getActivity(), 0, 1, 10, 3);
            }
        }
        return new AnimalAction(actionType, actionDuration);
    }



    public void walkForward(float speed) {
        if (isInWater()) {
            walkAmt = speed / 2;
        } else {
            walkAmt = speed;
        }

        Vector2f vec = TrigUtils.getCircumferencePoint(-getRotationYDeg(), speed);
        worldPosition.add(vec.x, 0, vec.y);

    }


    private void calculateWalkVelocity() {
        actionVelocity = random.nextFloat(0.01f, getMaxSpeed());
        if (activity < 0.66 && random.nextBoolean()) {
            actionVelocity /= 3;
        }
    }

    private void setVelocityOfTurnAction() {
        float rotationAction = MathUtils.clamp(getActivity(), 0.2f, 1);
        actionVelocity = random.noise(10) > 0 ? rotationAction * 10 : rotationAction * -10;
    }

    private void facePlayer() {
        setRotationYDeg((float) Math.toDegrees(getYDirectionToPlayer()));
    }

    public boolean move() {
        if (inFrustum) { //In Frustum movement
            inWater = inWater();
            if (isInWater()) {
                    moveInWater();
                    return true;
            } else {
                moveOnLand();
                return true;
            }
        } else { //Out of Frustum movement
            if (getAction() != null && getAction().type == FOLLOW) {
                return false;
            } else {
                walkForward(maxSpeed);
                walkAmt = 0;
                return true;
            }
        }
    }

    private void moveInWater() {
        pos.setGravityEnabled(false);
        if (action == null || action.pastDuration()) {
            if (action != null && action.type == FOLLOW) {
                action = new AnimalAction(FOLLOW, random.nextLong(50, 2000));
            } else {
                action = new AnimalAction(SWIM, random.nextLong(50, 2000));
            }
            rotationVelocity = random.noise(2) * 3;
            forwardVelocity = (float) MathUtils.mapAndClamp(random.noise(4), -0.5f, 1, 0.01f, getMaxSpeed());
            yVelocity = random.noise(1, -0.06f, 0.05f);
        }
        if (isPendingDestruction()) {
            setRotationYDeg(getRotationYDeg() + rotationVelocity / 3);
        } else if (distToPlayer < 10 && playerHasAnimalFeed()) {
            tameAnimal();
            float distAngle = TrigUtils.getSignedAngleDistance(getYDirectionToPlayer(), getRotationYDeg());
            if (Math.abs(distAngle) > 170) {
                setRotationYDeg(distAngle);
            } else {
                setRotationYDeg(getRotationYDeg() + distAngle * 0.1f);
            }
            if (distToPlayer < 3) {
                eatAnimalFeed();
            }
        } else {
            if (action.type == FOLLOW) {
                facePlayer();
            } else {
                setRotationYDeg(getRotationYDeg() + rotationVelocity);
            }
        }
        worldPosition.y += yVelocity;
    }

    private void moveOnLand() {
        pos.setGravityEnabled(true);
        if (getAction() == null || getAction().pastDuration()) {
            if (getAction() == null) {
                setAction(newRandomAction(null));
            } else {
                setAction(newRandomAction(getAction().type));
            }
        }

        if (null != getAction().type) {
            switch (getAction().type) {
                case FOLLOW:
                    if (getAction().getTimeSinceCreatedMS() < 50 || Main.frameCount % 25 == 0) {

                    }
                    if (distToPlayer < 15 && playerHasAnimalFeed()) {
                        if (getAction().getTimeSinceCreatedMS() > 500
                                && distToPlayer > 4
                                && random.noise(4) > -0.5f) {
                            walkForward(getActionVelocity());
                            setRotationYDeg(getRotationYDeg() + random.noise(2f, -3, 3));
                        } else {
                            walkAmt = 0;
                            if (distToPlayer < 4) {
                                eatAnimalFeed();
                            }
                        }
                        facePlayer();
                    } else {
                        action = new AnimalAction(AnimalAction.ActionType.IDLE, 500);
                    }
                    break;
                case TURN:
                    setRotationYDeg(getRotationYDeg() + getActionVelocity());
                    walkAmt = 0;
                    break;
                case WALK:
                    if (isPendingDestruction() && random.nextFloat() > 0.6) {
                        setRotationYDeg(getRotationYDeg() + random.nextFloat() * 2);
                    }
                    walkForward(getActionVelocity());
                    break;
                default:
                    walkAmt = 0;
                    break;
            }
        }
    }

}
