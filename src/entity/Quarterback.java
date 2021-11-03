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
    public static final int ANIM_SIZE = 4;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_THROW = 2;
    public static final int ANIM_WALK = 1;
    public static final int ANIM_IDLE = 0;

    public static double timePass = 0; // time of pass
    public static byte receiverPass;

    public Quarterback(Transform transform) {
        super(ANIM_SIZE,transform);
        hasBall = true;
        setAnimation(ANIM_IDLE, new Animation(1,1,"qbidle"));
        setAnimation(ANIM_WALK, new Animation(4,16,"qbrun"));
        setAnimation(ANIM_THROW, new Animation(2,4,"qbthrow"));
        setAnimation(ANIM_FALL, new Animation(1,1,"offensivefall"));
    }

    public void passOptions(Window window) {
        if (userControl && canPlay) {
            for (int count = 0; count < WideReceiver.totalReceivers; count++) {
                switch (count) {
                    case 0:
                        if (window.getInput().isKeyDown(GLFW_KEY_P)) {
                             receiverPass = 0;  pass = true; timePass = getTime(); hasBall = false;
                        }
                        break;
                    case 1:
                        if (window.getInput().isKeyDown(GLFW_KEY_O)) {
                            receiverPass = 1;  pass = true; timePass = getTime(); hasBall = false;
                        }
                        break;
                    case 2:
                        if (window.getInput().isKeyDown(GLFW_KEY_I)) {
                            receiverPass = 2;  pass = true; timePass = getTime(); hasBall = false;
                        }
                        break;
                    case 3:
                        if (window.getInput().isKeyDown(GLFW_KEY_U)) {
                            receiverPass = 3;  pass = true; timePass = getTime(); hasBall = false;
                        }
                        break;
                    case 4:
                        if (window.getInput().isKeyDown(GLFW_KEY_Y)) {
                            receiverPass = 4;  pass = true; timePass = getTime(); hasBall = false;
                        }
                        break;
                }



            }
        }



    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();
        double time_current = getTime();
        Entity football = world.getFootballEntity();

        if (hasBall && true) userControl = true; // change && true to gamemanger user is on team on offense
        else userControl = false;

        if (time_current - timePass < .35) {
            pass = true;

            if (time_current - timePass > .25) { // Waits until frame of qb animation throw to move football
                football.useAnimation(1);
                football.transform.pos.set(transform.getEntityPosX() - .3f, transform.getEntityPosY() + .75f, 0);
            }
        }
        else if (time_current - timePass > .35 && time_current - timePass < .37) {
            football.startPass();
            world.setBallCarrier(world.getFootballEntity());
            pass = false;
        }
        else {
            pass = false; // Prevents Bad Error if timing doesnt match up
        }


        passOptions(window);

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

        //zoomOutWhenNotVisible(this, camera);

        if (canPlay)
        move(movement);

        // Use Animations
        if (getAnimationIndex() == ANIM_FALL) {
            useAnimation(ANIM_FALL);
            world.getFootballEntity().transform.pos.set(this.transform.pos.x,this.transform.pos.y, 0);
        }
        else if (pass) {
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
