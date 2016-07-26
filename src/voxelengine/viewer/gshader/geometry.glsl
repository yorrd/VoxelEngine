#version 400

// already matrix-transformed before sent to the shader
uniform vec4 blf;
uniform vec4 blb;
uniform vec4 brf;
uniform vec4 brb;
uniform vec4 tlf;
uniform vec4 tlb;
uniform vec4 trf;
uniform vec4 trb;

layout(points) in;
//in vec3 viewer;

layout(triangle_strip, max_vertices = 36) out;
out vec2 texture_coord;

void main() {

    vec4 p = gl_in[0].gl_Position;

    gl_Position = p + blf;
    texture_coord = vec2(1, 0);
    EmitVertex();
    gl_Position = p + trf;
    texture_coord = vec2(1, 1);
    EmitVertex();
    gl_Position = p + tlf;
    texture_coord = vec2(0, 1);
    EmitVertex();
    EndPrimitive();

    gl_Position = p + blf;
    texture_coord = vec2(1, 0);
    EmitVertex();
    gl_Position = p + trf;
    texture_coord = vec2(1, 1);
    EmitVertex();
    gl_Position = p + brf;
    texture_coord = vec2(0, 1);
    EmitVertex();
    EndPrimitive();
}
