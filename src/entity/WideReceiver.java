package entity;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import static org.lwjgl.glfw.GLFW.*;

public class WideReceiver extends Entity {
    public static final int ANIM_SIZE = 6;
    public static final int ANIM_IDLE_BALL = 5;
    public static final int ANIM_RUN_BALL = 4;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_CATCH = 2;
    public static final int ANIM_RUN = 1;
    public static final int ANIM_IDLE = 0;

    public static int totalReceivers = 0;

    public boolean inCatch = false;
    public double timeCatch;

    public WideReceiver(Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"widereceiveridle"));
        setAnimation(ANIM_RUN, new Animation(4,16,"widereceiverrouterun"));
        setAnimation(ANIM_CATCH, new Animation(1,1,"widereceiverincatch"));
        setAnimation(ANIM_FALL, new Animation(1,1,"offensivefall"));
        setAnimation(ANIM_RUN_BALL, new Animation(4,16,"widereceiverrunwithball"));
        setAnimation(ANIM_IDLE_BALL, new Animation(1,1,"widereceiveridlewithball"));
        totalReceivers++;
    }

    public void catching() {
        if (timeCatch + .125 < Timer.getTime()) { // Parameter for time during catch, then set hasball and incatch in here.
            this.inCatch = false;
            this.hasBall = true;
        }
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();
        Entity football = world.getFootballEntity();

        if (hasBall && true) userControl = true; // change && true to gamemanager user controls offensive team
        else userControl = false;

        if (! (inCatch || hasBall) && collidingWithFootball(this,world)) { // Put Random Catch Element here
            this.inCatch = true;
            this.timeCatch = Timer.getTime();
        }

        if (inCatch) {
            football.useAnimation(1);
            passCaught(world);
            catching();
            world.setBallCarrier(this);
            camera.setProjection(640,480);
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

        if (!hasBall) { // Route Movements
            switch (route) {

                case 0 : if (routeMovement <= 90) { // Fade
                    movement.add(speed * delta, 0);
                    routeMovement += speed * delta;
                }
                else { reachedEndOfRoute = true; }
                break;

                case 1 : if (routeMovement <= 10) { // In Route
                    movement.add(speed*delta,0);
                    routeMovement += speed * delta;
                } else if (routeMovement <= 25) {
                    movement.add(0,-speed*delta);
                    routeMovement += speed * delta;
                }
                else { reachedEndOfRoute = true; }
                break;

                case 2 : if (routeMovement <= 10) {
                    movement.add(speed*delta,0);
                    routeMovement += speed * delta;
                }
                else if (routeMovement <= 40) {
                    movement.add(speed*delta,-speed*delta);
                    routeMovement += new Vector2f().distance(speed*delta,-speed*delta);
                }
                else { reachedEndOfRoute = true; }

                    break;
            }
        }

        if (canPlay)
        move(movement);

        // Movements for receiver symbol
            Entity receiverSymbol = world.getSpecifiedEntity(ReceiverSymbol.index);
            receiverSymbol.transform.pos.set(this.transform.pos.x, this.transform.pos.y + 1.5f, 0);
            receiverSymbol.useAnimation(totalReceivers - (ReceiverSymbol.index++));
            if (ReceiverSymbol.index > totalReceivers) {
                ReceiverSymbol.index = 1;
            }

        if (world.getQuarterbackEntity().route == 0 && world.getQuarterbackEntity().hasBall) {
            zoomOutWhenNotVisible(this, camera);
        }


        // Animations for wide receiver catch & football translations, try to move some of these for more effective coding
        if (getAnimationIndex() == ANIM_FALL) {
            useAnimation(ANIM_FALL);
            world.getFootballEntity().transform.pos.set(this.transform.pos.x,this.transform.pos.y, 0);
        }
        else if (hasBall && (movement.x != 0 || movement.y != 0)) {
            useAnimation(ANIM_RUN_BALL);
            football.transform.pos.set(transform.pos.x - .3f,transform.pos.y + .1f,0);
        }
        else if (hasBall) {
            useAnimation(ANIM_IDLE_BALL);
            football.transform.pos.set(transform.pos.x - .3f,transform.pos.y + .1f,0);
        }
        else if (inCatch) {
            useAnimation(ANIM_CATCH);
            football.transform.pos.set(transform.pos.x,transform.pos.y,0);
        }
        else if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_RUN);
        } else {
            useAnimation(ANIM_IDLE);
        }



    }

}