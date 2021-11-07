package entity;

import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class RunningBack extends Entity {
    public static final int ANIM_SIZE = 1;
    public static final int ANIM_IDLE = 0;

    public RunningBack(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"runningbackidle"));
        setRoute(1);
    }

    public Vector2f handoff(float delta, World world) {
        Vector2f movement = new Vector2f();

        if (routeMovement <= 5) {
            movement.add(speed * delta, 0);
            routeMovement += speed*delta;
        }
        else if (routeMovement > 5) {
            world.getBallCarrier().hasBall = false;
            hasBall = true;
            world.setBallCarrier(this);
            world.getFootballEntity().transform.pos.set(this.transform.pos.x,this.transform.pos.y,0);
        }

        return movement;
    }



    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (hasBall && true) {
            userControl = true;
        }

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && userControl) { // When S is pressed, player moves 5 down
            movement.add(0,-speed*delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && userControl) { // When A is pressed, camera shifts left 5
            movement.add(-speed*delta,0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && userControl) { // When W is pressed, camera shifts up 5
            movement.add(0,speed*delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && userControl) { // When D is pressed, camera shifts right 5
            movement.add(speed*delta,0);
        }

        if (route == 0) {
            movement.add(speed*delta,0);
        }
        else if (route == 1) { // Carry Out Handoff
            movement.add(handoff(delta, world));
        }

        if (canPlay) {
            move(movement);
        }

        useAnimation(ANIM_IDLE);

    }
}
