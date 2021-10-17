package world;
import collision.AABB;
import graphics.Camera;
import graphics.Shader;
import graphics.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class World {
    private final int view = 24; // controls screen render distance (6 squares)
    private byte[] tiles;
    private AABB[] bounding_boxes;
    private int width;
    private int height;
    private int scale;

    private Matrix4f world;

    public World(String stadium) {

        try {
             BufferedImage tile_sheet = ImageIO.read(new File("./res/stadiums/" + stadium + "_tiles.png"));
             //BufferedImage entity_sheet = ImageIO.read(new File("./stadiums/" + stadium + "_entities.png"));

             width = tile_sheet.getWidth();
             height = tile_sheet.getHeight();
             scale = 16;

            int[] colorTileSheet = tile_sheet.getRGB(0,0,width,height, null, 0, tile_sheet.getWidth());

            this.world = new Matrix4f().setTranslation(new Vector3f(0));
            this.world.scale(scale);// Tiles are 32x32, since scale = 16 * 2 due to renderTile using 2*length

            tiles = new byte[width*height];
            bounding_boxes = new AABB[width * height];

            for (int y = 0; y < height; y++) { // x and y represent id of tile
                for (int x = 0; x < width; x++) {
                    int red = (colorTileSheet[x + y * width] >> 16) & 0xFF; // represents red on tile of grid of map

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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public World() {
        width = 64; // width of world
        height = 64; // height of world
        scale = 16;

        tiles = new byte[width*height];
        bounding_boxes = new AABB[width*height];

        world = new Matrix4f().setTranslation(new Vector3f(0));
        world.scale(scale);// Tiles are 32x32, since scale = 16 * 2 due to renderTile using 2*length
    }

    public void render(TileRenderer render, Shader shader, Camera camera, Window window) {
       /* for (int count = 0; count < height; count++) {
            for (int counter = 0; counter < width; counter++) {
                render.renderTile(tiles[count + counter * width], counter, -count, shader, world, camera);
            }
        } */

        int posX = ((int) camera.getPosition().x + (window.getWidth()/2)) / (scale * 2);
        int posY = ((int) camera.getPosition().y - (window.getHeight()/2)) / (scale * 2);

        for (int count = 0; count < view; count++) {
            for (int counter = 0; counter < view; counter++) {
                Tile t = getTile(count-posX, counter+posY);
                if (t != null) {
                    render.renderTile(t, count-posX, -counter-posY, shader, world, camera);
                }
            }
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

    public int getScale() { return scale; }
}