package entity;
import collision.AABB;
import collision.Collision;
import graphics.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

public abstract class Entity {
    protected static Model model;
    protected Animation[] animations;
    private int use_animation;
    protected AABB bounding_box;
    protected Transform transform;
    protected boolean canCollide = true;
    protected boolean pass = false;
    public boolean hasBall = false;

    public Entity(int max_animations, Transform transform) {
        this.transform = transform;
        this.animations = new Animation[max_animations];
        this.use_animation = 0;

        bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(transform.scale.x, transform.scale.y));
    }

    protected void setAnimation(int index, Animation animation) {
        animations[index] = animation;
    }

    public void useAnimation(int index) {
        this.use_animation = index;
    }

    public abstract void update(float delta, Window window, Camera camera, World world);

    public void move(Vector2f direction) {
        transform.pos.add(new Vector3f(direction,0));

        bounding_box.getCenter().set(transform.pos.x,transform.pos.y);
    }

    public void render(Shader shader, Camera camera, Window window, World world) {
        Matrix4f target = camera.getProjection();
        target.mul(world.getWorld());

        Matrix4f sampler_0 = new Matrix4f();
        shader.bind();
        shader.setUniform("sampler", sampler_0);
        shader.setUniform("projection",transform.getProjection(target));
        animations[use_animation].bind(0);
        model.render();
    }

    public static void initAsset() {
        // Forms Tile Structure
        float[] vertices = new float[]{
                // VERTICES ARE TWO RIGHT TRIANGLES, Locations of the Corners of the Square formed by the Right Triangles are found below.
                -1f, 1f, 0, // TOP LEFT 0 (x,y,z) is the formatting
                1f, 1f, 0, // TOP RIGHT 1
                1f, -1f, 0, // BOTTOM RIGHT 2
                -1f, -1f, 0, // BOTTOM LEFT 3
        };

        float[] texture = new float[]{
                // Coordinates of Texture location on Model/Vertex Structure. (0,0 BL, 1,1 TR).
                0, 0, // 0, (x,y), this the location on the model or square produced in the Model Class, that the texture's vertices will occupy.
                1, 0, // 1
                1, 1, // 2
                0, 1, // 3
        };

        int[] indices = new int[]{ // Indices of the triangles. See Texture and Vertices comments. Each index corresponds to a vertex defined above.
                0, 1, 2,
                2, 3, 0,
        };

        model = new Model(vertices,texture,indices);
    }

    public boolean collidingWithFootball(Entity entity, World world) {
        Collision collision = world.getFootballEntity().bounding_box.getCollision(entity.bounding_box);

        if (collision.isIntersecting) {
            return true;
        }
        return false;
    }

    public void collideWithTiles(World world) {
        AABB[] boxes = new AABB[25];
        for (int count = 0; count < 5; count++) {
            for (int counter = 0; counter < 5; counter++) {
                boxes[count+counter*5] = world.getTileBoundingBox( (int) ((transform.pos.x / 2) + .5f) - (5/2) + count,
                        (int) ((-transform.pos.y/2) + .5f) - (5/2) + counter);

            }
        }

        AABB box = null;
        for (int count = 0; count < boxes.length; count++) {
            if (boxes[count] != null) {
                if (box == null) box = boxes[count];

                Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                Vector2f length2 = boxes[count].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

                if (length1.lengthSquared() > length2.lengthSquared()) {
                    box = boxes[count];
                }
            }
        }

        if (box != null) {
            Collision data = bounding_box.getCollision(box);
            if (data.isIntersecting) {
                bounding_box.correctPosition(box, data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }

            for (int count = 0; count < boxes.length; count++) {
                if (boxes[count] != null) {
                    if (box == null) box = boxes[count];

                    Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                    Vector2f length2 = boxes[count].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

                    if (length1.lengthSquared() > length2.lengthSquared()) {
                        box = boxes[count];
                    }
                }
            }
            data = bounding_box.getCollision(box);

            if (data.isIntersecting) {
                bounding_box.correctPosition(box, data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }
        }
    }

    public void collideWithEntity(Entity entity, World world) {
        Collision collision = bounding_box.getCollision(entity.bounding_box);

        if (collision.isIntersecting && entity.canCollide && canCollide) {
            collision.distance.x /= 2;
            collision.distance.y /= 2;

            bounding_box.correctPosition(entity.bounding_box, collision);
            transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);

            entity.bounding_box.correctPosition(bounding_box,collision);
            entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y,0);
        }
    }

    public void zoomOutWhenNotVisible(Entity entity, Camera cam) { // 640 width OG, 480 height OG
        float range = 25;
        float entityX = (16.1141f*entity.bounding_box.getCenter().x)-8.518391f;
        float entityY = 16f*(-entity.bounding_box.getCenter().y);
        float camX = (-cam.getPosition().x);
        float camY = (cam.getPosition().y);
        float camWidth = 640 * cam.getProjMultiplier();
        float camHeight = 480 * cam.getProjMultiplier();

        if (entityX + range > camX + camWidth/2) {  // Checks if entity is near end of window on right and adjusts projection to prevent them from leaving sight. This algorithm may need tweaking
            System.out.println("Nearing Window End Right");
            cam.setProjMultiplier(cam.getProjMultiplier()*1.007f);
            cam.setProjection(640*cam.getProjMultiplier(),480*cam.getProjMultiplier());
        }
        else if (entityY + range > camY + camHeight/2) {
            System.out.println("Nearing Window End Bottom");
        }
        else if (entityY - range < camY - camHeight/2) {
            System.out.println("Nearing Window End Top");
        }

    }

    public void noCollision() {
        canCollide = false;
    }

    public void startPass() {
        this.pass = true;
    }


    public static void deleteAsset() {
        model = null;
    }

}
