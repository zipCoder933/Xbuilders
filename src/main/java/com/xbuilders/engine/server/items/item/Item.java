/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.items.item;

import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.server.items.block.BlockArrayTexture;
import com.xbuilders.engine.server.items.entity.EntitySupplier;
import com.xbuilders.engine.client.player.raycasting.CursorRay;
import com.xbuilders.engine.utils.IntMap;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.window.utils.texture.Texture;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.lwjgl.nuklear.NkImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * An item is a static class. Each item has a unique ID, and can only exist once.
 * Item stack is an item with a quantity, it is a nonstatic class and can exist in many places
 *
 * @author zipCoder933
 */
public class Item implements Comparable<Item> {
    public final ArrayList<String> tags = new ArrayList<>();
    public Consumer<Item> initializationCallback;

    public final String id;
    public final String name;

    //Icon
    private int icon = 0;
    private final NkImage NKicon;
    public String iconFilename;

    //Block or entity
    private Block block;
    private EntitySupplier entity;

    //We have to have the IDs saved before we can load them as classes
    private short blockID = -1;
    private String blockAlias = null;
    private String entityID = null;

    public void setBlock(short blockID) {
        this.blockID = blockID;
    }

    public void setBlock(String blockName) {
        this.blockAlias = blockName;
    }

    public void setEntity(String entityID) {
        this.entityID = entityID;
    }


    public Block getBlock() {
        return block;
    }

    public EntitySupplier getEntity() {
        return entity;
    }


    public boolean isFood() {
        return hungerSaturation > 0;
    }

    public int maxDurability = 0;

    //If this is a food (>0 means it is)
    public float hungerSaturation = 0;

    //If this is a tool
    public float miningSpeedMultiplier = 1.0f;
    public int maxStackSize = ItemStack.MAX_STACK_SIZE;

    @Override
    public int compareTo(Item o) {
        return id.compareTo(o.id);
    }

    //Create a functional interface for setBlockEvent
    //A functional interface for onLocalChange
    @FunctionalInterface
    public interface OnClickEvent {
        public boolean run(CursorRay ray, ItemStack stack);
    }
    public OnClickEvent createClickEvent, destroyClickEvent;

    public Item(String id, String name) {
        this.id = id;
        if (id.isBlank()) throw new IllegalArgumentException("Item ID cannot be empty");
        else if (id.contains(" ")) throw new IllegalArgumentException("Item ID cannot contain spaces");
        if (id.contains("-"))
            System.err.println("It is not recommended to use dashes in item IDs, use underscores instead");

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public final void init(IntMap<Block> blockMap,
                           HashMap<String, EntitySupplier> entityMap,
                           HashMap<String, Short> blockAliasToIDMap,
                           HashMap<String, Short> entityAliasToIDMap,
                           BlockArrayTexture textures,
                           File blockIconDirectory,
                           File iconDirectory,
                           int defaultIcon) throws IOException {

        //If we have the aliases, get the IDs
        if (blockAlias != null) blockID = blockAliasToIDMap.get(blockAlias);

        if (blockID != -1) block = blockMap.get(blockID);
        if (entityID != null) entity = entityMap.get(entityID);

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
        } else if (getBlock() != null && getBlock().texture != null) { //If we have a block, use its texture as the icon
            File blockIcon = new File(blockIconDirectory, getBlock().id + ".png");
//            System.out.println("Block icon file: " + blockIcon.getAbsolutePath());
            if (blockIcon.exists()) {
                Texture icon = TextureUtils.loadTexture(blockIcon.getAbsolutePath(), true);
                setIcon(icon.id);
            } else {//If there is no generated block icon, default to the texture
                File file = textures.getTextureFile(getBlock().texture.NEG_Y_NAME);
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
        return id;
    }

}
