package plays;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class Kickoff {
    private List<Entity> entities;

        public Kickoff(float ballX, float ballY) {
            entities = new ArrayList<Entity>();
            entities.add(new Kicker(new Transform(ballX - 21, ballY + 1)));
            entities.get(0).setRoute(0);
            if (GameManager.userOffense)
                entities.get(0).forceUserControl = true;

            // Add Chasers
            entities.add(new RunningBack(new Transform(ballX - 18,ballY + 2)));
            entities.get(1).uniqueEvents = true;
            entities.get(1).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18,ballY + 4)));
            entities.get(2).uniqueEvents = true;
            entities.get(2).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18,ballY + 6)));
            entities.get(3).uniqueEvents = true;
            entities.get(3).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18,ballY + 8)));
            entities.get(4).uniqueEvents = true;
            entities.get(4).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18,ballY + 10)));
            entities.get(5).uniqueEvents = true;
            entities.get(5).setRoute(0);

            // Add WRs
            entities.add(new RunningBack(new Transform(ballX - 18, ballY - 2)));
            entities.get(6).uniqueEvents = true;
            entities.get(6).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18, ballY - 4)));
            entities.get(7).uniqueEvents = true;
            entities.get(7).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18, ballY - 6)));
            entities.get(8).uniqueEvents = true;
            entities.get(8).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18,ballY - 8)));
            entities.get(9).uniqueEvents = true;
            entities.get(9).setRoute(0);
            entities.add(new RunningBack(new Transform(ballX - 18,ballY - 10)));
            entities.get(10).uniqueEvents = true;
            entities.get(10).setRoute(0);

            // Add football
            entities.add(new Football(new Transform(ballX - 18,ballY,.5f)));
        }

        public List getEntities() { return entities; }
}

