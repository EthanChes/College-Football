package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class KickLevel extends Entity {
    public static int ANIM_SIZE = 1;
    public static int ANIM_EXIST = 0;

    public KickLevel(Transform transform) {
        super(ANIM_SIZE, transform);

        setAnimation(ANIM_EXIST, new Animation(1,1,"kickmarker"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
    }
}
