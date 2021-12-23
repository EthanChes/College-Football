package entity;
import assets.Assets;
import collision.AABB;
import collision.Collision;
import gameplay.Timer;
import graphics.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

import java.util.Random;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

public abstract class Entity {
    protected Animation[] animations;
    private int use_animation;
    protected AABB bounding_box;
    protected Transform transform;

    // Game booleans
    public static float throw_height;
    public static int totalReceivers = 0;
    public static boolean canPlay = false;
    public static boolean playStart = false;
    public boolean forceUserControl = false;
    public double timeUserControl;
    protected boolean canCollide = true;
    protected boolean pass = false;
    public boolean hasBall = false;
    public boolean reachedEndOfRoute = false;
    public boolean userControl = false;
    public boolean isBeingMovedExternally = false;
    public boolean pancaked = false;
    public double timePancaked;
    public boolean uniqueEvents = false;
    public boolean center = false;
    public boolean catchAttempt = true;
    public static double timeSnapped;
    public double timeSinceLastTackleAttempt = 0;
    public boolean inCatch = false;
    public static boolean incompletePass = false;
    public static double timeFumble = -1;
    public double timeFumbled = -1;
    public static double selectPlayerCooldown = -1;

    // Player Info
    public byte route = 0;
    public float throw_decisions = 10f;
    public float manCoverage = 10f;
    public float routeMovement = 0f;
    public float speed = 10f;
    public float strength = 10f;
    public float throw_power = 10f;
    public float throw_accuracy = 10f;
    public float catching = 10f;
    public float zoneCoverage = 10f;

