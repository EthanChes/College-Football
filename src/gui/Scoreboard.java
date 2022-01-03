package gui;

import assets.Assets;
import entity.Entity;
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
    private static TileSheet invisNumbers;
    private static TileSheet letters;
    private static TileSheet timeouts;

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
        invisNumbers = new TileSheet("INVISIBLENUMBERS.png",4);
        letters = new TileSheet("LETTERS.png",6);
        timeouts = new TileSheet("TIMEOUTS.png", 2);

        translateX = transX;
        translateY = transY;
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
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

        if (GameManager.pat) {
            mat.translate(-31,-13,0); // P
            shader.setUniform("projection", mat);
            pointer = 15;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(2f,0,0); // A
            shader.setUniform("projection", mat);
            pointer = 0;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(2f,0,0); // T
            shader.setUniform("projection", mat);
            pointer = 19;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(2f,0,0); // BLANK
            shader.setUniform("projection", mat);
            pointer = 35;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(1f,0,0); // BLANK
            shader.setUniform("projection", mat);
            pointer = 35;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();
        }
        else if (GameManager.kickoff) { // Render "KICKOFF"
            mat.translate(-31,-13,0); // K
            shader.setUniform("projection", mat);
            pointer = 10;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(2f,0,0); // I
            shader.setUniform("projection", mat);
            pointer = 8;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(2f,0,0); // C
            shader.setUniform("projection", mat);
            pointer = 2;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(4,0,0); // BLANK
            shader.setUniform("projection", mat);
            pointer = 35;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(-2,0,0); // K
            shader.setUniform("projection", mat);
            pointer = 10;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();

            mat.translate(1,0,0); // Pushes 1 Unit Right
        }
        else {
            // Render Down & Yardage to 1st Down
            mat.translate(-31, -13, 0);
            shader.setUniform("projection", mat);
            pointer = GameManager.down;
            numbers.bindTile(shader, pointer);
            Assets.getModel().render();

            // Render letters in accordance with down
            mat.translate(1.6f, 0, 0);
            shader.setUniform("projection", mat);
            pointer = GameManager.down + 11;
            numbers.bindTile(shader, pointer);
            Assets.getModel().render();

            // Render ampersand
            mat.translate(2f, 0, 0);
            shader.setUniform("projection", mat);
            pointer = 11;
            numbers.bindTile(shader, pointer);
            Assets.getModel().render();

            // Render yards to first down
            if (GameManager.firstDownLine >= GameManager.xEndzoneRight) {
                mat.translate(2,0,0);
                shader.setUniform("projection", mat);
                pointer = 26;
                letters.bindTile(shader, pointer);
                Assets.getModel().render();

                mat.translate(2f,0,0);
                shader.setUniform("projection", mat);
                pointer = 35;
                letters.bindTile(shader,pointer);
                Assets.getModel().render();

                mat.translate(-.6f,0,0);
            } else {
                mat.translate(2f, 0, 0);
                shader.setUniform("projection", mat);
                pointer = (int) Math.ceil(GameManager.firstDownLine - GameManager.ballPosX) / 20;
                numbers.bindTile(shader, pointer);
                if (pointer == 0) {
                    yellowNumbers.bindTile(shader, 15);
                }
                Assets.getModel().render();

                mat.translate(1.4f, 0, 0);
                shader.setUniform("projection", mat);
                pointer = (int) Math.ceil(GameManager.firstDownLine - GameManager.ballPosX) % 20 / 2;

                if (pointer == 0 && (int) Math.ceil(GameManager.firstDownLine - GameManager.ballPosX) / 20 == 0)// Replace with inches in future, prevents 2nd & 0
                    pointer++;

                numbers.bindTile(shader, pointer);
                Assets.getModel().render();
            }
        }

        if (GameManager.quarter <= 4) {
            // Render Quarter
            mat.translate(1.8f, 0, 0);
            shader.setUniform("projection", mat);
            pointer = GameManager.quarter;
            numbers.bindTile(shader, pointer);
            Assets.getModel().render();

            // Renders Quarter's following letters
            mat.translate(1.6f, 0, 0);
            shader.setUniform("projection", mat);
            pointer = GameManager.quarter + 11;
            numbers.bindTile(shader, pointer);
            Assets.getModel().render();
        } else {
            mat.translate(1.8f,0,0); // O
            shader.setUniform("projection", mat);
            pointer = 14;
            letters.bindTile(shader, pointer);
            Assets.getModel().render();

            mat.translate(1.8f,0,0); // T
            shader.setUniform("projection",mat);
            pointer = 19;
            letters.bindTile(shader,pointer);
            Assets.getModel().render();
            mat.translate(-.2f,0,0);
        }

        // Render Time in Game - 1st Digit Minutes
        mat.translate(2,0,0);
        shader.setUniform("projection",mat);
        pointer = (int) Math.ceil(GameManager.timeLeft)/60/10;
        numbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render Time in Game - 2nd Digit Minutes
        mat.translate(1.4f,0,0);
        shader.setUniform("projection",mat);
        pointer = (int) Math.ceil(GameManager.timeLeft)/60%10;
        numbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render Time in Game - Semicolon
        mat.translate(1.4f,0,0);
        shader.setUniform("projection",mat);
        pointer = 10;
        numbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render Time in Game - 1st Digit Seconds
        mat.translate(1.4f,0,0);
        shader.setUniform("projection",mat);
        pointer = (int) Math.ceil(GameManager.timeLeft)%60/10;
        numbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render Time in Game - 2nd Digit Seconds
        mat.translate(1.4f,0,0);
        shader.setUniform("projection",mat);
        pointer = (int) Math.ceil(GameManager.timeLeft)%60%10;
        numbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render PlayClock only if play hasn't started yet
        if (!Entity.playStart && !Entity.canPlay) {
            // Render 1st digit
            mat.translate(2,0,0);
            shader.setUniform("projection",mat);
            pointer = (int) Math.ceil(GameManager.playClock)/10;
            yellowNumbers.bindTile(shader,pointer);
            Assets.getModel().render();

            // Render 2nd digit
            mat.translate(1.5f,0,0);
            shader.setUniform("projection",mat);
            pointer = (int) Math.ceil(GameManager.playClock)%10;
            yellowNumbers.bindTile(shader,pointer);
            Assets.getModel().render();
        }

        // Render Scores for home team (first digit)
        camera.getUntransformedProjection().scale(25,mat);
        mat.translate(-12.42f,-8.75f,0);
        shader.setUniform("projection",mat);
        pointer = GameManager.homeScore/10;
        invisNumbers.bindTile(shader,pointer);
        if (pointer != 0)
            Assets.getModel().render();

        // Render Scores for home team (second digit)
        mat.translate(1,0,0);
        shader.setUniform("projection",mat);
        pointer = GameManager.homeScore%10;
        invisNumbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render Scores for away team (first digit)
        mat.translate(3,0,0);
        shader.setUniform("projection",mat);
        pointer = GameManager.awayScore/10;
        invisNumbers.bindTile(shader,pointer);
        if (pointer != 0)
            Assets.getModel().render();

        // Render Scores for away team (second digit)
        mat.translate(1,0,0);
        shader.setUniform("projection",mat);
        pointer = GameManager.awayScore%10;
        invisNumbers.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render Away Timeouts
        camera.getUntransformedProjection().scale(10,mat);
        mat.translate(-17,-18.55f,0);
        shader.setUniform("projection",mat);

        if (GameManager.timeOutsAway >= 3)
            pointer = 0;
        else
            pointer = 1;

        timeouts.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render 2nd Timeout
        mat.translate(-2,0,0);
        shader.setUniform("projection",mat);

        if (GameManager.timeOutsAway >= 2)
            pointer = 0;
        else
            pointer = 1;

        timeouts.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render 3rd Timeout
        mat.translate(-2,0,0);
        shader.setUniform("projection",mat);

        if (GameManager.timeOutsAway >= 1)
            pointer = 0;
        else
            pointer = 1;

        timeouts.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render Home Timeouts
        mat.translate(-6,0,0);
        shader.setUniform("projection",mat);

        if (GameManager.timeoutsHome >= 3)
            pointer = 0;
        else
            pointer = 1;

        timeouts.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render 2nd Timeout
        mat.translate(-2,0,0);
        shader.setUniform("projection",mat);

        if (GameManager.timeoutsHome >= 2)
            pointer = 0;
        else
            pointer = 1;

        timeouts.bindTile(shader,pointer);
        Assets.getModel().render();

        // Render 3rd Timeout
        mat.translate(-2,0,0);
        shader.setUniform("projection",mat);

        if (GameManager.timeoutsHome >= 1)
            pointer = 0;
        else
            pointer = 1;

        timeouts.bindTile(shader,pointer);
        Assets.getModel().render();










        // Same Thing as above
        yellowNumbers.bindTile(shader, pointer);
        //Assets.getModel().render();
    }
}
