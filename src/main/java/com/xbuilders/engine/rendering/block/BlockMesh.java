/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.block;

import com.xbuilders.engine.rendering.Mesh;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.window.utils.obj.OBJ;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.obj.buffers.OBJBufferSet;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.lwjgl.opengl.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;

/**
 * @author zipCoder933
 */
public class BlockMesh extends Mesh {

    private final int vao;
    private final int positionVBO;
    private final int uvVBO;
    private int textureID;
    //    private final int normalVBO;
    private int vertLength;

    public boolean isEmpty() {
        return vertLength == 0;
    }

    public void reset() {
        vertLength = 0;
    }

    public BlockMesh() {
        vao = GL30.glGenVertexArrays();
        positionVBO = GL15.glGenBuffers();
        uvVBO = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionVBO);
        GL20.glVertexAttribPointer(0/*id*/, 3/*size*/, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVBO);
        GL20.glVertexAttribPointer(1/*id*/, 3/*size*/, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public void sendBuffersToGPU(float[] position, float[] uvs) {
        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, position, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvs, GL15.GL_STATIC_DRAW);

        vertLength = position.length / 3;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public void delete() {
        GL30.glDeleteVertexArrays(vao);
        GL30.glDeleteBuffers(positionVBO);
        GL30.glDeleteBuffers(uvVBO);
    }

    public void draw(boolean wireframe) {
        GL30.glBindVertexArray(vao);

        if (wireframe) {
            GL11.glLineWidth(1); //Set the line width
            GL11.glBindTexture(GL33.GL_TEXTURE_2D_ARRAY, 0);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // Enable wireframe mode
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertLength); //We have to specify how many verticies we want
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Disable wireframe mode
        }

        GL11.glBindTexture(GL33.GL_TEXTURE_2D_ARRAY, textureID);//required to assign texture to mesh
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertLength); //We have to specify how many verticies we want
    }
}
