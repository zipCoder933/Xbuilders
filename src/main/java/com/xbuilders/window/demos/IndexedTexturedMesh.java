/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.window.demos;

import com.xbuilders.window.render.MVP;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * @author Patron
 */
public class IndexedTexturedMesh {

    int vertBuffer;
    int uvBuffer;
    int indiciesBuffer;
    Vector4f color = new Vector4f(1, 1, 1, 1);
    public final int vao;
    private int textureID, vertLength;
    private MVP mvp;
    BasicShader shader;


    public IndexedTexturedMesh() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao); //Every mesh should have its own VAO

        try {
            shader = new BasicShader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vertBuffer = GL30.glGenBuffers();
        uvBuffer = GL30.glGenBuffers();
        indiciesBuffer = GL30.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertBuffer);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT,
                false, // normalized?
                0, // stride
                0 // array buffer offset (no offset in this case)
        );
        GL20.glEnableVertexAttribArray(vertBuffer);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvBuffer);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT,
                false, // normalized?
                0, // stride
                0 // array buffer offset (no offset in this case)
        );
        GL20.glEnableVertexAttribArray(uvBuffer);

        mvp = new MVP();
        GL30.glBindVertexArray(0);
    }

    public void updateMVP(Matrix4f mat) {
        GL30.glBindVertexArray(vao);
        this.mvp.update(mat);
        mvp.sendToShader(shader.getID(), shader.mvpUniform);
    }

    /**
     * @param textureID the textureID to set
     */
    public void setTextureID(int textureID) {
        this.textureID = textureID;
        color = null;
    }

    public void setColor(Vector4f color) {
        if (color == null) color = new Vector4f(1, 1, 1, 1);
        color.set(color);
        textureID = 0;
        shader.loadVec4f(shader.colorUniform, color);
    }

    public void sendBuffersToGPU(float[] g_vertex_buffer_data, float[] g_uv_buffer_data, int[] indicies) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, g_vertex_buffer_data, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, g_uv_buffer_data, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiciesBuffer);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicies, GL15.GL_STATIC_DRAW);
        vertLength = indicies.length;
    }

    public void draw() {
        GL30.glBindVertexArray(vao);
        shader.bind();
        if (textureID != 0) GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);//required to assign texture to mesh
        //and to draw the mesh, simply replace glDrawArrays by this...
        // Draw the triangles !
        GL15.glDrawElements(
                GL11.GL_TRIANGLES, // mode
                vertLength, // count
                GL11.GL_UNSIGNED_INT, // type
                0L // element array buffer offset (long 0)
        );
        GL30.glBindVertexArray(0);
    }

}
