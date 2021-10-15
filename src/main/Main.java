package main;
import graphics.Model;
import graphics.Shader;
import graphics.Texture;
import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

        // Window Handle
        private long window ;
        // Declares Window as variable
        public void run() {
            System.out.println("LWJGL " + Version.getVersion());

            init();
            loop();

            // Free the Window callbacks and destroy window after loop ends
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }

        // Error Checking & initialization
        public void init() {
            // Set error callback (default)
            GLFWErrorCallback.createPrint(System.err).set();

            // Initialize GLFW
            if (!glfwInit()) {
                throw new IllegalStateException("Unable to Initialize GLFW");
            }

            // Configure GLFW
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

            // Create Window
            window = glfwCreateWindow(1000,1000,"Color Wheel", NULL,NULL);
            if (window == NULL) {
                throw new RuntimeException("Failed to create the GLFW window");
            }
            // Get the thread stack and push a new frame
            try ( MemoryStack stack = stackPush() ) {
                IntBuffer pWidth = stack.mallocInt(1); // int*
                IntBuffer pHeight = stack.mallocInt(1); // int*

                // Get the window size passed to glfwCreateWindow
                glfwGetWindowSize(window, pWidth, pHeight);

                // Get the resolution of the primary monitor
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

                // Center the window
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            } // the stack frame is popped automatically

            // Make the OpenGL context current
            glfwMakeContextCurrent(window);
            // Enable v-sync
            glfwSwapInterval(1);

            // Make the window visible
            glfwShowWindow(window);
        }

        // Loops window so it stays open and runs various functions
        public void loop() {
            // Imperative this is at the top, sets capabilities so window can make squares, textures etc.
            GL.createCapabilities();

            glEnable(GL_TEXTURE_2D);

            // Forms Tile Structure
            float[] vertices = new float[] {
                    // VERTICES ARE TWO RIGHT TRIANGLES
                    -.5f, .5f, 0, // TOP LEFT 0
                    .5f, .5f, 0, // TOP RIGHT 1
                    .5f, -.5f, 0, // BOTTOM RIGHT 2
                    -.5f, -.5f,0, // BOTTOM LEFT 3
            };

            float[] texture = new float[] {
                    // Coordinates of Texture location on Model/Vertex Structure. (0,0 BL, 1,1 TR).
                    0,0, // 0
                    1,0, // 1
                    1,1, // 2
                    0,1, // 3
            };

            int[] indices = new int[] {
                    0,1,2,
                    2,3,0,
            };

            Model model = new Model(vertices, texture, indices);
            Shader shader = new Shader("shader");
           Texture tile = new Texture("./res/grass.png");
           Matrix4f projection = new Matrix4f().ortho2D(-1000/2, 1000/2, -1000/2, 1000/2);
           Matrix4f scale = new Matrix4f().scale(64); // Set Scale of Tile
           Matrix4f target = new Matrix4f();

           projection.mul(scale,target);

            glClearColor(0.0f,0.0f,0.0f,0.0f); // Window Initial Color

            while (!glfwWindowShouldClose(window)) {
                // Keeps Window Running
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear Framebuffer
                glfwPollEvents();

                // Add Loop Code Here
                tile.bind(0);
                shader.bind();
                shader.setUniform("sampler", projection);
                shader.setUniform("projection", target);
                model.render(); // Renders Tiles (grass)

                glfwSwapBuffers(window); // MUST BE AT END OF LOOP (STARTS END OF FRAME)
            }
        }

        public static void main(String[] args) {
           new Main().run(); // Runs Frame Builder (run)
        }
}
