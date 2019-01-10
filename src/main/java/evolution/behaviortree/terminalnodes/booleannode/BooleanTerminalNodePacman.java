package evolution.behaviortree.terminalnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import evolution.behaviortree.terminalnodes.TerminalNodePacman;
import util.Utils;

public abstract class BooleanTerminalNodePacman extends TerminalNodePacman {

    public static BooleanTerminalNodePacman createRandom() {

        switch (Utils.RANDOM.nextInt(6)) {
            case 0:
                return new StaticBooleanTerminalNodePacman();
            case 1:
                return new IsEmpoweredPacman();
            case 2:
                return new SeeingGhosts();
            case 3:
                return new AmICloseToPower();
            case 4:
                return new IsGhostClosePacman();
            case 5:
                return new IsPowerPillStillAvailablePacman();
            default:
                System.out.println("BooleanTerminalNode, unknown case");
                return null;
        }

    }

    public abstract boolean getData(ExtendedGame extended_game);

    @Override
    public FunctionNodePacman.TargetPacman getTarget() {
        return FunctionNodePacman.TargetPacman.Boolean;
    }

}
