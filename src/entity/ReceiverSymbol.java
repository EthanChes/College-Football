package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class ReceiverSymbol extends Entity {
    public static final int ANIM_SIZE = 1;
    public static final int ANIM_SYMBOL_P = 0;

    public static int index = 1;

    public ReceiverSymbol(Transform transform) {
        super(ANIM_SIZE, transform);
        noCollision();

        setAnimation(ANIM_SYMBOL_P, new Animation(1,1,"symbol_p"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        // Bruh Momentum Nothing Goes Here, all the action is done in wide receiver class
    }
}
