package world;

import assets.Assets;
import entity.GameManager;
import graphics.*;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class SelectTeam {
    Shader guiShader;
    private Texture texture;
    private Matrix4f scale;
    private Matrix4f translation;
    private static TileSheet logos;
    public static boolean canRun = false;
    public static boolean controlHome = true;

    public SelectTeam(String texture) {
        this.texture = new Texture( "/interface/" + texture);

        scale = new Matrix4f();
        translation = new Matrix4f();

        logos = new TileSheet("LOGOS.png",3);
        guiShader = new Shader("gui");
    }

    public void update(Window window) {
        if (window.getInput().isKeyPressed(GLFW.GLFW_KEY_RIGHT) || (window.getInput().isKeyPressed(GLFW.GLFW_KEY_LEFT))) { // Switch Home team
            if (controlHome)
                controlHome = false;
            else
                controlHome = true;
        }

        if (controlHome) {
            if (window.getInput().isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
                GameManager.homeID--;
            } else if (window.getInput().isKeyPressed(GLFW.GLFW_KEY_UP)) {
                GameManager.homeID++;
            }
        } else {
            if (window.getInput().isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
                GameManager.awayID--;
            } else if (window.getInput().isKeyPressed(GLFW.GLFW_KEY_UP)) {
                GameManager.awayID++;
            }
        }
    }

    public void render(Shader shader, Camera camera) {
        // Render black screen
        Matrix4f mat = new Matrix4f();
        shader.bind();

        camera.getUntransformedProjection().scale(320,240,1, mat);
        shader.setUniform("projection", mat);
        this.bind(0,0,shader);
        Assets.getModel().render();

        // Render Individual Teams (AWAY)
        guiShader.bind();;
        camera.getUntransformedProjection().scale(100, mat);
        mat.translate(2,0,0);
        guiShader.setUniform("projection", mat);

        if (GameManager.awayID > 7) // Prevents Blank Teams
            GameManager.awayID = 0;
        else if (GameManager.awayID < 0)
            GameManager.awayID = 7;

        logos.bindTile(guiShader, GameManager.awayID);
        Assets.getModel().render();

        // Render Home Team
        mat.translate(-4,0,0);
        guiShader.setUniform("projection", mat);

        if (GameManager.homeID > 7) // Prevents Blank Teams
            GameManager.homeID = 0;
        else if (GameManager.homeID < 0)
            GameManager.homeID = 7;

        logos.bindTile(guiShader, GameManager.homeID);
        Assets.getModel().render();

        // Display Marker for home & away
        camera.getUntransformedProjection().scale(50,mat);
        if (controlHome)
            mat.translate(-4f,-3f,0);
        else
            mat.translate(4f,-3f,0);

        Texture marker = new Texture("interface/MARKER.png");
        guiShader.setUniform("projection",mat);
        guiShader.setUniform("texModifier", translation);
        marker.bind(0);
        Assets.getModel().render();
    }

    public void bind(float x, float y, Shader shader) {
        scale.translate(x,y,0,translation);

        shader.setUniform("sampler", new Matrix4f());
        shader.setUniform("texModifier", translation);
        texture.bind(0);
    }
}
