package gui;

import assets.Assets;
import graphics.Camera;
import graphics.Shader;
import graphics.TileSheet;
import graphics.Window;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SelectPlay {
    private Shader shader;
    private Camera camera;
    private TileSheet sheet;

    private float x;
    private int tileID;

    private static int lastTileID = -1;

    public SelectPlay(Window window, float x, int tileID) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());
        sheet = new TileSheet("PLAYS.png", 3);

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

        //shader.setUniform("color", new Vector4f(0,0,0,.4f));

        Assets.getModel().render();
    }

    public static int getNextTileID() { return lastTileID + 1; }

    public static void prepNextTileID() { lastTileID -= 3; }

    public static void incrementNextTileID() { if (lastTileID < 0) { lastTileID += 36; } lastTileID += 3; }

    public static void decrementNextTileID() { if (lastTileID < 0) { lastTileID += 36; } lastTileID -= 3; }
}
