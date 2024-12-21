package com.xbuilders.engine.utils;

import com.xbuilders.engine.items.entity.Entity;

@FunctionalInterface
public interface WorldCoord {
    /**
     * Gets a result.
     *
     * @return a result
     */
    boolean get(int wx, int wy, int wz);

}
