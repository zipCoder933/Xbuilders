package com.xbuilders.window.utils.texture;

public class TextureFile {
    String filepath;
    int regionX;
    int regionY;
    int regionWidth;
    int regionHeight;

    public TextureFile(String filepath, int regionX, int regionY, int regionWidth, int regionHeight) {
        this.filepath = filepath;
        this.regionX = regionX;
        this.regionY = regionY;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
    }

    public TextureFile(String filepath) {
        this.filepath = filepath;
        regionX = -1;
        regionY = -1;
        regionWidth = -1;
        regionHeight = -1;
    }
}
