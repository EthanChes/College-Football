package world;
import java.util.HashMap;

import graphics.Camera;
import graphics.Model;
import graphics.Shader;
import graphics.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TileRenderer {
    private HashMap<String, Texture> tile_textures;
    private Model model;

    public TileRenderer() {
        tile_textures = new HashMap<String, Texture>();

        // Forms Tile Structure
        float[] vertices = new float[]{
                // VERTICES ARE TWO RIGHT TRIANGLES, Locations of the Corners of the Square formed by the Right Triangles are found below.
                -.5f, .5f, 0, // TOP LEFT 0 (x,y,z) is the formatting
                .5f, .5f, 0, // TOP RIGHT 1
                .5f, -.5f, 0, // BOTTOM RIGHT 2
                -.5f, -.5f, 0, // BOTTOM LEFT 3
        };

        float[] texture = new float[]{
                // Coordinates of Texture location on Model/Vertex Structure. (0,0 BL, 1,1 TR).
                0, 0, // 0, (x,y), this the location on the model or square produced in the Model Class, that the texture's vertices will occupy.
                1, 0, // 1
                1, 1, // 2
                0, 1, // 3
        };

        int[] indices = new int[]{ // Indices of the triangles. See Texture and Vertices comments. Each index corresponds to a vertex defined above.
                0, 1, 2,
                2, 3, 0,
        };

        model = new Model(vertices,texture,indices);

        for (int count = 0; count < Tile.tiles.length ; count++) {
            if (Tile.tiles[count] != null) {
                if (!tile_textures.containsKey(Tile.tiles[count].getTexture())) {
                    String texture_name = Tile.tiles[count].getTexture();
                    tile_textures.put(texture_name, new Texture( texture_name+ ".png"));
                }
            }

        }
    }
    public void renderTile(byte id, int x, int y, Shader shader, Matrix4f world, Camera camera) { // puts tiles in correct spot
        shader.bind();
        if (tile_textures.containsKey (Tile.tiles[id].getTexture())) { // Tests if tile exists and if yes, then texture will bind
            tile_textures.get(Tile.tiles[id].getTexture()).bind(0);
        }

        Matrix4f tile_pos = new Matrix4f().translate (new Vector3f(x, y, 0)); // position is posx or posy * scale of Tile (2) in Matrix
        Matrix4f target = new Matrix4f();

        camera.getProjection().mul(world,target); // multiplies projection by world and stores into target.
        target.mul(tile_pos); // multiply target by tile position, all of this stuff puts tile in correct position

        Matrix4f samplerMatrix = new Matrix4f();

        shader.setUniform("sampler",samplerMatrix);
        shader.setUniform("projection", target);

        model.render(); // Render a square in the tile position that the texture is put on.
    }
}
