package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class KickMarker extends Entity {
    public static int ANIM_SIZE = 1;
    public static int ANIM_EXIST = 0;

    public static float level = 0;

    public static boolean stop = false;
    public boolean increasing = true;

    public KickMarker(Transform transform) {
        super(ANIM_SIZE, transform);

        setAnimation(ANIM_EXIST, new Animation(1,1,"kickpower"));

        level = 0;
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        if (! stop) {
            if (level == 20)
                increasing = false;
            if (level == 0)
                increasing = true;

            if (increasing) {
                level += .5f;
            } else {
                level -= .5f;
            }
        }


    }
}
