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

    public double timeSinceLastTackleAttempt;

    public DefensiveLineman(Transform transform) {
        super(ANIM_SIZE, transform);
        uniqueEvents = false;
        setAnimation(ANIM_IDLE, new Animation(1, 1, "defensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(4,16,"defensivemovement"));
        setAnimation(ANIM_UNKNOWN, new Animation(0,0, "defensivelinemovement"));
        setAnimation(ANIM_FALL, new Animation(1,1, "defensivefall"));
        setAnimation(ANIM_PRESNAP, new Animation(1,1, "presnap/defensiveline"));
        speed = 3f; // 8
        strength = 6f; // 10
    }

    public Vector2f defensive_movement(Entity ballCarrier, float delta) {
        Vector2f movement = new Vector2f();

        float posX = ballCarrier.transform.pos.x;
        float posY = ballCarrier.transform.pos.y;

        if (posX - speed*delta > this.transform.pos.x) {
            movement.add(speed*delta,0);
        }
        else if (posX + speed*delta < this.transform.pos.x){ movement.add(-speed*delta,0); }
        if (posY - delta*speed > this.transform.pos.y) {
            movement.add(0,speed*delta);
        }
        else if (posY + delta*speed < this.transform.pos.y){ movement.add(0,-speed*delta); }

        return movement;
    }

    public Vector2f pursuit(Entity ballCarrier, float delta, World world) {
        Vector2f movement = new Vector2f();

        switch (route) {
            case 0 : movement.add(defensive_movement(world.getBallCarrier(),delta)); break;
            case 1 :
                if (routeMovement <= 5) { movement.add(-speed*delta/2,speed*delta/3); routeMovement += new Vector2f(-speed*delta/2,speed*delta/3).distance(0,0);  }
                else { movement.add(defensive_movement(world.getBallCarrier(),delta)); }
                break;
            case 2 :
                if (routeMovement <= 5) { movement.add(-speed*delta/2,-speed*delta/3); routeMovement += new Vector2f(-speed*delta/2,-speed*delta/3).distance(0,0); }
                else { movement.add(defensive_movement(world.getBallCarrier(),delta)); }
                break;
        }

        return movement;
    }

    public boolean tackle(Entity ballCarrier) {
        boolean tackle = false;

        Random rand = new Random();
        int rand_output = rand.nextInt((int) (this.strength*200 + ballCarrier.strength*100));

        if (rand_output <= this.strength*200) {
            tackle = true;
            System.out.println("Tackle");
        }
        else {
            System.out.println("Tackle Evaded");
        }
        timeSinceLastTackleAttempt = Timer.getTime();

        return tackle;
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (false && hasBall) userControl = true; // change false to gamemanager on defense, make sure to have ids for different defenders to switch through them
        else if (false) userControl = true;
        else userControl = false;

        // Moves Player using various WASD directions using vectors.
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




        if (world.getBallCarrier().transform.pos.x > this.transform.pos.x && ! uniqueEvents) {
            move(new Vector2f(speed*delta,0));
            uniqueEvents = true;
        }




        if (canPlay && (! uniqueEvents) && (! pancaked) && ! isBeingMovedExternally) {
            movement.add(pursuit(world.getBallCarrier(), delta,world));
        } else if (uniqueEvents && canPlay && (! pancaked) && ! isBeingMovedExternally) {
            movement.add(defensive_movement(world.getBallCarrier(),delta));
        }

        if (canPlay && ! isBeingMovedExternally) {
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
        else if (movement.x != 0 || movement.y != 0 || (canPlay && true)) { // This may be where a potential error could occur because a user controlled player will always show run anim.
            useAnimation(ANIM_MOVE);
        }
        else {
            useAnimation(ANIM_IDLE);
        }

        if (canCollide && collidingWithBallCarrier(this,world)) {
            if (world.getBallCarrier() == world.getFootballEntity()) {
                if (collidingWithFootball(this,world)); // Interception, keep this nothing for now?
            }
            else if (canCollide) {
                if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && tackle(world.getBallCarrier())) {
                    world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                    canPlay = false;
                }
            }

        }

        if (! (canPlay || playStart)) {
            useAnimation(ANIM_PRESNAP);
        }

    }
}