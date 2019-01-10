package evolution.behaviortree.functionnodes.booleannode;

import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;
import util.Utils;

import java.util.Collections;
import java.util.List;

public abstract class UniVariateBooleanOperatorNodePacman extends FunctionNodePacman {
    protected BehaviorNodePacman condition;

    @Override
    public List<BehaviorNodePacman> getDirectChildren() {
        return Collections.singletonList(condition);
    }

    @Override
    public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        this.condition.getNodes(list);
        return list;
    }

    @Override
    public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        this.condition.getMutableNodes(list);
        return list;
    }

    @Override
    public void mutate() {
        if (Utils.RANDOM.nextBoolean()) {
            this.condition = BooleanTerminalNodePacman.createRandom();
        } else {
            this.condition = FunctionNodePacman.createRandomBooleanTarget();
        }
    }

    @Override
    public void overrideChildren(int whichChildren, BehaviorNodePacman newSubTree) {
        switch (whichChildren) {
            case 0:
                this.condition = newSubTree;
                break;
            default:
                throw new IllegalArgumentException(String.format("%s has no %sth children.", this.getClass().getName(), whichChildren));
        }
    }
}
