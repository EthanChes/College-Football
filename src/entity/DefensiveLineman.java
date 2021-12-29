package entity;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class DefensiveLineman extends Entity {
    public static final int ANIM_SIZE = 5;
    public static final int ANIM_PRESNAP = 4;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_UNKNOWN = 2;
    public static final int ANIM_MOVE = 1;
    public static final int ANIM_IDLE = 0;

    public DefensiveLineman(Transform transform) {
        super(ANIM_SIZE, transform);
        uniqueEvents = false;
        setAnimation(ANIM_IDLE, new Animation(1, 1, "defensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(4,16,"defensivemovement"));
        setAnimation(ANIM_UNKNOWN, new Animation(0,0, "defensivelinemovement"));
        setAnimation(ANIM_FALL, new Animation(1,1, "defensivefall"));
        setAnimation(ANIM_PRESNAP, new Animation(1,1, "presnap/defensiveline"));
        speed = 7f; // 8
        strength = 10f; // 10
    }

    public Vector2f pursuit(Entity ballCarrier, float delta, World world) {
        Vector2f movement = new Vector2f();

        switch (route) {
            case 0 : movement.add(defensive_movement(world.getBallCarrier(),delta)); break;
            case 1 :
                if (routeMovement <= 7) { movement.add(-speed*delta/2,speed*delta/3); routeMovement += new Vector2f(-speed*delta/2,speed*delta/3).distance(0,0);  }
                else { movement.add(defensive_movement(world.getBallCarrier(),delta)); }
                break;
            case 2 :
                if (routeMovement <= 7) { movement.add(-speed*delta/2,-speed*delta/3); routeMovement += new Vector2f(-speed*delta/2,-speed*delta/3).distance(0,0); }
                else { movement.add(defensive_movement(world.getBallCarrier(),delta)); }
                break;
        }

        return movement;
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (! (canPlay || playStart) && this.transform.pos.x - 2f < GameManager.ballPosX) {
            movement.add(speed*delta,0);
        } else if (! (canPlay || playStart) && forceUserControl) {
            canCollide = false;
        } else if (Football.timeSnapped + .3f > Timer.getTime()) {
            canCollide = true;
        }

        selectDefensivePlayer(window, world);

        if ( (! GameManager.userOffense) && hasBall) userControl = true; // change false to gamemanager on defense, make sure to have ids for different defenders to switch through them
        else if (forceUserControl && ! GameManager.userOffense) userControl = true;
        else userControl = false;

        if (forceUserControl) {
            userTackle(window, this, world.getBallCarrier(), world);
        }

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && userControl && ! pancaked && ! isBeingMovedExternally) { // When S is pressed, player moves 5 down
            movement.add(0, -speed * delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && userControl && ! pancaked && ! isBeingMovedExternally) { // When A is pressed, camera shifts left 5
            movement.add(-speed * delta, 0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && userControl && ! pancaked && ! isBeingMovedExternally) { // When W is pressed, camera shifts up 5
            movement.add(0, speed * delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && userControl && ! pancaked && ! isBeingMovedExternally) { // When D is pressed, camera shifts right 5
            movement.add(speed * delta, 0);
        }




        if (world.getBallCarrier().transform.pos.x > this.transform.pos.x && ! uniqueEvents) {
            move(new Vector2f(speed*delta,0));
            uniqueEvents = true;
        }





        if (! userControl) {
            if (canPlay && (!uniqueEvents) && (!pancaked) && !isBeingMovedExternally) {
                movement.add(pursuit(world.getBallCarrier(), delta, world));
            } else if (timeFumble > 0 && getAnimationIndex() != 3) {
                movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
            } else if (uniqueEvents && canPlay && world.getBallCarrier() != world.getFootballEntity() && !(pancaked || isBeingMovedExternally)) {
                canCollide = true;
                if (GameManager.offenseBall) {
                    movement.add(defensive_movement(world.getBallCarrier(), delta));
                } else {
                    if (hasBall) {
                        // Search For Nearby Players Too
                        movement.add(defenseHasBallMove(world, delta));
                    } else {
                        movement.add(defenseBlockUnique(world, delta));
                    }
                }
            }
        }

        if ((userControl && ! playStart) || (canPlay && ! pancaked && ! isBeingMovedExternally)) {
            move(movement);
        }
        else
        { isBeingMovedExternally = false; } // reset isBeingMovedExternally

        if (pancaked) {
            useAnimation(ANIM_FALL);
            canCollide = false;
            if (Timer.getTime() > timePancaked + 3) {
                pancaked = false;
                canCollide = true;
            }
        }
        else if (movement.x != 0 || movement.y != 0 || (canPlay && GameManager.offenseBall && !userControl)) { // This may be where a potential error could occur because a user controlled player will always show run anim.
            useAnimation(ANIM_MOVE);
        }
        else if (isBeingMovedExternally) {
            useAnimation(ANIM_MOVE);
        }
        else if (lastAnimationChange + .5f < Timer.getTime()){
            useAnimation(ANIM_IDLE);
            lastAnimationChange = Timer.getTime();
        }

        if (hasBall) {
            world.getFootballEntity().transform.pos.set(this.transform.pos);
        }

        if (canCollide && collidingWithBallCarrier(this,world)) {
            if (world.getBallCarrier() == world.getFootballEntity()) {
                if (collidingWithFootball(this,world)); // Interception, keep this nothing for now?
            }
            else if (canCollide) {
                if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && GameManager.offenseBall && tackle(world.getBallCarrier(), window, world)) {
                    world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                    canPlay = false;
                }
            }

        }

        if (! canPlay && world.getBallCarrier() == this) {
            useAnimation(ANIM_FALL);
            if (hasBall) {
                world.getFootballEntity().transform.pos.set(this.transform.pos.x, this.transform.pos.y, 0);
            }
        }

        if (movement.x != 0 || movement.y != 0 && ! (canPlay || playStart)) {
            useAnimation(ANIM_MOVE);
        }
        else if (! (canPlay || playStart)) {
            useAnimation(ANIM_PRESNAP);
        } else if (getAnimationIndex() == ANIM_PRESNAP) {
            useAnimation(ANIM_IDLE);
        }

        if (userControl) {
            PlayerMarker.setLocation.x = this.transform.pos.x;
            PlayerMarker.setLocation.y = this.transform.pos.y;
        }

    }
}