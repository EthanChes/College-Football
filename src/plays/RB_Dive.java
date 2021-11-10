package plays;

import entity.*;
import world.World;

import java.util.ArrayList;
import java.util.List;

public class RB_Dive {
    private List<Entity> entities;

    public RB_Dive(float ballX, float ballY) {
        entities = new ArrayList<Entity>();
        entities.add(new Quarterback(new Transform(ballX - 3, ballY)));
        entities.get(0).setRoute(1);

        entities.add(new RunningBack(new Transform(ballX - 3, ballY + 5)));
        entities.get(1).setRoute(1);

        // Add WRs
        entities.add(new WideReceiver(new Transform(ballX, ballY+5)));
        entities.get(2).setRoute(0);
        entities.add(new WideReceiver(new Transform(ballX, ballY+10)));
        entities.get(3).setRoute(0);
        entities.add(new WideReceiver(new Transform(ballX, ballY - 10)));
        entities.get(4).setRoute(0);
        entities.add(new WideReceiver(new Transform(ballX,ballY-12)));
        entities.get(5).setRoute(0);

        // Symbols for Pass
        for (int count = 0; count < WideReceiver.totalReceivers; count++) {
            entities.add(new ReceiverSymbol(new Transform(200, -250, .75f))); // Adds Symbol for Receiver
        }

        // Add football
        entities.add(new Football(new Transform(ballX-3,ballY,.5f)));
    }

    public List getEntities() { return entities; }
}
