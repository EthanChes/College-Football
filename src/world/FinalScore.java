package world;

import assets.Assets;
import entity.GameManager;
import graphics.Camera;
import graphics.Shader;
import graphics.Texture;
import graphics.TileSheet;
import org.joml.Matrix4f;

public class FinalScore {
    private Texture texture;
    private Matrix4f scale;
    private Matrix4f translation;
    public static boolean canRun = false;
    public static TileSheet teams;
    public static TileSheet invisibleNumbers;

    public FinalScore(String texture) {
        this.texture = new Texture( "/interface/" + texture);

        scale = new Matrix4f();
        translation = new Matrix4f();

        teams = new TileSheet("TEAMS.png",3);
        invisibleNumbers = new TileSheet("INVISIBLENUMBERS.png",4);
    }

    public void render(Shader shader, Camera camera) {
        // Render background image
        Matrix4f mat = new Matrix4f();
        camera.getUntransformedProjection().scale(320,240,1, mat);
        shader.bind();
        shader.setUniform("projection", mat);
        this.bind(0,0,shader);
        Assets.getModel().render();

        Shader tileSheetShader = new Shader("gui");
        tileSheetShader.bind();

        // Render Home Team With Shader designed for tileSHeets
        camera.getUntransformedProjection().scale(100,mat);
        mat.translate(-2,0,0);
        tileSheetShader.setUniform("projection",mat);
        teams.bindTile(tileSheetShader,GameManager.homeID);
        Assets.getModel().render();

        // Render away team
        mat.translate(4,0,0);
        tileSheetShader.setUniform("projection",mat);
        teams.bindTile(tileSheetShader,GameManager.awayID);
        Assets.getModel().render();

        // Render Home Score 1st Digit
        camera.getUntransformedProjection().scale(50,mat);
        mat.translate(-5.5f,-1,0);
        tileSheetShader.setUniform("projection",mat);
        invisibleNumbers.bindTile(tileSheetShader,GameManager.homeScore/10);
        System.out.println(GameManager.homeScore/10);
        Assets.getModel().render();

        // Render 2nd Digit of Home Score
        mat.translate(1,0,0);
        tileSheetShader.setUniform("projection",mat);
        invisibleNumbers.bindTile(tileSheetShader,GameManager.homeScore%10);
        Assets.getModel().render();

        // Render 1st Digit of Away Score
        mat.translate(7,0,0);
        tileSheetShader.setUniform("projection",mat);
        invisibleNumbers.bindTile(tileSheetShader,GameManager.awayScore/10);
        Assets.getModel().render();

        // Render 2nd Digit of Away Score
        mat.translate(1,0,0);
        tileSheetShader.setUniform("projection",mat);
        invisibleNumbers.bindTile(tileSheetShader,GameManager.awayScore%10);
        Assets.getModel().render();


    }

    public void bind(float x, float y, Shader shader) {
        scale.translate(x,y,0,translation);

        shader.setUniform("sampler", new Matrix4f());
        shader.setUniform("texModifier", translation);
        texture.bind(0);
    }
}
