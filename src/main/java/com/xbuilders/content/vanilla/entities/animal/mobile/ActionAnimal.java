package com.xbuilders.content.vanilla.entities.animal.mobile;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.entity.LivingEntity;

public class ActionAnimal  extends LivingEntity {
    private float maxSpeed = 0.10f;
    public AnimalAction currentAction;


    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }


    public ActionAnimal(long uniqueId, ClientWindow window) {
        super(uniqueId, window);
    }

    @Override
    public void server_update() {
        super.server_update();
        if (health <= 0) {
            runAwayAndDie();
        }
    }

    public void runAwayAndDie() {
        currentAction = new AnimalAction(AnimalAction.ActionType.RUN_AWAY_DIE, 30000);
        currentAction.velocity = maxSpeed;
    }

    public void walkAwayAndDie() {
        currentAction = new AnimalAction(AnimalAction.ActionType.RUN_AWAY_DIE, 30000);
        currentAction.velocity = maxSpeed / 2;
    }

    @Override
    public void animal_move() {

    }

    @Override
    public void animal_drawBody() {

    }
}
