package com.xbuilders.tests.fasterXML.json;

// POJO class
public class jsonObject {
    private String name;
    private int value;

    public jsonObject() {
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MyObject{name='" + getName() + "', value=" + getValue() + '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }
}