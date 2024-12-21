/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.items.entities.animal.landAndWater;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.game.vanilla.items.Blocks;
import com.xbuilders.game.vanilla.items.entities.animal.mobile.Animal;
import com.xbuilders.game.vanilla.items.entities.animal.mobile.AnimalAction;
import com.xbuilders.game.vanilla.items.entities.animal.mobile.AnimalUtils;
import org.joml.Vector2f;

import static com.xbuilders.game.vanilla.items.entities.animal.mobile.AnimalAction.ActionType.*;

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

    public LandAndWaterAnimal(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    @Override
    public void initSupplier(EntitySupplier entitySupplier) {
        super.initSupplier(entitySupplier);
        entitySupplier.spawnCondition = (x, y, z) -> {
            if (GameScene.world.getBlockID(x, y, z) == Blocks.BLOCK_WATER) return true;
            return false;
        };
        entitySupplier.isAutonomous = true;
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
            tamed = true;
            actionDuration = random.nextLong(4000, 25000);
            actionVelocity = getMaxSpeed() / 2;
        } else if (actionType == TURN) {
            actionDuration = 50 + (random.nextInt(100));
            actionVelocity = computeTurnVelocity();
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
        if (inWater) {
            walkAmt = speed / 2;
        } else {
            walkAmt = speed;
        }

        Vector2f vec = MathUtils.getCircumferencePoint(-getRotationYDeg(), speed);
        worldPosition.add(vec.x, 0, vec.y);

    }


    private void calculateWalkVelocity() {
        actionVelocity = random.nextFloat(0.01f, getMaxSpeed());
        if (activity < 0.66 && random.nextBoolean()) {
            actionVelocity /= 3;
        }
    }

    private float computeTurnVelocity() {
        float rotationAction = MathUtils.clamp(getActivity(), 0.2f, 1);
        return random.noise(10) > 0 ? rotationAction * 10 : rotationAction * -10;
    }


    public void animal_move() {
        if (inFrustum) { //In Frustum movement
            if (MainWindow.frameCount % 10 == 0) inWater = AnimalUtils.inWater(this);


            if (inWater) {
                moveInWater();
            } else {
                moveOnLand();
            }
        } else { //Out of Frustum movement
            if (MainWindow.frameCount % 10 != 0) return;
            else if (getAction() != null && getAction().type == FOLLOW) {
                return;
            } else {
                walkForward(maxSpeed);
                walkAmt = 0;
            }
        }
        pos.update();
    }

    private void moveInWater() {
        pos.setGravityEnabled(false);

        //When we are ready to change the action
        if (action == null || action.pastDuration()) {
            if (getAction() == null) {
                setAction(newRandomAction(null));
            } else {
                setAction(newRandomAction(getAction().type));
            }
            rotationVelocity = computeTurnVelocity();
            forwardVelocity = (float) MathUtils.mapAndClamp(random.noise(4), -0.5f, 1, 0.01f, getMaxSpeed());
            yVelocity = random.nextFloat(-0.05f, 0.05f);
        }

        //Perform the action
        if (null != getAction().type) {
            switch (getAction().type) {
                case FOLLOW:
                    followAction();
                    break;
                case TURN:
                    setRotationYDeg(getRotationYDeg() + getActionVelocity());
                    walkAmt = 0;
                    break;
                case WALK:
                    setRotationYDeg(getRotationYDeg() + rotationVelocity);
                    walkForward(getActionVelocity());
                    break;
                default:
                    walkAmt = 0;
                    break;
            }
        }
        worldPosition.y -= yVelocity;
    }

    private void moveOnLand() {
        pos.setGravityEnabled(true);

        //When we are ready to change the action
        if (getAction() == null || getAction().pastDuration()) {
            if (getAction() == null) {
                setAction(newRandomAction(null));
            } else {
                setAction(newRandomAction(getAction().type));
            }
        }

        //Perform the action
        if (null != getAction().type) {
            switch (getAction().type) {
                case FOLLOW:
                    followAction();
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

    private void followAction() {
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
    }

}
