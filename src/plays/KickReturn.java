package plays;

import entity.DefensiveBack;
import entity.DefensiveLineman;
import entity.Entity;
import entity.Transform;

import java.util.ArrayList;
import java.util.List;

public class KickReturn {
    private List<Entity> entities;
    private List<DefensiveBack> defensiveBacks;

        public KickReturn(float ballX, float ballY) {
            entities = new ArrayList<Entity>();
            defensiveBacks = new ArrayList<DefensiveBack>();

            // Add Defensive Backs
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 20, ballY - 6)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(0));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 20, ballY - 3)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(1));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 20, ballY)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(2));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 20, ballY + 3)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(3));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 20, ballY + 6)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(4));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 60, ballY - 6)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(5));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 60, ballY - 3)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(6));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 70, ballY)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(7));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 60, ballY + 3)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(8));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 60, ballY + 6)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(9));
            defensiveBacks.add(new DefensiveBack(new Transform(ballX + 140, ballY)));
            defensiveBacks.get(0).setDefenderID(-1,defensiveBacks.get(10));

            entities.addAll(defensiveBacks);
        }

        public List getEntities() { return entities; }
}

