package com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet;

import com.xbuilders.engine.rendering.chunk.mesh.CompactMesh;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.ResizableIntArray;

public class VertexSet_ResizableIntArray extends VertexSet {


    private final ResizableIntArray verts = new ResizableIntArray(6000);

    public int size() {
        return verts.size();
    }

    public int[] makeVertexSet() {     //The main contributor to the memory usage is the IntBuffer that gets created here
        return verts.getArray();
    }


    public void reset() {
        verts.clear();
    }

    @Override
    public void sendToMesh(CompactMesh mesh) {
        if(verts.size() == 0) {
            return;
        }
        mesh.sendBuffersToGPU(verts.getArray(), verts.size());
        reset();
    }
    //</editor-fold>

    public void vertex(int layer, int a, int b, int c) {
        verts.add(a);
        verts.add(b);
        verts.add(c);
    }

}