    public Entity(int max_animations, Transform transform) {
        this.transform = transform;
        this.animations = new Animation[max_animations];
        this.use_animation = 0;

        bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(transform.scale.x, transform.scale.y));
    }

    protected void setAnimation(int index, Animation animation) {
        animations[index] = animation;
    }

    public void useAnimation(int index) {
        this.use_animation = index;
    }

    public abstract void update(float delta, Window window, Camera camera, World world);

    public void move(Vector2f direction) {
        transform.pos.add(new Vector3f(direction,0));

        bounding_box.getCenter().set(transform.pos.x,transform.pos.y);
    }

    public void render(Shader shader, Camera camera, Window window, World world) {
        Matrix4f target = camera.getProjection();
        target.mul(world.getWorld());

        Matrix4f sampler_0 = new Matrix4f();
        shader.bind();
        shader.setUniform("sampler", sampler_0);
        shader.setUniform("projection",transform.getProjection(target));
        animations[use_animation].bind(0);
        Assets.getModel().render();
    }

    public boolean collidingWithFootball(Entity entity, World world) {
        Collision collision = world.getFootballEntity().bounding_box.getCollision(entity.bounding_box);

        if (collision.isIntersecting && throw_height > 0 && throw_height < 6) { // 6 Will Be Starting Receiver Height
            return true;
        }
        else if (collision.isIntersecting && ! entity.hasBall) { // Remove ! entity.hasBall for final version, this just helps with print message
            entity.catchAttempt = false;
            //System.out.println("Throw too high for catch.");
        }
        return false;
    }

    public boolean collidingWithBallCarrier(Entity entity, World world) {
        Collision hit = world.getBallCarrier().bounding_box.getCollision(entity.bounding_box);

        return hit.isIntersecting;
    }

    public void collideWithTiles(World world) {
        AABB[] boxes = new AABB[25];
        for (int count = 0; count < 5; count++) {
            for (int counter = 0; counter < 5; counter++) {
                boxes[count+counter*5] = world.getTileBoundingBox( (int) ((transform.pos.x / 2) + .5f) - (5/2) + count,
                        (int) ((-transform.pos.y/2) + .5f) - (5/2) + counter);

            }
        }

        AABB box = null;
        for (int count = 0; count < boxes.length; count++) {
            if (boxes[count] != null) {
                if (box == null) box = boxes[count];

                Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                Vector2f length2 = boxes[count].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

                if (length1.lengthSquared() > length2.lengthSquared()) {
                    box = boxes[count];
                }
            }
        }

        if (box != null) {
            Collision data = bounding_box.getCollision(box);
            if (data.isIntersecting) {
                bounding_box.correctPosition(box, data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }

            for (int count = 0; count < boxes.length; count++) {
                if (boxes[count] != null) {
                    if (box == null) box = boxes[count];

                    Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                    Vector2f length2 = boxes[count].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

                    if (length1.lengthSquared() > length2.lengthSquared()) {
                        box = boxes[count];
                    }
                }
            }
            data = bounding_box.getCollision(box);

            if (data.isIntersecting) {
                bounding_box.correctPosition(box, data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }
        }
    }

    public static void forceInitiateDefensivePlayer(World world) {
        world.getCountingUpEntity(0).forceUserControl = true;
        world.getCountingUpEntity(0).timeUserControl = Timer.getTime();
    }

    public void deselectAllDefenders(World world) {
        for (int i = 0; i < 11; i++) {
            world.getCountingUpEntity(i).forceUserControl = false;
        }
    }

    public void selectDefensivePlayer(Window win, World world) {
        if (!GameManager.userOffense && GameManager.offenseBall && win.getInput().isKeyPressed(GLFW_KEY_Z) && selectPlayerCooldown + .3f < Timer.getTime()) {
            // Enabled to select new player

            selectPlayerCooldown = Timer.getTime();

            if (playStart) {
                // Sort To Find New Closest Player
                Entity closestPlayer = world.getCountingUpEntity(0);
                for (int i = 0; i < 11; i++) {
                    world.getCountingUpEntity(i).forceUserControl = false;
                    if (!world.getCountingUpEntity(i).userControl) {
                        if (closestPlayer.timeUserControl + .5 < Timer.getTime()) {
                            if (world.getCountingUpEntity(i).transform.pos.distance(world.getBallCarrier().transform.pos) < closestPlayer.transform.pos.distance(world.getBallCarrier().transform.pos)) {
                                closestPlayer = world.getCountingUpEntity(i);
                            }
                        }
                    }
                }

                if (closestPlayer == world.getCountingUpEntity(0) && world.getCountingUpEntity(0).userControl) {
                    closestPlayer = world.getCountingUpEntity(1);
                }

                // Force User Control to closestPlayer
                closestPlayer.forceUserControl = true;
                closestPlayer.timeUserControl = Timer.getTime();
                System.out.println(closestPlayer);
            } else {
                for (int i = 0; i < 11; i++) {
                    world.getCountingUpEntity(i).forceUserControl = false;

                    if (world.getCountingUpEntity(i).userControl && i + 1 <= 10) {
                        world.getCountingUpEntity(i + 1).forceUserControl = true;
                        world.getCountingUpEntity(i + 1).timeUserControl = Timer.getTime();
                        i = 10;
                    } else if (i == 10) {
                        world.getCountingUpEntity(0).forceUserControl = true;
                        world.getCountingUpEntity(0).timeUserControl = Timer.getTime();
                    }
                }
            }
        }
    }

    public void selectOffensivePlayer(Window win, World world) {
        // Upon Key Press, Switch Player
        if (win.getInput().isKeyPressed(GLFW_KEY_Z) && GameManager.userOffense && ! GameManager.offenseBall && selectPlayerCooldown + .3f < Timer.getTime()) {
            selectPlayerCooldown = Timer.getTime();

            // Sort To Find New Closest Player
            Entity closestPlayer = world.getCountingUpEntity(11);
            for (int i = 11; i < 22; i++) {
                world.getCountingUpEntity(i).forceUserControl = false;
                if (!world.getCountingUpEntity(i).userControl) {
                    if (closestPlayer.timeUserControl + .5 < Timer.getTime()) {
                        if (world.getCountingUpEntity(i).transform.pos.distance(world.getBallCarrier().transform.pos) < closestPlayer.transform.pos.distance(world.getBallCarrier().transform.pos)) {
                            closestPlayer = world.getCountingUpEntity(i);
                        }
                    }
                }
            }

            if (closestPlayer == world.getCountingUpEntity(11) && world.getCountingUpEntity(11).userControl) {
                closestPlayer = world.getCountingUpEntity(12);
            }

            // Force User Control to closestPlayer
            closestPlayer.forceUserControl = true;
            closestPlayer.timeUserControl = Timer.getTime();
            System.out.println(closestPlayer);
        }
    }

    public void forceSelectOffensivePlayer(Window win, World world) {
        // Sort To Find New Closest Player
        Entity closestPlayer = world.getCountingUpEntity(11);
        for (int i = 11; i < 22; i++) {
            world.getCountingUpEntity(i).forceUserControl = false;
            if (closestPlayer.timeUserControl + .5 < Timer.getTime()) {
                if (world.getCountingUpEntity(i).transform.pos.distance(world.getBallCarrier().transform.pos) < closestPlayer.transform.pos.distance(world.getBallCarrier().transform.pos)) {
                    closestPlayer = world.getCountingUpEntity(i);
                }
            }
        }

        // Force User Control to closestPlayer
        closestPlayer.forceUserControl = true;
        closestPlayer.timeUserControl = Timer.getTime();
        System.out.println(closestPlayer);
    }


    public void collideWithEntity(Entity entity, World world, int earlyID, int lateID) {
        Collision collision = bounding_box.getCollision(entity.bounding_box);

        if (collision.isIntersecting && entity.canCollide && canCollide) {
            collision.distance.x /= 2;
            collision.distance.y /= 2;

            bounding_box.correctPosition(entity.bounding_box, collision);
            transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);

            if (this.hasBall || this.inCatch) {
                world.getFootballEntity().bounding_box.correctPosition(entity.bounding_box,collision);
                world.getFootballEntity().transform.pos.set(world.getFootballEntity().bounding_box.getCenter().x,world.getFootballEntity().bounding_box.getCenter().y,0);

                if (world.getCountingUpEntity(earlyID).userControl) {
                    world.getPlayerMarker().transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);
                }
            }

            entity.bounding_box.correctPosition(bounding_box, collision);
            entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);

            if (entity.hasBall || entity.inCatch) {
                world.getFootballEntity().bounding_box.correctPosition(bounding_box,collision);
                world.getFootballEntity().transform.pos.set(world.getFootballEntity().bounding_box.getCenter().x,world.getFootballEntity().bounding_box.getCenter().y,0);

                if (world.getCountingUpEntity(lateID).userControl) {
                    world.getPlayerMarker().transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
                }
            }
        }
    }

    public void zoomOutWhenNotVisible(Entity entity, Camera cam, World world) { // 640 width OG, 480 height OG
        float range = 25;
        float entityX = (16.1141f*entity.bounding_box.getCenter().x)-8.518391f;
        float entityY = 16f*(-entity.bounding_box.getCenter().y);
        float camX = (-cam.getPosition().x);
        float camY = (cam.getPosition().y);
        float camWidth = 640 * cam.getProjMultiplierX();
        float camHeight = 480 * cam.getProjMultiplierY();

        Entity currentFurthest = entity;

        // Get Furthest Offensive Entity
        for (int i = 11; i < 22; i++) {
            if (world.getCountingUpEntity(i).transform.pos.distance(world.getFootballEntity().transform.pos) > currentFurthest.transform.pos.distance(world.getFootballEntity().transform.pos)) {
                currentFurthest = world.getCountingUpEntity(i);
            }
        }

        Vector3f setCameraPos = new Vector3f();

        // Set Proper Camera position
        setCameraPos.set((currentFurthest.transform.pos.x + world.getFootballEntity().transform.pos.x)*-8, (currentFurthest.transform.pos.y + world.getFootballEntity().transform.pos.y)*-8, 0);

        cam.getPosition().lerp(setCameraPos.mul(1, new Vector3f()), .001f); // Camera adjusts to center football

        if (entityX + range > camX + camWidth/2) {  // Checks if entity is near end of window on right and adjusts projection to prevent them from leaving sight. This algorithm may need tweaking
            cam.setProjMultiplierX(cam.getProjMultiplierX()*1.5f);

            cam.setProjMultiplierY(cam.getProjMultiplierY()*1.5f);

            cam.setProjection(cam.getWidth()*cam.getProjMultiplierX(), cam.getHeight()*cam.getProjMultiplierY());

            System.out.println("Right");
        }
        else if (entityX - range < camX - camWidth/2) {  // Checks if entity is near end of window on right and adjusts projection to prevent them from leaving sight. This algorithm may need tweaking
            cam.setProjMultiplierX(cam.getProjMultiplierX()*1.5f);

            cam.setProjMultiplierY(cam.getProjMultiplierY()*1.5f);

            cam.setProjection(cam.getWidth()*cam.getProjMultiplierX(), cam.getHeight()*cam.getProjMultiplierY());

            System.out.println("LEFT");
        }

    }

    public void noCollision() {
        canCollide = false;
    }

    public void startPass() {
        this.pass = true;
    }

    public Vector2f chaseProjectedLocation(Entity defender, float x, float y, Entity ball, float delta, Entity quarterback) {
        Vector2f location = new Vector2f(defender.transform.pos.x, defender.transform.pos.y);
        Vector2f projBallMovement = new Vector2f(ball.transform.pos.x, ball.transform.pos.y);

        for (; projBallMovement.distance(quarterback.transform.pos.x, quarterback.transform.pos.y) <= quarterback.transform.pos.distance(x,y,0);) {
            projBallMovement.add(quarterback.throw_power*delta*5,0);
            location.add(defender.moveToward(x,y,delta));
        }

        return location;
    }

    public Vector2f getProjectedLocation(Entity entity, Entity ball, float delta, World world) {
        Vector2f location = new Vector2f(entity.transform.pos.x,entity.transform.pos.y);
        float projRouteMovement = entity.routeMovement;
        float quarterbackX = world.getQuarterbackEntity().transform.pos.x;
        float quarterbackY = world.getQuarterbackEntity().transform.pos.y;
        Vector2f projBallMovement = new Vector2f(ball.transform.pos.x,ball.transform.pos.y);
        for (; projBallMovement.distance(quarterbackX,quarterbackY) <= location.distance(quarterbackX,quarterbackY) && ! reachedEndOfRoute;) {
            switch (entity.route) { // Lookup Table
                case 0:
                    if (projRouteMovement <= 90) { // Fade
                        location.add(entity.speed*delta,0);
                        projRouteMovement += entity.speed*delta;
                        projBallMovement.add(throw_power*delta,0);
                    }
                    else {
                        reachedEndOfRoute = true;
                    }
                    break;

                case 1:
                    if (projRouteMovement <= 20) { // In Route From Left
                        location.add(entity.speed*delta,0);
                        projRouteMovement += entity.speed * delta;
                        projBallMovement.add(throw_power*delta,0);
                    } else if (projRouteMovement <= 35) {
                        location.add(0, -entity.speed*delta);
                        projRouteMovement += entity.speed * delta;
                        projBallMovement.add(throw_power*delta,0);
                    }
                    else { reachedEndOfRoute = true; }
                    break;

                case 2 : if (projRouteMovement <= 10) { // Slant
                    location.add(entity.speed*delta,0);
                    projRouteMovement += entity.speed * delta;
                    projBallMovement.add(throw_power*delta,0);
                }
                else if (projRouteMovement <= 40) {
                    location.add(entity.speed*delta,-entity.speed*delta);
                    projRouteMovement += new Vector2f().distance(entity.speed*delta,-entity.speed*delta);
                    projBallMovement.add(throw_power*delta,0);
                }
                else { reachedEndOfRoute = true; }
                break;

                case 3 : if (projRouteMovement <= 15) { // Curl
                    location.add(entity.speed*delta,0);
                    projRouteMovement += entity.speed * delta;
                    projBallMovement.add(throw_power*delta,0);
                } else { reachedEndOfRoute = true; }
                    break;

                case 4 :  // In Route From Right
                    if (projRouteMovement <= 20) { // In Route
                    location.add(entity.speed*delta,0);
                    projRouteMovement += entity.speed * delta;
                    projBallMovement.add(throw_power*delta,0);
                } else if (projRouteMovement <= 35) {
                    location.add(0, entity.speed*delta);
                    projRouteMovement += entity.speed * delta;
                    projBallMovement.add(throw_power*delta,0);
                }
                else { reachedEndOfRoute = true; }
                    break;

            }
        }
        return location;
    }

    public void setRoute(int index) {
        this.route = (byte) index;
    }

    public boolean snap(Window window, World world) {
        if (window.getInput().isKeyPressed(GLFW_KEY_SPACE) && ! playStart && GameManager.selectedPlay) {
            world.getCountingUpEntity(14).useAnimation(6);
            timeSnapped = Timer.getTime();
            canPlay = true;
            playStart = true;
            return true;
        }

        return false;
    }

    public void passCaught(World world) {
        throw_height = 0;
        world.getFootballEntity().pass = false;
        world.getFootballEntity().useAnimation(1);
    }

    public void setDefenderID(int ID, DefensiveBack defensiveBack) {
        defensiveBack.defenderID = ID;
    }

    public int getAnimationIndex() { return use_animation; }

    public Vector2f moveToward(float x, float y, float delta) {
        Vector2f movement = new Vector2f();

        if (this.transform.pos.x - speed * delta > x) {
            movement.add(-speed * delta, 0);
        } else if (this.transform.pos.x + speed * delta < x) {
            movement.add(speed * delta, 0);
        }

        if (this.transform.pos.y - speed * delta > y) {
            movement.add(0, -speed * delta);
        } else if (this.transform.pos.y + speed * delta < y) {
            movement.add(0, speed * delta);
        }

        return movement;
    }

    public Vector2f offenseHasBallMove(World world, float delta) {
        Vector2f move = new Vector2f();

        int playersLeft = 0;
        int playersRight = 0;

        for (int i = 0; i <= 22; i++) {
            if (world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos) < 6 && world.getCountingUpEntity(i).transform.pos.x > this.transform.pos.x) {
                if (world.getCountingUpEntity(i).transform.pos.y > this.transform.pos.y) {
                    playersRight++;
                } else {
                    playersLeft++;
                }
            }
        }

        if (playersRight + playersLeft == 0) {
            move.add(speed*delta,0);
            return move;
        }

        if (playersRight > playersLeft) {
            if (this.transform.pos.y - speed*delta < -265.9) {
                move.add(speed*delta,0);
            } else {
                move.add(speed*delta, -speed*delta);
            }
        } else {
            if (this.transform.pos.y + speed*delta > -234.3) {
                move.add(speed*delta,0);
            } else {
                move.add(speed * delta, speed * delta);
            }
        }

        return move;
    }

    public Vector2f defenseHasBallMove(World world, float delta) {
        Vector2f move = new Vector2f();

        int playersLeft = 0;
        int playersRight = 0;

        for (int i = 0; i <= 22; i++) {
            if (world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos) < 6 && world.getCountingUpEntity(i).transform.pos.x < this.transform.pos.x) {
                if (world.getCountingUpEntity(i).transform.pos.y > this.transform.pos.y) {
                    playersRight++;
                } else {
                    playersLeft++;
                }
            }
        }

        if (playersRight + playersLeft == 0) {
            move.add(-speed*delta,0);
            return move;
        }

        if (playersRight > playersLeft) {
            if (this.transform.pos.y - speed*delta < -265.9) {
                move.add(-speed*delta,0);
            } else {
            move.add(-speed*delta, -speed*delta);
            }
        } else {
            if (this.transform.pos.y + speed*delta > -234.3) {
                move.add(-speed*delta,0);
            } else {
                move.add(-speed * delta, speed * delta);
            }
        }

        return move;
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

    public void preventBallGlitchAfterPlay(Entity football) {
        football.transform.pos.set(this.transform.pos);
    }

    public void setAllOffenseForceUserControlFalse(World world) {
        for (int i = 11; i < 22; i++) {
            world.getCountingUpEntity(i).forceUserControl = false;
        }
    }

    public boolean tackle(Entity ballCarrier, Window win, World world) {
        boolean tackle = false;

        Random rand = new Random();
        int rand_output = rand.nextInt((int) (this.strength*100 + ballCarrier.strength*100));

        if (rand_output <= this.strength*2) { // set to 2
            timeFumble = Timer.getTime();

            if (GameManager.userOffense && GameManager.offenseBall) {
                forceSelectOffensivePlayer(win, world);
            }

            ballCarrier.pancaked = true;
            ballCarrier.timePancaked = Timer.getTime();
            ballCarrier.timeFumbled = Timer.getTime();
            System.out.println("Fumble");
        }
        else if (rand_output <= this.strength*100) {
            tackle = true;
            System.out.println("Tackle");
        }
        else {
            System.out.println("Tackle Evaded");
        }
        timeSinceLastTackleAttempt = Timer.getTime();

        return tackle;
    }

    public Vector2f offenseBlockUnique(World world, float delta) {
        Vector2f move = new Vector2f();

        int addY;

        if (this.transform.pos.y > world.getBallCarrier().transform.pos.y && world.getBallCarrier().transform.pos.y + 3 < GameManager.yMax) {
            addY = 3;
        } else if (world.getBallCarrier().transform.pos.y - 3 > GameManager.yMin){
            addY = -3;
        } else {
            addY = 3;
        }

        move.add(moveToward(world.getBallCarrier().transform.pos.x + 3, world.getBallCarrier().transform.pos.y + addY,delta));

        for (int i = 0; i < 11; i++) {
            boolean shouldBlock = false;

            if (collidingWithBallCarrier(this, world) && world.getBallCarrier() != this && world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos) < 3) {
                this.pancaked = true;
                this.timePancaked = Timer.getTime();
            }

            if (world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos) < 3) {
                shouldBlock = true;
            }

            if (shouldBlock) {
                if (this.transform.pos.y > world.getBallCarrier().transform.pos.y) {
                    move.x = speed*delta;
                } else {
                    move.y = -speed*delta*addY/3;
                }
            }
        }

        return move;
    }

    public Vector2f defenseBlockUnique(World world, float delta) {
        Vector2f move = new Vector2f();

        int addY;

        if (this.transform.pos.y > world.getBallCarrier().transform.pos.y && this.transform.pos.y + 3 < GameManager.yMax) {
            addY = 3;
        } else if (this.transform.pos.y - 3 > GameManager.yMin){
            addY = -3;
        } else {
            addY = 3;
        }

        move.add(moveToward(world.getBallCarrier().transform.pos.x - 3, world.getBallCarrier().transform.pos.y + addY,delta));

        for (int i = 11; i < 22; i++) {

            if (collidingWithBallCarrier(this, world) && world.getBallCarrier() != this && world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos) < 3) {
                this.pancaked = true;
                this.timePancaked = Timer.getTime();
            }

            boolean shouldBlock = false;
            if (world.getCountingUpEntity(i).transform.pos.distance(this.transform.pos) < 3) {
                shouldBlock = true;
            }

            if (shouldBlock) {
                if (this.transform.pos.y > world.getBallCarrier().transform.pos.y) {
                    move.x = -speed*delta;
                } else {
                    move.y = -speed*delta*addY/3;
                }
            }
        }

        return move;
    }
}
