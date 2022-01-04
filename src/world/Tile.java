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
    public static final Tile alabamatopleft = new Tile("fieldinfo/alabamatopleft"); // id 22
    public static final Tile alabamatop = new Tile("fieldinfo/alabamatop"); // id 23
    public static final Tile alabamatopright = new Tile("fieldinfo/alabamatopright"); // id 24
    public static final Tile alabamamiddleleft = new Tile("fieldinfo/alabamamiddleleft"); // id 25
    public static final Tile alabamamiddle = new Tile("fieldinfo/alabamamiddle"); // id 26
    public static final Tile alabamamiddleright = new Tile("fieldinfo/alabamamiddleright"); // id 27
    public static final Tile alabamabottomleft = new Tile("fieldinfo/alabamabottomleft"); // id 28
    public static final Tile alabamabottommiddle = new Tile("fieldinfo/alabamabottommiddle"); // id 29
    public static final Tile alabamabottomright = new Tile("fieldinfo/alabamabottomright"); // id 30
    public static final Tile boyfan = new Tile("fieldinfo/boyfan"); // id 31
    public static final Tile girlfan = new Tile("fieldinfo/girlfan"); // id 32
    public static final Tile black = new Tile("fieldinfo/black"); // id 33
    public static final Tile railing = new Tile("fieldinfo/railing"); // id 34
    public static final Tile stands = new Tile("fieldinfo/stands"); // id 35
    public static final Tile red = new Tile("fieldinfo/red"); // id 36
    public static final Tile white = new Tile("fieldinfo/white"); // id 37
    public static final Tile baylortopleft = new Tile("fieldinfo/baylortopleft"); // id 38
    public static final Tile baylortopmiddle = new Tile("fieldinfo/baylortopmiddle"); // id 39
    public static final Tile baylortopright = new Tile("fieldinfo/baylortopright"); // id 40
    public static final Tile baylormiddleleft = new Tile("fieldinfo/baylormiddleleft"); // id 41
    public static final Tile baylormiddlemiddle = new Tile("fieldinfo/baylormiddlemiddle"); // id 42
    public static final Tile baylormiddleright = new Tile("fieldinfo/baylormiddleright"); // id 43
    public static final Tile baylorbottomleft = new Tile("fieldinfo/baylorbottomleft"); // id 44
    public static final Tile baylorbottommiddle = new Tile("fieldinfo/baylorbottommiddle"); // id 45
    public static final Tile baylorbottomright = new Tile("fieldinfo/baylorbottomright"); // id 46
    public static final Tile clemsontopleft = new Tile("fieldinfo/clemsontopleft"); // id 47
    public static final Tile clemsontopmiddle = new Tile("fieldinfo/clemsontopmiddle"); // id 48
    public static final Tile clemsontopright = new Tile("fieldinfo/clemsontopright"); // id 49
    public static final Tile clemsonmiddleleft = new Tile("fieldinfo/clemsonmiddleleft"); // id 50
    public static final Tile clemsonmiddlemiddle = new Tile("fieldinfo/clemsonmiddlemiddle"); // id 51
    public static final Tile clemsonmiddleright = new Tile("fieldinfo/clemsontopleft"); // id 52
    public static final Tile clemsonbottomleft = new Tile("fieldinfo/clemsonbottomleft"); // id 53
    public static final Tile clemsonbottommiddle = new Tile("fieldinfo/clemsonbottommiddle"); // id 54
    public static final Tile clemsonbottomright = new Tile("fieldinfo/clemsonbottomright"); // id 55
    public static final Tile georgiatopleft = new Tile("fieldinfo/georgiatopleft"); // id 56
    public static final Tile georgiatopmiddle = new Tile("fieldinfo/georgiatopmiddle"); // id 57
    public static final Tile georgiatopright = new Tile("fieldinfo/georgiatopright"); // id 58
    public static final Tile georgiamiddleleft = new Tile("fieldinfo/georgiamiddleleft"); // id 59
    public static final Tile georgiamiddlemiddle = new Tile("fieldinfo/georgiamiddlemiddle"); // id 60
    public static final Tile georgiamiddleright = new Tile("fieldinfo/georgiamiddleright"); // id 61
    public static final Tile georgiabottomleft = new Tile("fieldinfo/georgiabottomleft"); // id 62
    public static final Tile georgiabottommiddle = new Tile("fieldinfo/georgiabottommiddle"); // id 63
    public static final Tile georgiabottomright = new Tile("fieldinfo/georgiabottomright"); // id 64
    public static final Tile psutopleft = new Tile("fieldinfo/psutopleft"); // id 65
    public static final Tile psutopmiddle = new Tile("fieldinfo/psutopmiddle"); // id 66
    public static final Tile psutopright = new Tile("fieldinfo/psutopright"); // id 67
    public static final Tile psumiddleleft = new Tile("fieldinfo/psumiddleleft"); // id 68
    public static final Tile psumiddlemiddle = new Tile("fieldinfo/psumiddlemiddle"); // id 69
    public static final Tile psumiddleright = new Tile("fieldinfo/psumiddleright"); // id 70
    public static final Tile psubottomleft = new Tile("fieldinfo/psubottomleft"); // id 71
    public static final Tile psubottommiddle = new Tile("fieldinfo/psubottommiddle"); // id 72
    public static final Tile psubottomright = new Tile("fieldinfo/psubottomright"); // id 73
    public static final Tile osutopleft = new Tile("fieldinfo/osutopleft"); // id 74
    public static final Tile osutopmiddle = new Tile("fieldinfo/osutopmiddle"); // id 75
    public static final Tile osutopright = new Tile("fieldinfo/osutopright"); // id 76
    public static final Tile osumiddleleft = new Tile("fieldinfo/osumiddleleft"); // id 77
    public static final Tile osumiddlemiddle = new Tile("fieldinfo/osumiddlemiddle"); // id 78
    public static final Tile osumiddleright = new Tile("fieldinfo/osumiddleright"); // id 79
    public static final Tile osubottomleft = new Tile("fieldinfo/osubottomleft"); // id 80
    public static final Tile osubottommiddle = new Tile("fieldinfo/osubottommiddle"); // id 81
    public static final Tile osubottomright = new Tile("fieldinfo/osubottomright"); // id 82
    public static final Tile oregontopleft = new Tile("fieldinfo/oregontopleft"); // id 83
    public static final Tile oregontopmiddle = new Tile("fieldinfo/oregontopmiddle"); // id 84
    public static final Tile oregontopright = new Tile("fieldinfo/oregontopright"); // id 85
    public static final Tile oregonmiddleleft = new Tile("fieldinfo/oregonmiddleleft"); // id 86
    public static final Tile oregonmiddlemiddle = new Tile("fieldinfo/oregonmiddlemiddle"); // id 87
    public static final Tile oregonmiddleright = new Tile("fieldinfo/oregonmiddleright"); // id 88
    public static final Tile oregonbottomleft = new Tile("fieldinfo/oregonbottomleft"); // id 89
    public static final Tile oregonbottommiddle = new Tile("fieldinfo/oregonbottommiddle"); // id 90
    public static final Tile oregonbottomright = new Tile("fieldinfo/oregonbottomright"); // id 91
    public static final Tile outopleft = new Tile("fieldinfo/outopleft"); // id 92
    public static final Tile outopmiddle = new Tile("fieldinfo/outopmiddle"); // id 93
    public static final Tile outopright = new Tile("fieldinfo/outopright"); // id 94
    public static final Tile oumiddleleft = new Tile("fieldinfo/oumiddleleft"); // id 95
    public static final Tile oumiddlemiddle = new Tile("fieldinfo/oumiddlemiddle"); // id 96
    public static final Tile oumiddleright = new Tile("fieldinfo/oumiddleright"); // id 97
    public static final Tile oubottomleft = new Tile("fieldinfo/oubottomleft"); // id 98
    public static final Tile oubottommiddle = new Tile("fieldinfo/oubottommiddle"); // id 99
    public static final Tile oubottomright = new Tile("fieldinfo/oubottomright"); // id 100

    private byte id; // Represents constant textures above
    private boolean solid; // represents if tile should collide with entities.
    private String texture; // Texture location

    public Tile(String texture) {
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
