package entity;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

import static gameplay.Timer.getTime;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Quarterback extends Entity {
    public static final int ANIM_SIZE = 3;
    public static final int ANIM_THROW = 2;
    public static final int ANIM_WALK = 1;
    public static final int ANIM_IDLE = 0;

    public static double timePass; // time of pass
    public static boolean hasBall = true;

    public Quarterback(Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"qbidle"));
        setAnimation(ANIM_WALK, new Animation(4,16,"qbrun"));
        setAnimation(ANIM_THROW, new Animation(2,4,"qbthrow"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();
        double time_current = getTime();
        boolean pass = false;
        boolean able_move;

        if (time_current - timePass < .35) {
            pass = true;
        }

        if (hasBall && !pass) { able_move = true; } else { able_move = false;}

        if (window.getInput().isKeyDown(GLFW_KEY_P) && hasBall) { // When P is pressed pass occurs
            pass = true; able_move = false; timePass = getTime(); //MAKE SURE TO SET HASBALL TO FALSE AFTER PASS
        }

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && able_move) { // When S is pressed, player moves 5 down
            movement.add(0,-10*delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && able_move) { // When A is pressed, camera shifts left 5
            movement.add(-10*delta,0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && able_move) { // When W is pressed, camera shifts up 5
            movement.add(0,10*delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && able_move) { // When D is pressed, camera shifts right 5
            movement.add(10*delta,0);
        }

        if (pass) {
            useAnimation(ANIM_THROW);
        } else if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_WALK);
        } else {
            useAnimation(ANIM_IDLE);
        }

        move(movement);

        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .05f);
    }

}
