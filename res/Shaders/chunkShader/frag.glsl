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
in vec3 position;

// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform sampler2DArray textureArray;
uniform int viewDistance;
uniform vec3 skyColor;
uniform float flashlightDistance;
uniform vec4 solidColor;

void main() {
    if (solidColor.a > 0.0) {
        color = solidColor;
        return;
    }

    vec4 val = texture(textureArray, UV);
    if (val.a == 0.0) {  // ditch transparent fragments
        discard;
    }

    // Light
    //  POS_X = 0;
    //  NEG_X = 1;
    //  POS_Z = 2;
    //  NEG_Z = 3;
    //  NEG_Y = 4;
    //  POS_Y = 5;
    float flashlight = 0;
    float fragDistance = length(position);

    if (fragDistance < flashlightDistance) {
        flashlight = map(fragDistance, 0, flashlightDistance, 1, 0);
    }

    //Normal lighting
    float sun2 = sun;
    if (normal == 2.0f) sun2 *= 0.9;
    if (normal == 1.0f) sun2 *= 0.85;
    if (normal == 3.0f) sun2 *= 0.8;
    if (normal == 5.0f) sun2 *= 0.7;

    val.rgb *= max(flashlight, max(torch, sun2));

    // Fog visiblity
    float fogGradient = 16; //The length of the fog gradient
    float fogStart = viewDistance - fogGradient; //Start of fog
    float visibility = 1.0;
    if (fragDistance > fogStart) {
        visibility = map(fragDistance, fogStart, fogStart + fogGradient, 1.0, 0.0);
        visibility = clamp(visibility, 0.01, 1.0);
    }

    //The final color
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