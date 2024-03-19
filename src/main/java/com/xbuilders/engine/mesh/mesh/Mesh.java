package com.xbuilders.engine.mesh.mesh;

import java.nio.IntBuffer;

public interface Mesh {

    public void setTextureID(int textureID);

    public void delete();

    public void draw(boolean wireframe);
}
