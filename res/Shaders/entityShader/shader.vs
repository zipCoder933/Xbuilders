#version 330 core

layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec2 uv_coords;

out vec2 UV;
out float normals;
uniform mat4 projViewMatrix;
uniform mat4 modelMatrix;
 
void main(){
    gl_Position =  projViewMatrix * modelMatrix * vec4(vertexPosition_modelspace,1);
    UV = uv_coords;
}