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
    public static final Tile sideLines = new Tile("fieldinfo/sidelines"); // id 5
    public static final Tile numberOne = new Tile("fieldinfo/numberOne"); // id 6
    public static final Tile numberTwo = new Tile("fieldinfo/numberTwo"); // id 7
    public static final Tile numberThree = new Tile("fieldinfo/numberThree"); // id 8
    public static final Tile numberFour = new Tile("fieldinfo/numberFour"); // id 9
    public static final Tile numberFive = new Tile("fieldinfo/numberFive"); // id 10 Unused
    public static final Tile numberZero = new Tile("fieldinfo/numberZero"); // id 11
    public static final Tile midline = new Tile("fieldinfo/midline"); // id 12
    public static final Tile reversedOne = new Tile("fieldinfo/reversedone"); // id 13
    public static final Tile reversedTwo = new Tile("fieldinfo/reversedtwo"); // id 14
    public static final Tile reversedThree = new Tile("fieldinfo/reversedthree"); // id 15
    public static final Tile reversedFour = new Tile("fieldinfo/reversedfour"); // id 16
    public static final Tile reversedFive = new Tile("fieldinfo/reversedfive"); // id 17 Unused
    public static final Tile midBottomFive = new Tile("fieldinfo/midbottomfive"); // id 18
    public static final Tile midtopZero = new Tile("fieldinfo/midtopzero"); // id 19
    public static final Tile midbottomZero = new Tile("fieldinfo/midbottomzero"); // id 20
    public static final Tile midTopFive = new Tile("fieldinfo/midtopfive"); // id 21
    public static final Tile alabamaLogo = new Tile("fieldinfo/alabamaLogo"); // id 22

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
