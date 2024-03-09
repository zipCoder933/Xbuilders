/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.utils.math.AABB;

/**
 *
 * @author zipCoder933
 */
public class CollisionCandidate {

        public AABB aabb;
        public boolean isEntity;

        public CollisionCandidate(AABB aabb, boolean isEntity) {
            this.aabb = aabb;
            this.isEntity = isEntity;
        }
}
