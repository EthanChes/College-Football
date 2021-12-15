package entity;

import entity.Entity;
import world.World;

public class GameManager {
    float yMax;
    float yMin;
    float xMax;
    float xMin;
    float xEndzoneLeft;
    float xEndzoneRight;
    public static float ballPosX = 192f;
    public static float ballPosY = -250f;
    public static int down = 0;
    public static float firstDownLine = ballPosX + 20;
    public static boolean offenseBall = true;
    public static boolean userOffense = true;

    public GameManager(float yMax, float yMin, float xMax, float xMin, float xEndzoneLeft, float xEndzoneRight) {
        this.yMax = yMax;
        this.yMin = yMin;
        this.xMax = xMax;
        this.xMin = xMin;
        this.xEndzoneLeft = xEndzoneLeft;
        this.xEndzoneRight = xEndzoneRight;
    }

    public boolean ballCarrierOutOfBounds(World world) { // Check if Out of Bounds (ALL)
        if (world.getBallCarrier().transform.pos.x > xMax) {
            Entity.canPlay = false;
            return true;
        }
        else if (world.getBallCarrier().transform.pos.x < xMin) {
            Entity.canPlay = false;
            return true;
        }
        else if (world.getBallCarrier().transform.pos.y < yMin) {
            Entity.canPlay = false;
            return true;
        }
        else if (world.getBallCarrier().transform.pos.y > yMax) {
            Entity.canPlay = false;
            return true;
        }
        return false;
    }

    public boolean touchDown(World world) { // Check If TD (Offense)
        if (world.getBallCarrier().transform.pos.x > xEndzoneRight && world.getBallCarrier().transform.pos.x < xMax) {
            return true;
        }
        return false;
    }

    public void setBallPosX(World world) {
        down++;
        if (world.getBallCarrier() != null && ! Entity.incompletePass) {
            this.ballPosX = world.getFootballEntity().transform.pos.x - .6f;
        }

        if (ballPosX > firstDownLine) {
            down = 1;
            firstDownLine = ballPosX + 20;
            System.out.println("FIRST DOWN");
        }

    }
    public void setBallPosY(World world) {
        if (world.getBallCarrier() != null && !Entity.incompletePass) {
            if (world.getFootballEntity().transform.pos.y > -245.9) {
                this.ballPosY = -245.9f;
            } else if (world.getFootballEntity().transform.pos.y < -254) {
                this.ballPosY = -254f;
            } else {
                this.ballPosY = world.getFootballEntity().transform.pos.y + .3f;
            }
        }
    }

    public static void printDownInfo() {
        System.out.println(down + " & " + (firstDownLine - ballPosX)/2);
    }

    public float getBallPosX() { return ballPosX; }
    public float getBallPosY() { return ballPosY; }

}
