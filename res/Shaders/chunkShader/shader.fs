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
in float normal;
in float sun;
in float torch;
in float fragDistance;

// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform sampler2DArray textureArray;
uniform int viewDistance;
uniform vec3 skyColor;

void main() {
    vec4 val = texture(textureArray, UV);
    if (val.a == 0.0) {  // ditch transparent fragments
        discard;
    } else if (val.r == 0.0 && val.g == 0.0 && val.b == 0.0) {
        color = vec4(0.0,0.0,0.0, 1.0);
        // color=vec4(skyColor,1.0);
        return;
    }
    
    //Light
    // POS_X = 0;
    // NEG_X = 1;
    // POS_Z = 2;
    // NEG_Z = 3;
    // NEG_Y = 4;
    // POS_Y = 5;
    float sun2 = sun;
    if (normal == 2.0f) sun2 *= 0.9;
    if (normal == 1.0f) sun2 *= 0.85;
    if (normal == 3.0f) sun2 *= 0.8;
    if (normal == 5.0f) sun2 *= 0.7;
    val.rgb *= max(torch,sun2);

    //Fog visiblity
    float visibility = 1.0;
    float viewGradient = 128;
    if (fragDistance > viewDistance - viewGradient) {  // Fog
        visibility = (fragDistance - viewDistance + (viewGradient / 2)) / (viewGradient / 2);
        visibility = clamp(1 - visibility, 0.0, 1.0);
    }
    color = mix(vec4(skyColor, 1.0), val, visibility);



//      color = vec4(sun,sun,sun,1.0);
    // // X is red, Y is green, Z is blue
    // if (normal == 0.0f) color = vec4(1.0, 0.0, 0.0, 1.0);  // positive x
    // if (normal == 1.0f) color = vec4(0.5, 0.0, 0.0, 1.0);  // negative x

    // if (normal == 2.0f) color = vec4(0.0, 0.0, 1.0, 1.0);  // positive z
    // if (normal == 3.0f) color = vec4(0.0, 0.0, 0.5, 1.0);  // negative z

    // if (normal == 4.0f) color = vec4(0.0, 1.0, 0.0, 1.0);  // positive y
    // if (normal == 5.0f) color = vec4(0.0, 0.5, 0.0, 1.0);  // negative y
}