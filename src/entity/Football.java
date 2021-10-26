package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

public class Football extends Entity {
    public static final int ANIM_SIZE = 2;
    public static final int ANIM_QB_THROW_START = 1;
    public static final int ANIM_QB_HOLD = 0;

    public static float throw_height = 100f;
    public static float throw_speed = 50f;
    public static float slow_fb_in_air = .5f;

    public Football(Transform transform) {
        super(ANIM_SIZE, transform);
        noCollision();

        setAnimation(ANIM_QB_HOLD,new Animation(1,1,"footballqb"));
        setAnimation(ANIM_QB_THROW_START, new Animation(1,1,"footballthrowstart"));
    }


    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f(); // for passes

        if (pass) { // Pass Algorithm Here

            movement.add(throw_speed*delta,0); // pass movement
            if (throw_speed > 0) {
                throw_speed -= slow_fb_in_air; // Decrement Throw Speed, Removal of if statement results in boomerang effect
            }

            if (throw_height > 0) {
                throw_height -= (1/(throw_speed - 1)); // Stronger throw means in air longer
            }
            else {
                throw_speed = 0; // if height = 0, then throw ends
            }
        }

        move(movement);

        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .07f); // Camera adjusts to center football
    }
}
