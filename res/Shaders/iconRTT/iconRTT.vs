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
 
    // FIRST INTEGER
    uint packedInt = uint(vertex.x);
    // Extract the vertex X value (12 bits) by shifting right by 20 bits
    //The subtraction in the vertex positions are part of the unpacking process (see comment in vertexSet class in source code)
    float vertX = (float((packedInt >> 20u) & 0xFFFu) / maxMult12bits) - 1; 
    // Extract the vertex Y value (12 bits) by shifting right by 8 bits
    float vertY = (float((packedInt >> 8u) & 0xFFFu) / maxMult12bits) - 1;
    // Extract the normals value (3 bits) by shifting right by 5 bits
    // normal = int((packedInt >> 5u) & 0x7u);

    // SECOND INTEGER
    float vertZ = (float((uint(vertex.y) >> 20u) & 0xFFFu) / maxMult12bits) - 1;
    float u = float((uint(vertex.y) >> 10u) & 0x3FFu) / maxMult10bits;
    float v = float(uint(vertex.y) & 0x3FFu) / maxMult10bits;

    // THIRD INTEGER
    packedInt = uint(vertex.z);
    // Extract the texture value (16 bits) by shifting right by 16 bits
    uint textureID = (packedInt >> 16) & 0xFFFFu;

    
    float type = floor(textureID - 0.5f);//-1+0.5
    float textureLayer = float(max(0,min(textureLayerCount, type)));

    // Output position of the vertex, in clip space : MVP * position
    gl_Position =  MVP * vec4(vertX,vertY,vertZ,1);
    UV = vec3(u,v,textureLayer);
}