package gui;

import assets.Assets;
import entity.GameManager;
import graphics.Camera;
import graphics.Shader;
import graphics.TileSheet;
import graphics.Window;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SelectPlay {
    private static Shader shader;
    private static Camera camera;
    private static TileSheet sheet;

    private float x;
    private int tileID;
    private static int playID;

    private static int lastTileID = -1;

    public SelectPlay(Window window, float x, int tileID) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());

        // Select Correct Sheet
        if (GameManager.userOffense) {
            sheet = new TileSheet("PLAYS.png", 3);
        } else {
            sheet = new TileSheet("PLAYS.png",3);
        }

        this.x = x;
        this.tileID = tileID;

        lastTileID = tileID;
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
    }

    public void Render() {
        Matrix4f mat = new Matrix4f();

        camera.getUntransformedProjection().scale(107, mat);
        mat.translate(x,0,0);

        shader.bind();

        shader.setUniform("projection", mat);
        sheet.bindTile(shader, tileID);
        Assets.getModel().render();

        mat.translate(2,0,0);
        shader.setUniform("projection", mat);
        sheet.bindTile(shader, tileID + 1);
        Assets.getModel().render();

        mat.translate(2,0,0);
        shader.setUniform("projection", mat);
        sheet.bindTile(shader, tileID + 2);
        Assets.getModel().render();

        //shader.setUniform("color", new Vector4f(0,0,0,.4f));
    }

    public static int getNextTileID() { return lastTileID + 1; }

    public static void prepNextTileID() { lastTileID -= 1; }

    public static void incrementNextTileID() { if (lastTileID < 0) { lastTileID += 36; } lastTileID += 3; }

    public static void decrementNextTileID() { if (lastTileID < 0) { lastTileID += 36; } lastTileID -= 3; }

    public static void calculatePlayID(int input) { playID = (lastTileID + input + 1) % 9; } // Only 9 plays

    public static int getPlayID() { return playID; }
}
