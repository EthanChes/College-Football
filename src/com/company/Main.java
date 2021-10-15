package com.company;
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

            glClearColor(0.0f,0.0f,0.0f,0.0f); // Window Initial Color

            while (!glfwWindowShouldClose(window)) {
                // Keeps Window Running
                glClear(GL_COLOR_BUFFER_BIT);
                glfwPollEvents();
                glfwSwapBuffers(window);

                // Add Loop Code Here
            }
        }

        public static void main(String[] args) {
           new Main().run(); // Runs Frame Builder (run)
        }
}
