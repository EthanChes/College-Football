package entity;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import static gameplay.Timer.getTime;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Quarterback extends Entity {
    public static final int ANIM_SIZE = 3;
    public static final int ANIM_THROW = 2;
    public static final int ANIM_WALK = 1;
    public static final int ANIM_IDLE = 0;

    public static double timePass = 0; // time of pass
    public static boolean hasBall = true;
    public static float speed = 10f;

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
        boolean able_move;
        Entity football = world.getFootballEntity();

        if (time_current - timePass < .35) {
            pass = true;

            if (time_current - timePass > .25) { // Waits until frame of qb animation throw to move football
                football.useAnimation(1);
                football.transform.pos.set(transform.getEntityPosX() - .3f, transform.getEntityPosY() + .75f, 0);
            }
        }
        else if (time_current - timePass > .35 && time_current - timePass < .37) {
            football.startPass();
            pass = false;
        }
        else {
            pass = false; // Prevents Bad Error if timing doesnt match up
        }



        if (hasBall && !pass) { able_move = true; } else { able_move = false;}

        if (window.getInput().isKeyDown(GLFW_KEY_P) && hasBall) { // When P is pressed pass occurs
            pass = true; able_move = false; timePass = getTime(); hasBall = false;
        }

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && able_move) { // When S is pressed, player moves 5 down
            movement.add(0,-speed*delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && able_move) { // When A is pressed, camera shifts left 5
            movement.add(-speed*delta,0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && able_move) { // When W is pressed, camera shifts up 5
            movement.add(0,speed*delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && able_move) { // When D is pressed, camera shifts right 5
            movement.add(speed*delta,0);
        }

        //zoomOutWhenNotVisible(this, camera);

        move(movement);


        if (pass) {
            useAnimation(ANIM_THROW);
        }
        else if (movement.x != 0 || movement.y != 0) {
            if (hasBall) {
                football.transform.pos.set(transform.pos.x + .125f, transform.pos.y + .125f, 0);
            }
            useAnimation(ANIM_WALK);
        }
        else {
            if (hasBall) {
                football.transform.pos.set(transform.getEntityPosX() + .125f, transform.getEntityPosY() + .125f, 0);
            }
            useAnimation(ANIM_IDLE);
        }
    }

}
