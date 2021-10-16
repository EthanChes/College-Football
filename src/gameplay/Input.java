package gameplay;
import static org.lwjgl.glfw.GLFW.*;

// REDO "keys" array since it takes up unnecessary in keys (0-31), and has array size of 347. (348-1)

public class Input {
    private long window;

    private boolean keys[]; // For multiple keys that can be pressed instead of an individual boolean for each

    public Input(long window) {
        this.window = window;
        this.keys = new boolean[GLFW_KEY_LAST]; // keys array size is determined by largest key value

        // Initialize all keys to false
        for (int count = 0; count < GLFW_KEY_LAST; count++) {
            keys[count] = false;
        }


    }

    public boolean isKeyDown(int key) { return glfwGetKey(window, key) == 1; } // Returns true if specific "key" is pressed
    public boolean isMouseButtonDown(int button) { return glfwGetMouseButton(window, button) == 1; } // Returns true if specific "button" on mouse is pressed

    public boolean isKeyPressed(int key) { // Only sends a true isKeyDown(key) for 1 frame, key must be released to instate another value.
        return (isKeyDown(key) && !keys[key]);
    }

    public void update() { // Called, checks if any keys are pressed.
        for (int count = 32; count < GLFW_KEY_LAST; count++) {
            keys[count] = isKeyDown(count);
        }
    }
}
