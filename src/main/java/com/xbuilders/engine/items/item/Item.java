/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.BlockArrayTexture;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.window.utils.texture.Texture;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.lwjgl.nuklear.NkImage;

/**
 * An item is a static class. Each item has a unique ID, and can only exist once.
 * Item stack is an item with a quantity, it is a nonstatic class and can exist in many places
 *
 * @author zipCoder933
 */
public class Item {

    public final ArrayList<String> tags = new ArrayList<>();

    public Consumer<Item> initializationCallback;


    public final short id; //TODO: Find a way to represent block ID as an unsigned short (up to 65,000 IDs)

    public final String name;
    private int icon = 0;
    private final NkImage NKicon;
    public String iconFilename;
    public Block block;
    public EntityLink entity;


    // <editor-fold defaultstate="collapsed" desc="tool events">
    //Create a functional interface for setBlockEvent
    //A functional interface for onLocalChange
    @FunctionalInterface
    public interface OnClickEvent {
        public boolean run(CursorRay ray);
    }

    public OnClickEvent createClickEvent, destroyClickEvent;


    // </editor-fold>


    public Item(int id, String name) {
        if (id > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Item ID Can not exceed " + Short.MAX_VALUE);
        }
        this.id = (short) id;
        this.name = MiscUtils.capitalizeWords(name.trim()); //We can auto-format the name
        NKicon = NkImage.create();
    }

    /**
     * @return the tags
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Set the item to an icon in the icons directory
     *
     * @param iconFilename
     */
    public void setIcon(String iconFilename) {
        this.iconFilename = iconFilename;
    }

    protected void setIcon(int textureID) {
        icon = textureID;
        NKicon.handle(it -> it.id(textureID));
    }

    public int getIconID() {
        return icon;
    }

    public NkImage getNKIcon() {
        return NKicon;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        return this.id == other.id;
    }

    public final void initIcon(BlockArrayTexture textures,
                               File blockIconDirectory,
                               File iconDirectory,
                               int defaultIcon) throws IOException {

        if (block != null) {
            iconFilename = block.iconFilename;
        } else if (entity != null) {
            iconFilename = entity.iconFilename;
        }

        if (iconFilename != null) { //If we have a custom icon
            File iconFile = new File(iconDirectory, iconFilename);
            if (!iconFile.getAbsolutePath().endsWith(".png") && !iconFile.exists()) {
                iconFile = new File(iconDirectory, iconFilename + ".png"); //Add .png if it doesn't exist
            }
            if (iconFile.exists()) {
                Texture icon = TextureUtils.loadTexture(iconFile.getAbsolutePath(), false);
                setIcon(icon.id);
            } else {
                System.err.println("Icon file not found: " + iconFile.getAbsolutePath());
                setIcon(defaultIcon);
            }
        } else if (block != null && block.texture != null) { //If we have a block, use its texture as the icon
            File blockIcon = new File(blockIconDirectory, block.id + ".png");
//            System.out.println("Block icon file: " + blockIcon.getAbsolutePath());
            if (blockIcon.exists()) {
                Texture icon = TextureUtils.loadTexture(blockIcon.getAbsolutePath(), true);
                setIcon(icon.id);
            } else {//If there is no generated block icon, default to the texture
                File file = textures.getTextureFile(block.texture.NEG_Y_NAME);
                if (file != null) {
                    Texture tex = TextureUtils.loadTexture(file.getAbsolutePath(), false);
                    setIcon(tex.id);
                } else {
                    setIcon(defaultIcon);
                }
            }
        } else {
            setIcon(defaultIcon);
        }
    }

    @Override
    public String toString() {
        return "Item \"" + name + "\" (id: " + id + ")";
    }

}
