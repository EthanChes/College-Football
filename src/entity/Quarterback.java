package entity;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import static gameplay.Timer.getTime;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Quarterback extends Entity {
    public static final int ANIM_SIZE = 7;
    public static final int ANIM_RUN = 6;
    public static final int ANIM_PRESNAP = 5;
    public static final int ANIM_HANDOFF = 4;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_THROW = 2;
    public static final int ANIM_WALK = 1;
    public static final int ANIM_IDLE = 0;

    public static double timePass = 0; // time of pass
    public static boolean hasHandedOff = false;
    public static byte receiverPass;

    public Quarterback(Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"qbidle"));
        setAnimation(ANIM_WALK, new Animation(4,16,"qbrun"));
        setAnimation(ANIM_THROW, new Animation(2,4,"qbthrow"));
        setAnimation(ANIM_FALL, new Animation(1,1,"offensivefall"));
        setAnimation(ANIM_HANDOFF, new Animation(1,1,"qbhandoff"));
        setAnimation(ANIM_PRESNAP, new Animation(1,1, "presnap/quarterback"));
        setAnimation(ANIM_RUN, new Animation(4, 16, "widereceiverrouterun"));
        speed = 7f;
        strength = 10f;
        throw_accuracy = 10f;
    }

    public void passOptions(Window window) {
        if (userControl && canPlay && this.transform.pos.x < GameManager.ballPosX) {
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

        selectOffensivePlayer(window, world);

         if (route != 1) {
            if (timeFumble > 0) {
                movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
            }
            else if (uniqueEvents) {
                if (canPlay && ! (pancaked || isBeingMovedExternally)) {
                    if (GameManager.offenseBall) {
                        if (hasBall && ! GameManager.userOffense) {
                            movement.add(offenseHasBallMove(world,delta));
                        }
                        else if (! hasBall) {
                            movement.add(offenseBlockUnique(world, delta));
                        }
                    } else {
                        movement.add(defensive_movement(world.getBallCarrier(), delta));

                        if (collidingWithBallCarrier(this,world)) {
                            if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && ! GameManager.offenseBall) {
                                boolean tackResult = tackle(world.getBallCarrier(), window, world);
                                if (tackResult) {
                                    world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                                    canPlay = false;
                                }
                                else {
                                    this.pancaked = true;
                                    timePancaked = Timer.getTime();
                                }
                            }
                        }
                    }
                }

            }

            if ((hasBall && GameManager.offenseBall) || forceUserControl) userControl = true; // change && true to gamemanger user is on team on offense
            else userControl = false;

            if (time_current - timePass < .35) {
                pass = true;

                if (time_current - timePass > .25) { // Waits until frame of qb animation throw to move football
                    football.useAnimation(1);
                    football.transform.pos.set(transform.getEntityPosX() - .3f, transform.getEntityPosY() + .75f, 0);
                } else {
                    football.transform.pos.set(transform.pos.x + .125f, transform.pos.y + .125f, 0);
                }
            } else if (time_current - timePass > .35 && time_current - timePass < .37 && canPlay) {
                football.startPass();
                world.setBallCarrier(world.getFootballEntity());
                pass = false;
            } else {
                pass = false; // Prevents Bad Error if timing doesnt match up
            }

            passOptions(window);

            // Moves Player using various WASD directions using vectors.
            if (canPlay) {
                if (window.getInput().isKeyDown(GLFW_KEY_S) && userControl) { // When S is pressed, player moves 5 down
                    movement.add(0, -speed * delta); // multiply by delta (framecap) to move 10 frames in a second.
                }
                if (window.getInput().isKeyDown(GLFW_KEY_A) && userControl) { // When A is pressed, camera shifts left 5
                    movement.add(-speed * delta, 0);
                }
                if (window.getInput().isKeyDown(GLFW_KEY_W) && userControl) { // When W is pressed, camera shifts up 5
                    movement.add(0, speed * delta);
                }
                if (window.getInput().isKeyDown(GLFW_KEY_D) && userControl) { // When D is pressed, camera shifts right 5
                    movement.add(speed * delta, 0);
                }
            }

            //zoomOutWhenNotVisible(this, camera);

            if (canPlay && ! pancaked) {
                move(movement);
            }
            else {
                snap(window,world);
            }

            // Use Animations

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
            else if (pass) {
                useAnimation(ANIM_THROW);
            } else if (movement.x != 0 || movement.y != 0) {
                if (hasBall) {
                    football.transform.pos.set(transform.pos.x + .125f, transform.pos.y + .125f, 0);
                }
                useAnimation(ANIM_WALK);
            } else {
                if (hasBall) {
                    football.transform.pos.set(transform.getEntityPosX() + .125f, transform.getEntityPosY() + .125f, 0);
                }
                useAnimation(ANIM_IDLE);
            }
        }
        else if (route == 1) {
            if (uniqueEvents) {
                canCollide = true;
            } else {
                canCollide = false;
            }
             if (timeFumble > 0) {
                 movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
             }
             else if (uniqueEvents) {
                 if (canPlay && ! (pancaked || isBeingMovedExternally)) {
                     if (GameManager.offenseBall) {
                         if (hasBall && ! GameManager.userOffense) {
                             movement.add(offenseHasBallMove(world,delta));
                         }
                         else if (! hasBall) {
                             movement.add(offenseBlockUnique(world, delta));
                         }
                     } else {
                         movement.add(defensive_movement(world.getBallCarrier(), delta));

                         if (collidingWithBallCarrier(this,world)) {
                             if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && ! GameManager.offenseBall) {
                                 boolean tackResult = tackle(world.getBallCarrier(), window, world);
                                 if (tackResult) {
                                     world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                                     canPlay = false;
                                 }
                                 else {
                                     this.pancaked = true;
                                     timePancaked = Timer.getTime();
                                 }
                             }
                         }
                     }
                 }

             }

             if ((hasBall && GameManager.offenseBall) || forceUserControl) userControl = true; // change && true to gamemanger user is on team on offense
             else userControl = false;

             if (canPlay) {
                 if (window.getInput().isKeyDown(GLFW_KEY_S) && userControl) { // When S is pressed, player moves 5 down
                     movement.add(0, -speed * delta); // multiply by delta (framecap) to move 10 frames in a second.
                 }
                 if (window.getInput().isKeyDown(GLFW_KEY_A) && userControl) { // When A is pressed, camera shifts left 5
                     movement.add(-speed * delta, 0);
                 }
                 if (window.getInput().isKeyDown(GLFW_KEY_W) && userControl) { // When W is pressed, camera shifts up 5
                     movement.add(0, speed * delta);
                 }
                 if (window.getInput().isKeyDown(GLFW_KEY_D) && userControl) { // When D is pressed, camera shifts right 5
                     movement.add(speed * delta, 0);
                 }
             }

            if (canPlay && hasHandedOff && ! pancaked) {
                move(movement);
            }
            else {
                snap(window,world);
            }

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
             else if (pass) {
                 useAnimation(ANIM_THROW);
             } else if (movement.x != 0 || movement.y != 0) {
                 if (hasBall) {
                     football.transform.pos.set(transform.pos.x + .125f, transform.pos.y + .125f, 0);
                 }
                 useAnimation(ANIM_WALK);
             } else {
                 if (hasBall) {
                     football.transform.pos.set(transform.getEntityPosX() + .125f, transform.getEntityPosY() + .125f, 0);
                 }
                 useAnimation(ANIM_IDLE);
             }
        } // Handoff Route Support

        if (userControl) {
            PlayerMarker.setLocation.x = this.transform.pos.x;
            PlayerMarker.setLocation.y = this.transform.pos.y;
        }




        if (! (canPlay || playStart)) {
            useAnimation(ANIM_PRESNAP);
        }


    }

}
