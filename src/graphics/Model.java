package graphics;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;

public class Model {
    private int draw_count;
    private int vertex_id;
    private int texture_id;
    private int i_id;
    public Model(float[] vertices, float[] tex_coords, int[] indices) {
        draw_count = indices.length;

        vertex_id = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER,vertex_id);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices) , GL_STATIC_DRAW);

        texture_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, texture_id);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(tex_coords), GL_STATIC_DRAW);

        i_id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindBuffer(GL_ARRAY_BUFFER, i_id);

    }

    protected void finalize() {
        glDeleteBuffers(vertex_id);
        glDeleteBuffers(texture_id);
        glDeleteBuffers(i_id);
    }

    public void render() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, vertex_id);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);

        glBindBuffer(GL_ARRAY_BUFFER, texture_id);
        glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
        GL11.glDrawElements(GL_TRIANGLES, draw_count, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }
    private FloatBuffer createBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
