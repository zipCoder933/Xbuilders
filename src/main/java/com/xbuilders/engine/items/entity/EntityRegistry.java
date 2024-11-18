/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.entity;

import com.xbuilders.engine.utils.IntMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xbuilders.engine.utils.ArrayUtils.combineArrays;

/**
 * @author zipCoder933
 */
public class EntityRegistry {

    //Predefined entities
//    public static ItemDropEntityLink ENTITY_ITEM_DROP = new ItemDropEntityLink();

    final IntMap<EntitySupplier> idMap = new IntMap<>(EntitySupplier.class);
    private EntitySupplier[] list;

    public EntityRegistry() {
    }


    public EntitySupplier[] getList() {
        return list;
    }

    public EntitySupplier getItem(short blockID) {
        return idMap.get(blockID);
    }

    public IntMap<EntitySupplier> getIdMap() {
        return idMap;
    }

    private int verifyEntityIds(List<EntitySupplier> inputItems) {
        System.out.println("\nChecking entity IDs");
        int highestId = 0;
        HashMap<Integer, EntitySupplier> map = new HashMap<>();

        for (int i = 0; i < inputItems.size(); i++) {
            if (inputItems.get(i) == null) {
                System.err.println("item at index " + i + " is null");
                continue;
            }
            int id = inputItems.get(i).get(1).id;
            if (map.get(id) != null) {
                System.err.println("Entity " + inputItems.get(i) + " ID conflicts with an existing ID: " + id);
            }
            map.put(id, inputItems.get(i));
            if (id > highestId) {
                highestId = id;
            }
        }
        System.out.println("\t(The highest item ID is: " + highestId + ")");
        System.out.print("\tID Gaps: ");
        //iterate over map
        for( Map.Entry<Integer, EntitySupplier> entry : map.entrySet()) {
            int id = entry.getKey();
            if(idMap.get(id) == null) {
                System.out.print(id + " ");
            }
        }
        System.out.println("");
        idMap.setList(map);
        return highestId;
    }

    public void initialize(List<EntitySupplier> inputBlocks) {
//        inputBlocks.add(ENTITY_ITEM_DROP);
        verifyEntityIds(inputBlocks);
        list = inputBlocks.toArray(new EntitySupplier[0]);
    }
}