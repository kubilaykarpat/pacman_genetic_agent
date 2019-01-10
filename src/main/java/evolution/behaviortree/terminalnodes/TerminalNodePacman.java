package evolution.behaviortree.terminalnodes;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;

import java.util.Collections;
import java.util.List;

public abstract class TerminalNodePacman extends BehaviorNodePacman {

    @Override
    public BehaviorNodePacman eval(ExtendedGame game) {
        return this;
    }

    @Override
    public List<BehaviorNodePacman> getDirectChildren() {
        return Collections.emptyList();
    }

    @Override
    public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        return list;
    }

    public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
        return list;
    }

    public void mutate() {
        System.out.println("Node tries to mutate but can't");
        this.disp(0);
    }
}
