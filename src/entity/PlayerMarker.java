package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

public class PlayerMarker extends Entity {
    public static int ANIM_SIZE = 1;
    public static int ANIM_EXIST = 0;

    public static Vector2f setLocation = new Vector2f();

    public PlayerMarker(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_EXIST, new Animation(1, 1, "misc/controlplayermarker"));

        noCollision();
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        useAnimation(ANIM_EXIST);
        this.transform.pos.set(setLocation.x, setLocation.y, 0);
        setLocation.set(0,0);
    }
}
