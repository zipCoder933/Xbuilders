package com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet;

import com.xbuilders.engine.rendering.chunk.mesh.CompactMesh;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.ResizableIntArray;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.ResizableIntBuffer;

public class VertexSet_ResizableIntBuffer extends VertexSet {


    private final ResizableIntBuffer verts = new ResizableIntBuffer(6000);

    public int size() {
        return verts.size();
    }

    public void makeVertexSet() {     //The main contributor to the memory usage is the IntBuffer that gets created here

    }


    public void reset() {
        verts.clear();
    }

    @Override
    public void sendToMesh(CompactMesh mesh) {
        if(verts.size() == 0) {
            return;
        }
        verts.getBuffer().clear();
        mesh.sendBuffersToGPU(verts.getBuffer(), verts.size());
        reset();
    }
    //</editor-fold>

    public void vertex(int layer, int a, int b, int c) {
        verts.add(a);
        verts.add(b);
        verts.add(c);
    }

}
