package plays;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class Line_Blitz {
    private List<Entity> entities;

    public Line_Blitz(float ballX, float ballY) {
        entities = new ArrayList<Entity>();

        for (int i = 0; i < 11; i++) {
            entities.add(new DefensiveLineman(new Transform(ballX + 2, ballY - 3.5f + i)));
        }
        for (int i = 11; i < 11; i++) {
            entities.add(new DefensiveLineman(new Transform(1000,0)));
        } // For Testing Purposes

    }

    public List getEntities() { return entities; }
}
