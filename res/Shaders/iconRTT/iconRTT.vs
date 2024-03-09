#version 330 core
 
// Input vertex data, different for all executions of this shader.
layout(location = 0) in uvec3 vertex;
 
// Output data ; will be interpolated for each fragment.
out vec3 UV;
out float normals;
 
// Values that stay constant for the whole mesh.
uniform mat4 MVP;

uniform float maxMult12bits;
uniform float maxMult10bits;
uniform int textureLayerCount;
 
void main(){
    float vertX = float((uint(vertex.x) >> 20u) & 0xFFFu) / maxMult12bits;
    float vertY = float((uint(vertex.x) >> 8u) & 0xFFFu) / maxMult12bits;
    int normalsAO = int(vertex.x & 0xFFu);

    float vertZ = float((uint(vertex.y) >> 20u) & 0xFFFu) / maxMult12bits;
    float u = float((uint(vertex.y) >> 10u) & 0x3FFu) / maxMult10bits;
    float v = float(uint(vertex.y) & 0x3FFu) / maxMult10bits;

    float textureID = float((uint(vertex.z) >> 16u) & 0xFFFFu);
    float light = float(uint(vertex.z) & 0xFFFFu);

    // float vertY = (vertex.x >> 18u) & 0x1FFu;
    // float vertX = (vertex.x >> 9u) & 0x1FFu;
    // float vertZ = (vertex.x) & 0x1FFu;

    // uint v = (vertex.y >> 18u) & 0x1FFu;
    // uint u = (vertex.y >> 9u) & 0x1FFu;
    // uint textureID = (vertex.y) & 0x1FFu;


    
    float type = floor(textureID - 0.5f);//-1+0.5
    float textureLayer = float(max(0,min(textureLayerCount, type)));

    // Output position of the vertex, in clip space : MVP * position
    gl_Position =  MVP * vec4(vertX,vertY,vertZ,1);
    UV = vec3(u,v,textureLayer);
    normals = normalsAO;
}