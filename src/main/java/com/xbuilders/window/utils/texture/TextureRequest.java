package com.xbuilders.window.utils.texture;

import com.xbuilders.engine.utils.FileUtils;
import com.xbuilders.engine.utils.ResourceLoader;
import com.xbuilders.engine.utils.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TextureRequest {
    ByteBuffer image;
    int regionX;
    int regionY;
    int regionWidth;
    int regionHeight;

    public TextureRequest(ByteBuffer buffer,
                          int regionX, int regionY, int regionWidth, int regionHeight) {
        image = buffer;
        this.regionX = regionX;
        this.regionY = regionY;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
    }

    public TextureRequest(ByteBuffer buffer) {
        image = buffer;
        regionX = -1;
        regionY = -1;
        regionWidth = -1;
        regionHeight = -1;
    }

    private final static ResourceLoader resourceLoader = new ResourceLoader();

    public TextureRequest(String resource,
                          int regionX, int regionY, int regionWidth, int regionHeight) throws IOException {

        image = StreamUtils.toByteBuffer(resourceLoader.getResourceAsStream(resource));
        this.regionX = regionX;
        this.regionY = regionY;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
    }

    public TextureRequest(String resource) throws IOException {

        image = StreamUtils.toByteBuffer(resourceLoader.getResourceAsStream(resource));
        regionX = -1;
        regionY = -1;
        regionWidth = -1;
        regionHeight = -1;
    }


}
