package entity;
import gameplay.Timer;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import java.util.ArrayList;
import java.util.List;
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

    public double timeSinceLastCoverageAttempt = -12f;
    public static int guardedReceivers = 0;
    public int guardedReceiver = 0;
    public boolean setLoc = true;
    public int defenderID = 0; // For Linebackers (Consistent Placement)
    public Vector2f coverageMovement = new Vector2f(0,0);
    public Vector2f receiverKnownPos = new Vector2f(0,0);
    public boolean inCatch = false;
    public double timeCatch = 0;
    Random rand = new Random();
    float xZoneError = rand.nextInt((int) (11 - zoneCoverage)) - ((1/2) * (11 - zoneCoverage));
    float yZoneError = rand.nextInt((int) (11 - zoneCoverage)) - ((1/2) * (11 - zoneCoverage));

    public DefensiveBack(Transform transform) {
        super(ANIM_SIZE, transform);
        uniqueEvents = false;
        setAnimation(ANIM_IDLE, new Animation(1, 1, "defensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(4,16,"defensivemovement"));
        setAnimation(ANIM_UNKNOWN, new Animation(0,0, "defensivelinemovement"));
        setAnimation(ANIM_FALL, new Animation(1,1, "defensivefall"));
        setAnimation(ANIM_PRESNAP, new Animation(1,1, "presnap/defensiveback"));
        speed = 8f;
        manCoverage = 10f;
        strength = 10f;
        catching = 10f;
        zoneCoverage = 10f;
    }

    public void catching() {
        if (timeCatch <= Timer.getTime()) { // Parameter for time during catch, then set hasball and incatch in here.
            this.inCatch = false;
            this.hasBall = true;
        }
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
                case 1 : this.transform.pos.set(GameManager.ballPosX + 5, -240,0); break; // Zones
                case 2 : this.transform.pos.set(GameManager.ballPosX + 5, -260,0); break;
                case 3 : this.transform.pos.set(GameManager.ballPosX + 20, -260,0); break;
                case 4 : this.transform.pos.set(GameManager.ballPosX + 20, -240,0); break;
                case 5 : this.transform.pos.set(GameManager.ballPosX + 12, -250,0); break;
            }
        }
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (world.getBallCarrier().transform.pos.x > GameManager.ballPosX + .7f) {
            uniqueEvents = true;
        }

        // Set DB Position In Accordance With Receivers
        if (this.setLoc) {
            this.setLocation(world, GameManager.ballPosX);
            this.setLoc = false;
        }

        // Cornerback Plays Off Ball Defense in Man-Man or Blitzes
        if (uniqueEvents && canPlay && world.getBallCarrier() != world.getFootballEntity() && ! (pancaked || isBeingMovedExternally)) {
            canCollide = true;
            if (GameManager.offenseBall) {
                movement.add(defensive_movement(world.getBallCarrier(), delta));
            } else if (hasBall) {
                // Search For Nearby Players Too
                movement.add(defenseHasBallMove(world,delta));
            }
            else {
                    // Block For Player
            }
        }
        else if (world.getFootballEntity().pass) {
            movement.add(moveToward(Football.wideReceiverX - 1, Football.wideReceiverY,delta));
        }
        else if (canPlay) {
            switch (route) {
                case -3:
                    // Blitz Left
                case -2:
                    // Blitz Right
                case -1:
                    uniqueEvents = true;
                    break; // Blitz Middle
                case 0: // Man-Man
                    if (guardedReceiver != 0) {
                        Vector2f newReceiverPos = new Vector2f();
                        Entity receiver = world.getCountingUpEntity(22 - guardedReceiver);
                        boolean canDefend = true;
                        int rand_check = rand.nextInt(((int) manCoverage * 100) + 2500);
                        if (rand_check <= manCoverage * 100 && receiverKnownPos.x != 0) {
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

                            coverageMovement.set(expectedX, expectedY);


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
                case 1: { // Low Zone Top
                    int zoneRadius = 8;
                    Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 5, -240);
                    List<Entity> receiversInZone = new ArrayList<Entity>();;
                    for (int i = 16; i < 22; i++) {
                        if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x,zoneLoc.y,0) < zoneRadius) {
                            receiversInZone.add(world.getCountingUpEntity(i));
                        }
                    }
                    Vector2f averageZonePos = new Vector2f(0,0);
                    for (int i = 0; i < receiversInZone.size(); i++) {
                        averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                        averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                    }
                    averageZonePos.x /= receiversInZone.size();
                    averageZonePos.y /= receiversInZone.size();
                    if (receiversInZone.size() == 0) {
                        movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                    } else {
                        movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError,delta));
                    }
                    break; // Zones
                }
                case 2 : { // Low Zone Bottom
                    int zoneRadius = 8;
                    Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 5, -260);
                    List<Entity> receiversInZone = new ArrayList<Entity>();;
                    for (int i = 16; i < 22; i++) {
                        if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x,zoneLoc.y,0) < zoneRadius) {
                            receiversInZone.add(world.getCountingUpEntity(i));
                        }
                    }
                    Vector2f averageZonePos = new Vector2f(0,0);
                    for (int i = 0; i < receiversInZone.size(); i++) {
                        averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                        averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                    }
                    averageZonePos.x /= receiversInZone.size();
                    averageZonePos.y /= receiversInZone.size();
                    if (receiversInZone.size() == 0) {
                        movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                    } else {
                        movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError,delta));
                    }
                    break;
                }

                case 3 : { // High Zone Bottom
                    int zoneRadius = 15;
                    boolean playerBeyondSafety = false;
                    Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 20, -260);
                    List<Entity> receiversInZone = new ArrayList<Entity>();;
                    for (int i = 16; i < 22; i++) {
                        if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x,zoneLoc.y,0) < zoneRadius) {
                            receiversInZone.add(world.getCountingUpEntity(i));
                        }
                        if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y,0)  < zoneRadius) {
                            playerBeyondSafety = true;
                            i = 22;
                        }
                    }
                    Vector2f averageZonePos = new Vector2f(0,0);
                    for (int i = 0; i < receiversInZone.size(); i++) {
                        averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                        averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                    }
                    averageZonePos.x /= receiversInZone.size();
                    averageZonePos.y /= receiversInZone.size();
                    if (playerBeyondSafety) {
                        movement.add(speed*delta,0);
                    }
                    else if (receiversInZone.size() == 0) {
                        movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                    } else {
                        movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError,delta));
                    }
                    break;
                }


                case 4 : { // High Zone Top
                    int zoneRadius = 15;
                    Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 20, -240);
                    List<Entity> receiversInZone = new ArrayList<Entity>();;
                    boolean playerBeyondSafety = false;
                    for (int i = 16; i < 22; i++) {
                        if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x,zoneLoc.y,0) < zoneRadius) {
                            receiversInZone.add(world.getCountingUpEntity(i));
                        }
                        if (world.getCountingUpEntity(i).transform.pos.x > this.transform.pos.x) {
                            playerBeyondSafety = true;
                            i = 22;
                        }
                    }
                    Vector2f averageZonePos = new Vector2f(0,0);
                    for (int i = 0; i < receiversInZone.size(); i++) {
                        averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                        averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                    }
                    averageZonePos.x /= receiversInZone.size();
                    averageZonePos.y /= receiversInZone.size();
                    if (playerBeyondSafety) {
                        movement.add(speed*delta,0);
                    }
                    else if (receiversInZone.size() == 0) {
                        movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                    } else {
                        movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError,delta));
                    }
                    break;
                }

                case 5 : { // Mid Zone Mid
                    int zoneRadius = 8;
                    Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 12, -250);
                    List<Entity> receiversInZone = new ArrayList<Entity>();;
                    for (int i = 16; i < 22; i++) {
                        if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x,zoneLoc.y,0) < zoneRadius) {
                            receiversInZone.add(world.getCountingUpEntity(i));
                        }
                    }
                    Vector2f averageZonePos = new Vector2f(0,0);
                    for (int i = 0; i < receiversInZone.size(); i++) {
                        averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                        averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                    }
                    averageZonePos.x /= receiversInZone.size();
                    averageZonePos.y /= receiversInZone.size();
                    if (receiversInZone.size() == 0) {
                        movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                    } else {
                        movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError,delta));
                    }
                    break;
                }

            } // End of Switch - Route


        }

        if (! (pancaked || isBeingMovedExternally)) {
            move(movement);
        }



        if (pancaked) {
            useAnimation(ANIM_FALL);
            canCollide = false;
            if (Timer.getTime() > timePancaked + 3) {
                pancaked = false;
                canCollide = true;
            }
        }
        else if (getAnimationIndex() == 3 && ! canPlay) {
            useAnimation(ANIM_FALL);
            world.getFootballEntity().transform.pos.set(this.transform.pos);
        }
        else if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_MOVE);
            if (hasBall) {
                world.getFootballEntity().transform.pos.set(this.transform.pos.x, this.transform.pos.y,0);
            }
        }
        else {
            useAnimation(ANIM_IDLE);
            if (hasBall) {
                world.getFootballEntity().transform.pos.set(this.transform.pos.x, this.transform.pos.y,0);
            }
        }



        if (canCollide && collidingWithBallCarrier(this,world)) {
            if (world.getBallCarrier() == world.getFootballEntity()) {
                if ((! (inCatch || hasBall)) && collidingWithFootball(this,world)) { // Interception

                    Entity closestDefender = world.getCountingUpEntity(11);
                    for (int i = 11; i < 22; i++) {
                        if (closestDefender.transform.pos.distance(this.transform.pos) > world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos)) {
                            closestDefender = world.getCountingUpEntity(i);
                        }
                    }

                    Random rand = new Random();
                    if (closestDefender.transform.pos.distance(this.transform.pos) <= 2.75f && catchAttempt) {
                        int rand_output = rand.nextInt((int) (this.catching * 100 + (closestDefender.catching * 100) * (3 - closestDefender.transform.pos.distance(this.transform.pos))));
                        if (rand_output <= this.catching * 100) {
                            this.inCatch = true;
                            for (int i = 0; i < 22; i++) {
                                world.getCountingUpEntity(i).timeSinceLastTackleAttempt = Timer.getTime() - 1;
                            }
                            this.timeCatch = Timer.getTime();
                            GameManager.offenseBall = false;
                        } else {
                            world.getFootballEntity().pass = false;
                            timeCatch = Timer.getTime();
                            Football.passDropStart = Timer.getTime();
                        }
                    } else if (catchAttempt) {
                        this.inCatch = true;
                        for (int i = 0; i < 22; i++) {
                            world.getCountingUpEntity(i).timeSinceLastTackleAttempt = Timer.getTime() - 1;
                        }
                        GameManager.offenseBall = false;
                        this.timeCatch = Timer.getTime();
                    }

                    catchAttempt = false;
                }
            }
            else if (canCollide) {
                if (timeSinceLastTackleAttempt + 1.5 < Timer.getTime() && GameManager.offenseBall) {
                    boolean tackResult = tackle(world.getBallCarrier());
                    if (tackResult) {
                        world.getBallCarrier().useAnimation(3); // 3 is universal falling animation
                        canPlay = false;
                    }
                    else {
                        this.pancaked = true;
                        timePancaked = Timer.getTime();
                    }
                }
            }

        }

        if (inCatch) {
            for (int i = 0; i < 22; i++) {
                world.getCountingUpEntity(i).uniqueEvents = true;
            }

            world.getFootballEntity().useAnimation(1);
            passCaught(world);
            catching();
            world.setBallCarrier(this);
            camera.setProjection(640,480);
        }

        if (! (canPlay || playStart)) {
            useAnimation(ANIM_PRESNAP);
        }

    }



}


