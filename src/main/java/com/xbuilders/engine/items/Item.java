/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.xbuilders.engine.utils.ByteUtils;
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
public abstract class Item {

    public final ArrayList<String> tags = new ArrayList<>();
    public final HashMap<String, String> properties = new HashMap<>();


    public final short id; //TODO: Find a way to represent block ID as an unsigned short (up to 65,000 IDs)
    public final ItemType itemType;
    public final String name;
    private int icon = 0;
    private final NkImage NKicon;
    public String iconFilename;

    /**
     * @return the type
     */
    public ItemType getType() {
        return itemType;
    }

    public Item(int id, String name, ItemType itemType) {
        this.itemType = itemType;
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
        return this.id == other.id && this.getType() == other.getType();
    }

    protected boolean initIcon(File iconDirectory, int defaultIcon) throws IOException {
        if (iconFilename != null) {
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
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getType().toString().toLowerCase().replace("_", " ") + " \"" + name + "\" (id: " + id + ")";
    }

}
