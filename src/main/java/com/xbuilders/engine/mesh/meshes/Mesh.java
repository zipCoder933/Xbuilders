package com.xbuilders.engine.mesh.meshes;

import java.nio.IntBuffer;

public interface Mesh {

    public void setTextureID(int textureID);


    public void delete();

    public void draw(boolean wireframe);
}
