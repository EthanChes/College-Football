package load;
import entity.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class Away {
    List<DefensiveLineman> LDE = new ArrayList<DefensiveLineman>(); // Left Defensive End
    List<DefensiveLineman> RDE = new ArrayList<DefensiveLineman>(); // Right Defensive End
    List<DefensiveLineman> DT = new ArrayList<DefensiveLineman>(); // Defensive Tackle
    List<DefensiveBack> LB = new ArrayList<DefensiveBack>(); // Linebacker
    List<DefensiveBack> CB = new ArrayList<DefensiveBack>(); // Cornerback
    List<DefensiveBack> S = new ArrayList<DefensiveBack>(); // Safety
    List<Kicker> K = new ArrayList<Kicker>(); // Kicker
    List<Kicker> P = new ArrayList<Kicker>(); // Punter
    List<OffensiveLineman> C = new ArrayList<OffensiveLineman>(); // Center
    List<OffensiveLineman> OL = new ArrayList<OffensiveLineman>(); // Offensive Line
    List<RunningBack> RB = new ArrayList<RunningBack>(); // Running Back
    List<WideReceiver> WR = new ArrayList<WideReceiver>(); // Wide Receiver
    List<Quarterback> QB = new ArrayList<Quarterback>(); // Quarterback
    List<RunningBack> FB = new ArrayList<RunningBack>(); // Fullbacks
    List<Entity> SPECIAL = new ArrayList<Entity>(); // Special Teams
    List<Entity> R = new ArrayList<Entity>(); // Returner


    public Away(int teamID) {
        Read.readNextWord("alabama");
    }
}
