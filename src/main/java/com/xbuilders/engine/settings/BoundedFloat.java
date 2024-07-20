package com.xbuilders.engine.settings;

public class BoundedFloat {

    public float value;
    public float min;
    public float max;

    public BoundedFloat(float value) {
        this.value = value;
    }

    public void setBounds(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public void clamp() {
        if (value < min) value = min;
        if (value > max) value = max;
    }
}
