package plays;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class Line_Blitz {
    private List<Entity> entities;

    public Line_Blitz(float ballX, float ballY) {
        entities = new ArrayList<Entity>();

        for (int i = 0; i < 4 ; i++) {
            entities.add(new DefensiveLineman(new Transform(ballX + 2, ballY - 3.5f + 2f*i)));
        }
        entities.get(0).setRoute(2);
        entities.get(3).setRoute(1);
        for (int i = 4; i < 11; i++) {
            entities.add(new DefensiveLineman(new Transform(100000,0)));
        } // For Testing Purposes

    }

    public List getEntities() { return entities; }
}
