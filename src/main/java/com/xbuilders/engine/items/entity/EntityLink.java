/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.entity;

import com.xbuilders.engine.utils.ResourceUtils;

import java.io.File;
import java.util.HashSet;
import java.util.function.Supplier;

/**
 * @author zipCoder933
 */
public class EntityLink {
    public final short id;
    public final String name;
    public final HashSet<String> tags = new HashSet<>();

    public final Supplier<Entity> supplier;

    public String iconFilename;
    public void setIcon(String iconFilename) {
        this.iconFilename = iconFilename;
    }

    public EntityLink(int id, String name, Supplier<Entity> supplier) {
        this.id = (short)id;
        this.name = name;
        this.supplier = supplier;
    }

}
