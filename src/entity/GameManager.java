package entity;

import entity.Entity;
import gameplay.Timer;
import org.joml.Vector2f;
import world.World;

public class GameManager {
    public static float yMax;
    public static float yMin;
    public static float xMax;
    public static float xMin;
    public static float xEndzoneLeft;
    public static float xEndzoneRight;
    public static float ballPosX = 300; // 223
    public static float ballPosY = -250f;
    public static int down = 0;
    public static float firstDownLine;
    public static boolean offenseBall = true;
    public static boolean userOffense = true;
    public static boolean selectedPlay = false;
    public static boolean hasEntities = false;
    public static double timePlayEnd = 0;
    public static int homeID = 0;
    public static int awayID = 1;
    public static float timeLeft = 300; // seconds
    public static float playClock = 20; // seconds
    public static int quarter = 1;
    public static boolean userHome = true;
    public static int homeScore = 0;
    public static int awayScore = 0;
    public static double previousKnownTime = Timer.getTime();
    public static boolean runClock = false;
    public static boolean homeDefer = false;
    public static boolean kickoff = true;
    public static boolean pat = false;
    public static boolean scoreHome = false;
    public static boolean scoreAway = false;
    public static boolean touchback = false;
    public static boolean gameStarted = false;
    public static boolean touchDown = false;
    public static boolean hasUpdated = false;
    public static boolean shouldPAT = false;

    public GameManager(float yMax, float yMin, float xMax, float xMin, float xEndzoneLeft, float xEndzoneRight) {
        this.yMax = yMax;
        this.yMin = yMin;
        this.xMax = xMax;
        this.xMin = xMin;
        this.xEndzoneLeft = xEndzoneLeft;
        this.xEndzoneRight = xEndzoneRight;
    }

