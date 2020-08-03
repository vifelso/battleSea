precision mediump float;

varying lowp vec4 vColor;
varying vec2 vTexCoord;

uniform sampler2D u_texture;
uniform float halfwidth;

void main()
{
  vec3 color = texture2D(u_texture, vTexCoord).rgb;
  if (gl_FragCoord.x > halfwidth)
  {
    gl_FragColor = vColor * texture2D(u_texture, vTexCoord);
  }
  else if (color.g > 0.7)
  {
    vec3 colorswapped = vec3(color.g, color.b, color.r);
    gl_FragColor = vec4(colorswapped, texture2D(u_texture, vTexCoord).a);
  }
  else
  {
    float gray = (color.r + color.g + color.b) / 6.0;
    vec3 grayscale = vec3(gray);
    gl_FragColor = vec4(grayscale, texture2D(u_texture, vTexCoord).a);
  }
}