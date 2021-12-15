package world;
import collision.AABB;
import entity.*;
import entity.GameManager;
import graphics.Camera;
import graphics.Shader;
import graphics.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import plays.Four_Verticals;
import plays.Line_Blitz;
import plays.RB_Dive;
import plays.Zone;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class World {
    GameManager gameManager = new GameManager(-234.3f, -265.9f, 366.6f, 141.8f, 153f, 354.5f);
    private int viewX;
    private int viewY;
    private byte[] tiles;
    private AABB[] bounding_boxes;
    private List<Entity> entities;
    private int width;
    private int height;
    private int scale;

    private Matrix4f world;

    private Entity ballCarrier;






    public World(String stadium) { // Load world from file

        try {
             BufferedImage tile_sheet = ImageIO.read(new File("res/stadiums/" + stadium + "_tiles.png"));

             width = tile_sheet.getWidth();
             height = tile_sheet.getHeight();
             scale = 16;

            int[] colorTileSheet = tile_sheet.getRGB(0,0,tile_sheet.getWidth(),tile_sheet.getHeight(), null, 0, tile_sheet.getWidth());

            this.world = new Matrix4f().setTranslation(new Vector3f(0));
            this.world.scale(scale);// Tiles are 32x32, since scale = 16 * 2 due to renderTile using 2*length

            tiles = new byte[width*height];
            bounding_boxes = new AABB[width * height];
            entities = new ArrayList<Entity>();

            for (int y = 0; y < tile_sheet.getHeight(); y++) { // x and y represent id of tile
                for (int x = 0; x < tile_sheet.getWidth(); x++) {
                    int red = (colorTileSheet[x + y * tile_sheet.getWidth()] >> 16) & 0xFF; // represents red on tile of grid of map

                    Tile t;
                    try {
                        t = Tile.tiles[red];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        t = null;
                    }

                    if (t != null) {
                        setTile(t, x, y); // sets tile
                    }

                }
            }



            /*entities.add(new Quarterback(new Transform(200,-250)));
            entities.add(new DefensiveLineman(new Transform(210,-240,1.5f)));
            //entities.add(new OffensiveLineman(new Transform(200, -237,1.5f)));
            entities.add(new RunningBack(new Transform(190,-240)));
            entities.add(new WideReceiver(new Transform(200,-240)));
            entities.add(new WideReceiver(new Transform(200,-245)));
            entities.add(new WideReceiver(new Transform(200,-235)));

            for (int count = 0; count < WideReceiver.totalReceivers; count++) {
                entities.add(new ReceiverSymbol(new Transform(200, -250, .75f))); // Adds Symbol for Receiver
            }

            entities.add(new Football(new Transform(200f,-250f,.5f))); // MUST BE AT BOTTOM

            setBallCarrier(getSpecifiedEntity(entities.size() - 1));*/








        } catch (IOException e) {
            e.printStackTrace();
        }





    }






    public World() { // straight grass world
        width = 64; // width of world
        height = 64; // height of world
        scale = 16;

        tiles = new byte[width*height];
        bounding_boxes = new AABB[width*height];

        world = new Matrix4f().setTranslation(new Vector3f(0));
        world.scale(scale);// Tiles are 32x32, since scale = 16 * 2 due to renderTile using 2*length
    }









    public void render(TileRenderer render, Shader shader, Camera camera, Window window) {
        int posX = ((int) camera.getPosition().x/ (scale * 2));
        int posY = ((int) camera.getPosition().y/ (scale * 2));

        for (int count = 0; count < viewX; count++) {
            for (int counter = 0; counter < viewY; counter++) {
                Tile t = getTile(count-posX-(viewX/2)+1, counter+posY-(viewY/2));
                if (t != null) {
                    render.renderTile(t, count-posX-(viewX/2)+1, -counter-posY+(viewY/2), shader, world, camera);
                }
            }
        }

        for (Entity entity : entities) {
            entity.render(shader,camera,window,this);
        }

        for (int count = 0; count < entities.size(); count++) {
            entities.get(count).collideWithTiles(this);
        }
    }








    public void update(float delta, Window window, Camera camera) {
        for (Entity entity : entities) {
            entity.update(delta,window,camera,this);
        }

        for (int i = 0; i < 22; i++) {
            if (getCountingUpEntity(i).hasBall && getBallCarrier() != getCountingUpEntity(i)) {
                getCountingUpEntity(i).hasBall = false;
            }
        }

        for (int count = 0; count < entities.size(); count++) {
            entities.get(count).collideWithTiles(this);
            for (int counter = count+1; counter < entities.size(); counter++) {
                 entities.get(count).collideWithEntity(entities.get(counter), this);
            }
            entities.get(count).collideWithTiles(this);
        }

        if (gameManager.ballCarrierOutOfBounds(this)) {
            System.out.println("Out Of Bounds");
            Entity.canPlay = false;
        }

        if (gameManager.touchDown(this)) {
            System.out.println("Touchdown Offense");
        }

    }








    public void correctCamera(Camera camera, Window window) {
        Vector3f pos = camera.getPosition();

        int w = -width * scale * 2; // Width of world, multiply by 2 once again due to the 2* in renderTile()
        int h = height * scale * 2; // Height of world


       if (pos.x > -(window.getWidth()/2) + scale) { // Camera corrections, cannot leave world on -x side.
            pos.x = -(window.getWidth()/2) + scale; // add scale to prevent camera from stopping at half the tile.
        }

        if (pos.x < w + (window.getWidth()/2) + scale) { // Corrects camera such that it may not leave world on x side.
            pos.x = w + (window.getWidth()/2 + scale);
        }

        if (pos.y < (window.getHeight()/2) - scale) { // Camera cannot leave world on y side
            pos.y = (window.getHeight()/2)-scale;
        }

        if (pos.y > h - (window.getHeight()/2) - scale) { // Camera cannot leave world on -y side
            pos.y = h - (window.getHeight()/2) - scale;
        }
    }











    public void setTile(Tile tile, int x, int y) {
        tiles[x + y * width] = tile.getId();
        if (tile.isSolid()) {
            bounding_boxes[x+y*width] = new AABB(new Vector2f(x*2, -y*2), new Vector2f(1,1));
        } else {
            bounding_boxes[x+y*width] = null;
        }
    }









    public Tile getTile(int x, int y) {
        try {
            return Tile.tiles[tiles[x+y*width]];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }








    public AABB getTileBoundingBox(int x, int y) {
        try {
            return bounding_boxes[x+y*width];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public void calculateView(Window window, Camera cam) {
        viewX = (int) ((window.getWidth()*cam.getProjMultiplier())/(scale*2)) + 4;
        viewY = (int) (((window.getHeight()*cam.getProjMultiplier())/(scale*2)) + 4);
    }

    public Entity getFootballEntity() {
        return entities.get(entities.size()-1);
    }

    public Entity getSpecifiedEntity(int index) { return entities.get(entities.size()- (index + 1)); }

    public Entity getCountingUpEntity(int index) { return entities.get(index); }

    public Entity getQuarterbackEntity() { return entities.get(11); } // Set this to 12 eventually

    public int totalEntities() { return entities.size(); }

    public void setBallCarrier(Entity BC) { this.ballCarrier = BC; }

    public Entity getBallCarrier() { return ballCarrier; }

    public void initReset() {
        Entity.timeFumble = -1f;
        Football.fumbleMovements = new Vector2f();
        gameManager.setBallPosX(this);
        gameManager.setBallPosY(this);
        entities.clear();
        Entity.canPlay = false;
        Entity.incompletePass = false;
        Entity.playStart = false;
        Football.gotWideReceiverPos = true;
        WideReceiver.totalReceivers = 0;
        DefensiveBack.guardedReceivers = 0;
        GameManager.offenseBall = true;
        enterEntities();
        GameManager.printDownInfo();
    }

    public void enterEntities() {
        Four_Verticals O_play = new Four_Verticals(GameManager.ballPosX,GameManager.ballPosY);
        //RB_Dive O_play = new RB_Dive(GameManager.ballPosX,GameManager.ballPosY);
        //Zone D_play = new Zone(GameManager.ballPosX,GameManager.ballPosY);
        Line_Blitz D_play = new Line_Blitz(GameManager.ballPosX,GameManager.ballPosY);
        entities.addAll(D_play.getEntities());
        entities.addAll(O_play.getEntities());
        setBallCarrier(this.getFootballEntity());
    }



    public int getScale() { return scale; }
    public Matrix4f getWorld() { return world;}



}