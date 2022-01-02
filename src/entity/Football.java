package entity;

import collision.Collision;
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
    public static boolean keepMoving = false;
    public static boolean puntEndsInBounds = false;

    public static double passDropStart = 0;
    public static Vector2f fumbleMovements = new Vector2f();

    public static boolean fieldGoal = false;
    public static boolean kickoff = false;
    public static boolean punt = false;

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

                throw_height = projLoc.distance(this.transform.pos.x,this.transform.pos.y) + 2;

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
                if (world.getFootballEntity() == world.getBallCarrier()) {
                    canPlay = false;
                }
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

        if (timeFumble + .9f > Timer.getTime() && canPlay) {
            if (fumbleMovements.x == 0 && fumbleMovements.y == 0) {
                Random rand = new Random();

                world.getBallCarrier().hasBall = false;
                world.setBallCarrier(this);

                float setX = rand.nextInt(500) - 250;
                float setY = rand.nextInt(500) - 250;

                // Set X and Y Vectors for Fumble Movements if They Are Too Low
                if (setX < 100 && setX > -100) {
                    setX = 100;
                }

                if (setY < 100 && setY > -100) {
                    setY = 100;
                }

                fumbleMovements.set(setX/25*delta, setY/25*delta);

                for (int i = 0; i < 22; i++) {
                    world.getCountingUpEntity(i).uniqueEvents = true;
                }
            }

            movement.add(fumbleMovements);

        }


        if (timeFumble > 0 && canPlay) {
            for (int i = 0; i < 22 && timeFumble != -1; i++) {
                Collision collide = world.getFootballEntity().bounding_box.getCollision(world.getCountingUpEntity(i).bounding_box);

                if (collide.isIntersecting && world.getCountingUpEntity(i).timeFumbled + 3 < Timer.getTime() && ! (world.getCountingUpEntity(i).pancaked || world.getCountingUpEntity(i).isBeingMovedExternally)) {
                    if (i < 11) {
                        GameManager.offenseBall = false;
                        if (GameManager.userOffense) {
                            deselectAllDefenders(world);
                        }
                        turnover = true;
                    } else {
                        GameManager.offenseBall = true;
                        if (GameManager.userOffense) {
                            setAllOffenseForceUserControlFalse(world);
                        }

                        turnover = false;
                    }

                    useAnimation(ANIM_QB_THROW_START);
                    world.getFootballEntity().transform.pos.set(world.getCountingUpEntity(i).transform.pos);
                    world.getCountingUpEntity(i).hasBall = true;
                    world.setBallCarrier(world.getCountingUpEntity(i));
                    System.out.println("Ball Picked Up");
                    timeFumble = -1;
                }
            }
        } else {
            timeFumble = -1;
        }

        if (kickoff) {
            if (gotWideReceiverPos) {
                Entity.turnover = true;
                speed = world.getQuarterbackEntity().kickPower * 3f * delta;
                throw_height = world.getQuarterbackEntity().kickPower*4f;

                gotWideReceiverPos = false;
            }

            movement.add(speed,ball_slope*speed);

            if (throw_height > 0) {
                throw_height -= 8*delta;
            } else {
                useAnimation(ANIM_QB_THROW_START);
                kickoff = false;
                timeFumble = Timer.getTime();
            }
        }

        if (punt) {
            if (gotWideReceiverPos) {
                Entity.turnover = true;
                world.setBallCarrier(this);
                speed = world.getQuarterbackEntity().kickPower*3*delta;
                throw_height = world.getQuarterbackEntity().kickPower*3.5f;

                gotWideReceiverPos = false;
            }

            useAnimation(ANIM_QB_THROW);

            movement.add(speed, ball_slope*speed);

            if (throw_height > 0) {
                throw_height -= 8*delta;
            } else {
                useAnimation(ANIM_QB_THROW_START);
                punt = false;
                if (world.getBallCarrier() == world.getFootballEntity()) {
                    canPlay = false;
                }
            }
        }

        if (fieldGoal) {
            if (gotWideReceiverPos) {
                world.setBallCarrier(this);
                speed = world.getCountingUpEntity(12).kickPower*3*delta;
                throw_height = world.getCountingUpEntity(12).kickPower*3f;

                gotWideReceiverPos = false;
            }

            useAnimation(ANIM_QB_THROW);

            movement.add(speed,ball_slope*speed);

            if (this.transform.pos.x > world.getGoalPost().transform.pos.x && this.transform.pos.y < world.getGoalPost().transform.pos.y + 5 && this.transform.pos.y > world.getGoalPost().transform.pos.y - 5 && throw_height > 2) {
                fieldGoal = false;
                throw_height = 0;
                System.out.println("GOOD");

                if (GameManager.userHome && GameManager.userOffense) {
                    if (! GameManager.pat) {
                        GameManager.homeScore += 3; GameManager.kickoff = true;
                    } else {
                        GameManager.homeScore += 1;
                    }
                    GameManager.scoreHome = true;
                }
                else if (! GameManager.userHome && ! GameManager.userOffense) {
                    if (! GameManager.pat) {
                        GameManager.homeScore += 3; GameManager.kickoff = true;
                    } else {
                        GameManager.homeScore += 1;
                    }
                    GameManager.scoreHome = true;
                }
                else {
                    if (! GameManager.pat) {
                        GameManager.awayScore += 3; GameManager.kickoff = true;
                    } else {
                        GameManager.awayScore += 1;
                    }
                    GameManager.scoreAway = true;
                }
            }

            if (throw_height > 0) {
                throw_height -= 8*delta;
            } else {
                useAnimation(ANIM_QB_THROW_START);
                fieldGoal = false;
                canPlay = false;
                System.out.println("NO GOOD");

                GameManager.down = 4;

                Entity.incompletePass = true;
            }
        }

        if (canPlay || keepMoving) {
            move(movement);
        }

        for (int i = 0; i < 22; i++) {
            if (world.getBallCarrier() == world.getCountingUpEntity(i) && ! canPlay) {
                world.getFootballEntity().transform.pos.set(world.getCountingUpEntity(i).transform.pos);
            }
        }

        if (Entity.incompletePass) {
            useAnimation(ANIM_QB_THROW_START);
        }

        if (camera.getProjMultiplierX() == 1 && camera.getProjMultiplierY() == 1) {
            camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .07f); // Camera adjusts to center football
        }
    }
}
