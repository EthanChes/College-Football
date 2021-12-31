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

    public Scoreboard(Window window, float transX, float transY) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());

        // Select Teams Sheet, Numbers + Characters Sheet, Yellow Numbers Sheet
        teams = new TileSheet("TEAMS.png",3);
        numbers = new TileSheet("NUMBERS.png",4);
        yellowNumbers = new TileSheet("YELLOWNUMBERS.png",4);

        translateX = transX;
        translateY = transY;
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
    }

    public void update() {

    }

    public void Render() {
        Matrix4f mat = new Matrix4f();

        camera.getUntransformedProjection().scale(50, mat);
        mat.translate(translateX,translateY,0);

        shader.bind();

        // Render home team
        shader.setUniform("projection", mat);
        pointer = GameManager.homeID;
        teams.bindTile(shader, pointer);
        Assets.getModel().render();

        // Render away team
        mat.translate(2, 0, 0);
        shader.setUniform("projection",mat);
        pointer = GameManager.awayID;
        teams.bindTile(shader, pointer);
        Assets.getModel().render();

        // New Camera scale for yardage to 1st down & time in Game
        camera.getUntransformedProjection().scale(10,mat);

        // Render Down & Yardage to 1st Down
        mat.translate(-31,-13,0);
        shader.setUniform("projection",mat);
        pointer = GameManager.down;
        numbers.bindTile(shader, pointer);
        Assets.getModel().render();

        // Render letters in accordance with down
        mat.translate(1.6f,0,0);
        shader.setUniform("projection",mat);
        pointer = GameManager.down + 11;
        numbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render ampersand
        mat.translate(2f,0,0);
        shader.setUniform("projection",mat);
        pointer = 11;
        numbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render yards to first down
        mat.translate(2f,0,0);
        shader.setUniform("projection",mat);
        pointer = (int) Math.ceil(GameManager.firstDownLine - GameManager.ballPosX)/20;
        numbers.bindTile(shader,pointer);
        if (pointer == 0) {
            yellowNumbers.bindTile(shader,15);
        }
        Assets.getModel().render();

        mat.translate(1.4f,0,0);
        shader.setUniform("projection",mat);
        pointer = (int) Math.ceil(GameManager.firstDownLine - GameManager.ballPosX)%20/2;

        if (pointer == 0 && (int) Math.ceil(GameManager.firstDownLine - GameManager.ballPosX)/20 == 0)// Replace with inches in future, prevents 2nd & 0
            pointer++;

        numbers.bindTile(shader,pointer);
        Assets.getModel().render();


        // Same Thing as above
        yellowNumbers.bindTile(shader, pointer);
        //Assets.getModel().render();
    }
}
