#version 330

uniform sampler2D tex;
uniform vec2 tex_size;

in vec2 texture_coord;

layout(location = 0) out vec4 out_color;

void main()
{
    out_color = texture(tex, texture_coord);
}
