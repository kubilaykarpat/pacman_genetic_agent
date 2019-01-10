package evolution.behaviortree.functionnodes.booleannode;

import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;

import java.util.Arrays;
import java.util.List;

public abstract class BiVariateBooleanOperatorPacman extends FunctionNodePacman {
    protected BehaviorNodePacman firstCond;
    protected BehaviorNodePacman secondCond;

    @Override
    public List<BehaviorNodePacman> getDirectChildren() {
        return Arrays.asList(firstCond, secondCond);
    }

    @Override
    public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        this.firstCond.getNodes(list);
        this.secondCond.getNodes(list);
        return list;
    }

    @Override
    public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        this.firstCond.getMutableNodes(list);
        this.secondCond.getMutableNodes(list);
        return list;
    }

    @Override
    public void mutate() {
        switch (BehaviorNodePacman.RANDOM.nextInt(4)) {
            case 0:
                this.firstCond = BooleanTerminalNodePacman.createRandom();
                break;
            case 1:
                this.secondCond = BooleanTerminalNodePacman.createRandom();
                break;
            case 2:
                this.firstCond = FunctionNodePacman.createRandomBooleanTarget();
                break;
            case 3:
                this.secondCond = FunctionNodePacman.createRandomBooleanTarget();
                break;
        }

    }
}
