package world;
public class Tile {
    public static Tile tiles[] = new Tile[255];
    public static byte not = 0;

    public static final Tile normalGrass = new Tile("fieldinfo/grass");
    public static final Tile redEndzone = new Tile("fieldinfo/RedEndzone").setSolid();
    public static final Tile darkGrass = new Tile("fieldinfo/darkgrass");

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
