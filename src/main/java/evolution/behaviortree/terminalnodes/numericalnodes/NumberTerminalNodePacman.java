package evolution.behaviortree.terminalnodes.numericalnodes;

import evolution.ExtendedGame;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import evolution.behaviortree.terminalnodes.TerminalNodePacman;
import util.Utils;

public abstract class NumberTerminalNodePacman extends TerminalNodePacman {


    public static NumberTerminalNodePacman createRandom() {
        NumberTerminalNodePacman node;
        switch (Utils.RANDOM.nextInt(8)) {
            case 0:
                node = new DoubleTerminalNodePacman();
                break;

            case 1:
                node = new RandomNumberTerminalNodePacman();
                break;

            case 2:
                node = new EmpoweredTimePacman();
                break;

            case 3:
                node = new DistanceToClosestGhostPacman();
                break;

            case 4:
                node = new DistanceToSecondClosestGhostPacman();
                break;

            case 5:
                node = new DistanceToThirdClosestGhostPacman();
                break;

            case 6:
                node = new DistanceToFourthClosestGhostPacman();
                break;

            case 7:
                node = new IntegerTerminalNodePacman();
                break;

            default:
                System.out.println("NumberTerminalNode unknown case");
                return null;

        }

        return node;
    }

    public abstract double getData(ExtendedGame extendedgame);

    @Override
    public FunctionNodePacman.TargetPacman getTarget() {
        return FunctionNodePacman.TargetPacman.Numerical;
    }

}
