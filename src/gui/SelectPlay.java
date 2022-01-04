package gui;

import assets.Assets;
import entity.GameManager;
import graphics.Camera;
import graphics.Shader;
import graphics.TileSheet;
import graphics.Window;
import org.joml.Matrix4f;

public class SelectPlay {
    private static Shader shader;
    private static Camera camera;
    private static TileSheet sheet;
    private static TileSheet special;
    private static TileSheet numbers;
    private static TileSheet recommended;

    private float x;
    private int tileID;
    private static int playID;

    private static int lastTileID = -1;

    public SelectPlay(Window window, float x, int tileID) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());

        // Select Correct Sheet
        if (GameManager.userOffense) {
            if (GameManager.kickoff) {
                sheet = new TileSheet("KICKOFF.png",1);
            } else {
                sheet = new TileSheet("O_PLAYS.png", 3);
                special = new TileSheet("SPECIALOFFENSE.png",1);
            }
        } else {
            if (GameManager.kickoff) {
                sheet = new TileSheet("KICKOFFRETURN.png",1);
            } else {
                sheet = new TileSheet("D_PLAYS.png", 3);
                special = new TileSheet("SPECIALDEFENSE.png",1);
            }
        }

        numbers = new TileSheet("INVISIBLENUMBERS.png",4);
        recommended = new TileSheet("RECOMMENDED.png",2);

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

        mat.translate(0,1.8f,0);
        shader.setUniform("projection",mat);
        numbers.bindTile(shader,1);
        Assets.getModel().render();

        mat.translate(2,-1.8f,0);
        shader.setUniform("projection", mat);
        sheet.bindTile(shader, tileID + 1);
        Assets.getModel().render();

        mat.translate(0,1.8f,0);
        shader.setUniform("projection",mat);
        numbers.bindTile(shader,2);
        Assets.getModel().render();

        mat.translate(2,-1.8f,0);
        shader.setUniform("projection", mat);
        sheet.bindTile(shader, tileID + 2);
        Assets.getModel().render();

        mat.translate(0,1.8f,0);
        shader.setUniform("projection",mat);
        numbers.bindTile(shader,3);
        Assets.getModel().render();

        if (! GameManager.kickoff) {
            camera.getUntransformedProjection().scale(75,mat);
            mat.translate(3.3f,-2.4f,0);
            shader.setUniform("projection",mat);
            special.bindTile(shader, 0);
            Assets.getModel().render();
        }

        camera.getUntransformedProjection().scale(75,mat);
        mat.translate(0,-2.4f,0);
        shader.setUniform("projection",mat);
        int pointer = 0;

        int scoreDiff;
        if (GameManager.userHome) {
            scoreDiff = GameManager.awayScore - GameManager.homeScore;
        } else {
            scoreDiff = GameManager.homeScore - GameManager.awayScore;
        }

        if ((GameManager.ballPosX > GameManager.xEndzoneRight - 70 && GameManager.down == 4 && true) || (GameManager.pat && ! (GameManager.quarter == 4 && (scoreDiff == -2 || scoreDiff == -5 || scoreDiff == 1))))
            pointer = 1;
        else if (GameManager.down == 4 && true)
            pointer = 2;
        else
            pointer = 0;


        recommended.bindTile(shader, pointer);
        Assets.getModel().render();

        //shader.setUniform("color", new Vector4f(0,0,0,.4f));
    }

    public static int getNextTileID() { return lastTileID + 1; }

    public static void prepNextTileID() { lastTileID -= 1; }

    public static void incrementNextTileID() { if (lastTileID < 0) { lastTileID += 36; } lastTileID += 0; } // 3 for future plays

    public static void decrementNextTileID() { if (lastTileID < 0) { lastTileID += 36; } lastTileID -= 0; } // 3 for future plays

    public static void calculatePlayID(int input) { playID = (lastTileID + input + 1) % 9; System.out.println(playID); } // Only 3 Plays

    public static void calculateSpecificPlayID(int input) { playID = input; System.out.println(playID); } // For Special Plays

    public static int getPlayID() { return playID; }
}
