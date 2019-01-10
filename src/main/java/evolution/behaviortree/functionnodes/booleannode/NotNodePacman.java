package evolution.behaviortree.functionnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.StaticBooleanTerminalNodePacman;

public class NotNodePacman extends UniVariateBooleanOperatorNodePacman {


    public NotNodePacman(BehaviorNodePacman negate) {
        this.condition = negate;
        this.target = TargetPacman.Boolean;
    }

    public NotNodePacman() {
        this(BooleanTerminalNodePacman.createRandom());
    }

    @Override
    public NotNodePacman copy() {
        return new NotNodePacman(this.condition.copy());
    }

    @Override
    public BehaviorNodePacman eval(ExtendedGame extendedgame) {
        if (((BooleanTerminalNodePacman) condition.eval(extendedgame)).getData(extendedgame))
            return StaticBooleanTerminalNodePacman.falseNode;
        return StaticBooleanTerminalNodePacman.trueNode;
    }

    @Override
    public void disp(int depth) {
        String str = "<NotNode>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);

        condition.disp(depth + 1);

        str = "</NotNode>";
        padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

}
