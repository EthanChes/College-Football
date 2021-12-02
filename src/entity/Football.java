package entity;

import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

import java.util.Random;

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

    public static double passDropStart = 0;

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
                this.throw_power = (world.getQuarterbackEntity().throw_power) * 5f;
                Entity wideReceiver = world.getSpecifiedEntity(WideReceiver.totalReceivers + Quarterback.receiverPass + 1);
                Vector2f projLoc = getProjectedLocation(wideReceiver, this, delta,world);

                Random rand = new Random();
                float rand_outputX = rand.nextInt((int) (12 - world.getQuarterbackEntity().throw_accuracy)) - (1/2 * (12 - world.getQuarterbackEntity().throw_accuracy));
                float rand_outputY = rand.nextInt((int) (12 - world.getQuarterbackEntity().throw_accuracy)) - (1/2 * (12 - world.getQuarterbackEntity().throw_accuracy));

                wideReceiverX = projLoc.x + rand_outputX;
                wideReceiverY = projLoc.y + rand_outputY;

                // Calculate Slope to get to receiver
                this.ball_slope = (this.transform.pos.y - wideReceiverY)/(this.transform.pos.x - wideReceiverX);

                throw_height = projLoc.distance(this.transform.pos.x,this.transform.pos.y);

                if (Float.isInfinite(ball_slope)) { // Recalculate ball slope in case of infinite slope
                    System.out.println("Infinite Slope");
                    ball_slope = (this.transform.pos.y - wideReceiverY) / ((this.transform.pos.x -.0001f) - wideReceiverX);
                }

                this.distance_multiplier =  (float) ((throw_power*delta) / (Math.sqrt(Math.pow(throw_power*delta,2) + Math.pow(throw_power*delta*ball_slope,2))));

                gotWideReceiverPos = false;
            }

                movement.add(throw_power*delta*distance_multiplier,throw_power*delta*ball_slope*distance_multiplier); // Ball Movements



            if (throw_height > 0) {
                throw_height -= (throw_power*delta);
            } else {
                canPlay = false;
            }

        }

        if (passDropStart != 0) {
            if (passDropStart + .375 > Timer.getTime()) {
                movement.add(-delta*2, 0);
            } else {
                passDropStart = 0;
                canPlay = false;
            }
        }

        if (timeSnapped + .25 > Timer.getTime()) {
            // Move Towards QB
            if (gotWideReceiverPos) {
                gotWideReceiverPos = false;

                this.speed = ((world.getFootballEntity().transform.pos.x - world.getQuarterbackEntity().transform.pos.x)*delta*4);
            }

            movement.add(-speed,0);
        }
        else if (timeSnapped + .27 > Timer.getTime()) {
            this.speed = 0;
            gotWideReceiverPos = true;
            world.getQuarterbackEntity().hasBall = true;
            world.setBallCarrier(world.getQuarterbackEntity());
            world.getFootballEntity().useAnimation(0);
        }

        if (canPlay) {
            move(movement);
        }

        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .07f); // Camera adjusts to center football
    }
}
