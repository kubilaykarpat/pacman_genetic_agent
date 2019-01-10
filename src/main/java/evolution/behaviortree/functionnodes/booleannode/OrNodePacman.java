package evolution.behaviortree.functionnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.StaticBooleanTerminalNodePacman;


public class OrNodePacman extends BiVariateBooleanOperatorNodePacman {

    public OrNodePacman() {
        this(BooleanTerminalNodePacman.createRandom(), BooleanTerminalNodePacman.createRandom());
    }

    public OrNodePacman(BehaviorNodePacman firstCond, BehaviorNodePacman secondCond) {
        this.firstCond = firstCond;
        this.secondCond = secondCond;
        this.target = TargetPacman.Boolean;
    }

    @Override
    public OrNodePacman copy() {
        return new OrNodePacman(this.firstCond.copy(), this.secondCond.copy());
    }

    @Override
    public BehaviorNodePacman eval(ExtendedGame extendedgame) {
        if (((BooleanTerminalNodePacman) firstCond.eval(extendedgame)).getData(extendedgame) ||
                ((BooleanTerminalNodePacman) secondCond.eval(extendedgame)).getData(extendedgame))
            return StaticBooleanTerminalNodePacman.trueNode;
        return StaticBooleanTerminalNodePacman.falseNode;
    }

    @Override
    public void disp(int depth) {
        String str = "<OrNode>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);

        firstCond.disp(depth + 1);
        secondCond.disp(depth + 1);

        str = "</OrNode>";
        padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }
}
