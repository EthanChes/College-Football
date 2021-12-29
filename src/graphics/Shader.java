package graphics;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int program;
    private int vertex_shader;
    private int fragment_shader;

    public Shader(String filename) {
        program = glCreateProgram();

        vertex_shader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex_shader, readFile(filename+".vs"));
        glCompileShader(vertex_shader);
        glEnable(GL_TEXTURE_2D);

        // Check Shader Error
        if (glGetShaderi(vertex_shader, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(vertex_shader));
            System.exit(1);
        }

        fragment_shader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment_shader, readFile(filename+".fs"));
        glCompileShader(fragment_shader);

        if (glGetShaderi(fragment_shader, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(fragment_shader));
            System.exit(1);
        }

        glAttachShader(program, vertex_shader);
        glAttachShader(program, fragment_shader);

        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");

        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
    }

    protected void finalize() {
        /*glDetachShader(program, vertex_shader);
        glDetachShader(program,fragment_shader);
        glDeleteShader(vertex_shader);
        glDeleteShader(fragment_shader);
        glDeleteProgram(program);*/
    }

    public void setUniform(String uniformName, Vector4f value) {
        int location = glGetUniformLocation(program, uniformName);
        if (location != -1) {
            glUniform4f(location, value.x, value.y, value.z, value.w);
        }
    }

    public void setUniform(String name, Matrix4f value) {
        int location = glGetUniformLocation(program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        if (location != -1) { // returns -1 if invalid
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    private String readFile(String filename) {
        StringBuilder string = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(new File("./shaders/" + filename)));
            String line;
            while ((line = br.readLine()) != null) {
                string.append(line);
                string.append("\n");
            }
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return string.toString();
    }
}
