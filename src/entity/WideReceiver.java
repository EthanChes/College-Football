package entity;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class WideReceiver extends Entity {
    public static final int ANIM_SIZE = 7;
    public static final int ANIM_PRESNAP = 6;
    public static final int ANIM_IDLE_BALL = 5;
    public static final int ANIM_RUN_BALL = 4;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_CATCH = 2;
    public static final int ANIM_RUN = 1;
    public static final int ANIM_IDLE = 0;

    public double timeCatch = 0;

    public WideReceiver(Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"widereceiveridle",true));
        setAnimation(ANIM_RUN, new Animation(4,16,"widereceiverrouterun",true));
        setAnimation(ANIM_CATCH, new Animation(1,1,"widereceiverincatch",true));
        setAnimation(ANIM_FALL, new Animation(1,1,"offensivefall",true));
        setAnimation(ANIM_RUN_BALL, new Animation(4,16,"widereceiverrunwithball",true));
        setAnimation(ANIM_IDLE_BALL, new Animation(1,1,"widereceiveridlewithball",true));
        setAnimation(ANIM_PRESNAP, new Animation(1,1, "presnap/receiver",true));
        speed = 10f;
        catching = 10f;
        strength = 10f;
        totalReceivers++;
        noCollision();
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


        if (this.uniqueEvents) {
            this.canCollide = true;
        }

        if ((hasBall && GameManager.userOffense) || forceUserControl) userControl = true; // change && true to gamemanager user controls offensive team
        else userControl = false;

        if (forceUserControl) {
            userTackle(window, this, world.getBallCarrier(), world);
        }

        if (! (inCatch || hasBall) && collidingWithFootball(this,world)) { // Put Random Catch Element here
            Entity closestDefender = world.getCountingUpEntity(0);
            for (int i = 0; i < 11; i++) {
                if (closestDefender.transform.pos.distance(this.transform.pos) > world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos)) {
                    closestDefender = world.getCountingUpEntity(i);
                }
            }

            Random rand = new Random();
            if (closestDefender.transform.pos.distance(this.transform.pos) <= 2.75f && catchAttempt) {
                int rand_output = rand.nextInt((int) (this.catching * 100 + (closestDefender.catching * 100) * (3 - closestDefender.transform.pos.distance(this.transform.pos))));
                if (rand_output <= this.catching * 100) {
                    this.inCatch = true;
                    incompletePass = false;
                    this.timeCatch = Timer.getTime();
                    for (int i = 0; i < 22; i++) {
                        world.getCountingUpEntity(i).timeSinceLastTackleAttempt = Timer.getTime() - 1;
                    }
                } else {
                    world.getFootballEntity().pass = false;
                    incompletePass = true;
                    timeCatch = Timer.getTime();
                    Football.passDropStart = Timer.getTime();
                }
            } else if (catchAttempt) {
                this.inCatch = true;
                incompletePass = false;
                for (int i = 0; i < 22; i++) {
                    world.getCountingUpEntity(i).timeSinceLastTackleAttempt = Timer.getTime() - 1;
                }
                this.timeCatch = Timer.getTime();
            }
            catchAttempt = false;
        }

        if (inCatch) {
            for (int i = 0; i < 22; i++) {
                world.getCountingUpEntity(i).uniqueEvents = true;
            }

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

        if (! userControl) {
            if (!hasBall && canPlay && !(world.getFootballEntity().pass || uniqueEvents)) { // Route Movements
                switch (route) {

                    case 0:
                        if (routeMovement <= 90) { // Fade
                            movement.add(speed * delta, 0);
                            routeMovement += speed * delta;
                        } else {
                            reachedEndOfRoute = true;
                        }
                        break;

                    case 1:
                        if (routeMovement <= 20) { // In Route From Left
                            movement.add(speed * delta, 0);
                            routeMovement += speed * delta;
                        } else if (routeMovement <= 35) {
                            movement.add(0, -speed * delta);
                            routeMovement += speed * delta;
                        } else {
                            reachedEndOfRoute = true;
                        }
                        break;

                    case 2:
                        if (routeMovement <= 10) { // Slant
                            movement.add(speed * delta, 0);
                            routeMovement += speed * delta;
                        } else if (routeMovement <= 40) {
                            movement.add(speed * delta, -speed * delta);
                            routeMovement += new Vector2f().distance(speed * delta, -speed * delta);
                        } else {
                            reachedEndOfRoute = true;
                        }
                        break;

                    case 3:
                        if (routeMovement <= 15) { // Curl
                            movement.add(speed * delta, 0);
                            routeMovement += speed * delta;
                        } else {
                            reachedEndOfRoute = true;
                        }
                        break;
                    case 4 : // In Route From Right
                        if (routeMovement <= 20) {
                            movement.add(speed*delta, 0);
                            routeMovement += speed *delta;
                        } else if (routeMovement <= 35) {
                            movement.add(0, speed*delta);
                            routeMovement += speed * delta;
                        } else {
                            reachedEndOfRoute = true;
                        }

                }

                if (this.transform.pos.x + movement.x > 366.5f) {
                    routeMovement = 1000;
                    movement.set(0,0);
                    reachedEndOfRoute = true;
                }

            } else if (world.getFootballEntity().pass && !uniqueEvents) {
                movement.add(moveToward(Football.wideReceiverX, Football.wideReceiverY, delta));
            } else if (timeFumble > 0) {
                movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
            } else if (uniqueEvents && !(pancaked || isBeingMovedExternally)) {
                if (GameManager.offenseBall) {
                    if (hasBall && !GameManager.userOffense) {
                        movement.add(offenseHasBallMove(world, delta));
                    } else if (!hasBall) {
                        movement.add(offenseBlockUnique(world, delta));
                    }
                } else {
                    movement.add(defensive_movement(world.getBallCarrier(), delta));

                    if (collidingWithBallCarrier(this, world)) {
                        if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && !GameManager.offenseBall) {
                            boolean tackResult = tackle(world.getBallCarrier(), window, world);
                            if (tackResult) {
                                world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                                canPlay = false;
                            } else {
                                this.pancaked = true;
                                timePancaked = Timer.getTime();
                            }
                        }
                    }
                }
            }
        }

        if (canPlay && ! (isBeingMovedExternally || pancaked)) {
            move(movement);
        } else {
            isBeingMovedExternally = false;
        }

        // Movements for receiver symbol
            Entity receiverSymbol = world.getSpecifiedEntity(ReceiverSymbol.index);
            receiverSymbol.transform.pos.set(this.transform.pos.x, this.transform.pos.y + 1.5f, 0);
            receiverSymbol.useAnimation(totalReceivers - (ReceiverSymbol.index++));
            if (ReceiverSymbol.index > totalReceivers) {
                ReceiverSymbol.index = 1;
            }

        if (world.getQuarterbackEntity().route == 0 && world.getQuarterbackEntity().hasBall) {
            zoomOutWhenNotVisible(this, camera, world);
        }


        // Animations for wide receiver catch & football translations, try to move some of these for more effective coding
        if (pancaked) {
            useAnimation(ANIM_FALL);
            canCollide = false;
            if (Timer.getTime() > timePancaked + 3) {
                pancaked = false;
                canCollide = true;
            }
        }
        else if (getAnimationIndex() == ANIM_FALL && world.getBallCarrier() == this) {
            useAnimation(ANIM_FALL);
            if (hasBall) {
                world.getFootballEntity().transform.pos.set(this.transform.pos.x, this.transform.pos.y, 0);
            }
        }
        else if (hasBall && (movement.x != 0 || movement.y != 0)) {
            useAnimation(ANIM_RUN_BALL);
            football.transform.pos.set(transform.pos.x - .3f,transform.pos.y + .1f,0);
        }
        else if (hasBall) {
            useAnimation(ANIM_IDLE_BALL);
            football.transform.pos.set(transform.pos.x - .3f,transform.pos.y + .1f,0);
        }
        else if (timeCatch + .125 > Timer.getTime()) {
            useAnimation(ANIM_CATCH);
            if (inCatch) {
                world.getFootballEntity().transform.pos.set(transform.pos.x, transform.pos.y, 0);
            }
        }
        else if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_RUN);
        } else {
            useAnimation(ANIM_IDLE);
        }

        if (! (canPlay || playStart)) {
            useAnimation(ANIM_PRESNAP);
        }

        if (userControl) {
            PlayerMarker.setLocation.x = this.transform.pos.x;
            PlayerMarker.setLocation.y = this.transform.pos.y;
        }



    }

}