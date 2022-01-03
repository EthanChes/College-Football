package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector3f;
import world.World;

public class Route extends Entity {

    public static int ANIM_SIZE = 7;
    public static int ANIM_ZONE_LOW = 6;
    public static int ANIM_ZONE_MID = 5;
    public static int ANIM_ZONE_HIGH = 4;
    public static int ANIM_DIAGONAL_DOWN = 3;
    public static int ANIM_DIAGONAL_UP = 2;
    public static int ANIM_RIGHT = 1;
    public static int ANIM_DOWN = 0;

    public Route(Vector3f position, float distance, int direction, Transform transform) {
        super(ANIM_SIZE, transform);
        transform.pos.set(position);

        setAnimation(ANIM_DIAGONAL_DOWN, new Animation(1,1, "routebases/diagonaldown"));
        setAnimation(ANIM_DIAGONAL_UP, new Animation(1,1, "routebases/diagonalup"));
        setAnimation(ANIM_RIGHT, new Animation(1,1, "routebases/right"));
        setAnimation(ANIM_DOWN, new Animation(1,1, "routebases/down"));

        switch (direction) {
            case 0 : transform.scale.set(1,1,1); useAnimation(ANIM_DIAGONAL_DOWN); break;
            case 1 : transform.scale.set(2,2,1); useAnimation(ANIM_DIAGONAL_UP); break;
            case 2 : transform.scale.set(distance/2,1,1); useAnimation(ANIM_RIGHT); transform.pos.x += distance/2; break;
            case 3 : transform.scale.set(1,distance/2,1); useAnimation(ANIM_DOWN); transform.pos.y += distance/2; break;
        }

        noCollision();
    }

    public Route(Transform transform, int zone, Vector3f position) {
        super(ANIM_SIZE, transform);

        setAnimation(ANIM_ZONE_LOW, new Animation(1,1, "routebases/zonelow"));
        setAnimation(ANIM_ZONE_MID, new Animation(1,1, "routebases/zonemid"));
        setAnimation(ANIM_ZONE_HIGH, new Animation(1,1, "routebases/zonehigh"));

        switch (zone) {
            case 0 : useAnimation(ANIM_ZONE_LOW); this.transform.pos.set(position); this.transform.scale.set(2,4,1); break;
            case 1 : useAnimation(ANIM_ZONE_MID); this.transform.pos.set(position); this.transform.scale.set(4,7,1); break;
            case 2 : useAnimation(ANIM_ZONE_HIGH); this.transform.pos.set(position); this.transform.scale.set(6,7,1); break;
        }
    }

    public Route(Vector3f position, float distanceX, float distanceY, float degrees, Transform transform, boolean normalAssetUsage) {
        super(ANIM_SIZE, transform);
        transform.pos.set(position);

        setAnimation(ANIM_DIAGONAL_DOWN, new Animation(1,1, "routebases/diagonaldown"));
        setAnimation(ANIM_DIAGONAL_UP, new Animation(1,1, "routebases/diagonalup"));
        setAnimation(ANIM_RIGHT, new Animation(1,1, "routebases/right"));
        setAnimation(ANIM_DOWN, new Animation(1,1, "routebases/down"));

        this.degrees = degrees;

        transform.scale.set(distanceX*2.5f,.25f,1);

        useAnimation(ANIM_DOWN);

        transform.pos.y += distanceY/2;
        transform.pos.x += distanceX/2;

        normalAssets = normalAssetUsage;

        noCollision();
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
    }
}
