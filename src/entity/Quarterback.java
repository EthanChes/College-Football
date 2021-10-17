package entity;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Quarterback extends Entity {
    public Quarterback(Transform transform) {
        super(new Animation(2,4,"qbthrow"), transform);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S)) { // When S is pressed, player moves 5 down
            movement.add(0,-10*delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A)) { // When A is pressed, camera shifts left 5
            movement.add(-10*delta,0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W)) { // When W is pressed, camera shifts up 5
            movement.add(0,10*delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D)) { // When D is pressed, camera shifts right 5
            movement.add(10*delta,0);
        }

        move(movement);

        collideWithTiles(world);

        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .05f);
    }

}
