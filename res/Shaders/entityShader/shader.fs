#version 330 core
 
in vec2 UV;
in float normals;
out vec4 color;
float sun;
float torch;
uniform sampler2D tex;
 
void main(){
   vec4 val = texture( tex, UV );
   if(val.a == 0.0){//ditch transparent fragments
      discard;
   }
   // if(normals == 1.0f) val *= 0.9;
   // if(normals == 2.0f) val *= 0.8;
   // if(normals == 3.0f) val *= 0.7;
   // if(normals == 4.0f) val *= 0.6;
   // if(normals == 5.0f) val *= 0.5;
   color = val;
   // color = vec4(UV.x,UV.y,0.0,1.0);
   // color = vec4(1.0, 1.0, 1.0, 1.0);
}