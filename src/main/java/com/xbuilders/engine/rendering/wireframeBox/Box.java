/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.wireframeBox;

import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.render.MVP;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * @author zipCoder933
 */

//TODO: The box drawing is a major drawing bottleneck
public class Box {

    /**
     * @param lineWidth the lineWidth to set
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    int vertBuffer;
    SolidColorShader shader;
    private float lineWidth = 1.0f;
    private MVP mvp;
    public final Matrix4f position = new Matrix4f();
    float[] vertices;
    int vao;


    public void setPosition(Vector3f pos) {
        position.set(0, 0, 0).translation(pos);
    }

    public Box() {
        vao = GL30.glGenVertexArrays();
        shader = new SolidColorShader();
        vertBuffer = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertBuffer);
        GL20.glVertexAttribPointer(
                0, 3, GL11.GL_FLOAT,
                false, // normalized?
                0, // stride
                0 // array buffer offset (no offset in this case)
        );
        GL20.glEnableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        mvp = new MVP();
    }

    public void setColor(Vector4f color) {
        shader.setColor(color);
    }


    public void set(AABB box) {
        setSize(
                box.max.x - box.min.x,
                box.max.y - box.min.y,
                box.max.z - box.min.z);

        position.identity().translation(box.min);
    }

    public void setPosAndSize(float startX, float startY, float startZ, float lengthX, float lengthY, float lengthZ) {
        setSize(lengthX, lengthY, lengthZ);
        position.identity().translation(startX, startY, startZ);
    }

    public void set(Vector3i start, Vector3i end) {
        setSize(end.x - start.x, end.y - start.y, end.z - start.z);
        position.identity().translation(start.x, start.y, start.z);
    }

    float MAX_z, MAX_x, MAX_y;

    public void setPosition(float x, float y, float z) {
        position.identity().translation(x, y, z);
    }

    public void setSize(
            float maxX, float maxY, float maxZ) {
        if (vertices == null
                || maxX != MAX_x
                || maxY != MAX_y
                || maxZ != MAX_z) {
            MAX_x = maxX;
            MAX_y = maxY;
            MAX_z = maxZ;

            vertices = new float[]{
                    0, 0, 0, // V0
                    MAX_x, 0, 0, // VMAX
                    MAX_x, MAX_y, 0, // V2
                    0, MAX_y, 0, // V3
                    0, 0, MAX_z, // V4
                    MAX_x, 0, MAX_z, // V5
                    MAX_x, MAX_y, MAX_z, // V6
                    0, MAX_y, MAX_z, // V7
                    0, 0, 0, // V8
                    0, 0, MAX_z, // V9
                    MAX_x, 0, 0, // VMAXMIN
                    MAX_x, 0, MAX_z, // VMAXMAX
                    MAX_x, MAX_y, 0, // VMAX2
                    MAX_x, MAX_y, MAX_z, // VMAX3
                    0, MAX_y, 0, // VMAX4
                    0, MAX_y, MAX_z, // VMAX5
                    0, 0, 0, // VMAX6
                    MAX_x, 0, 0, // VMAX7
                    0, 0, MAX_z, // VMAX8
                    MAX_x, 0, MAX_z // V19
            };
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertBuffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        }
    }

    public void draw(Matrix4f projection, Matrix4f view) {
        GL30.glBindVertexArray(vao);
        //Set line mode
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // Enable wireframe mode
        glEnable(GL_CULL_FACE); // Enable face culling
        mvp.update(projection, view, position);
        mvp.sendToShader(shader.getID(), shader.mvpUniform);
        shader.bind();

        BaseWindow.printDebugsEnabled(false);
        //Line width cannot be higher than 1, wide lines are depracated
        GL11.glLineWidth(lineWidth); //Set the line width
        BaseWindow.printDebugsEnabled(true);

        // Draw each line segment individually
        glDrawArrays(GL_LINE_LOOP, 0, 4); // Draw the four edges of the front face
        glDrawArrays(GL_LINE_LOOP, 4, 4); // Draw the four edges of the back face
        glDrawArrays(GL_LINES, 8, 8); // Draw the connectors
        glDrawArrays(GL_LINES, 16, 4);

//DONT disable GL_CULL_FACE. chunks need CULL_FACE to do backface culling
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

}
