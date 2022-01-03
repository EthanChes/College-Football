package plays;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class T_Form_FB_Dive {
    private List<Entity> entities;
    private RunningBack runner;

    public T_Form_FB_Dive(float ballX, float ballY) {
        entities = new ArrayList<Entity>();
        entities.add(new Quarterback(new Transform(ballX - 3, ballY)));
        entities.get(0).setRoute(1);

        // Add Offensive Line
        entities.add(new OffensiveLineman(new Transform(ballX,ballY + 2)));
        entities.get(1).setRoute(1);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY)));
        entities.get(2).setRoute(2);
        entities.get(2).center = true;
        entities.add(new OffensiveLineman(new Transform(ballX,ballY + 4)));
        entities.get(3).setRoute(1);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY - 2)));
        entities.get(4).setRoute(2);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY - 4)));
        entities.get(5).setRoute(2);
        entities.add(new OffensiveLineman(new Transform(ballX, ballY - 6)));
        entities.get(6).setRoute(2);
        entities.add(new OffensiveLineman(new Transform(ballX, ballY + 6)));
        entities.get(7).setRoute(1);

        entities.add(new RunningBack(new Transform(ballX - 6, ballY + 3)));
        entities.get(8).setRoute(2);
        entities.add(new RunningBack(new Transform(ballX - 6, ballY - 3)));
        entities.get(9).setRoute(3);

        runner = new RunningBack(new Transform(ballX - 6, ballY));
        runner.setRunnerRoute(0);
        entities.add(runner);
        entities.get(10).setRoute(1);

        WideReceiver.totalReceivers += 5;

        // Add football
        entities.add(new Football(new Transform(ballX-3,ballY,.5f)));
    }

    public List getEntities() { return entities; }
}
