package com.xbuilders.engine.rendering;

public interface Mesh {

    public void setTextureID(int textureID);

    public void delete();

    public void draw(boolean wireframe);
}
