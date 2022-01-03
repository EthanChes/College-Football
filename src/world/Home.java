package world;
import assets.Assets;
import graphics.Camera;
import graphics.Shader;
import graphics.Texture;
import graphics.Window;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class Home {
    private Texture texture;
    private Matrix4f scale;
    private Matrix4f translation;
    public static boolean canRun = true;

    public Home(String texture) {
        this.texture = new Texture( "/interface/" + texture);

        scale = new Matrix4f();
        translation = new Matrix4f();
    }

    public void render(Shader shader, Camera camera) {
        Matrix4f mat = new Matrix4f();
        camera.getUntransformedProjection().scale(350, mat);
        shader.bind();
        shader.setUniform("projection", mat);
        this.bind(0,0,shader);
        Assets.getModel().render();
    }

    public void bind(float x, float y, Shader shader) {
        scale.translate(x,y,0,translation);

        shader.setUniform("sampler", new Matrix4f());
        shader.setUniform("texModifier", translation);
        texture.bind(0);
    }
}
