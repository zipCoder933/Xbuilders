/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.rendering.rect;

import com.xbuilders.window.demos.IndexedTexturedMesh;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import org.lwjgl.opengl.GL33;

/**
 *
 * @author zipCoder933
 */
public class Rect {

    // Vertex Buffer
    float[] vertices = {
        0.0f, 0.5f, -0.5f, // Top-left
        0.0f, -0.5f, -0.5f, // Bottom-left
        0.0f, -0.5f, 0.5f, // Bottom-right
        0.0f, 0.5f, 0.5f, // Top-right
    };

    // UV Buffer (assuming full texture coverage)
    float[] uv = {
        0.0f, 1.0f, // Top-left
        0.0f, 0.0f, // Bottom-left
        1.0f, 0.0f, // Bottom-right
        1.0f, 1.0f // Top-right
    };

    static final int[] indicies = {0, 1, 2, 2, 3, 0};
    public IndexedTexturedMesh quad;

    public Rect() {
        quad = new IndexedTexturedMesh();
        quad.sendBuffersToGPU(vertices, uv, indicies);
    }

    public void setTextureID(int texID) {
        quad.setTextureID(texID);
    }

    public void draw(boolean wireframe) {
        if (true) {
            GL11.glLineWidth(2); //Set the line width
            GL11.glBindTexture(GL33.GL_TEXTURE_2D_ARRAY, 0);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // Enable wireframe mode
            quad.draw();
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Disable wireframe mode
        }
//        quad.draw();
    }

}
