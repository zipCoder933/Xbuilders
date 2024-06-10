package com.xbuilders.engine.rendering.chunk.mesh;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.rendering.chunk.occlusionCulling.BoundingBoxMesh;
import com.xbuilders.engine.rendering.chunk.occlusionCulling.EmptyShader;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.window.render.MVP;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class CompactOcclusionMesh extends CompactMesh {

    long lastQueryTime = 0;
    BoundingBoxMesh boundingBox;
    int samplesPassedLastFrame = 0;
    boolean queried = false;
    int queryId;


    public boolean isVisible() {
        return samplesPassedLastFrame > 0;
    }

    public CompactOcclusionMesh() {
        this.samplesPassedLastFrame = 0;
        boundingBox = new BoundingBoxMesh();
        queryId = GL30.glGenQueries(); // Create an occlusion query

        if (boundaryShader == null) { //Initialize the boundary shader
            boundaryShader = new EmptyShader();
        }
    }

    public void init(AABB boundaries) {
        boundingBox.setBounds(boundaries.min.x, boundaries.min.y, boundaries.min.z,
                boundaries.max.x, boundaries.max.y, boundaries.max.z);
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


    public void getQueryResult(){
        if (queried)
            samplesPassedLastFrame = GL30.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT);// Get the query result
    }

    public void drawVisible(boolean wireframe) {
        if (samplesPassedLastFrame > 0 && !isEmpty()) {
            boolean canQuery = System.currentTimeMillis() - lastQueryTime > 1000;
            if (canQuery) GL30.glBeginQuery(GL15.GL_SAMPLES_PASSED, queryId); //Start the occlusion query
            super.draw(wireframe);
            if (canQuery) {
                GL30.glEndQuery(GL15.GL_SAMPLES_PASSED); // End the occlusion query
                queried = true;
                lastQueryTime = System.currentTimeMillis();
            }
        }
    }

    public void drawBoundingBoxWithWireframe() {
        boundaryShader.bind();
        boundaryMVP.update(GameScene.projection, GameScene.view);
        boundaryMVP.sendToShader(boundaryShader.getID(), boundaryShader.mvpUniform);
        boundingBox.renderWireframe();
        boundaryShader.unbind();
    }

    private static EmptyShader boundaryShader;
    final static MVP boundaryMVP = new MVP();

    public static void startInvisible() {
        boundaryShader.bind();
        boundaryMVP.update(GameScene.projection, GameScene.view);
        boundaryMVP.sendToShader(boundaryShader.getID(), boundaryShader.mvpUniform);
        //Disable depth mask and color mask
        GL30.glDepthMask(false);
        GL30.glColorMask(false, false, false, false);
//        GL30.glDisable(GL30.GL_DEPTH_TEST);//Disable depth test
        GL30.glDisable(GL30.GL_CULL_FACE);  //Disable backface culling
    }

    public static void endInvisible() {
        boundaryShader.unbind();
//        GL30.glEnable(GL30.GL_DEPTH_TEST);  //Enable depth test
        GL30.glDepthMask(true);
        GL30.glColorMask(true, true, true, true);
        GameScene.enableBackfaceCulling(); //Enable backface culling
    }

    public void drawInvisible() {
        /**
         * If we are inside the bounding box, the object is not visible becuase backface culling is enabled.
         * A few solutions?
         *  * If the player distance is less than chunk width (gives us a bit of margin)
         *  * If the boundaries of the player head are within the chunkAABB (could have edge cases)
         *  * Disable backface culling if we are drawing bounding boxes
         */
        if (samplesPassedLastFrame == 0) {
//            boolean canQuery = System.currentTimeMillis() - lastQueryTime > 500; //For testing purposes
//            if (canQuery) lastQueryTime = System.currentTimeMillis();
//            else return;

            GL30.glBeginQuery(GL15.GL_SAMPLES_PASSED, queryId); //Start the occlusion query
            boundingBox.render();
            GL30.glEndQuery(GL15.GL_SAMPLES_PASSED); // End the occlusion query
            queried = true;
        }
    }

    public String toString() {
        return "Samples: " + samplesPassedLastFrame
                +"; Last query ms: " + (System.currentTimeMillis() - lastQueryTime)
                +"; Empty: "+isEmpty();
    }
}
