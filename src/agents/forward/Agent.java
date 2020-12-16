package agents.forward;
import java.util.ArrayList;
import java.util.Arrays;
import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

public class Agent implements MarioAgent{
    private ArrayList<boolean[]> choices; // The list of stored actions

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        choices = new ArrayList<>();

        //Add a "Run Right" action, where RIGHT and SPEED in the boolean array are set
        boolean[] action = new boolean[5];
        Arrays.fill(action, Boolean.FALSE); //Fill with 'false' initially
        action[MarioActions.RIGHT.getValue()] = true;
        action[MarioActions.SPEED.getValue()] = true;
        choices.add(action); // This adds the array {false, true, false, true, false} to choices
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        return choices.get(0); // get the only choice available
    }

    @Override
    public String getAgentName() {
        return "ForwardAgent";
    }

}

