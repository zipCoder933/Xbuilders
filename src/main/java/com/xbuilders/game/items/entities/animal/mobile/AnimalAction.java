/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.mobile;

import java.util.Random;

/**
 * @author zipCoder933
 */
public class AnimalAction {

    public enum ActionType {
        TURN,
        IDLE,
        WALK,
        FOLLOW,
        SWIM,
        CLIMB,
        JUMP,
        FLY,
        OTHER
    }

    ;

    public static ActionType getRandomActionType(Random random, ActionType... types) {
        int indx = random.nextInt(types.length);
        return types[indx];
    }


    public AnimalAction(ActionType type) {
        this.type = type;
        this.createdTimeMS = System.currentTimeMillis();
    }

    public long getTimeSinceCreatedMS() {
        return System.currentTimeMillis() - createdTimeMS;
    }

    public boolean pastDuration() {
        return getTimeSinceCreatedMS() > duration;
    }

    public ActionType type;
    public long duration;
    public float velocity;
    private long createdTimeMS;


    @Override
    public String toString() {
        return "AnimalAction{" + "type=" + type + ", duration=" + duration + '}';
    }

}
