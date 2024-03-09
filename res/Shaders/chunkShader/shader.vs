#version 400 core
 
precision mediump float;
precision mediump int;

// Input vertex data, different for all executions of this shader.
layout(location = 0) in uvec3 vertex;
 
// Output data ; will be interpolated for each fragment.
out vec3 UV;
out float normals;
out float fragDistance;
 
// Values that stay constant for the whole mesh.
uniform mat4 MVP;
uniform int animationTime;

uniform float maxMult12bits;
uniform float maxMult10bits;
uniform int textureLayerCount;
 
void main(){
    //FIRST INTEGER
    float vertX = float((uint(vertex.x) >> 20u) & 0xFFFu) / maxMult12bits;
    float vertY = float((uint(vertex.x) >> 8u) & 0xFFFu) / maxMult12bits;
    normals = int(vertex.x & 0xFFu);

    //SECOND INTEGER
    float vertZ = float((uint(vertex.y) >> 20u) & 0xFFFu) / maxMult12bits;
    float u = float((uint(vertex.y) >> 10u) & 0x3FFu) / maxMult10bits;
    float v = float(uint(vertex.y) & 0x3FFu) / maxMult10bits;

    //THIRD INTEGER
    uint textureID = (uint(vertex.z) >> 16) & 0xFFFF;          // Extract first 16 bits for texture
    uint animationSize = uint(vertex.z) & 0xFFFF; //Extract the second 16 bits for the animation size
    if(animationSize > 1){
          int maxTicks = int(animationTime / animationSize);
        textureID += animationTime - (animationSize * maxTicks);
    }
    
    float type = floor(textureID - 0.5f);//-1+0.5
    float textureLayer = float(max(0,min(textureLayerCount, type)));

    // Output position of the vertex, in clip space : MVP * position
    gl_Position =  MVP * vec4(vertX,vertY,vertZ,1);
    fragDistance = length(gl_Position.xyz);
    UV = vec3(u,v,textureLayer);
}