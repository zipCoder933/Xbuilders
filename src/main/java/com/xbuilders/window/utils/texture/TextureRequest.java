package com.xbuilders.window.utils.texture;

public class TextureRequest {
    String filepath;
    int regionX;
    int regionY;
    int regionWidth;
    int regionHeight;

    public TextureRequest(String path, int regionX, int regionY, int regionWidth, int regionHeight) {
        this.filepath = path;
        this.regionX = regionX;
        this.regionY = regionY;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
    }

    public TextureRequest(String filepath) {
        this.filepath = filepath;
        regionX = -1;
        regionY = -1;
        regionWidth = -1;
        regionHeight = -1;
    }


}
