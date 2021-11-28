package entity;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import java.util.Random;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class DefensiveBack extends Entity {
    public static final int ANIM_SIZE = 5;
    public static final int ANIM_PRESNAP = 4;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_UNKNOWN = 2;
    public static final int ANIM_MOVE = 1;
    public static final int ANIM_IDLE = 0;

    public double timeSinceLastTackleAttempt;
    public double timeSinceLastCoverageAttempt = -12f;
    public static int guardedReceivers = 0;
    public int guardedReceiver = 0;
    public boolean setLoc = true;
    public int defenderID = 0; // For Linebackers (Consistent Placement)
    public Vector2f coverageMovement = new Vector2f(0,0);
    public Vector2f receiverKnownPos = new Vector2f(0,0);

    public DefensiveBack(Transform transform) {
        super(ANIM_SIZE, transform);
        uniqueEvents = false;
        canCollide = false;
        setAnimation(ANIM_IDLE, new Animation(1, 1, "defensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(4,16,"defensivemovement"));
        setAnimation(ANIM_UNKNOWN, new Animation(0,0, "defensivelinemovement"));
        setAnimation(ANIM_FALL, new Animation(1,1, "defensivefall"));
        setAnimation(ANIM_PRESNAP, new Animation(1,1, "presnap/defensiveback"));
        speed = 10f;
        manCoverage = 10f;
        strength = 10f;
    }

    public boolean tackle(Entity ballCarrier) {
        boolean tackle = false;

        Random rand = new Random();
        int rand_output = rand.nextInt((int) (this.strength*200 + ballCarrier.strength*100));

        if (rand_output <= this.strength*200) {
            tackle = true;
            System.out.println("Tackle");
        }
        else {
            System.out.println("Tackle Evaded");
        }
        timeSinceLastTackleAttempt = Timer.getTime();

        return tackle;
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

    public void setLocation(World world, float ballX) {
        if (guardedReceivers - totalReceivers < 0 && route == 0) {
            guardedReceivers++;
            guardedReceiver = guardedReceivers;

            Entity guardedEntity = world.getCountingUpEntity(22-guardedReceiver);
            this.transform.pos.x = GameManager.ballPosX + 7;
            this.transform.pos.y = guardedEntity.transform.pos.y;
        }
        else {
            switch (route) {
                case -3 : break; // Blitz on Left Side (Acts as LDE)
                case -2 : break; // Blitz on Right Side (Acts as RDE)
                case 0 : // In case There are less receivers than Backs, Blitz instead of man-man. Set Locs Here
                case -1 : // Regular Blitz Down Middle
                    this.transform.pos.x = GameManager.ballPosX + 4.5f;
                    switch (defenderID) {
                        case 0 : this.transform.pos.y = GameManager.ballPosY + 4f; break; // Left
                        case 1 : this.transform.pos.y = GameManager.ballPosY; break; // Central
                        case 2 : this.transform.pos.y = GameManager.ballPosY - 4f; break; // Right
                    }
                    break;
                case 1 : break; // Zones
                case 2 : break;
            }
        }
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        // Set DB Position In Accordance With Receivers
        if (this.setLoc) {
            this.setLocation(world, GameManager.ballPosX);
            this.setLoc = false;
        }

        // Cornerback Plays Off Ball Defense in Man-Man or Blitzes
        if (uniqueEvents && canPlay) {
            canCollide = true;
            movement.add(defensive_movement(world.getBallCarrier(),delta));
        }
        else if (canPlay) {
            switch (route) {
                case -3 : break; // Blitz Left
                case -2 : break; // Blitz Right
                case -1 :
                    uniqueEvents = true;
                    break; // Blitz Middle
                case 0 : // Man-Man
                    if (guardedReceiver != 0) {
                        Vector2f newReceiverPos = new Vector2f();
                        Entity receiver = world.getCountingUpEntity(22-guardedReceiver);
                        boolean canDefend = true;
                        if (timeSinceLastCoverageAttempt + (10.5 - manCoverage)/5 < Timer.getTime() && receiverKnownPos.x != 0) {
                            timeSinceLastCoverageAttempt = Timer.getTime();

                            // Find Out Which Side DB should be on Receiver

                            if (this.transform.pos.y > receiver.transform.pos.y) {
                                newReceiverPos.set(receiver.transform.pos.x + 2, receiver.transform.pos.y + 2);
                            } else {
                                newReceiverPos.set(receiver.transform.pos.x + 2, receiver.transform.pos.y - 2);
                            }

                            // Project Player Location
                            Vector2f changes = new Vector2f(newReceiverPos.x - receiverKnownPos.x, newReceiverPos.y - receiverKnownPos.y);
                            float expectedX = (newReceiverPos.x + changes.x);
                            float expectedY = (newReceiverPos.y + changes.y);

                            coverageMovement.set(expectedX,expectedY);

                            System.out.println(receiverKnownPos.x);

                            receiverKnownPos.set(newReceiverPos.x, newReceiverPos.y);
                        } else if (receiverKnownPos.x == 0) {
                            receiverKnownPos.x = receiver.transform.pos.x;
                            receiverKnownPos.y = receiver.transform.pos.y;

                            canDefend = false;
                        }

                        if (canDefend) {
                            if (this.transform.pos.x - speed * delta > coverageMovement.x) {
                                movement.add(-speed * delta, 0);
                            } else if (this.transform.pos.x + speed * delta < coverageMovement.x) {
                                movement.add(speed * delta, 0);
                            }

                            if (this.transform.pos.y - speed * delta > coverageMovement.y) {
                                movement.add(0, -speed * delta);
                            } else if (this.transform.pos.y + speed * delta < coverageMovement.y) {
                                movement.add(0, speed * delta);
                            }
                        }

                    } else {
                        uniqueEvents = true;
                    }

                    break;
                case 1 : break; // Zones
                case 2 : break;

            } // End of Switch - Route


        }

        move(movement);




        if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_MOVE);
        }
        else {
            useAnimation(ANIM_IDLE);
        }



        if (canCollide && collidingWithBallCarrier(this,world)) {
            if (world.getBallCarrier() == world.getFootballEntity()) {
                if (collidingWithFootball(this,world)); // Interception, keep this nothing for now?
            }
            else if (canCollide) {
                if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && tackle(world.getBallCarrier())) {
                    world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                    canPlay = false;
                }
            }

        }

        if (! (canPlay || playStart)) {
            useAnimation(ANIM_PRESNAP);
        }

    }



}


