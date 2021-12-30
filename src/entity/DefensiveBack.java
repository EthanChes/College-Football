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
    public static int addX = 10; // To Prevent Player Stacking in man-man
    public static int guardedReceivers = 0;
    public boolean setLoc = true;
    public int defenderID = 0; // For Linebackers (Consistent Placement)
    public Vector2f coverageMovement = new Vector2f(0,0);
    public Vector2f receiverKnownPos = new Vector2f(0,0);
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
        manCoverage = 8f;
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

            this.transform.pos.x = GameManager.ballPosX + addX;
            addX -= 2;

            this.transform.pos.y = guardedEntity.transform.pos.y;

            noCollision();
        }
        else {
            switch (route) {
                case -3 : break; // Blitz on Left Side (Acts as LDE)
                case -2 : break; // Blitz on Right Side (Acts as RDE)
                case 0 : // In case There are less receivers than Backs, Blitz instead of man-man. Set Locs Here
                default : // Default Position Setters
                    this.transform.pos.x = GameManager.ballPosX + 8; // Initializer For LBs
                    switch (defenderID) { // For LBs
                        case 0 : this.transform.pos.y = GameManager.ballPosY + 4f; break; // Left Normal
                        case 1 : this.transform.pos.y = GameManager.ballPosY; break; // Central Normal
                        case 2 : this.transform.pos.y = GameManager.ballPosY - 4f; break; // Right Normal
                        case 3 : this.transform.pos.x = GameManager.ballPosX + 2.5f; this.transform.pos.y = GameManager.ballPosY + 4.5f; break; // Left Low
                        case 4 : this.transform.pos.x = GameManager.ballPosX + 2.5f; this.transform.pos.y = GameManager.ballPosY - 5.5f; break; // Right Low
                        case 12 : this.transform.pos.x = GameManager.ballPosX + 2.5f; this.transform.pos.y = GameManager.ballPosY + 9; break; // DB Left Low
                        case 13 : this.transform.pos.x = GameManager.ballPosX + 2.5f; this.transform.pos.y = GameManager.ballPosY - 10f; break; // DB Right Low
                        case 14 : this.transform.pos.x = GameManager.ballPosX + 15f; this.transform.pos.y = GameManager.ballPosY; break; // DB Deep Mid
                    }
                    break;
                /*case 1 : this.transform.pos.set(GameManager.ballPosX + 5, -240,0); break; // low top (ZONES)
                case 2 : this.transform.pos.set(GameManager.ballPosX + 5, -260,0); break; // low bottom
                case 3 : this.transform.pos.set(GameManager.ballPosX + 20, -260,0); break; // high bottom
                case 4 : this.transform.pos.set(GameManager.ballPosX + 20, -240,0); break; // high top
                case 5 : this.transform.pos.set(GameManager.ballPosX + 12, -250,0); break; // center
                case 6 : this.transform.pos.set(GameManager.ballPosX + 20, -250,0); break; // high mid*/
            }
        }
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (! (canPlay || playStart) && this.transform.pos.x - 2f < GameManager.ballPosX) {
            movement.add(speed*delta,0);
        }

        if ( (! GameManager.userOffense) && hasBall) userControl = true; // change false to gamemanager on defense, make sure to have ids for different defenders to switch through them
        else if (forceUserControl && ! GameManager.userOffense) userControl = true;
        else userControl = false;

        if (forceUserControl) {
            userTackle(window, this, world.getBallCarrier(), world);
        }

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S) && userControl && ! pancaked && ! isBeingMovedExternally) { // When S is pressed, player moves 5 down
            movement.add(0, -speed * delta); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A) && userControl && ! pancaked && ! isBeingMovedExternally) { // When A is pressed, camera shifts left 5
            movement.add(-speed * delta, 0);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W) && userControl && ! pancaked && ! isBeingMovedExternally) { // When W is pressed, camera shifts up 5
            movement.add(0, speed * delta);
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D) && userControl && ! pancaked && ! isBeingMovedExternally) { // When D is pressed, camera shifts right 5
            movement.add(speed * delta, 0);
        }

        if (world.getBallCarrier().transform.pos.x > GameManager.ballPosX + .7f) {
            uniqueEvents = true;
        }

        // Set DB Position In Accordance With Receivers
        if (this.setLoc) {
            this.setLocation(world, GameManager.ballPosX);
            this.setLoc = false;
        }


        if (! userControl) {
            // Cornerback Plays Off Ball Defense in Man-Man or Blitzes
            if (timeFumble > 0) {
                movement.add(moveToward(world.getFootballEntity().transform.pos.x, world.getFootballEntity().transform.pos.y, delta));
            } else if (uniqueEvents && canPlay && world.getBallCarrier() != world.getFootballEntity() && !(pancaked || isBeingMovedExternally)) {
                canCollide = true;
                if (GameManager.offenseBall) {
                    movement.add(defensive_movement(world.getBallCarrier(), delta));
                } else if (hasBall) {
                    // Search For Nearby Players Too
                    movement.add(defenseHasBallMove(world, delta));
                } else {
                    movement.add(defenseBlockUnique(world, delta));
                }
            } else if (world.getFootballEntity().pass) {
                movement.add(moveToward(Football.wideReceiverX - 1, Football.wideReceiverY, delta));
            } else if (canPlay) {
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
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;


                        if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break; // Zones
                    }
                    case 2: { // Low Zone Bottom
                        int zoneRadius = 8;
                        Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 5, -260);
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;

                        if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break;
                    }

                    case 3: { // High Zone Bottom
                        int zoneRadius = 15;
                        boolean playerBeyondSafety = false;
                        Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 20, -260);
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                            if (world.getCountingUpEntity(i).transform.pos.x + 10 > this.transform.pos.x) {
                                playerBeyondSafety = true;
                                i = 22;
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;


                        if (playerBeyondSafety) {
                            movement.add(speed * delta, 0);
                        } else if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break;
                    }


                    case 4: { // High Zone Top
                        int zoneRadius = 15;
                        Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 20, -240);
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        boolean playerBeyondSafety = false;
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                            if (world.getCountingUpEntity(i).transform.pos.x + 10 > this.transform.pos.x) {
                                playerBeyondSafety = true;
                                i = 22;
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;

                        if (playerBeyondSafety) {
                            movement.add(speed * delta, 0);
                        } else if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break;
                    }

                    case 5: { // Mid Zone Mid
                        int zoneRadius = 8;
                        Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 12, -250);
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;

                        if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break;
                    }

                    case 6: { // Deep Zone Mid
                        int zoneRadius = 15;
                        Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 20, -250);
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        boolean playerBeyondSafety = false;
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                            if (world.getCountingUpEntity(i).transform.pos.x + 10 > this.transform.pos.x) {
                                playerBeyondSafety = true;
                                i = 22;
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;

                        if (playerBeyondSafety) {
                            movement.add(speed * delta, 0);
                        } else if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break;
                    }

                    case 7: { // Left Zone Mid
                        int zoneRadius = 8;
                        Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 12, -240);
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;

                        if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break;
                    }

                    case 8: { // Right Zone Mid
                        int zoneRadius = 8;
                        Vector2f zoneLoc = new Vector2f(GameManager.ballPosX + 12, -260);
                        List<Entity> receiversInZone = new ArrayList<Entity>();
                        for (int i = 16; i < 22; i++) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(zoneLoc.x, zoneLoc.y, 0) < zoneRadius) {
                                receiversInZone.add(world.getCountingUpEntity(i));
                            }
                        }
                        Vector2f averageZonePos = new Vector2f(0, 0);
                        for (int i = 0; i < receiversInZone.size(); i++) {
                            averageZonePos.x += receiversInZone.get(i).transform.pos.x;
                            averageZonePos.y += receiversInZone.get(i).transform.pos.y;
                        }
                        averageZonePos.x /= receiversInZone.size();
                        averageZonePos.y /= receiversInZone.size();

                        averageZonePos.x += 2;

                        if (receiversInZone.size() == 0) {
                            movement.add(moveToward(zoneLoc.x + xZoneError, zoneLoc.y + yZoneError, delta));
                        } else {
                            movement.add(moveToward(averageZonePos.x + xZoneError, averageZonePos.y + yZoneError, delta));
                        }
                        break;
                    }

                } // End of Switch - Route


            }
        }

        if (userControl && !playStart || (! (pancaked || isBeingMovedExternally) && canPlay)) {
            move(movement);
        } else {
            isBeingMovedExternally = false;
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
            if (lastAnimationChange + .5f <= Timer.getTime())
            useAnimation(ANIM_IDLE);
            lastAnimationChange = Timer.getTime();
            if (hasBall) {
                world.getFootballEntity().transform.pos.set(this.transform.pos.x, this.transform.pos.y,0);
            }
        }



        if (canCollide && collidingWithBallCarrier(this,world)) {
            if (world.getBallCarrier() == world.getFootballEntity()) {
                if ((! (inCatch || hasBall)) && collidingWithFootball(this,world)) { // Interception Attempt

                    Entity closestDefender = world.getCountingUpEntity(11);
                    for (int i = 11; i < 22; i++) {
                        if (closestDefender.transform.pos.distance(this.transform.pos) > world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos)) {
                            closestDefender = world.getCountingUpEntity(i);
                        }
                    }

                    Random rand = new Random();
                    if (closestDefender.transform.pos.distance(this.transform.pos) <= 2.75f && catchAttempt) {
                        int rand_output = rand.nextInt((int) (this.catching * 100 + (closestDefender.catching * 100) * (3 - closestDefender.transform.pos.distance(this.transform.pos))));
                        if (rand_output <= this.catching * 50) {

                            if (GameManager.userOffense) {
                                forceSelectOffensivePlayer(window, world);
                            }

                            turnover = true;

                            deselectAllDefenders(world);

                            this.inCatch = true;
                            for (int i = 0; i < 22; i++) {
                                world.getCountingUpEntity(i).timeSinceLastTackleAttempt = Timer.getTime() - 1;
                            }
                            this.timeCatch = Timer.getTime();
                            GameManager.offenseBall = false;
                            incompletePass = false;
                        } else {
                            world.getFootballEntity().pass = false;
                            incompletePass = true;
                            timeCatch = Timer.getTime();
                            Football.passDropStart = Timer.getTime();
                        }
                    } else if (catchAttempt) {
                        this.inCatch = true;

                        if (GameManager.userOffense) {
                            forceSelectOffensivePlayer(window, world);
                        }

                        turnover = true;

                        deselectAllDefenders(world);


                        incompletePass = false;
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
                    boolean tackResult = tackle(world.getBallCarrier(), window, world);
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

        if (movement.x != 0 || movement.y != 0 && ! (canPlay || playStart)) {
            useAnimation(ANIM_MOVE);
        }
        else if (! (canPlay || playStart)) {
            useAnimation(ANIM_PRESNAP);
        }

        if (! canPlay && this.hasBall) {
            useAnimation(ANIM_FALL);
            world.getFootballEntity().transform.pos.set(this.transform.pos);
        }

        if (userControl) {
            PlayerMarker.setLocation.x = this.transform.pos.x;
            PlayerMarker.setLocation.y = this.transform.pos.y;
        }

        if (isBeingMovedExternally) {
            useAnimation(ANIM_MOVE);
        }

        if (pancaked) {
            useAnimation(ANIM_FALL);
        }

    }



}


