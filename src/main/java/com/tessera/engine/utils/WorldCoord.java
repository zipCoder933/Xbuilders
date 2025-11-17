package com.tessera.engine.utils;

@FunctionalInterface
public interface WorldCoord {
    /**
     * Gets a result.
     *
     * @return a result
     */
    boolean get(int wx, int wy, int wz);

}
