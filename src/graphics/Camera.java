package graphics;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Matrix4f projection;
    private float projMultiplierX = 1;
    private float projMultiplierY = 1;
    private float width;
    private float height;

    public Camera(int width, int height) {
            position = new Vector3f(-200*16,250*16,0); // multiply actual position on screen for entities by 16.
            setProjection(640,480);

            this.width = width;
            this.height = height;
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

    public Matrix4f getUntransformedProjection() {
        return projection;
    }

    public Matrix4f getProjection() {
        return projection.translate(position, new Matrix4f());
    }

    public float getProjMultiplierX() { return projMultiplierX; }

    public float getProjMultiplierY() { return projMultiplierY; }

    public void setProjMultiplierY(float setter) { projMultiplierY = setter; }

    public void setProjMultiplierX(float setter) { projMultiplierX = setter; }

    public float getWidth() { return width; }

    public float getHeight() { return height; }



}
