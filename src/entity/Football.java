package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

public class Football extends Entity {
    public static final int ANIM_SIZE = 1;
    public static final int ANIM_QB_HOLD = 0;

    public Football(Transform transform) {
        super(ANIM_SIZE, transform);

        setAnimation(ANIM_QB_HOLD,new Animation(1,1,"footballqb"));
    }



    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f(); // for passes
    }
}
