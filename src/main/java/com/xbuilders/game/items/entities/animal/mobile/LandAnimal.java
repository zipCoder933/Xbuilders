package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.BaseWindow;
import org.joml.Matrix4f;

import java.util.ArrayList;

public abstract class LandAnimal extends Animal {

    public LandAnimal(BaseWindow window) {
        super(window);
    }

    long actionDuration = 0;
    AnimalAction action = null;
    private float activity = 0.5f;
    private float maxSpeed = 0.17f;


    public void setActivity(float activity) {
        this.activity = MathUtils.clamp(activity, 0, 1);
    }

    public AnimalAction newRandomAction(Enum lastAction) {
        AnimalAction.ActionType actionType = AnimalAction.getRandomActionType(random.getRandom(),
                AnimalAction.ActionType.IDLE, AnimalAction.ActionType.WALK, AnimalAction.ActionType.TURN);
        AnimalAction action = new AnimalAction(actionType);

        if (lastAction != null) {
            AnimalAction.ActionType lastAction2 = (AnimalAction.ActionType) lastAction;
            if (actionType == lastAction2) {
                switch (lastAction2) {
                    case IDLE:
                        actionType = AnimalAction.ActionType.TURN;
                        break;
                    case TURN:
                        actionType = AnimalAction.ActionType.WALK;
                        break;
                    case FOLLOW:
                        actionType = AnimalAction.ActionType.TURN;
                        break;
                    default:
                        break;
                }
            }
        }


        /*
         * if (distToPlayer < 5 && playerHasAnimalFeed()) {
         * actionType = AnimalAction.ActionType.FOLLOW;
         * tameAnimal();
         * actionDuration = getRandom().nextLong(4000, 25000);
         * actionVelocity = getMaxSpeed() / 2;
         * } else
         */
        if (actionType == AnimalAction.ActionType.TURN) {
            actionDuration = 50 + (random.nextInt(100));
            float rotationAction = MathUtils.clamp(activity, 0.2f, 1);
            action.velocity = random.noise(10) > 0 ? rotationAction * 10 : rotationAction * -10;
            if (!inFrustum) {
                action.velocity *= 1.5;
            }
        } else if (actionType == AnimalAction.ActionType.WALK) {
            actionDuration = 600 + (random.nextInt(500) - 250);
            action.velocity = random.nextFloat(0.01f, maxSpeed);

            if (activity < 0.66 && random.nextBoolean()) {
                action.velocity /= 3;
            }
            if (!inFrustum) {
                action.velocity *= 1.5;
            }
        } else {
            if (activity > 0.8) {
                actionDuration = random.nextInt(-40, 200);
                if (actionDuration < 0) {
                    actionDuration = 0;
                }
            } else {
                actionDuration = random.nextInt(50, 400);
            }
            actionDuration *= MathUtils.map(activity, 0, 1, 5, 1);
            if (random.noise(10) > MathUtils.map(activity, 0, 1, -0.2f, 0.3f)) {
                actionDuration *= MathUtils.map(activity, 0, 1, 10, 3);
            }
        }
        action.duration = actionDuration;
        return action;
    }

    public void move() {
        if (freezeMode) {
            return;
        }
        if (action == null || action.pastDuration()) {
            if (action == null) {
                action = newRandomAction(null);
            } else {
                action = newRandomAction(action.type);
            }
        }

        if (null != action.type) {
            switch (action.type) {
                case TURN:
                    yRotDegrees = (yRotDegrees + action.velocity);
                    break;
                case WALK:
                    goForward(action.velocity);
                    break;
                default:
            }
        }
    }

}
