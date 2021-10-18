package entity;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;
import static org.lwjgl.glfw.GLFW.*;

public class WideReceiver extends Entity {
    public static final int ANIM_SIZE = 2;
    public static final int ANIM_RUN = 1;
    public static final int ANIM_IDLE = 0;

    public static boolean hasBall = false;
    public static float routeMovement = 0f;
    public static float speed = 10f;

    public WideReceiver(Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"widereceiveridle"));
        setAnimation(ANIM_RUN, new Animation(4,16,"widereceiverrouterun"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();
        boolean able_move;

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && hasBall) { // When S is pressed, player moves 5 down
            movement.add(0,-speed*delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && hasBall) { // When A is pressed, camera shifts left 5
            movement.add(-speed*delta,0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && hasBall) { // When W is pressed, camera shifts up 5
            movement.add(0,speed*delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && hasBall) { // When D is pressed, camera shifts right 5
            movement.add(speed*delta,0);
        }

        if (!hasBall) { // Route Movements
            if (routeMovement <= 80) { // 80 is full length of field. 20 tiles means 4/tile. each tile represents 5 yds. So 4pts/5yds
                movement.add(speed * delta, 0);
                routeMovement += speed * delta; // total movement so far, for change of route direction etc.
            }
        }



        if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_RUN);
        } else {
            useAnimation(ANIM_IDLE);
        }



        move(movement);
    }

}