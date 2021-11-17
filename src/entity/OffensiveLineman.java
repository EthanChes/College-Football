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
    public static final int ANIM_SIZE = 4;
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_MOVE = 1;
    public static final int ANIM_BLOCK = 2;
    public static final int ANIM_BLOCK_MOVING = 3;

    public boolean isBlocking = false;
    public boolean wonBlock = false;
    public double timeSinceBlock = 0;

    public OffensiveLineman (Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"offensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(3,12, "offensivelinemove"));
        setAnimation(ANIM_BLOCK, new Animation(1,1, "offensivelineblock"));
        setAnimation(ANIM_BLOCK_MOVING, new Animation(4, 16, "offensivelineblockmoving"));
        speed = 3f;
        strength = 10f;
    }

    public Vector2f passBlockMovement(float delta, World world) {
        Vector2f move = new Vector2f();
        boolean hasBlockedPlayerInLoop = false;

        if (! isBlocking) {
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
            if (defender.transform.pos.x > this.transform.pos.x || route == 0) {

                Collision blocking = this.bounding_box.getCollision(defender.bounding_box);

                if (blocking.isIntersecting) {
                    hasBlockedPlayerInLoop = true;
                    move.add(passBlock(defender, delta, world));
                }
            } else { // For Outside Tackles
                move.add(-speed*delta,0);
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

        if (rand_output <= this.strength*100 && timeSinceBlock + .5 < Timer.getTime()) {
            timeSinceBlock = Timer.getTime();
            wonBlock = true;
        } else if (timeSinceBlock + .5 < Timer.getTime()) {
            timeSinceBlock = Timer.getTime();
            wonBlock = false;
        }

        if (wonBlock && this.transform.pos.x < player.transform.pos.x) {
            player.move(new Vector2f(this.strength*delta/5, 0));
            player.isBeingMovedExternally = true;
        } else {
            float blitzerPushesLineAwayFromQB;
            if (player.transform.pos.y > world.getQuarterbackEntity().transform.pos.y) {
                blitzerPushesLineAwayFromQB = -(player.strength* delta)/5;
            } else {
                blitzerPushesLineAwayFromQB = (player.strength * delta)/5;

                movement.add(new Vector2f(-(player.strength * delta)/5, blitzerPushesLineAwayFromQB));
                player.move(new Vector2f(-player.strength*delta/5,-blitzerPushesLineAwayFromQB));
                player.isBeingMovedExternally = true;
            }
        }
        return movement;
    }

    public Vector2f runBlockMovement(float delta, World world) {
        Vector2f move = new Vector2f();
        boolean hasBlockedPlayerInLoop = false;

        if (! isBlocking && this.transform.pos.x - 5 < world.getBallCarrier().transform.pos.x) {
            switch (route) {
                case 1:
                    move.add(speed * delta, speed * delta / 5);
                    break;
                case 2:
                    move.add(speed * delta, -speed * delta / 5);
                    break;
            }
        }
        else { move.add(speed*delta/5,0); }

        move(move);
        move.set(.00001f,.00001f);

        for (int i = 0; i < 11; i++) { // Check if any defenders are higher up than the OL : Note This Loop May Cause Game Rendering Issues, if there are any issues remove this
            Entity defender = world.getCountingUpEntity(i);

            Collision blocking = defender.bounding_box.getCollision(this.bounding_box);

            if (blocking.isIntersecting) { hasBlockedPlayerInLoop = true; move.add(runBlock(defender, delta, world)); }
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

        if (rand_output <= this.strength*100 && timeSinceBlock + .1 < Timer.getTime()) { wonBlock = true; timeSinceBlock = Timer.getTime(); }
        else if (timeSinceBlock + .1 < Timer.getTime()) { wonBlock = false; timeSinceBlock = Timer.getTime(); }

        float yPush;

        switch (route) {
            case 1 : yPush = this.strength*delta/25f; break;

            case 2 : yPush = -this.strength*delta/25f; break;

            default : yPush = 0;
        }

        if (wonBlock) {

            player.move(new Vector2f((this.strength * delta)/5, 3*yPush));
            movement.add(this.strength * delta/5, yPush);
            player.isBeingMovedExternally = true;
        } else {
            player.move(new Vector2f(-player.strength*delta/5,-yPush));
            movement.add(-player.strength*delta/5,-yPush);
            player.isBeingMovedExternally = true;
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
            System.out.println(rand_output);
            if (rand_output == 3) {
                world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                canPlay = false;
            }
        }

        if (isBlocking || (movement.x != 0 || movement.y != 0)) {
            useAnimation(ANIM_BLOCK_MOVING);
        }
        else {
            useAnimation(ANIM_BLOCK);
        }
    }
}
