package entity;

import entity.Entity;
import gameplay.Timer;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

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
    public static int homeID = 2;
    public static int awayID = 4;
    public static float timeLeft = 300; // seconds
    public static float playClock = 20; // seconds
    public static int quarter = 1;
    public static boolean userHome = false;
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
    public static boolean updatedQuarter = false;
    public static boolean shouldPAT = false;
    public static int homeTimeStrategy = 0;
    public static int awayTimeStrategy = 0;
    public static boolean appliedPenalty = false;
    public static boolean appliedTimeCut = false;
    public static int timeoutsHome = 3;
    public static int timeOutsAway = 3;
    public static double callingTimeout = 0;
    public static int overtime = 0;
    public static boolean firstTouchOT = false;

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
                        awayScore += 6; shouldPAT = true; if (GameManager.userOffense) GameManager.userOffense = false; else GameManager.userOffense = true;
                    }
                    else {
                        awayScore += 2; kickoff = true;
                    }
                } else {
                    if (! pat) {
                        homeScore += 6; shouldPAT = true; if (GameManager.userOffense) GameManager.userOffense = false; else GameManager.userOffense = true;
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

    public void setBallPosX(World world, Window win) {
        // Controls start of game functions
        if (overtime == 0) {
            if (!gameStarted) {
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

            if (down > 4 && hasEntities && (firstDownLine + .6f > world.getFootballEntity().transform.pos.x || Entity.incompletePass))
                Entity.turnover = true;

            if (!Entity.turnover && !pat && !kickoff) {
                if (world.getBallCarrier() != null && !Entity.incompletePass && hasEntities) {
                    this.ballPosX = world.getFootballEntity().transform.pos.x - .6f;
                    System.out.println(pat);
                }

                if (ballPosX > firstDownLine) {
                    down = 1;
                    firstDownLine = ballPosX + 20;
                    System.out.println("FIRST DOWN");
                }
            } else if (!pat && !kickoff) {
                // Set Ball Pos
                System.out.println(world.getFootballEntity().transform.pos.x);
                if (!Entity.incompletePass)
                    this.ballPosX = (507 - world.getFootballEntity().transform.pos.x);
                else
                    this.ballPosX = (507 - ballPosX);



                // Set Vars
                Entity.turnover = false;
                down = 1;

                firstDownLine = ballPosX + 20;

                if (!pat && !kickoff) {
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

                if (down > 4 && (firstDownLine + .6f > world.getFootballEntity().transform.pos.x || Entity.incompletePass))
                    Entity.turnover = true;
            }

            if (ballPosX < 153f) {
                if (((GameManager.userOffense && GameManager.userHome) || (!GameManager.userOffense && !GameManager.userHome)) && !touchback) {
                    scoreHome = true;
                    awayScore += 2;
                    kickoff = true;
                } else {
                    scoreAway = true;
                    homeScore += 2;
                    kickoff = true;
                }

                GameManager.ballPosX = 223;
                System.out.println("KICKOFF");
                GameManager.ballPosY = -250;

            }
        } else { // Overtime capabilities
            if (firstTouchOT) {
                down++;

                if (pat) {
                    GameManager.ballPosX = 350;
                    System.out.println("PAT");
                    GameManager.ballPosY = -250;
                }

                if (down > 4 && (firstDownLine + .6f > world.getFootballEntity().transform.pos.x || Entity.incompletePass)) {
                    Entity.turnover = true;
                }

                if (!Entity.turnover && !pat && !kickoff) {
                    if (world.getBallCarrier() != null && !Entity.incompletePass && hasEntities) {
                        this.ballPosX = world.getFootballEntity().transform.pos.x - .6f;
                    }

                    if (ballPosX > firstDownLine) {
                        down = 1;
                        firstDownLine = ballPosX + 20;
                        System.out.println("FIRST DOWN");
                    }
                } else if (!pat && !kickoff) {
                    // Set Ball Pos
                    ballPosX = xEndzoneRight - 50;


                    overtime++;

                    if (overtime % 2 == 1) {  // Check scores
                        if (homeScore != awayScore) {
                            endGame(win);
                        }
                    }


                    // Set Vars
                    Entity.turnover = false;
                    down = 1;

                    firstDownLine = ballPosX + 20;

                    if (!pat && !kickoff) {
                        if (userOffense) {
                            userOffense = false;
                        } else {
                            userOffense = true;
                        }
                    }


                }

                if (ballPosX < 153f) {
                    if (((GameManager.userOffense && GameManager.userHome) || (!GameManager.userOffense && !GameManager.userHome)) && !touchback) {
                        if (! homeDefer)
                            overtime++;

                        scoreHome = true;
                        awayScore += 2;
                    } else {
                        homeScore += 2;
                        scoreAway = true;

                        if (homeDefer)
                            overtime++;
                    }
                }
            } else { firstTouchOT = true; System.out.println("ILLEGAL 2"); }

        } // End OT


    }
    public void setBallPosY(World world) {
        if (world.getBallCarrier() != null && !Entity.incompletePass && ! kickoff && ! pat && hasEntities) {
            if (world.getFootballEntity().transform.pos.y > -245.9) {
                this.ballPosY = -245.9f;
            } else if (world.getFootballEntity().transform.pos.y < -254) {
                this.ballPosY = -254f;
            } else {
                this.ballPosY = world.getFootballEntity().transform.pos.y + .3f;
            }
        }
    }

    public static void postUpdate(Window win, World world) {
        if (overtime == 0) {
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
                    if (GameManager.userHome)
                        GameManager.userOffense = true;
                    else
                        GameManager.userOffense = false;
                }
                if (scoreAway) {
                    kickoff = true;
                    if (GameManager.userHome)
                        GameManager.userOffense = false;
                    else
                        GameManager.userOffense = true;
                }

                if (Entity.incompletePass || Entity.turnover) {
                    GameManager.runClock = false;
                } else {
                    GameManager.runClock = true;
                }

                if (timePlayEnd + 2 < Timer.getTime() && timePlayEnd != 0)
                    updateQuarter(win, world);

                // Set Time Strategies throughout game
                if (homeScore > awayScore && quarter == 4 && timeLeft <= 90) {
                    homeTimeStrategy = 2;
                    awayTimeStrategy = 1;
                } else if (homeScore < awayScore && quarter == 4 && timeLeft <= 90) {
                    homeTimeStrategy = 1;
                    awayTimeStrategy = 2;
                } else {
                    homeTimeStrategy = 1;
                    awayTimeStrategy = 1;
                }


            }
        } else { // Overtime post update
            if (! hasUpdated) {
                hasUpdated = true;

                if (pat) {
                    pat = false;
                    Entity.turnover = true;
                }

                if (shouldPAT) {
                    pat = true;
                    shouldPAT = false;
                }

                if (scoreHome || scoreAway) {
                    Entity.turnover = true;
                }
            }
        }
    }

    public static void updateTimer(double time, World world, Window win) {
        // Update Timer if Clock should be running
        if (runClock && hasEntities && ! pat && overtime == 0) {

            timeLeft -= (time - previousKnownTime);

            if (! appliedTimeCut && ! Entity.canPlay && ! Entity.playStart) {
                appliedTimeCut = true;
                if (! GameManager.userOffense) {
                    if (userHome) {
                        System.out.println(homeTimeStrategy);
                        switch (awayTimeStrategy) {
                            case 0:
                                timeLeft -= 5;
                                break;
                            case 2:
                                timeLeft -= 10;
                                break;
                        }
                    } else {
                        switch (homeTimeStrategy) {
                            case 0:
                                timeLeft -= 5;
                                break;
                            case 2:
                                timeLeft -= 10;
                                break;
                        }
                    }
                }

            }
        }

        if (! Entity.playStart && ! Entity.canPlay && hasEntities && ! (userOffense && (pat || kickoff))) {
            playClock -= (time - previousKnownTime);
        }

        if (timeLeft <= 0) {
            timeLeft = 0;

            if (! Entity.playStart && ! Entity.canPlay && ! pat) {
                updateQuarter(win, world);
            }
        }

        if (playClock <= 0) { // DOG Penalty
            playClock = 0;

            if (! appliedPenalty) {
                if (world.getFootballEntity().transform.pos.x > xEndzoneLeft + 10) {
                    world.getFootballEntity().transform.pos.x -= 10;
                } else {
                    world.getFootballEntity().transform.pos.x -= (world.getFootballEntity().transform.pos.x-xEndzoneLeft)/2;
                }
                down--; // prevents down increment
                Entity.incompletePass = true;
                timePlayEnd = Timer.getTime();
                appliedPenalty = true;
            }

            if (timePlayEnd + 2 < Timer.getTime() && timePlayEnd != 0) {
                world.initReset(win);
            }

            Entity.playStart = true;
            Entity.canPlay = false;
        }

        previousKnownTime = time;
    }

    public static void printDownInfo() {
        System.out.println(firstDownLine + " " + ballPosX);
        System.out.println(down + " & " + (firstDownLine - ballPosX)/2);
    }

    public static void updateQuarter(Window win, World world) {
        if (! updatedQuarter) {
            updatedQuarter = true;
            if (!pat) {
                if (timeLeft <= 0 && overtime == 0) {
                    // Update Next Quarter
                    runClock = false;
                    System.out.println(timeLeft + " Seconds");
                    if (quarter <= 3) {
                        quarter++;
                        timeLeft = 300;
                        if (quarter == 3) {
                            kickoff = true;
                            runClock = false;
                            timeOutsAway = 3;
                            timeoutsHome = 3;
                            if ((homeDefer && userHome) || (! homeDefer && ! userHome))
                                userOffense = false;
                            else
                                userOffense = true;
                        }
                    } else {
                        if (homeScore == awayScore) {
                            System.out.println("DOESWORK");
                            quarter = 5;
                            overtime = 1;

                            timeoutsHome = 1;
                            timeOutsAway = 1;

                            ballPosX = xEndzoneRight - 50;
                            ballPosY = -251;
                            firstDownLine = ballPosX + 20;
                            down = 1;


                            if ((userHome && homeDefer) || (! userHome && ! homeDefer))
                                userOffense = false;
                            else
                                userOffense = true;
                        }
                        else
                        endGame(win);
                        // Add End of Game Stuff Here
                    }

                    if (quarter == 2 || quarter == 4) // Prevents Down Increment From InitReset
                        down--;

                    world.initReset(win);
                }
            }
        }
    }

    public static void endGame(Window win) {
        System.out.println("GAME OVER");
        win.closeWindow();
    }

    public float getBallPosX() { return ballPosX; }
    public float getBallPosY() { return ballPosY; }

}
