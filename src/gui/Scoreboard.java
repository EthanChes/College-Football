package gui;

import assets.Assets;
import entity.GameManager;
import graphics.Camera;
import graphics.Shader;
import graphics.TileSheet;
import graphics.Window;
import org.joml.Matrix4f;

public class Scoreboard {
    private static Shader shader;
    private static Camera camera;
    private static TileSheet teams;
    private static TileSheet numbers;
    private static TileSheet yellowNumbers;

    private static float translateX;
    private static float translateY;

    private static int pointer;

    public Scoreboard(Window window, float transX, float transY, int tileID) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());

        // Select Teams Sheet, Numbers + Characters Sheet, Yellow Numbers Sheet
        teams = new TileSheet("D_PLAYS.png",3);
        numbers = new TileSheet("D_PLAYS.png",4);
        yellowNumbers = new TileSheet("D_PLAYS.png",4);

        translateX = transX;
        translateY = transY;
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
    }

    public void Render() {
        Matrix4f mat = new Matrix4f();

        camera.getUntransformedProjection().scale(107, mat);
        mat.translate(translateX,translateY,0);

        shader.bind();

        shader.setUniform("projection", mat);
        teams.bindTile(shader, pointer);
        Assets.getModel().render();

        // translate Matrix to new position & scale Camera & set new Pointer Value
        numbers.bindTile(shader, pointer);
        Assets.getModel().render();

        // Same Thing as above
        yellowNumbers.bindTile(shader, pointer);
        Assets.getModel().render();
    }
}
