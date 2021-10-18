package entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f pos;
    public Vector3f scale;

    public Transform() { // default location of entity
        pos = new Vector3f(4,-14,0);
        scale = new Vector3f(1,1,1);
    }

    public Transform(float x, float y) { // Set location of entity
        pos = new Vector3f(x,y,0);
        scale = new Vector3f(1,1,1);
    }

    public Matrix4f getProjection(Matrix4f target) {
        target.translate(pos);
        target.scale(scale);
        return target;
    }
}
