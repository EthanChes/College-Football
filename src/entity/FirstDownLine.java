package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class FirstDownLine extends Entity {
    public static int ANIM_SIZE = 1;
    public static int ANIM_EXIST = 0;

    public FirstDownLine(Transform transform) {
        super(ANIM_SIZE, transform);
        noCollision();

        setAnimation(ANIM_EXIST, new Animation(1,1, "firstdownline"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        if (GameManager.pat || GameManager.kickoff || GameManager.firstDownLine >= GameManager.xEndzoneRight) { // Place The first down marker in unseeable location in the event of a pat, kickoff ,or goal line play
            this.transform.pos.set(0,0,0);
        }
    }
}
