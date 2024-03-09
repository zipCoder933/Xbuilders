#version 400 core
// #ifdef GL_ES
// precision mediump float;
// precision mediump int;
// #endif

// METHODS =============================================================================
//=====================================================================================
float map(float value, float inMin, float inMax, float outMin, float outMax) {
    return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

vec2 map(vec2 value, vec2 inMin, vec2 inMax, vec2 outMin, vec2 outMax) {
    return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

vec3 map(vec3 value, vec3 inMin, vec3 inMax, vec3 outMin, vec3 outMax) {
    return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

vec4 map(vec4 value, vec4 inMin, vec4 inMax, vec4 outMin, vec4 outMax) {
    return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}
 
// Interpolated values from the vertex shaders
in vec3 UV;
in float normals;
in float fragDistance;

// Ouput data
out vec4 color;
 
// Values that stay constant for the whole mesh.
uniform sampler2DArray textureArray;
uniform int viewDistance;
uniform vec3 skyColor;
 
void main(){
   vec4 val = texture( textureArray, UV );
   if(val.a == 0.0){//ditch transparent fragments
      discard;
   }
   if(normals == 1.0f) val *= 0.9;
   if(normals == 2.0f) val *= 0.85;
   if(normals == 3.0f) val *= 0.8;
   if(normals == 4.0f) val *= 0.75;
   if(normals == 5.0f) val *= 0.7;


   float visibility = 1.0;
   float viewGradient = 128;
   if (fragDistance > viewDistance - viewGradient) { //Fog
      visibility = (fragDistance - viewDistance + (viewGradient/2)) / (viewGradient/2);
      visibility = clamp(1 - visibility, 0.0, 1.0);
   }
   
   color = mix(vec4(skyColor, 1.0), val, visibility);
   // color = val;
   // color = vec4(map(fragDistance,0,viewDistance2,0,1),0.0,0.0,1.0);
}