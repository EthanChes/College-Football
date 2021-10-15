package graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    public void setUniform(String name, int value) {
        int location = glGetUniformLocation(program, name);
        if (location != -1) { // returns -1 if invalid
            glUniform1i(location, value);
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
