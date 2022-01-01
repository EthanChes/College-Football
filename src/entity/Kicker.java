package entity;

import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class Kicker extends Entity {
    public static int ANIM_SIZE = 3;
    public static int ANIM_KICK = 2;
    public static int ANIM_MOVE = 1;
    public static int ANIM_IDLE = 0;

    public boolean hasKicked = false;
    public boolean canStart = false;
    public double timeKicked = 0;
    public boolean preventDoubleKick = true;

    public Kicker(Transform transform) {
        super(ANIM_SIZE, transform);

        setAnimation(ANIM_IDLE, new Animation(1,1,"widereceiveridle"));
        setAnimation(ANIM_MOVE, new Animation(4,16,"widereceiverrouterun"));
        setAnimation(ANIM_KICK, new Animation(2,2, "kick"));
    }

    public Vector2f runToBall(World world, float delta) {
        Vector2f movement = new Vector2f();

        movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y,delta));

        return movement;
    }

    public void kick(Window window) {
        if (window.getInput().isKeyPressed(GLFW_KEY_SPACE) && ! playStart && GameManager.selectedPlay) {
            canStart = true;
            KickMarker.stop = true;
        }
    }

    public void kickoff(World world) {
        Football.kickoff = true;
        this.kickPower -= (20 - KickMarker.level)/10;
        System.out.println(KickMarker.level + " Actual");
        Football.ball_slope = (20-KickMarker.level)/(15*kickAccuracy);

        Random rand = new Random();
        boolean random = rand.nextBoolean();
        if (random)
            Football.ball_slope *= -1;

        uniqueEvents = true;
        hasBall = false;
        world.setBallCarrier(world.getFootballEntity());

        if (GameManager.offenseBall)
            GameManager.offenseBall = false;
        else
            GameManager.offenseBall = true;

        world.getFootballEntity().useAnimation(2);
    }

    public void punt(World world) {
        Football.punt = true;
        this.kickPower -= ((20 - KickMarker.level)/10);
        System.out.println(KickMarker.level + " Actual");
        Football.ball_slope = (20-KickMarker.level)/(15*kickAccuracy);

        Random rand = new Random();
        boolean random = rand.nextBoolean();
        if (random)
            Football.ball_slope *= -1;

        uniqueEvents = true;
        hasBall = false;
        world.setBallCarrier(world.getFootballEntity());


        if (GameManager.offenseBall)
            GameManager.offenseBall = false;
        else
            GameManager.offenseBall = true;

        world.getFootballEntity().useAnimation(2);
    }

    public void fieldGoal(World world) {
        Football.fieldGoal = true;
        this.kickPower -= ((20 - KickMarker.level)/10);
        System.out.println(KickMarker.level + " Actual");
        Football.ball_slope = (world.getFootballEntity().transform.pos.y - world.getGoalPost().transform.pos.y)/(world.getFootballEntity().transform.pos.x - world.getGoalPost().transform.pos.x);
        Football.ball_slope -= (20-KickMarker.level)/(15*kickAccuracy);
        uniqueEvents = true;
        hasBall = false;
        world.setBallCarrier(world.getFootballEntity());

        world.getFootballEntity().useAnimation(2);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();
        boolean notMovingAlready = true;

        if ((hasBall && GameManager.userOffense) || forceUserControl) {
            userControl = true;
        } else {
            userControl = false;
        }

        if (forceUserControl) {
            userTackle(window, this, world.getBallCarrier(), world);
        }

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && userControl) { // When S is pressed, player moves 5 down
            movement.add(0,-speed*delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && userControl) { // When A is pressed, camera shifts left 5
            movement.add(-speed*delta/2,0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && userControl) { // When W is pressed, camera shifts up 5
            movement.add(0,speed*delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && userControl) { // When D is pressed, camera shifts right 5
            movement.add(speed*delta,0);
        }

        kick(window);

        if (canStart) {
            if (!uniqueEvents) {
                switch (route) {
                    case 0: // Kickoff
                        // Run to ball
                        if (!hasKicked) {
                            movement.add(runToBall(world, delta));
                            move(movement);
                            notMovingAlready = false;

                            if (this.transform.pos.distance(world.getFootballEntity().transform.pos) < .5f) {
                                hasKicked = true;
                                kickoffSnap();
                                timeKicked = Timer.getTime();
                            }
                        } else {
                            // Kick Ball
                            if (preventDoubleKick) {
                                kickoff(world);
                                preventDoubleKick = false;
                            }
                        }

                        break;
                    case 1: // Field Goal
                        if (canStart) {
                            if (! hasKicked) {
                                snap(window, world);
                                if (world.getQuarterbackEntity().hasBall) {
                                    movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
                                }

                                if (this.transform.pos.distance(world.getFootballEntity().transform.pos) < .5f) {
                                    kickoffSnap();
                                    world.getQuarterbackEntity().hasBall = false;
                                    world.setBallCarrier(world.getFootballEntity());
                                    hasKicked = true;
                                    timeKicked = Timer.getTime();
                                }
                            } else {
                                if (preventDoubleKick) {
                                    fieldGoal(world);
                                    preventDoubleKick = false;
                                }
                            }
                        }
                        break;
                    case 2: // Punt
                        if (canStart) {
                            if (! hasKicked) {
                                snap(window, world);

                                if (this.transform.pos.distance(world.getFootballEntity().transform.pos) < .5f) {
                                    kickoffSnap();
                                    timeKicked = Timer.getTime();
                                    hasKicked = true;
                                }
                            } else {
                                if (preventDoubleKick) {
                                    punt(world);
                                    preventDoubleKick = false;
                                }
                            }
                        }
                        break;
                }
            } // end of uniqueevents false
        }

        if (uniqueEvents && ! userControl) {
            if (timeFumble > 0 && getAnimationIndex() != 3) {
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
                        if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && !GameManager.offenseBall && world.getBallCarrier() != world.getFootballEntity()) {
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

        if (notMovingAlready && canPlay)
            move(movement);

        if (timeKicked + 1 > Timer.getTime())
            useAnimation(ANIM_KICK);
        else if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_MOVE);
        } else {
            useAnimation(ANIM_IDLE);
        }

        if (! canStart) {
            world.getKickMarker().transform.pos.set(this.transform.pos.x - 5, this.transform.pos.y - 12 + KickMarker.level/1.17f,0);
            world.getKickLevel().transform.pos.set(this.transform.pos.x - 5,this.transform.pos.y,0);
        } else {
            world.getKickMarker().transform.pos.set(0,0,0);
            world.getKickLevel().transform.pos.set(0,0,0);
        }

        if (userControl) {
            PlayerMarker.setLocation.x = this.transform.pos.x;
            PlayerMarker.setLocation.y = this.transform.pos.y;
        }



    }
}
