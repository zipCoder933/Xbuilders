package com.xbuilders.engine.settings;

public class BoundedInt {

    public int value;
    public  int min;
    public  int max;

    public BoundedInt(int value) {
        this.value = value;
    }

    public void setBounds(int min, int max) {
        this.min = min;
        this.max = max;
    }

}
