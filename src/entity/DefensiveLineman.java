package entity;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;
import static org.lwjgl.glfw.GLFW.*;

public class DefensiveLineman extends Entity {
    public static final int ANIM_SIZE = 2;
    public static final int ANIM_MOVE = 1;
    public static final int ANIM_IDLE = 0;

    public DefensiveLineman(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(1, 1, "defensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(4,16,"defensivemovement"));
        speed = 5f;
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

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (false && hasBall) userControl = true; // change false to gamemanager on defense, make sure to have ids for different defenders to switch through them
        else if (false) userControl = true;
        else userControl = false;

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && hasBall) { // When S is pressed, player moves 5 down
            movement.add(0, -speed * delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && hasBall) { // When A is pressed, camera shifts left 5
            movement.add(-speed * delta, 0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && hasBall) { // When W is pressed, camera shifts up 5
            movement.add(0, speed * delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && hasBall) { // When D is pressed, camera shifts right 5
            movement.add(speed * delta, 0);
        }

        movement.add(defensive_movement(world.getBallCarrier(), delta));
        if (canPlay)
        move(movement);

        if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_MOVE);
        }
        else {
            useAnimation(ANIM_IDLE);
        }

        if (collidingWithBallCarrier(this,world)) {
            if (world.getBallCarrier() == world.getFootballEntity()) {
                if (collidingWithFootball(this,world)); // Interception, keep this nothing for now?
            }
            else {
                world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                canPlay = false;
            }

        }

    }
}