/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.utils.ByteUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zipCoder933
 */
public class AnimalAction {

    public ActionType type;
    public long duration;
    public float velocity;
    private long createdTimeMS;



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

    public static ActionType getRandomActionType(AnimalRandom random, ActionType... types) {
        int indx = random.nextInt(types.length);
        return types[indx];
    }

    public void toBytes(ByteArrayOutputStream baos) throws IOException {
        ByteUtils.writeInt(baos, type.ordinal());
        ByteUtils.writeLong(baos, duration);
        ByteUtils.writeFloat(baos, velocity);
        ByteUtils.writeLong(baos, createdTimeMS);
        System.out.println("AnimalAction: " + this);
    }

    public AnimalAction fromBytes(byte[] state, AtomicInteger start) {
        type = ActionType.values()[ByteUtils.bytesToInt(state, start)];
        duration = ByteUtils.bytesToLong(state, start);
        velocity = ByteUtils.bytesToFloat(state, start);
        createdTimeMS = ByteUtils.bytesToLong(state, start);
        return this;
    }

    public AnimalAction() {}

    public AnimalAction(ActionType type) {
        this.type = type;
        this.createdTimeMS = System.currentTimeMillis();
    }
    public AnimalAction(ActionType type, long duration) {
        this.type = type;
        this.createdTimeMS = System.currentTimeMillis();
        this.duration = duration;
    }

    public long getTimeSinceCreatedMS() {
        return System.currentTimeMillis() - createdTimeMS;
    }

    public boolean pastDuration() {
        return getTimeSinceCreatedMS() > duration;
    }

    @Override
    public String toString() {
        return "AnimalAction{" + "type=" + type + ", duration=" + duration + '}';
    }

}
