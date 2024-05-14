package com.xbuilders.window.demos;

import com.xbuilders.window.render.Shader;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicShader extends Shader {

    static final String vertShader = """
            #version 330 core
            layout(location = 0) in vec3 vertexPosition_modelspace;
            layout(location = 1) in vec2 vertexUV;
             
            out vec2 UV;
            uniform mat4 MVP;
             
            void main(){
                gl_Position =  MVP * vec4(vertexPosition_modelspace,1);
                UV = vertexUV;
            }
            """;

    static final String fragShader = """
            #version 330 core
             
            in vec2 UV;
            out vec4 color;
            uniform sampler2D myTextureSampler;
            uniform vec4 colorToSet;
             
            void main(){
                color=colorToSet;
                //color = texture( myTextureSampler, UV ).rgb;
            }
            """;

    public final int mvpUniform, colorUniform;

    public BasicShader() throws IOException {
        super(vertShader, fragShader);
        mvpUniform = getUniformLocation("MVP");
        colorUniform = getUniformLocation("colorToSet");
    }
}
