package entity;
import collision.Collision;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;
import java.util.Random;

public class OffensiveLineman extends Entity {
    public static final int ANIM_SIZE = 5;
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_MOVE = 1;
    public static final int ANIM_BLOCK = 2;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_BLOCK_MOVING = 4;

    public boolean isBlocking = false;
    public byte blockOutcome = 0;
    public double timeSinceBlock = 0;

    public OffensiveLineman (Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"offensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(3,12, "offensivelinemove"));
        setAnimation(ANIM_BLOCK, new Animation(1,1, "offensivelineblock"));
        setAnimation(ANIM_FALL, new Animation(1,1,"offensivefall"));
        setAnimation(ANIM_BLOCK_MOVING, new Animation(4, 16, "offensivelineblockmoving"));
        speed = 3f;
        strength = 10f;
    }

    public Vector2f passBlockMovement(float delta, World world) {
        Vector2f move = new Vector2f();
        boolean hasBlockedPlayerInLoop = false;

        if (! isBlocking && ! pancaked) {
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

        move(move);
        move.set(.00001f,.00001f);

        for (int i = 0; i < 11; i++) { // Check if any defenders are higher up than the OL : Note This Loop May Cause Game Rendering Issues, if there are any issues remove this
            Entity defender = world.getCountingUpEntity(i);

            if (this.transform.pos.x < defender.transform.pos.x && (! (defender.pancaked || this.pancaked))) {
                Collision blocking = this.bounding_box.getCollision(defender.bounding_box);

                if (blocking.isIntersecting) {
                    hasBlockedPlayerInLoop = true;
                    move.add(passBlock(defender, delta, world));
                }
            }
        }

        if (hasBlockedPlayerInLoop) {
            isBlocking = true;
        } else { isBlocking = false; }

        return move;
    }

    public Vector2f passBlock(Entity player, float delta, World world) { // This Needs Work
        Vector2f movement = new Vector2f();
        Random rand = new Random();

        int rand_output = rand.nextInt((int) ((this.strength * 100) + (player.strength * 100)));



        if (rand_output <= this.strength * 10 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 2; timeSinceBlock = Timer.getTime(); }
        else if (rand_output <= this.strength*100 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 1; timeSinceBlock = Timer.getTime(); }
        else if (rand_output <= this.strength*100 + player.strength * 10 && timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 4; timeSinceBlock = Timer.getTime(); }
        else if (timeSinceBlock + 1 < Timer.getTime()) { blockOutcome = 3; timeSinceBlock = Timer.getTime(); }

        float yPush;

        if (this.transform.pos.y < world.getQuarterbackEntity().transform.pos.y) {
            yPush = this.strength*delta/5;
        }
        else {
            yPush = this.strength*delta/5;
        }

        if (blockOutcome == 1) { // OL Pushes DL back
            player.move(new Vector2f((this.strength * delta)/5, yPush*2));
            movement.add(this.strength * delta/5, yPush);
            player.isBeingMovedExternally = true;
        }
        else if (blockOutcome == 2) { // OL Pancakes DL
            player.pancaked = true;
            player.timePancaked = Timer.getTime();
            timeSinceBlock -= 3;
        }
        else if (blockOutcome == 3) { // DL Pushes OL back
            player.move(new Vector2f(-player.strength*delta/5,-yPush));
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

        if (blockOutcome == 1) {
            player.move(new Vector2f((this.strength * delta)/5, 3*yPush));
            movement.add(this.strength * delta/5, yPush);
            player.isBeingMovedExternally = true;
        }
        else if (blockOutcome == 2) { // OL Pancakes DL
            player.pancaked = true;
            player.timePancaked = Timer.getTime();
            timeSinceBlock -= 3;
        }
        else if (blockOutcome == 3) {
            player.move(new Vector2f(-player.strength*delta/5,-yPush));
            movement.add(-player.strength*delta/4,-yPush);
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

        if (route == 0 && canPlay) {
            movement.add(passBlockMovement(delta,world));
        } else if (canPlay) { movement.add(runBlockMovement(delta,world)); }

        if (canPlay)
            move(movement);

        if (collidingWithBallCarrier(this,world) && world.getBallCarrier() != world.getFootballEntity() && route == 0) { // If offensivelineman hits ballcarrier, they can fall
            Random rand = new Random();
            int rand_output = rand.nextInt(300);
            if (rand_output == 3) {
                world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                canPlay = false;
            }
        }

        if (pancaked) {
            useAnimation(ANIM_FALL);
            canCollide = false;
            if (Timer.getTime() > timePancaked + 3) {
                pancaked = false;
                canCollide = true;
            }
        }
        else if (isBlocking || (movement.x != 0 || movement.y != 0)) {
            useAnimation(ANIM_BLOCK_MOVING);
        }
        else {
            useAnimation(ANIM_BLOCK);
        }
    }
}