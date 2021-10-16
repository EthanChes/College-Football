package main;
import gameplay.Timer;
import graphics.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
// Note 10 Spaces Indicates New Function/End of Previous Function ONLY in Main








        // Loops window so it stays open and runs various functions
        public static void loop() {
            // Create window object
            Window window = new Window();
            window.createWindow("CFB WIP"); // Creates window using window object
            // Imperative this is at the top, sets capabilities so window can make squares, textures etc.
            GL.createCapabilities();

            // Creates a camera, including and rendering tiles and objects throughout the frame that is 1000x1000, meaning the camera has to be 1000x1000 to use the entire frame.
            Camera camera = new Camera(window.getWidth(), window.getHeight());

            // Forms Tile Structure
            float[] vertices = new float[]{
                    // VERTICES ARE TWO RIGHT TRIANGLES, Locations of the Corners of the Square formed by the Right Triangles are found below.
                    -.5f, .5f, 0, // TOP LEFT 0 (x,y,z) is the formatting
                    .5f, .5f, 0, // TOP RIGHT 1
                    .5f, -.5f, 0, // BOTTOM RIGHT 2
                    -.5f, -.5f, 0, // BOTTOM LEFT 3
            };

            float[] texture = new float[]{
                    // Coordinates of Texture location on Model/Vertex Structure. (0,0 BL, 1,1 TR).
                    0, 0, // 0, (x,y), this the location on the model or square produced in the Model Class, that the texture's vertices will occupy.
                    1, 0, // 1
                    1, 1, // 2
                    0, 1, // 3
            };

            int[] indices = new int[]{ // Indices of the triangles. See Texture and Vertices comments. Each index corresponds to a vertex defined above.
                    0, 1, 2,
                    2, 3, 0,
            };

            Model model = new Model(vertices, texture, indices); // Draws the square using 2 right triangles to put the tile image on.
            Shader shader = new Shader("shader"); // Creates a new shader, filename is singular, because in the directory, the shader files start with "shader" Shader Class Handles Names.
            Texture tile = new Texture("./res/grass.png"); // Creates a texture of grass/field
            Matrix4f scale = new Matrix4f().translate(new Vector3f(100, 0, 0)).scale(64); // Set Scale of Tile, Use .translate(x,y,z) to translate.
            Matrix4f target = new Matrix4f(); // Makes target a new Matrix.
            camera.setPosition(new Vector3f(-100, 0, 0)); // Sets Camera Position. Notice that the tile is translated 100 right, but by moving the camera it appears in the center.

            glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Window Initial Color

            double frame_cap = 1.0 / 60.0; // Max frames per second

            double time = Timer.getTime(); // Sets First Time, enabling us to calculate the time in the future and use that with this time to get the time in between to decide whether a frame should be produced. whether
            double unprocessed = 0; // unprocessed time. The time where nothing has occured yet, but waiting in queue to produce a frame when the time gets high enugh.
            double frame_time = 0; // The total time the loop (max 1s) has been running for. When reaching 1, this will reset and output the total frames created. Used to calculate FPS
            int frames = 0; // Total Number of frames that have occured. When frame_time = 1s, this will output the frames produced in 1s (fps) and will set to 0.

            // While loop for frame to stay open while it should not close.
            while (!window.shouldClose()) {

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
                    if (window.getInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
                        glfwSetWindowShouldClose(window.getWindow(), true);
                    }

                    // updates keys
                    window.update();

                    if (frame_time >= 1.0) { // When Frame_time = 1.0, reset frame_time and print frames as well as set frames to 0.
                        frame_time = 0;
                        System.out.println("FPS: " + frames); // Thus, this prints fps
                        frames = 0;
                    }
                }

                // Renders images only if they are enabled to render as determined by a boolean activated once every frame at a rate of the given fps, 60.
                if (can_render) {
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear Framebuffer
                    shader.bind(); // Enables program and frame to use the shader making images more clear and with outline parameters for size and color determined in shader.fs and shader.vs
                    shader.setUniform("sampler", new Matrix4f()); // Sets shader's image's sampler, or color. See shader.fs file for exact procedures of color.
                    shader.setUniform("projection", camera.getProjection().mul(target)); // Sets location of the image. See shader.vs for exact procedures of positioning.
                    model.render(); // Renders Tiles (grass/field)
                    tile.bind(0); // binds tiles/field to frame.
                    window.swapBuffers();
                    frames++; // total frames increases when 1 frame render is performed
                }
            }
        }









    public static void main(String[] args) {
        Window.setCallbacks(); // Provides Better Error Codes
        if (glfwInit() != true) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(1);
        }

        System.out.println("LWJGL " + Version.getVersion());

        loop();

        glfwTerminate();
    }
}
