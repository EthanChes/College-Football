package entity;

import collision.Collision;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class RunningBack extends Entity {
    public static final int ANIM_SIZE = 5;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_IDLE_WITH_BALL = 4;
    public static final int ANIM_RUN_WITH_BALL = 2;
    public static final int ANIM_RUN_WITHOUT_BALL = 1;
    public static final int ANIM_IDLE = 0;

    public RunningBack(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"runningbackidle"));
        setAnimation(ANIM_RUN_WITHOUT_BALL, new Animation(3,12, "runningbackmovewithoutball"));
        setAnimation(ANIM_RUN_WITH_BALL, new Animation(3,12,"runningbackmovewithball"));
        setAnimation(ANIM_IDLE_WITH_BALL, new Animation(1,1,"runningbackidlewithball"));
        setAnimation(ANIM_FALL, new Animation(1,1, "offensivefall"));
        setRoute(1);
        strength = 8f;
    }

    public void receiveHandoff(World world) {
        if (route == 1) {
            Collision collision = this.bounding_box.getCollision(world.getBallCarrier().bounding_box);
            if (collision.isIntersecting) {
                Quarterback.hasHandedOff = true;
                world.getBallCarrier().hasBall = false;
                hasBall = true;
                world.setBallCarrier(this);
            }
        }
    }
    public Vector2f handoff(float delta, World world) {
        Collision collision = this.bounding_box.getCollision(world.getBallCarrier().bounding_box);
        Vector2f movement = new Vector2f();

        if (! collision.isIntersecting) {
            if (this.transform.pos.x + delta*speed < world.getBallCarrier().transform.pos.x) {
                movement.add(speed*delta*.5f,0);
            }
            else if (this.transform.pos.x - delta*speed > world.getBallCarrier().transform.pos.x){
                movement.add(-speed*delta*.5f,0);
            }
            if (this.transform.pos.y + delta*speed < world.getBallCarrier().transform.pos.y) {
                movement.add(0,speed*delta*.5f);
            }
            else if (this.transform.pos.y - delta*speed > world.getBallCarrier().transform.pos.y) {
                movement.add(0,-speed*delta*.5f);
            }
        }

        return movement;
    }



    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        selectOffensivePlayer(window, world);

        if ((hasBall && GameManager.offenseBall) || forceUserControl) {
            userControl = true;
        } else {
            userControl = false;
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
            if (canPlay && !uniqueEvents) {
                switch (route) {
                    case 0:
                        movement.add(speed * delta, 0);
                        break;
                    case 1:
                        movement.add(handoff(delta, world));
                        break;
                    case 2:
                        if (routeMovement < 8) {
                            movement.add(0, speed * delta);
                            routeMovement += speed * delta;
                        } else {
                            uniqueEvents = true;
                        }
                        break;
                    case 3:
                        if (routeMovement < 8) {
                            movement.add(0, -speed * delta);
                            routeMovement += speed * delta;
                        } else {
                            uniqueEvents = true;
                        }
                        break;
                    case 4:
                        if (routeMovement < 8) {
                            movement.add(speed * delta, 0);
                            routeMovement += speed * delta;
                        } else {
                            uniqueEvents = true;
                        }
                        break;
                }
            }
        }
        else if (timeFumble > 0 && getAnimationIndex() != 3) {
                movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
            } else if (uniqueEvents && !(isBeingMovedExternally || pancaked)) {
                if (GameManager.offenseBall) {
                    if (hasBall) {
                        movement.add(offenseHasBallMove(world, delta));
                    } else {
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


        if (canPlay && ! (pancaked || isBeingMovedExternally)) {
            move(movement);
            receiveHandoff(world);
        } else {
            isBeingMovedExternally = false;
        }



        // Set Animations
        if (pancaked) {
            useAnimation(ANIM_FALL);
            canCollide = false;
            if (Timer.getTime() > timePancaked + 3) {
                pancaked = false;
                canCollide = true;
            }
        }
        else if (getAnimationIndex() == 3 && world.getBallCarrier() == this) {
            useAnimation(ANIM_FALL);
            world.getFootballEntity().useAnimation(0);
            world.getFootballEntity().transform.pos.set(this.transform.pos.x,this.transform.pos.y,0);
        }
        else if (hasBall && (movement.x != 0 || movement.y != 0)) {
            useAnimation(ANIM_RUN_WITH_BALL);
            world.getFootballEntity().transform.pos.set(this.transform.pos.x - .25f,this.transform.pos.y,0);
            world.getFootballEntity().useAnimation(1);
        }
        else if (hasBall) {
            useAnimation(ANIM_IDLE_WITH_BALL);
            world.getFootballEntity().transform.pos.set(this.transform.pos.x - .25f,this.transform.pos.y,0);
        }
        else if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_RUN_WITHOUT_BALL);
        }
        else {
            useAnimation(ANIM_IDLE);
        }

        if (userControl) {
            PlayerMarker.setLocation.x = this.transform.pos.x;
            PlayerMarker.setLocation.y = this.transform.pos.y;
        }

    }
}
