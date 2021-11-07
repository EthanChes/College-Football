package entity;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;

public class OffensiveLineman extends Entity {
    public static final int ANIM_SIZE = 3;
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_MOVE = 1;
    public static final int ANIM_BLOCK = 2;

    public OffensiveLineman (Transform transform) {
        super(ANIM_SIZE,transform);
        setAnimation(ANIM_IDLE, new Animation(1,1,"offensivelineidle"));
        setAnimation(ANIM_MOVE, new Animation(4,16, "offensivelinemove"));
        setAnimation(ANIM_BLOCK, new Animation(1,1, "offensivelineblock"));
        speed = 3f;
    }

    public Vector2f moveToDefender(float delta) {
        Vector2f move = new Vector2f();

        move.add(speed*delta,0);

        return move;
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (! hasBall) {
            movement.add(moveToDefender(delta));
        }

        move(movement);

        if (movement.x != 0 || movement.y != 0) {
            useAnimation(ANIM_MOVE);
        }
        else {
            useAnimation(ANIM_IDLE);
        }
    }
}
