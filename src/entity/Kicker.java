package entity;

import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

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
        }
    }

    public void kickoff(World world) {
        Football.kickoff = true;
        Football.ball_slope = 0;

        if (GameManager.offenseBall)
            GameManager.offenseBall = false;
        else
            GameManager.offenseBall = true;

        world.getFootballEntity().useAnimation(2);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        kick(window);

        if (canStart) {
            if (!uniqueEvents) {
                switch (route) {
                    case 0: // Kickoff
                        // Run to ball
                        if (!hasKicked) {
                            movement.add(runToBall(world, delta));
                            move(movement);

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
                        break;
                    case 2: // Punt
                        break;
                }
            } // end of uniqueevents false
        }

        if (timeKicked + 1 > Timer.getTime())
            useAnimation(ANIM_KICK);
        else if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_MOVE);
        } else {
            useAnimation(ANIM_IDLE);
        }



    }
}
