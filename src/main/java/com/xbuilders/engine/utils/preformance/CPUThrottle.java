/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.preformance;

import java.util.concurrent.locks.LockSupport;

/**
 * Inspired by https://stackoverflow.com/questions/1202184/throttling-cpu-memory-usage-of-a-thread-in-java
 * @author zipCoder933
 */
public class CPUThrottle {

    public CPUThrottle() {
    }

    /**
     *
     * @param sleepConstantMultiplier the multiplier of the original sleep
     * constant
     */
    public CPUThrottle(float sleepConstantMultiplier) {
        sleepConstant = (long) (1000000 * sleepConstantMultiplier);
    }

    /**
     * @return the sleepConstant
     */
    public long getSleepConstant() {
        return sleepConstant;
    }

    /**
     * @param sleepConstant the sleepConstant to set
     */
    public void setSleepConstant(long sleepConstant) {
        this.sleepConstant = sleepConstant;
    }

    long starttime;
    private long sleepConstant = 1000000;

    /**
     * Called before an action is preformed. Sets the start timestamp
     */
    public void startAction() {
        starttime = System.currentTimeMillis();
    }

    /**
     * Called after an action is preformed. Sleeps the thread a linear amount of
     * time based on how long the action took.
     */
    public void endAction() {
        // Wait until the desired next time arrives using nanosecond
        // accuracy timer (wait(time) isn't accurate enough on most platforms) 
        long wait = (long) ((System.currentTimeMillis() - starttime) * getSleepConstant());
        LockSupport.parkNanos(wait);
    }

}
