package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.TrigUtils;
import com.xbuilders.window.BaseWindow;

public abstract class LandAnimal extends Animal {

    public LandAnimal(BaseWindow window) {
        super(window);
    }

    public AnimalAction currentAction = null;
    private float activity = 0.5f;
    private float maxSpeed = 0.17f;


    public void setActivity(float activity) {
        this.activity = MathUtils.clamp(activity, 0, 1);
    }

    public AnimalAction newRandomAction(Enum lastAction) {


        AnimalAction.ActionType newBehavior = AnimalAction.getRandomActionType(random.getRandom(), AnimalAction.ActionType.IDLE, AnimalAction.ActionType.WALK, AnimalAction.ActionType.TURN);
        if (lastAction != null) { //Shake it up if the last action was the same
            AnimalAction.ActionType lastAction2 = (AnimalAction.ActionType) lastAction;
            if (newBehavior == lastAction2) {
                switch (lastAction2) {
                    case IDLE:
                        newBehavior = AnimalAction.ActionType.TURN;
                        break;
                    case TURN:
                        newBehavior = AnimalAction.ActionType.WALK;
                        break;
                    case FOLLOW:
                        newBehavior = AnimalAction.ActionType.TURN;
                        break;
                    default:
                        break;
                }
            }
        }
        AnimalAction action = new AnimalAction(newBehavior);


        if (distToPlayer < 5 && playerHasAnimalFeed()) {
            action = new AnimalAction(AnimalAction.ActionType.FOLLOW);

            action.duration = random.nextLong(4000, 25000);
            action.velocity = maxSpeed / 2;
            return action;
        }

        if (newBehavior == AnimalAction.ActionType.TURN) {
            action.duration = 50 + (random.nextInt(100));
            float rotationAction = MathUtils.clamp(activity, 0.2f, 1);
            action.velocity = random.noise(10) > 0 ? rotationAction * 10 : rotationAction * -10;
            if (!inFrustum) {
                action.velocity *= 1.5;
            }
        } else if (newBehavior == AnimalAction.ActionType.WALK) {
            action.duration = 600 + (random.nextInt(500) - 250);
            action.velocity = random.nextFloat(0.01f, maxSpeed);

            if (activity < 0.66 && random.nextBoolean()) {
                action.velocity /= 3;
            }
            if (!inFrustum) {
                action.velocity *= 1.5;
            }
        } else if (newBehavior == AnimalAction.ActionType.IDLE) {
            int inverseDuration = ((int) (1 - activity) * 10) + 1;
            action.duration = random.nextInt(50, inverseDuration * 1000);
        } else {
            if (activity > 0.8) {
                action.duration = random.nextInt(-40, 200);
                if (action.duration < 0) {
                    action.duration = 0;
                }
            } else {
                action.duration = random.nextInt(50, 400);
            }
            action.duration *= MathUtils.map(activity, 0, 1, 5, 1);
            if (random.noise(10) > MathUtils.map(activity, 0, 1, -0.2f, 0.3f)) {
                action.duration *= MathUtils.map(activity, 0, 1, 10, 3);
            }
        }
        return action;
    }


    public void move() {
        if (freezeMode) {
            return;
        }
        if (currentAction == null || currentAction.pastDuration()) {
            if (currentAction == null) {
                currentAction = newRandomAction(null);
            } else {
                currentAction = newRandomAction(currentAction.type);
            }
        }

        if (null != currentAction.type) {
            switch (currentAction.type) {
                case TURN:
                    yRotDegrees = (yRotDegrees + currentAction.velocity);
                    break;
                case WALK:
                    goForward(currentAction.velocity);
                    break;
                case FOLLOW:

                    yRotDegrees = Math.toDegrees(getDirectionToPlayer()) + random.noise(2f, -3, 3);

                    if (distToPlayer < 15 && playerHasAnimalFeed()) {
                        if (currentAction.getTimeSinceCreatedMS() > 500
                                && distToPlayer > 3
                                && random.noise(4) > -0.5f) {

                            if (distToPlayer > 10) goForward(currentAction.velocity * 2);
                            else goForward(currentAction.velocity);

                        } else {
                            //Eat the food
//                            eatFood();
                        }
                    } else {
                        currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 500);
                    }


                    break;
                default:
            }
        }
    }


    public float getDirectionToPlayer() {
        return (float) (-MathUtils.calcRotationAngle(worldPosition.x, worldPosition.z, GameScene.player.worldPosition.x, GameScene.player.worldPosition.z) + MathUtils.HALF_PI);
    }

}
