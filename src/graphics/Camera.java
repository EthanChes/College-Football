package graphics;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Matrix4f projection;
    private float projMultiplier = 1;

    public Camera(int width, int height) {
            position = new Vector3f(-200*16,250*16,0); // multiply actual position on screen for entities by 16.
            setProjection(640,480);
    }
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addPosition(Vector3f position) {
        this.position.add(position);
    }

    public void setProjection(float width, float height) {
        projection = new Matrix4f().setOrtho2D(-width/2,width/2,-height/2,height/2);
    }

    public Vector3f getPosition() { return position;}

    public Matrix4f getProjection() {
        return projection.translate(position, new Matrix4f());
    }

    public float getProjMultiplier() { return projMultiplier; }

    public void setProjMultiplier(float projMultiplier) {
        this.projMultiplier = projMultiplier;
    }



}
