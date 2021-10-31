package world;
public class Tile {
    public static Tile tiles[] = new Tile[255];
    public static byte not = 0;

    // .setSolid() sets a tile solid meaning entities cannot walk over it.

    public static final Tile normalGrass = new Tile("fieldinfo/grass"); // id 0
    public static final Tile redEndzone = new Tile("fieldinfo/RedEndzone"); // id 1
    public static final Tile darkGrass = new Tile("fieldinfo/lightgrass"); // id 2
    public static final Tile grassLeft = new Tile("fieldinfo/grassleft"); // id 3
    public static final Tile grassRight = new Tile("fieldinfo/grassright"); // id 4

    private byte id; // Represents constant textures above
    private boolean solid; // represents if tile should collide with entities.
    private String texture; // Texture location

    Tile(String texture) {
        this.id = not;
        not++;
        this.solid = false;
        this.texture = texture;
        if (tiles[id] != null) {
            throw new IllegalStateException("Tiles at: [" + id + "] is already being used!");
        }
        tiles[id] = this;
    }

    public Tile setSolid() { this.solid = true; return this; }
    public boolean isSolid() { return solid; }

    public byte getId() {
        return id;
    }

    public String getTexture() {
        return texture;
    }
}
