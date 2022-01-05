package entity;
import collision.Collision;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class OffensiveLineman extends Entity {
    public static final int ANIM_SIZE = 8;
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_MOVE = 1;
    public static final int ANIM_BLOCK = 2;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_BLOCK_MOVING = 4;
    public static final int ANIM_PRESNAP = 5;
    public static final int ANIM_CENTER = 6;
    public static final int ANIM_PLACE_HOLDER = 7;

    public boolean isBlocking = false;
    public byte blockOutcome = 0;
    public double timeSinceBlock = 0;

    public OffensiveLineman (Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"offensivelineidle",true));
        setAnimation(ANIM_MOVE, new Animation(3,12, "offensivelinemove",true));
        setAnimation(ANIM_BLOCK, new Animation(1,1, "offensivelineblock",true));
        setAnimation(ANIM_FALL, new Animation(1,1,"offensivefall",true));
        setAnimation(ANIM_BLOCK_MOVING, new Animation(4, 16, "offensivelineblockmoving",true));
        setAnimation(ANIM_PRESNAP, new Animation(1,1, "presnap/offensiveline",true));
        setAnimation(ANIM_CENTER, new Animation(2, 4, "presnap/center",true));
        setAnimation(ANIM_PLACE_HOLDER, new Animation(1,1, "placeholder",true));
        speed = 6f;
        strength = 10f;
    }

    public Vector2f passBlockMovement(float delta, World world) {
        Vector2f move = new Vector2f();
        boolean pocketMovement = true;
        boolean hasBlockedPlayerInLoop = false;

        for (int i = 0; i < 11; i++) { // Check if any defenders are higher up than the OL : Note This Loop May Cause Game Rendering Issues, if there are any issues remove this
            Entity defender = world.getCountingUpEntity(i);

            if (this.transform.pos.x < defender.transform.pos.x && (! (defender.pancaked || this.pancaked))) {
                Collision blocking = this.bounding_box.getCollision(defender.bounding_box);

                if (blocking.isIntersecting) {
                    hasBlockedPlayerInLoop = true;
                    move.add(passBlock(defender, delta, world));
                }
            }
            else if (pocketMovement && this.route == -1 && ! this.pancaked) {
                pocketMovement = false;
                move.add(-speed*delta,0);
            }
        }

        if (hasBlockedPlayerInLoop) {
            isBlocking = true;
        } else { isBlocking = false; }

        if (pocketMovement && ! isBlocking && ! pancaked) {
            if (this.transform.pos.distance(world.getBallCarrier().transform.pos) > 5.5) {
                if (this.transform.pos.x + this.speed*delta > world.getBallCarrier().transform.pos.x) {
                    move.add(-this.speed*delta,0);
                }
                else if (this.transform.pos.x - this.speed * delta < world.getBallCarrier().transform.pos.x) {
                    move.add(this.speed*delta,0);
                }

                if (this.transform.pos.y + this.speed*delta > world.getBallCarrier().transform.pos.y) {
                    move.add(0,-speed*delta);
                }
                else if (this.transform.pos.y - this.speed*delta < world.getBallCarrier().transform.pos.y) {
                    move.add(0,speed*delta);
                }
            }
        }

        return move;
    }

    public Vector2f passBlock(Entity player, float delta, World world) { // This Needs Work
        Vector2f movement = new Vector2f();
        Random rand = new Random();

        int rand_output = rand.nextInt((int) ((this.strength * 100) + (player.strength * 100)));



        if (rand_output <= this.strength * 10 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 2; timeSinceBlock = Timer.getTime(); }
        else if (rand_output <= this.strength*100 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 1; timeSinceBlock = Timer.getTime(); }
        else if (rand_output <= this.strength*100 + player.strength * 20 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 4; timeSinceBlock = Timer.getTime(); }
        else if (timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 3; timeSinceBlock = Timer.getTime(); }

        float yPush;

        if (this.transform.pos.y < world.getQuarterbackEntity().transform.pos.y) {
            yPush = this.strength*delta/5;
        }
        else {
            yPush = this.strength*delta/5;
        }

        if (blockOutcome == 1) { // OL Pushes DL back
            player.move(new Vector2f(this.strength*delta/4, yPush*3));
            player.routeMovement += new Vector2f(this.strength*delta/4,yPush*3).distance(0,0);
            movement.add(this.strength*delta/5, yPush);
            player.isBeingMovedExternally = true;
        }
        else if (blockOutcome == 2) { // OL Pancakes DL
            player.pancaked = true;
            player.timePancaked = Timer.getTime();
            timeSinceBlock -= 3;
        }
        else if (blockOutcome == 3) { // DL Pushes OL back
            player.move(new Vector2f(-player.strength*delta/6,-yPush));
            player.routeMovement += new Vector2f(player.strength*delta/6,yPush).distance(0,0);
            movement.add(-player.strength*delta/5,-yPush);
            player.isBeingMovedExternally = true;
        }
        else if (blockOutcome == 4) { // Defender Pancakes OL
            this.pancaked = true;
            this.timePancaked = Timer.getTime();
            timeSinceBlock -= 3;
        }
        return movement;
    }

    public Vector2f runBlockMovement(float delta, World world) {
        Vector2f move = new Vector2f();
        boolean hasBlockedPlayerInLoop = false;

        if (! isBlocking && this.transform.pos.x - 5 < world.getBallCarrier().transform.pos.x && !pancaked) {
            switch (route) {
                case 1:
                    move.add(speed * delta, speed * delta / 5);
                    break;
                case 2:
                    move.add(speed * delta, -speed * delta / 5);
                    break;
            }
        }
        else if (! pancaked) { move.add(speed*delta/5,0); }

        move(move);
        move.set(.00001f,.00001f);

        for (int i = 0; i < 11; i++) { // Check if any defenders are higher up than the OL : Note This Loop May Cause Game Rendering Issues, if there are any issues remove this
            Entity defender = world.getCountingUpEntity(i);

            if (canCollide && defender.canCollide) {
                Collision blocking = defender.bounding_box.getCollision(this.bounding_box);
                if (blocking.isIntersecting) { hasBlockedPlayerInLoop = true; move.add(runBlock(defender, delta, world)); }
            }
        }

        if (hasBlockedPlayerInLoop) {
            isBlocking = true;
        } else { isBlocking = false; }


        return move;
    }

    public Vector2f runBlock(Entity player, float delta, World world) {
        Vector2f movement = new Vector2f();
        Random rand = new Random();

        int rand_output = rand.nextInt((int) ((this.strength * 100) + (player.strength * 100)));

        if (rand_output <= this.strength * 10 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 2; timeSinceBlock = Timer.getTime(); }
        else if (rand_output <= this.strength*100 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 1; timeSinceBlock = Timer.getTime(); }
        else if (rand_output <= this.strength*100 + player.strength * 10 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 4; timeSinceBlock = Timer.getTime(); }
        else if (timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 3; timeSinceBlock = Timer.getTime(); }

        float yPush;

        switch (route) {
            case 1 : yPush = this.strength*delta/25f; break;

            case 2 : yPush = -this.strength*delta/25f; break;

            default : yPush = 0;
        }

        float defenderPush = yPush;

        switch (player.route) {
            case 1 : if (player.routeMovement <= 5) { defenderPush = player.strength*delta/3; } break;
            case 2 : if (player.routeMovement <= 5) { defenderPush = -player.strength*delta/3;} break;
            default : defenderPush = yPush;
        }

        if (blockOutcome == 1) {
            player.move(new Vector2f((this.strength * delta)/1.5f, defenderPush*3));
            player.routeMovement += defenderPush;
            movement.add(this.strength * delta/1.5f, yPush);
            player.isBeingMovedExternally = true;
        }
        else if (blockOutcome == 2) { // OL Pancakes DL
            player.pancaked = true;
            player.timePancaked = Timer.getTime();
            timeSinceBlock -= 3;
        }
        else if (blockOutcome == 3) {
            player.move(new Vector2f(-player.strength*delta/6,defenderPush));
            movement.add(-player.strength*delta/5,defenderPush);
            player.isBeingMovedExternally = true;
        }
        else if (blockOutcome == 4) { // Defender Pancakes OL
            this.pancaked = true;
            this.timePancaked = Timer.getTime();
            timeSinceBlock -= 3;
        }

        return movement;
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if ((hasBall && GameManager.userOffense) || forceUserControl) userControl = true; // change && true to gamemanager user controls offensive team
        else userControl = false;

        if (forceUserControl) {
            userTackle(window, this, world.getBallCarrier(), world);
        }

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
            if (canPlay && !(pancaked || isBeingMovedExternally)) {
                if ((route == 0 || route == -1) && !uniqueEvents) {
                    movement.add(passBlockMovement(delta, world));
                } else if (!uniqueEvents) {
                    movement.add(runBlockMovement(delta, world));
                } else if (timeFumble > 0 && getAnimationIndex() != 3) {
                    movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
                } else if (uniqueEvents && GameManager.offenseBall) {
                    if (hasBall) {
                        movement.add(speed * delta, 0);
                    } else if (!hasBall) {
                        movement.add(offenseBlockUnique(world, delta));
                    }
                } else if (uniqueEvents) {
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

        if (route == -2) {
            movement.set(0,0);
            useAnimation(ANIM_PLACE_HOLDER);
            if (hasBall)
                world.getFootballEntity().transform.pos.set(this.transform.pos);
        }

        if (canPlay && ! pancaked)
            move(movement);

        if (collidingWithBallCarrier(this,world) && world.getBallCarrier() != world.getFootballEntity() && route == 0) { // If offensivelineman hits ballcarrier, they can fall
            Random rand = new Random();
            int rand_output = rand.nextInt(300);
            if (rand_output == 3) {
                world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                canPlay = false;
            }
        }

        if (! (playStart || center)) {
            useAnimation(ANIM_PRESNAP);
        }
        else if (! (playStart || getAnimationIndex() == 6)) {
            useAnimation(ANIM_PRESNAP);
            world.getFootballEntity().transform.pos.set(this.transform.pos.x + .6f, this.transform.pos.y - .3f,0);
            world.getFootballEntity().useAnimation(1);
        }
        else if (getAnimationIndex() == 6 && timeSnapped + .4 > Timer.getTime()) {
            useAnimation(ANIM_CENTER);
        } else if (getAnimationIndex() == ANIM_PLACE_HOLDER) {
            useAnimation(ANIM_PLACE_HOLDER);
        }
        else if (pancaked) {
            useAnimation(ANIM_FALL);
            canCollide = false;
            if (Timer.getTime() > timePancaked + 3) {
                pancaked = false;
                canCollide = true;
            }
        }
        else if (canPlay || isBlocking || (movement.x != 0 || movement.y != 0)) {
            useAnimation(ANIM_BLOCK_MOVING);
        }
        else if (! canPlay && hasBall) {
            useAnimation(ANIM_FALL);
            if (hasBall || world.getBallCarrier() == this) {
                world.getFootballEntity().transform.pos.set(this.transform.pos.x, this.transform.pos.y, 0);
            }
        }
        else {
            useAnimation(ANIM_BLOCK);
        }

        if (! canPlay && world.getBallCarrier() == this) {
            useAnimation(ANIM_FALL);
            world.getFootballEntity().transform.pos.set(this.transform.pos);
        }

        if (hasBall) {
            world.getFootballEntity().transform.pos.set(this.transform.pos);
        }

        if (userControl) {
            PlayerMarker.setLocation.x = this.transform.pos.x;
            PlayerMarker.setLocation.y = this.transform.pos.y;
        }

    }
}