    public boolean ballCarrierOutOfBounds(World world) { // Check if Out of Bounds (ALL)
        if (world.getBallCarrier() != world.getFootballEntity() && world.getBallCarrier() != null) {
            if (world.getBallCarrier().transform.pos.x > xMax) {
                Entity.canPlay = false;

                if (timePlayEnd == 0) {
                    timePlayEnd = Timer.getTime();
                }

                if (timePlayEnd + .5f > Timer.getTime()) {
                    world.getBallCarrier().useAnimation(1);
                    world.getBallCarrier().move(new Vector2f(world.getBallCarrier().speed/60, 0));
                }

                return true;
            } else if (world.getBallCarrier().transform.pos.x < xMin) {
                Entity.canPlay = false;

                if (timePlayEnd == 0) {
                    timePlayEnd = Timer.getTime();
                }

                if (timePlayEnd + .5f > Timer.getTime()) {
                    world.getBallCarrier().useAnimation(1);
                    world.getBallCarrier().move(new Vector2f(-world.getBallCarrier().speed/60, 0));
                }

                return true;
            } else if (world.getBallCarrier().transform.pos.y < yMin) {
                Entity.canPlay = false;

                if (timePlayEnd == 0) {
                    timePlayEnd = Timer.getTime();
                }

                if (timePlayEnd + .5f > Timer.getTime()) {
                    world.getBallCarrier().useAnimation(1);
                    world.getBallCarrier().move(new Vector2f(0, -world.getBallCarrier().speed / 60));
                }
                return true;
            } else if (world.getBallCarrier().transform.pos.y > yMax) {
                Entity.canPlay = false;

                if (timePlayEnd == 0) {
                    timePlayEnd = Timer.getTime();
                }

                if (timePlayEnd + .5f > Timer.getTime()) {
                    world.getBallCarrier().useAnimation(1);
                    world.getBallCarrier().move(new Vector2f(0, world.getBallCarrier().speed / 60));
                }

                return true;
            }
        }
        else { // For FUmbles
            if (! Football.fieldGoal) {
                if (world.getBallCarrier().transform.pos.x > 366.76367) {
                    return true;
                } else if (world.getBallCarrier().transform.pos.x < 141.26694) {
                    return true;
                } else if (world.getBallCarrier().transform.pos.y > -235.13287) {
                    return true;
                } else if (world.getBallCarrier().transform.pos.y < -266.79953) {
                    return true;
                }
            }
            if (Football.punt || Football.kickoff) {
                if (world.getBallCarrier().transform.pos.x > 354.5f) {
                    Entity.canPlay = false;
                    touchback = true;
                    Football.keepMoving = true;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean touchDown(World world) { // Check If TD (Offense)
        if (world.getBallCarrier().transform.pos.x > xEndzoneRight && world.getBallCarrier().transform.pos.x < xMax && world.getBallCarrier() != world.getFootballEntity() && ! world.getBallCarrier().defender) {
            if (timePlayEnd == 0) {
                timePlayEnd = Timer.getTime();
                if ((GameManager.userHome && GameManager.userOffense) || (!GameManager.userHome && !GameManager.userOffense)) {
                    if (! pat) {
                        homeScore += 6; shouldPAT = true;
                    } else {
                        homeScore += 2; scoreHome = true;
                    }
                } else {
                    if (! pat) {
                        awayScore += 6; shouldPAT = true;
                    } else {
                        awayScore += 2; scoreAway = true;
                    }
                }
            }

            if (timePlayEnd + .5f > Timer.getTime()) {
                world.getBallCarrier().useAnimation(1);
                world.getBallCarrier().move(new Vector2f(world.getBallCarrier().speed/60,0));
            }

            return true;
        }
        return false;
    }

    public boolean defensiveTouchDown(World world) {
        if (world.getBallCarrier().transform.pos.x < xEndzoneLeft && world.getBallCarrier().transform.pos.x < xMax && world.getBallCarrier() != world.getFootballEntity() && world.getBallCarrier().defender) {
            if (timePlayEnd == 0) {
                timePlayEnd = Timer.getTime();
                if ((GameManager.userHome && GameManager.userOffense) || (!GameManager.userHome && !GameManager.userOffense)) {
                    if (! pat) {
                        awayScore += 6; shouldPAT = true;
                    }
                    else {
                        awayScore += 2; kickoff = true;
                    }
                } else {
                    if (! pat) {
                        homeScore += 6; shouldPAT = true;
                    } else {
                        homeScore += 2; kickoff = true;
                    }
                }
            }

            if (timePlayEnd + .5f > Timer.getTime()) {
                world.getBallCarrier().useAnimation(1);
                world.getBallCarrier().move(new Vector2f(-world.getBallCarrier().speed/60,0));
            }

            return true;

        }
        return false;
    }

    public void setBallPosX(World world) {
        // Controls start of game functions
        if (! gameStarted) {
            gameStarted = true;

            if (homeDefer) {
                // Set user Offense
                if (userHome) {
                    userOffense = true;
                } else {
                    userOffense = false;
                }

                kickoff = true;
            } else {
                if (userHome) {
                    userOffense = false;
                } else {
                    userOffense = true;
                }
            }
        }

        down++;

        if (kickoff) {
            GameManager.ballPosX = 223;
            System.out.println("KICKOFF");
            GameManager.ballPosY = -250;
        }

        if (pat) {
            GameManager.ballPosX = 350;
            System.out.println("PAT");
            GameManager.ballPosY = -250;
        }

        if (down > 4 && (firstDownLine + .6f > world.getFootballEntity().transform.pos.x || Entity.incompletePass))
            Entity.turnover = true;

        if (! Entity.turnover && ! pat && ! kickoff) {
            if (world.getBallCarrier() != null && !Entity.incompletePass) {
                this.ballPosX = world.getFootballEntity().transform.pos.x - .6f;
                System.out.println("RUNS");
                System.out.println(pat);
            }

            if (ballPosX > firstDownLine) {
                down = 1;
                firstDownLine = ballPosX + 20;
                System.out.println("FIRST DOWN");
            }
        } else if (! pat && ! kickoff) {
            System.out.println("RUNNING");
            // Set Ball Pos
            System.out.println(world.getFootballEntity().transform.pos.x);
            if (! Entity.incompletePass)
                this.ballPosX = (507 - world.getFootballEntity().transform.pos.x);
            else
                this.ballPosX = (507 - ballPosX);


            // Set Vars
            Entity.turnover = false;
            down = 1;

            firstDownLine = ballPosX + 20;

            if (! pat && ! kickoff) {
                if (userOffense) {
                    userOffense = false;
                } else {
                    userOffense = true;
                }
            }


        }

        if (touchback) {
            Entity.incompletePass = true;
            GameManager.ballPosX = 203;
            GameManager.ballPosY = -250f;

            firstDownLine = 223;
        }

        if (ballPosX < 153f) {
            if ((GameManager.userOffense && GameManager.userHome) || (!GameManager.userOffense && !GameManager.userHome)) {
                scoreHome = true; homeScore += 2; kickoff = true;
            } else {
                scoreAway = true; awayScore += 2; kickoff = true;
            }

            GameManager.ballPosX = 223;
            System.out.println("KICKOFF");
            GameManager.ballPosY = -250;

        }

    }
    public void setBallPosY(World world) {
        if (world.getBallCarrier() != null && !Entity.incompletePass && ! kickoff && ! pat) {
            if (world.getFootballEntity().transform.pos.y > -245.9) {
                this.ballPosY = -245.9f;
            } else if (world.getFootballEntity().transform.pos.y < -254) {
                this.ballPosY = -254f;
            } else {
                this.ballPosY = world.getFootballEntity().transform.pos.y + .3f;
            }
        }
    }

    public static void postUpdate() {
        if (!hasUpdated) {
            hasUpdated = true;

            if (kickoff) {
                kickoff = false;
            }

            if (pat) {
                System.out.println("RAN");
                pat = false;
                kickoff = true;
            }

            if (shouldPAT) {
                pat = true;
                shouldPAT = false;
            }

            if (scoreHome) {
                kickoff = true;
                if ((GameManager.userHome && GameManager.userOffense) || (!GameManager.userHome && !GameManager.userOffense))
                    GameManager.userOffense = false;
                else {
                    GameManager.userOffense = true;
                }
            }
            if (scoreAway) {
                kickoff = true;
                if ((!GameManager.userHome && GameManager.userOffense) || (GameManager.userHome && !GameManager.userOffense)) {
                    GameManager.userOffense = true;
                } else {
                    GameManager.userOffense = false;
                }
            }

            if (Entity.incompletePass || Entity.turnover) {
                GameManager.runClock = false;
            } else {
                GameManager.runClock = true;
            }
        }
    }

    public static void updateTimer(double time) {
        // Update Timer if Clock should be running
        if (runClock) {

            timeLeft -= (time - previousKnownTime);

        }

        if (! Entity.playStart && ! Entity.canPlay) {
            playClock -= (time - previousKnownTime);
        }

        previousKnownTime = time;
    }

    public static void printDownInfo() {
        System.out.println(firstDownLine + " " + ballPosX);
        System.out.println(down + " & " + (firstDownLine - ballPosX)/2);
    }

    public float getBallPosX() { return ballPosX; }
    public float getBallPosY() { return ballPosY; }

}
