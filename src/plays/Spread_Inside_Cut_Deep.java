package plays;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class Spread_Inside_Cut_Deep {
    private List<Entity> entities;

    public Spread_Inside_Cut_Deep(float ballX, float ballY) {
        entities = new ArrayList<Entity>();
        entities.add(new Quarterback(new Transform(ballX - 3, ballY)));
        entities.get(0).setRoute(0);

        // Add Offensive Line
        entities.add(new OffensiveLineman(new Transform(ballX,ballY + 4)));
        entities.get(1).setRoute(-1);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY + 2)));
        entities.get(2).setRoute(0);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY)));
        entities.get(3).setRoute(0);
        entities.get(3).center = true;
        entities.add(new OffensiveLineman(new Transform(ballX,ballY - 2)));
        entities.get(4).setRoute(0);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY - 4)));
        entities.get(5).setRoute(-1);

        // Add WRs
        entities.add(new WideReceiver(new Transform(ballX, ballY+10)));
        entities.get(6).setRoute(0);
        entities.add(new WideReceiver(new Transform(ballX - 3, ballY+8.75f)));
        entities.get(7).setRoute(0);
        entities.add(new WideReceiver(new Transform(ballX, ballY - 10)));
        entities.get(8).setRoute(0);
        entities.add(new WideReceiver(new Transform(ballX - 3,ballY-8)));
        entities.get(9).setRoute(4);
        entities.add(new WideReceiver(new Transform(ballX - 3, ballY + 7.5f)));
        entities.get(10).setRoute(1);

        // Symbols for Pass
        for (int count = 0; count < WideReceiver.totalReceivers; count++) {
            entities.add(new ReceiverSymbol(new Transform(200, -250, .75f))); // Adds Symbol for Receiver
        }

        // Add football
        entities.add(new Football(new Transform(ballX,ballY,.5f)));
    }

    public List getEntities() { return entities; }
}
