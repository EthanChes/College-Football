package graphics;
import entity.GameManager;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL13.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Texture {
    private int id;
    private int width;
    private int height;
    public Texture(String filename) {
        BufferedImage bi;
        try {
            bi = ImageIO.read(new File("res/" +filename));
            width = bi.getWidth();
            height = bi.getHeight();

            int[] pixels_raw = new int[width*height*4];
            pixels_raw = bi.getRGB(0,0,width,height,null,0,width);

            ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixel = pixels_raw[i*width+j];

                    pixels.put((byte) ((pixel >> 16) & 0xFF)); // Red
                    pixels.put((byte) ((pixel >> 8) & 0xFF)); // Green
                    pixels.put((byte) (pixel & 0xFF)); // Blue
                    pixels.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
                }
            }

            pixels.flip();

            id = glGenTextures();

            glBindTexture(GL_TEXTURE_2D,id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // Substitute GL_NEAREST WITH GL_LINEAR POSSIBlE
            glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,pixels);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Texture(String filename, boolean offense) { // Constructor for player animations
        BufferedImage bi;
        try {
            bi = ImageIO.read(new File("res/" +filename));
            width = bi.getWidth();
            height = bi.getHeight();

            int[] pixels_raw = new int[width*height*4];
            pixels_raw = bi.getRGB(0,0,width,height,null,0,width);

            ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixel = pixels_raw[i*width+j];

                    // Set Proper Jersey, Pant, Helmet Color
                    int pantsOriginal = 0xFFffffff;
                    int defenseJersey = -769226;
                    int offenseJersey = 0xFF3f51b5;
                    if (offense) {
                        if ((GameManager.userHome && GameManager.userOffense) || (! GameManager.userHome && ! GameManager.userOffense)) {
                            switch (GameManager.homeID) {
                                case 0 : if (pixel == offenseJersey) {pixel = 0xFF9E1B32; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 1 : if (pixel == offenseJersey) {pixel = 0xFF1b5e1f; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 2 : if (pixel == offenseJersey) {pixel = 0xFFF56600; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 3 : if (pixel == offenseJersey) {pixel = 0xFFBA0C2F; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 4 : if (pixel == offenseJersey) {pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 5 : if (pixel == offenseJersey) {pixel = 0xFFBB0000; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 6 : if (pixel == offenseJersey) {pixel = 0xFF154733; } else if (pixel == pantsOriginal) { pixel = 0xFF154733; } break;
                                case 7 : if (pixel == offenseJersey) {pixel = 0xFF841617; } else if (pixel == pantsOriginal) { pixel = 0xFFFDF9D8; } break;
                            }
                        } else {
                            switch (GameManager.awayID) {
                                case 0 : if (pixel == offenseJersey) { pixel = 0xFF828A8F; } else if (pixel == pantsOriginal) { pixel = 0xFFffebee; } break;
                                case 1 :  if (pixel == offenseJersey) { pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFF1b5e1f; } break;
                                case 2 : if (pixel == offenseJersey) {pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFFF56600; } break;
                                case 3 : if (pixel == offenseJersey) {pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFFBA0C2F; } break;
                                case 4 : if (pixel == offenseJersey) {pixel = 0xFF3f51b5; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 5 : if (pixel == offenseJersey) {pixel = 0xFF666666; } else if (pixel == pantsOriginal) { pixel = 0xFF666666; } break;
                                case 6 : if (pixel == offenseJersey) {pixel = 0xFF1b5e1f; } else if (pixel == pantsOriginal) { pixel = 0xFF154733; } break;
                                case 7 : if (pixel == offenseJersey) {pixel = 0xFFFDF9D8; } else if (pixel == pantsOriginal) { pixel = 0xFF841617; } break;
                            }
                        }
                    } else {
                        if ((GameManager.userHome && GameManager.userOffense) || (! GameManager.userHome && ! GameManager.userOffense)) {
                            switch (GameManager.awayID) {
                                case 0 : if (pixel == defenseJersey) { pixel = 0xFF828A8F; } else if (pixel == pantsOriginal) { pixel = 0xFFffebee; } break;
                                case 1 : if (pixel == defenseJersey) { pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFF1b5e1f; } break;
                                case 2 : if (pixel == defenseJersey) {pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFFF56600; } break;
                                case 3 : if (pixel == defenseJersey) {pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFFBA0C2F; } break;
                                case 4 : if (pixel == defenseJersey) {pixel = 0xFF3f51b5; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 5 : if (pixel == defenseJersey) {pixel = 0xFF666666; } else if (pixel == pantsOriginal) { pixel = 0xFF666666; } break;
                                case 6 : if (pixel == defenseJersey) {pixel = 0xFF1b5e1f; } else if (pixel == pantsOriginal) { pixel = 0xFF154733; } break;
                                case 7 : if (pixel == defenseJersey) {pixel = 0xFFFDF9D8; } else if (pixel == pantsOriginal) { pixel = 0xFF841617; } break;
                            }
                        } else {
                            switch (GameManager.homeID) {
                                case 0 : if (pixel == defenseJersey) {pixel = 0xFF9E1B32; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 1 : if (pixel == defenseJersey) {pixel = 0xFF1b5e1f; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 2 : if (pixel == defenseJersey) {pixel = 0xFFF56600; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 3 : if (pixel == defenseJersey) {pixel = 0xFFBA0C2F; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 4 : if (pixel == defenseJersey) {pixel = 0xFFffffff; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 5 : if (pixel == defenseJersey) {pixel = 0xFFBB0000; } else if (pixel == pantsOriginal) { pixel = 0xFFffffff; } break;
                                case 6 : if (pixel == defenseJersey) {pixel = 0xFF154733; } else if (pixel == pantsOriginal) { pixel = 0xFF154733; } break;
                                case 7 : if (pixel == defenseJersey) {pixel = 0xFF841617; } else if (pixel == pantsOriginal) { pixel = 0xFFFDF9D8; } break;
                            }
                        }
                    }

                    pixels.put((byte) ((pixel >> 16) & 0xFF)); // Red
                    pixels.put((byte) ((pixel >> 8) & 0xFF)); // Green
                    pixels.put((byte) (pixel & 0xFF)); // Blue
                    pixels.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
                }
            }

            pixels.flip();

            id = glGenTextures();

            glBindTexture(GL_TEXTURE_2D,id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // Substitute GL_NEAREST WITH GL_LINEAR POSSIBlE
            glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,pixels);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        //glDeleteTextures(id); // Code Doesn't Work with this involved
    }

    public void bind(int sampler) {
        if (sampler >= 0 && sampler <= 31) {
            glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, id);
        }
    }
}
