package assets;

import graphics.Model;

public class Degrees {
    private static Model model;

        public static void initAsset(float degrees) {
            // Forms Tile Structure
            float[] vertices = new float[]{
                    // VERTICES ARE TWO RIGHT TRIANGLES, Locations of the Corners of the Square formed by the Right Triangles are found below.
                    -1f, 1f, 0, // TOP LEFT 0 (x,y,z) is the formatting
                    1f, 1f, 0, // TOP RIGHT 1
                    1f, -1f, 0, // BOTTOM RIGHT 2
                    -1f, -1f, 0, // BOTTOM LEFT 3
            };


            double sin = Math.sin(degrees*Math.PI/180);
            double cos = Math.cos(degrees*Math.PI/180);

            float[] texture = new float[]{
                    // Coordinates of Texture location on Model/Vertex Structure post rotation
                    (float) (-.5f * cos + .5f * sin) + .5f, (float) (-.5f * sin + .5f * cos) + .5f, // 0 Degrees 0,0
                    (float) (.5f * cos + .5f * sin) + .5f, (float) (.5f * sin + .5f * cos) + .5f, // 0 Degrees 1, 0
                    (float) (.5f * cos - .5f * sin) + .5f, (float) (.5f * sin - .5f * cos) + .5f, // 0 Degrees 1,1
                    (float) (-.5f * cos - .5f * sin) + .5f, (float) (-.5f * sin - .5f * cos) + .5f, // 0 Degrees 0,1
            };

            int[] indices = new int[]{ // Indices of the triangles. See Texture and Vertices comments. Each index corresponds to a vertex defined above.
                    0, 1, 2,
                    2, 3, 0,
            };

            model = new Model(vertices,texture,indices);
        }

        public static void deleteAsset() {
            model = null;
        }

        public static Model getModel() { return model; }

}
