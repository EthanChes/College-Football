package main;
import gameplay.Timer;
import graphics.Camera;
import graphics.Model;
import graphics.Shader;
import graphics.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
            window = glfwCreateWindow(1000,1000,"CFB WIP", NULL,NULL);
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

            // Creates a camera, including and rendering tiles and objects throughout the frame that is 1000x1000, meaning the camera has to be 1000x1000 to use the entire frame.
            Camera camera = new Camera(1000,1000);

            glEnable(GL_TEXTURE_2D); // Enables 2 Dimensional Textures like the square produced by the Model Class to appear on the Frame.

            // Forms Tile Structure
            float[] vertices = new float[] {
                    // VERTICES ARE TWO RIGHT TRIANGLES, Locations of the Corners of the Square formed by the Right Triangles are found below.
                    -.5f, .5f, 0, // TOP LEFT 0 (x,y,z) is the formatting
                    .5f, .5f, 0, // TOP RIGHT 1
                    .5f, -.5f, 0, // BOTTOM RIGHT 2
                    -.5f, -.5f,0, // BOTTOM LEFT 3
            };

            float[] texture = new float[] {
                    // Coordinates of Texture location on Model/Vertex Structure. (0,0 BL, 1,1 TR).
                    0,0, // 0, (x,y), this the location on the model or square produced in the Model Class, that the texture's vertices will occupy.
                    1,0, // 1
                    1,1, // 2
                    0,1, // 3
            };

            int[] indices = new int[] { // Indices of the triangles. See Texture and Vertices comments. Each index corresponds to a vertex defined above.
                    0,1,2,
                    2,3,0,
            };

            Model model = new Model(vertices, texture, indices); // Draws the square using 2 right triangles to put the tile image on.
            Shader shader = new Shader("shader"); // Creates a new shader, filename is singular, because in the directory, the shader files start with "shader" Shader Class Handles Names.
           Texture tile = new Texture("./res/grass.png"); // Creates a texture of grass/field
           Matrix4f scale = new Matrix4f().translate(new Vector3f(100,0,0)).scale(128); // Set Scale of Tile, Use .translate(x,y,z) to translate.
           Matrix4f target = new Matrix4f(); // Makes target a new Matrix.
           camera.setPosition(new Vector3f(-100 ,0,0)); // Sets Camera Position. Notice that the tile is translated 100 right, but by moving the camera it appears in the center.

            glClearColor(0.0f,0.0f,0.0f,0.0f); // Window Initial Color

            double frame_cap = 1.0/60.0 ; // Max frames per second

            double time = Timer.getTime(); // Sets First Time, enabling us to calculate the time in the future and use that with this time to get the time in between to decide whether a frame should be produced. whether
            double unprocessed = 0; // unprocessed time. The time where nothing has occured yet, but waiting in queue to produce a frame when the time gets high enugh.
            double frame_time = 0; // The total time the loop (max 1s) has been running for. When reaching 1, this will reset and output the total frames created. Used to calculate FPS
            int frames = 0; // Total Number of frames that have occured. When frame_time = 1s, this will output the frames produced in 1s (fps) and will set to 0.

            // While loop for frame to stay open while it should not close.
            while (!glfwWindowShouldClose(window)) {
                // Keeps Window Running
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear Framebuffer

                // Add Loop Code Here
                boolean can_render = false; // Initially, images cannot render
                double time_2 = Timer.getTime(); // Sets most recent time
                double time_passed = time_2 - time; // Calculates time passed
                unprocessed += time_passed; // unprocessed time, so that if it builds up, it will try to catch up.
                frame_time += time_passed; // time passed resets every loop, so to store total time built up, add this to a total frame time declared outside the loop.
                time = time_2; // Reset time, such that it can calculate difference between this and next frame in next loop.

                while (unprocessed >= frame_cap) { // Loop at rate of fps, only occurs when the unprocessed time is greater than time available for a frame.
                    unprocessed -= frame_cap; // The amount of unprocessed time decreases by 1 frames amount of time.
                    can_render = true; // If this is set to true, then images may render. Thus it only renders at frame_cap speed. Line 166, this is used for controlling rendering at fps.
                    target = scale; // Sets Object location to a target Matrix. Since scale is a matrix, we could just use scale, but this makes the code look cleaner and easier to understand the loop.

                    // Window Closes when Key Escape is Pressed
                    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GL_TRUE) {
                        glfwSetWindowShouldClose(window, true);
                    }
                    // Keeps Window Open
                    glfwPollEvents();

                    if (frame_time >= 1.0) { // When Frame_time = 1.0, reset frame_time and print frames as well as set frames to 0.
                        frame_time = 0;
                        System.out.println("FPS: " + frames); // Thus, this prints fps
                        frames = 0;
                    }
                }

                // Renders images only if they are enabled to render as determined by a boolean activated once every frame at a rate of the given fps, 60.
                if (can_render) {
                    shader.bind(); // Enables program and frame to use the shader making images more clear and with outline parameters for size and color determined in shader.fs and shader.vs
                    shader.setUniform("sampler", new Matrix4f()); // Sets shader's image's sampler, or color. See shader.fs file for exact procedures of color.
                    shader.setUniform("projection", camera.getProjection().mul(target)); // Sets location of the image. See shader.vs for exact procedures of positioning.
                    model.render(); // Renders Tiles (grass/field)
                    tile.bind(0); // binds tiles/field to frame.
                    frames++; // total frames increases when 1 frame render is performed
                }

                glfwSwapBuffers(window); // MUST BE AT END OF LOOP (STARTS END OF FRAME/BUFFER SWAP)
            }
        }

        public static void main(String[] args) {
           new Main().run(); // Runs Frame Builder, basically the entire game's function.
        }
}
