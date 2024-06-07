package com.xbuilders.engine.rendering.chunk.mesh;

import com.xbuilders.engine.rendering.chunk.occlusionCulling.BoundingBoxMesh;
import com.xbuilders.engine.utils.math.AABB;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

public class CompactOcclusionMesh extends CompactMesh{

    long lastQueryTime = 0;
    BoundingBoxMesh boundingBox;
    int samplesPassedLastFrame = 0;
    boolean queried = false;
    int queryId;

    public CompactOcclusionMesh(AABB boundaries) throws IOException {
        this.samplesPassedLastFrame = 0;
        boundingBox = new BoundingBoxMesh(boundaries.min.x, boundaries.min.y, boundaries.min.z,
                boundaries.max.x, boundaries.max.y, boundaries.max.z);
        queryId = GL30.glGenQueries(); // Create an occlusion query
    }

    /*
The basic layout for query occlusion culling is:

1. Create the query (or queries).
2. Render loop:
	a. Do AI / physics etc...
	b. Rendering:
		i. Check the query result from the previous frame.
		ii. Issue query begin:
			1. If the object was visible in the last frame:
				a. Enable rendering to screen.
				b. Enable or disable writing to depth buffer (depends on whether the object is translucent or opaque).
				c. Render the object itself.
			2. If the object wasn't visible in the last frame:
				a. Disable rendering to screen.
				b. Disable writing to depth buffer.
				c. "Render" the object's bounding box.
		iii. (End query)
		iv. (Repeat for every object in scene.)
	c. Swap buffers.
(End of render loop)
*/


    public void drawVisible(boolean wireframe) {
        if (queried)
            samplesPassedLastFrame = GL30.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT);// Get the query result
        if (samplesPassedLastFrame > 0) {
            boolean canQuery = System.currentTimeMillis() - lastQueryTime > 1000;
            if (canQuery) GL30.glBeginQuery(GL15.GL_SAMPLES_PASSED, queryId); //Start the occlusion query
            draw(wireframe);
            if (canQuery) {
                GL30.glEndQuery(GL15.GL_SAMPLES_PASSED); // End the occlusion query
                queried = true;
                lastQueryTime = System.currentTimeMillis();
            }
        }
    }

    public void drawInvisible() {
        if (samplesPassedLastFrame == 0) {
            GL30.glBeginQuery(GL15.GL_SAMPLES_PASSED, queryId); //Start the occlusion query
            boundingBox.render();
            GL30.glEndQuery(GL15.GL_SAMPLES_PASSED); // End the occlusion query
            queried = true;
        }
    }
}
