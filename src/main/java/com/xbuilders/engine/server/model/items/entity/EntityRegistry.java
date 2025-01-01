/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.model.items.entity;

import com.xbuilders.engine.utils.IntMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.xbuilders.engine.utils.ArrayUtils.combineArrays;

/**
 * @author zipCoder933
 */
public class EntityRegistry {

    //Predefined entities
    public static EntitySupplier ENTITY_ITEM_DROP;

    final IntMap<EntitySupplier> idMap = new IntMap<>(EntitySupplier.class);
    public HashMap<String, Short> aliasToIDMap;
    private EntitySupplier[] list;
    public List<EntitySupplier> autonomousList;
    public static final int NULL_ID = -1;

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

    public void setup(List<EntitySupplier> inputEntities) {
        HashSet<String> uniqueAliases = new HashSet<>();
        ENTITY_ITEM_DROP = new EntitySupplier(NULL_ID, (uniqueID2) -> new ItemDrop(NULL_ID, uniqueID2));
        inputEntities.add(ENTITY_ITEM_DROP);

        verifyEntityIds(inputEntities);
        list = inputEntities.toArray(new EntitySupplier[0]);
        //Now make a list of autonomous entities
        autonomousList = new java.util.ArrayList<>();
        for (EntitySupplier entity : list) {
            if (entity.isAutonomous) {
                autonomousList.add(entity);
            }
        }
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
        for (Map.Entry<Integer, EntitySupplier> entry : map.entrySet()) {
            int id = entry.getKey();
            if (idMap.get(id) == null) {
                System.out.print(id + " ");
            }
        }
        System.out.println("");
        idMap.setList(map);
        return highestId;
    }


}