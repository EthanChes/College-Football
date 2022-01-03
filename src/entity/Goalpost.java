package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class Goalpost extends Entity {
    public static int ANIM_SIZE = 2;
    public static int ANIM_LEFT = 1;
    public static int ANIM_RIGHT = 0;
    public Goalpost(Transform transform, int animation) {
        super(ANIM_SIZE, transform);

        setAnimation(ANIM_LEFT, new Animation(1,1,"goalpostleft"));
        setAnimation(ANIM_RIGHT, new Animation(1,1, "goalpostright"));
        noCollision();

        useAnimation(animation);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {

    }
}
