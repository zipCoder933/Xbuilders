/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.utils.IntMap;

import java.util.HashMap;

/**
 * @param <T>
 * @author zipCoder933
 */
abstract class ItemGroup<T extends Item> {

    final Class<T> type;
    protected final IntMap<T> idMap;
    private T[] itemList;


    public ItemGroup(Class<T> type) {
        this.type = type;
        idMap = new IntMap<>(type);
    }

    public abstract void setItems(T[] inputBlocks);

    /**
     * @return the items
     */
    public T[] getList() {
        return itemList;
    }

    public T getItem(short id) {
        return idMap.get(id);
    }

    protected final int setList(T[] inputItems) {
        //get typename of t
        String typename = inputItems.getClass().getComponentType().getSimpleName();
        System.out.println("\nChecking IDs for " + typename);
        int highestId = 0;
        HashMap<Integer, T> map = new HashMap<>();
        for (int i = 0; i < inputItems.length; i++) {
            if (inputItems[i] == null) {
                System.err.println("item at index " + i + " is null");
                continue;
            }
            int id = inputItems[i].id;
            if (map.get(id) != null) {
                System.err.println("Block " + inputItems[i] + " ID conflicts with an existing ID: " + id);
            }
            map.put(id, inputItems[i]);
            if (id > highestId) {
                highestId = id;
            }
        }
        System.out.println("\t(The highest item ID is: " + highestId + ")");
        System.out.print("\tID Gaps: ");
        for (int id = 1; id < highestId; id++) {
            boolean found = false;
            for (Item item : inputItems) {
                if (item.id == id) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.print(id + ", ");
            }
        }
        System.out.println("");
        idMap.setList(map);
        itemList = inputItems;
        return highestId;
    }
}
