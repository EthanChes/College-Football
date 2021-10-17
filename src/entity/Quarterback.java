package entity;
import collision.AABB;
import collision.Collision;
import graphics.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Quarterback {
    private Model model;
    private Animation qbthrow;
    private AABB bounding_box;
    private Texture qb;
    private Transform transform;

    public Quarterback() {
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
        qbthrow = new Animation(2, 2, "qbthrow/");

        transform = new Transform();
        transform.scale = new Vector3f(16,16,1);

        bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(1,1));

    }

    public void update(float delta, Window window, Camera camera, World world) {

        // Moves Player using various WASD directions using vectors.
        if (window.getInput().isKeyDown(GLFW_KEY_S)) { // When S is pressed, player moves 5 down
            transform.pos.add(new Vector3f(0,-10*delta,0)); // multiply by delta (framecap) to move 10 frames in a second.
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A)) { // When A is pressed, camera shifts left 5
            transform.pos.add(new Vector3f(-10*delta,0,0));
        }
        if (window.getInput().isKeyDown(GLFW_KEY_W)) { // When W is pressed, camera shifts up 5
            transform.pos.add(new Vector3f(0,10*delta,0));
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D)) { // When D is pressed, camera shifts right 5
            transform.pos.add(new Vector3f(10*delta,0,0));
        }

        bounding_box.getCenter().set(transform.pos.x,transform.pos.y);

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
        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), .05f);

    }

    public void render(Shader shader, Camera camera, Window window) {
        Matrix4f sampler_0 = new Matrix4f();
        shader.bind();
        shader.setUniform("sampler", sampler_0);
        shader.setUniform("projection",transform.getProjection(camera.getProjection()));
        qbthrow.bind(0);
        model.render();
    }

}
