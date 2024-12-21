package com.xbuilders.content.vanilla.items.entities.animal.mobile;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.items.entity.EntitySupplier;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.content.vanilla.items.Blocks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LandAnimal extends Animal {

    public LandAnimal(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    public AnimalAction currentAction = null;
    private float activity = 0.5f;
    private float maxSpeed = 0.17f;
    public boolean jumpOverBlocks = true;

    @Override
    public void initSupplier(EntitySupplier entitySupplier) {
        super.initSupplier(entitySupplier);
        entitySupplier.spawnCondition = (x, y, z) -> {
            Block floor = GameScene.world.getBlock(x, (int) (y + Math.ceil(aabb.box.getYLength())), z);
            if (floor.solid && GameScene.world.getBlockID(x, y, z) == Blocks.BLOCK_AIR) return true;
            return false;
        };
        entitySupplier.isAutonomous = true;
    }

    public void animal_writeState(ByteArrayOutputStream baos) throws IOException {
        if (currentAction != null) currentAction.toBytes(baos);
        baos.write(tamed ? 1 : 0);
    }

    public void animal_readState(byte[] state, AtomicInteger start) {
        if (start.get() < state.length - 1) {
            currentAction = new AnimalAction().fromBytes(state, start);
        }
        tamed = state[start.getAndIncrement()] == 1;
    }

    public void setActivity(float activity) {
        this.activity = MathUtils.clamp(activity, 0, 1);
    }

    public AnimalAction newRandomAction(Enum lastAction) {
        AnimalAction.ActionType newBehavior = AnimalAction.getRandomActionType(random, AnimalAction.ActionType.IDLE, AnimalAction.ActionType.WALK, AnimalAction.ActionType.TURN);
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

            action.duration = (int) random.nextLong(4000, 25000);
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

    public void animal_move() {
        multiplayerProps.controlMode = false;
        if (freezeMode || multiplayerProps.controlledByAnotherPlayer) {
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
                    setRotationYDeg((getRotationYDeg() + currentAction.velocity));
                    break;
                case WALK:
                    goForward(currentAction.velocity, jumpOverBlocks);
                    break;
                case FOLLOW:
                    multiplayerProps.controlMode = true;
                    setRotationYDeg((float) Math.toDegrees(getYDirectionToPlayer()) + random.noise(2f, -3, 3));

                    if (distToPlayer < 15 && playerHasAnimalFeed()) {
                        if (currentAction.getTimeSinceCreatedMS() > 500
                                && distToPlayer > 3
                                && random.noise(4) > -0.5f) {

                            if (distToPlayer > 10) goForward(currentAction.velocity * 2, jumpOverBlocks);
                            else goForward(currentAction.velocity, jumpOverBlocks);

                        } else {
                            //Eat the food
                            eatAnimalFeed();
                        }
                    } else {
                        currentAction = new AnimalAction(AnimalAction.ActionType.IDLE, 500);
                    }
                    break;
                default:
            }
        }
    }


}
