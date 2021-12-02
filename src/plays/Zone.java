package plays;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private List<Entity> entities;

    public Zone(float ballX, float ballY) {
        entities = new ArrayList<Entity>();
        List<DefensiveBack> defensiveBacks = new ArrayList<DefensiveBack>();

        // Add Defensive Line
        for (int i = 0; i < 4 ; i++) {
            entities.add(new DefensiveLineman(new Transform(ballX + 2, ballY - 3.5f + 2f*i)));
        }
        entities.get(0).setRoute(2);
        entities.get(3).setRoute(1);

        // Add Defensive Backs
        defensiveBacks.add(new DefensiveBack(new Transform(ballX + 10, ballY - 5)));
        defensiveBacks.add(new DefensiveBack(new Transform(ballX + 10, ballY - 10)));
        defensiveBacks.add(new DefensiveBack(new Transform(ballX + 10, ballY - 15)));
        defensiveBacks.add(new DefensiveBack(new Transform(ballX + 10, ballY - 20)));
        defensiveBacks.add(new DefensiveBack(new Transform(ballX + 10, ballY - 25)));
        defensiveBacks.add(new DefensiveBack(new Transform(ballX + 10, ballY - 30)));
        defensiveBacks.add(new DefensiveBack(new Transform(ballX + 10, ballY - 35)));

        entities.addAll(defensiveBacks);

        entities.get(4).setDefenderID(0,defensiveBacks.get(0));
        entities.get(5).setDefenderID(2,defensiveBacks.get(1));
        entities.get(6).setDefenderID(3,defensiveBacks.get(2));
        entities.get(7).setDefenderID(4,defensiveBacks.get(3));
        entities.get(8).setDefenderID(5,defensiveBacks.get(4));
        entities.get(9).setDefenderID(6,defensiveBacks.get(5));
        entities.get(10).setDefenderID(7,defensiveBacks.get(6));

        entities.get(4).setRoute(-1);
        entities.get(5).setRoute(-1);
        entities.get(6).setRoute(1);
        entities.get(7).setRoute(2);
        entities.get(8).setRoute(3);
        entities.get(9).setRoute(4);
        entities.get(10).setRoute(5);


    }

    public List getEntities() { return entities; }
}
