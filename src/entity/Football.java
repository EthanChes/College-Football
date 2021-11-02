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

    public static float ball_slope;
    public static float distance_multiplier;

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
                throw_power = world.getQuarterbackEntity().throw_power * 2.5f;
                Entity wideReceiver = world.getSpecifiedEntity(WideReceiver.totalReceivers + Quarterback.receiverPass + 1);
                Vector2f currentLocation = getProjectedLocation(wideReceiver, this, delta,world);
                wideReceiverX = currentLocation.x;
                wideReceiverY = currentLocation.y;

                // Calculate Slope to get to receiver
                this.ball_slope = (this.transform.pos.y - wideReceiverY)/(this.transform.pos.x - wideReceiverX);

                this.distance_multiplier =  (float) ((throw_power*delta) / (Math.sqrt(Math.pow(throw_power*delta,2) + Math.pow(throw_power*delta*ball_slope,2))));

                throw_height = currentLocation.distance(this.transform.pos.x,this.transform.pos.y);

                gotWideReceiverPos = false;
            }

                movement.add(throw_power*delta*distance_multiplier,throw_power*delta*ball_slope*distance_multiplier); // Ball Movements



            if (throw_height > 0) {
                throw_height -= (throw_power*delta);
                System.out.println(throw_height);
            }
            else {
                pass = false;
                useAnimation(1);
            }
        }

        move(movement);

        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .07f); // Camera adjusts to center football
    }
}
