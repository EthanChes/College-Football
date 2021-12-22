package plays;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class T_Form_HB_Stretch {
    private List<Entity> entities;
    private RunningBack runner;

    public T_Form_HB_Stretch(float ballX, float ballY) {
        entities = new ArrayList<Entity>();
        entities.add(new Quarterback(new Transform(ballX - 3, ballY)));
        entities.get(0).setRoute(1);

        entities.add(new RunningBack(new Transform(ballX - 6, ballY)));
        entities.get(1).setRoute(4);

        // Add Offensive Line
        entities.add(new OffensiveLineman(new Transform(ballX,ballY + 2)));
        entities.get(2).setRoute(1);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY)));
        entities.get(3).setRoute(1);
        entities.get(3).center = true;
        entities.add(new OffensiveLineman(new Transform(ballX,ballY + 4)));
        entities.get(4).setRoute(1);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY - 2)));
        entities.get(5).setRoute(1);
        entities.add(new OffensiveLineman(new Transform(ballX,ballY - 4)));
        entities.get(6).setRoute(1);
        entities.add(new OffensiveLineman(new Transform(ballX, ballY - 6)));
        entities.get(7).setRoute(1);
        entities.add(new OffensiveLineman(new Transform(ballX, ballY + 6)));
        entities.get(8).setRoute(1);

        entities.add(new RunningBack(new Transform(ballX - 6, ballY + 3)));
        entities.get(9).setRoute(2);
        runner = new RunningBack(new Transform(ballX - 6, ballY - 3));
        runner.setRunnerRoute(2);
        entities.add(runner);
        entities.get(10).setRoute(1);

        // Add football
        entities.add(new Football(new Transform(ballX-3,ballY,.5f)));
    }

    public List getEntities() { return entities; }
}
