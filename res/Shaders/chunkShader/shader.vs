#version 400 core
 
precision mediump float;
precision mediump int;

// Input vertex data, different for all executions of this shader.
layout(location = 0) in uvec3 vertex;
 
// Output data ; will be interpolated for each fragment.
out vec3 UV;
out float normal;
out float sun;
out float torch;
// out float torch;
out float fragDistance;
 
// Values that stay constant for the whole mesh.
uniform mat4 MVP;
uniform int animationTime;

uniform float maxMult12bits;
uniform float maxMult10bits;
uniform int textureLayerCount;
 
void main(){

    //FIRST INTEGER
    uint packedInt = uint(vertex.x);
    // Extract the vertex X value (12 bits) by shifting right by 20 bits
    float vertX = float((packedInt >> 20u) & 0xFFFu) / maxMult12bits;
    // Extract the vertex Y value (12 bits) by shifting right by 8 bits
    float vertY = float((packedInt >> 8u) & 0xFFFu) / maxMult12bits;
    // Extract the normals value (3 bits) by shifting right by 5 bits
    normal = int((packedInt >> 5u) & 0x7u);
    // Extract the animation value (5 bits) by masking with 0b11111
    uint animationSize = packedInt & 0x1Fu;


    //SECOND INTEGER
    float vertZ = float((uint(vertex.y) >> 20u) & 0xFFFu) / maxMult12bits;
    float u = float((uint(vertex.y) >> 10u) & 0x3FFu) / maxMult10bits;
    float v = float(uint(vertex.y) & 0x3FFu) / maxMult10bits;


    //THIRD INTEGER
    packedInt = uint(vertex.z);
    // Extract the texture value (16 bits) by shifting right by 16 bits
    uint textureID = (packedInt >> 16) & 0xFFFFu;

    // Extract the next 4 bits as sunlight and the rest of the bits as torchlight
    uint packedSun = (packedInt >> 4) & 0xFu;
    uint packedTorch = packedInt & 0xFu;
    sun = float(packedSun) / 15.0;
    torch = float(packedTorch) / 15.0;
  
    
    //----------------------------------------
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