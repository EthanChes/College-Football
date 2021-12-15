package entity;

import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class ReceiverSymbol extends Entity {
    public static final int ANIM_SIZE = 5;
    public static final int ANIM_SYMBOL_P = 0;
    public static final int ANIM_SYMBOL_O = 1;
    public static final int ANIM_SYMBOL_I = 2;
    public static final int ANIM_SYMBOL_U = 3;
    public static final int ANIM_SYMBOL_Y = 4;

    public static int index = 1;

    public ReceiverSymbol(Transform transform) {
        super(ANIM_SIZE, transform);
        noCollision();

        setAnimation(ANIM_SYMBOL_P, new Animation(1,1,"symbol_p"));
        setAnimation(ANIM_SYMBOL_O, new Animation(1,1,"symbol_o"));
        setAnimation(ANIM_SYMBOL_I, new Animation(1,1,"symbol_i"));
        setAnimation(ANIM_SYMBOL_U, new Animation(1,1,"symbol_u"));
        setAnimation(ANIM_SYMBOL_Y, new Animation(1,1,"symbol_y"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        // Bruh Momentum Nothing Goes Here, all the action is done in wide receiver class
    }
}
