///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.engine.mesh.old;
//
//import com.xbuilders.window.render.VBO;
//import java.util.ArrayList;
//import org.joml.Vector3f;
//import org.lwjgl.opengl.GL11;
//import static org.lwjgl.opengl.GL11.GL_FILL;
//import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
//import static org.lwjgl.opengl.GL11.GL_LINE;
//import static org.lwjgl.opengl.GL11.glPolygonMode;
//import org.lwjgl.opengl.GL15;
//import org.lwjgl.opengl.GL20;
//import org.lwjgl.opengl.GL33;
//
///**
// *
// * @author zipCoder933
// */
//public class Mesh {
//
//    VBO vertBuffer;
//    VBO uvBuffer;
//    private int textureID, vertLength;
//
//    public Mesh() {
//        vertBuffer = new VBO();
//        uvBuffer = new VBO();
//    }
//
//    /**
//     * @param textureID the textureID to set
//     */
//    public void setTextureID(int textureID) {
//        this.textureID = textureID;
//    }
//
//    public void sendBuffersToGPU(float[] g_vertex_buffer_data, float[] g_uv_buffer_data) {
//        vertBuffer.sendToGPU(g_vertex_buffer_data);
//        uvBuffer.sendToGPU(g_uv_buffer_data);
//        vertLength = g_vertex_buffer_data.length / 3;
//    }
//
//    public void delete() {
//        vertBuffer.delete();
//        uvBuffer.delete();
//        vertBuffer = null;
//        uvBuffer = null;
//    }
//
//    public boolean empty;
//    
//    public void draw(boolean wireframe) {
////        vertBuffer.bindToVAO(0, 3, GL11.GL_FLOAT);
////        uvBuffer.bindToVAO(1, 3, GL11.GL_FLOAT);
//
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertBuffer.bufferID);
//        GL20.glEnableVertexAttribArray(0); //Enables the vertex attribute array at index 0.
//        GL20.glVertexAttribPointer( //Specifies how the data in the buffer is to be interpreted. In this case, it configures attribute 0 to expect 3 floats.
//                0, // attribute 0. No particular reason for 0, but must match the layout in the shader.
//                3, // size
//                GL11.GL_FLOAT, // type
//                false, 0, 0
//        );
//
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvBuffer.bufferID);
//        GL20.glEnableVertexAttribArray(1); //Enables the vertex attribute array at index 0.
//        GL20.glVertexAttribPointer( //Specifies how the data in the buffer is to be interpreted. In this case, it configures attribute 0 to expect 3 floats.
//                1, // attribute 0. No particular reason for 0, but must match the layout in the shader.
//                3, // size
//                GL11.GL_FLOAT, // type
//                false, 0, 0
//        );
//
//        if (wireframe) {
//            GL11.glLineWidth(2); //Set the line width
//            GL11.glBindTexture(GL33.GL_TEXTURE_2D_ARRAY, 0);
//            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // Enable wireframe mode
//            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertLength); //We have to specify how many verticies we want
//            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Disable wireframe mode
//        }
//
//        GL11.glBindTexture(GL33.GL_TEXTURE_2D_ARRAY, textureID);//required to assign texture to mesh
//        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertLength); //We have to specify how many verticies we want
//    }
//}
