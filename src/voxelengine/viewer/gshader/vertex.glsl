#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 viewerPosition;

layout(location = 0) in vec4 in_position;
out vec3 viewer;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_position;
    viewer = normalize(projectionMatrix * vec4(viewerPosition, 1) - gl_Position).xyz;
}
