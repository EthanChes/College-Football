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
    public static float ballPosX = 194f;
    public static float ballPosY = -250f;

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
            return true;
        }
        else if (world.getBallCarrier().transform.pos.x < xMin) {
            return true;
        }
        else if (world.getBallCarrier().transform.pos.y < yMin) {
            return true;
        }
        else if (world.getBallCarrier().transform.pos.y > yMax) {
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

    public void setBallPosX(World world) { this.ballPosX = world.getFootballEntity().transform.pos.x; }
    public void setBallPosY(World world) { this.ballPosY = world.getFootballEntity().transform.pos.y; }

    public float getBallPosX() { return ballPosX; }
    public float getBallPosY() { return ballPosY; }

}
