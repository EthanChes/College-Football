package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

public class Football extends Entity {
    public static final int ANIM_SIZE = 3;
    public static final int ANIM_QB_THROW = 2;
    public static final int ANIM_QB_THROW_START = 1;
    public static final int ANIM_QB_HOLD = 0;

    public static float throw_speed;
    public static float throw_height;
    public static float slow_fb_in_air = .5f;
    public static float ball_slope;

    public static float wideReceiverX;
    public static float wideReceiverY;
    public static boolean gotWideReceiverPos = true;

    public Football(Transform transform) {
        super(ANIM_SIZE, transform);
        noCollision();

        setAnimation(ANIM_QB_HOLD,new Animation(1,1,"footballqb"));
        setAnimation(ANIM_QB_THROW_START, new Animation(1,1,"footballthrowstart"));
        setAnimation(ANIM_QB_THROW, new Animation(6,50,"footballthrow"));
    }


    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f(); // for passes

        if (pass) { // Pass Algorithm Here
            useAnimation(ANIM_QB_THROW);

            if (gotWideReceiverPos) { // Gets Location of WR at time of pass
                Entity wideReceiver = world.getSpecifiedEntity(WideReceiver.totalReceivers + Quarterback.receiverPass + 1);
                this.wideReceiverX = wideReceiver.transform.pos.x;
                this.wideReceiverY = wideReceiver.transform.pos.y;
                this.throw_speed = Quarterback.throw_power * 2.5f;

                // Calculate Slope to get to receiver
                this.ball_slope = (this.transform.pos.y - wideReceiverY)/(this.transform.pos.x - wideReceiverX);

                throw_height = (wideReceiverX-transform.pos.x) + 2;

                gotWideReceiverPos = false;
            }



                movement.add(throw_speed*delta,throw_speed*delta*ball_slope); // Ball Movements




            if (throw_height > 0) {
                throw_height -= (9.8/throw_speed);
            }
            else {
                pass = false;
                System.out.println("PASS ENDED");
            }
        }

        move(movement);

        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .07f); // Camera adjusts to center football
    }
}
