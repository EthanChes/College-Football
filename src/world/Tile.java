package world;
public class Tile {
    public static Tile tiles[] = new Tile[16];
    public static byte not = 0;

    public static final Tile test_tile = new Tile("grass");
    public static final Tile test_tile2 = new Tile("RedEndzone"); // Make sure to increment byte

    private byte id; // Represents constant textures above
    private String texture; // Texture location

    Tile(String texture) {
        this.id = not;
        not++;
        this.texture = texture;
        if (tiles[id] != null) {
            throw new IllegalStateException("Tiles at: [" + id + "] is already being used!");
        }
        tiles[id] = this;
    }

    public byte getId() {
        return id;
    }

    public String getTexture() {
        return texture;
    }
}
