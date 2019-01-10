package evolution.behaviortree.functionnodes;

import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.functionnodes.booleannode.AndNodePacman;
import evolution.behaviortree.functionnodes.booleannode.NotNodePacman;
import evolution.behaviortree.functionnodes.booleannode.OrNodePacman;
import evolution.behaviortree.functionnodes.booleannode.XorNodePacman;
import util.Utils;

public abstract class FunctionNodePacman extends BehaviorNodePacman {

    protected TargetPacman target;

    public static FunctionNodePacman createRandomBooleanTarget() {
        switch (Utils.RANDOM.nextInt(6)) {
            case 0:
                return new AndNodePacman();
            case 1:
                return new NotNodePacman();
            case 2:
                return new OrNodePacman();
            case 3:
                return new XorNodePacman();
            case 4:
                return new IfElseNodePacman(TargetPacman.Boolean);
            case 5:
                return new IfLessThanElseNodePacman(TargetPacman.Boolean);
            default:
                System.out.println("you screwed up mate, createRandomBooleanTarget");
                return null;
        }
    }

    public static FunctionNodePacman createRandomNumericalTarget() {
        switch (Utils.RANDOM.nextInt(2)) {
            case 0:
                return new IfElseNodePacman(TargetPacman.Numerical);
            case 1:
                return new IfLessThanElseNodePacman(TargetPacman.Numerical);
            default:
                System.out.println("you screwed up mate, createRandomNumericalTarget");
                return null;
        }
    }

    public static FunctionNodePacman createRandomActionTarget() {
        if (Utils.RANDOM.nextBoolean()) {
            return new IfElseNodePacman(TargetPacman.Action);
        } else {
            return new IfLessThanElseNodePacman(TargetPacman.Action);
        }
    }

    public abstract void overrideChildren(int whichChildren, BehaviorNodePacman newSubTree);

    public TargetPacman getTarget() {
        return this.target;
    }

    public enum TargetPacman {Action, Numerical, Boolean}

}
