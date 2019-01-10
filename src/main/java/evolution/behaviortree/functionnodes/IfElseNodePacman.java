package evolution.behaviortree.functionnodes;


import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.terminalnodes.actionnodes.ActionTerminalNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.terminalnodes.numericalnodes.NumberTerminalNodePacman;
import util.Utils;

import java.util.Arrays;
import java.util.List;

public class IfElseNodePacman extends FunctionNodePacman {

    private BehaviorNodePacman cond;
    private BehaviorNodePacman ifcase;
    private BehaviorNodePacman elsecase;

    public IfElseNodePacman(TargetPacman target) {
        this.cond = BooleanTerminalNodePacman.createRandom();
        this.target = target;

        switch (target) {
            case Action:
                this.ifcase = ActionTerminalNodePacman.createRandom();
                this.elsecase = ActionTerminalNodePacman.createRandom();
                break;
            case Boolean:
                this.ifcase = BooleanTerminalNodePacman.createRandom();
                this.elsecase = BooleanTerminalNodePacman.createRandom();
                break;
            case Numerical:
                this.ifcase = NumberTerminalNodePacman.createRandom();
                this.elsecase = NumberTerminalNodePacman.createRandom();
                break;
            default:
                System.out.println("IfElseNode enum unknown");
                this.ifcase = null;
                this.elsecase = null;
                break;
        }
    }


    public IfElseNodePacman(BehaviorNodePacman cond, BehaviorNodePacman ifcase, BehaviorNodePacman elsecase, TargetPacman target) {
        this.cond = cond;
        this.ifcase = ifcase;
        this.elsecase = elsecase;
        this.target = target;
    }

    @Override
    public IfElseNodePacman copy() {
        return new IfElseNodePacman(this.cond.copy(), this.ifcase.copy(), this.elsecase.copy(), this.target);
    }


    public BehaviorNodePacman eval(ExtendedGame extendedgame) {
        if (((BooleanTerminalNodePacman) this.cond.eval(extendedgame)).getData(extendedgame))
            return this.ifcase.eval(extendedgame);
        //else
        return this.elsecase.eval(extendedgame);
    }

//	public static BehaviorNode createRandom(StaticBooleanTerminalNode cond, ActionTerminalNode ifcase, ActionTerminalNode elsecase, Target target){
//		return new IfElseNode(cond, ifcase, elsecase, target);
//	}

    @Override
    public void disp(int depth) {
        String str = "<IfElseNode>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
        padded = String.format("%1$" + (4 * (depth + 1) + target.toString().length()) + "s", target.toString());
        System.out.println(padded);

        cond.disp(depth + 1);
        ifcase.disp(depth + 1);
        elsecase.disp(depth + 1);

        str = "</IfElseNode>";
        padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

    @Override
    public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        this.cond.getNodes(list);
        this.ifcase.getNodes(list);
        this.elsecase.getNodes(list);
        return list;
    }

    @Override
    public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        this.cond.getMutableNodes(list);
        this.ifcase.getMutableNodes(list);
        this.elsecase.getMutableNodes(list);
        return list;
    }


    private void mutate_action() {
        switch (Utils.RANDOM.nextInt(4)) {
            case 0:
                this.ifcase = ActionTerminalNodePacman.createRandom();
                break;
            case 1:
                this.elsecase = ActionTerminalNodePacman.createRandom();
                break;
            case 2:
                this.ifcase = createRandomActionTarget();
                break;
            case 3:
                this.elsecase = createRandomActionTarget();
                break;
        }
    }

    private void mutate_boolean() {
        switch (Utils.RANDOM.nextInt(4)) {
            case 0:
                this.ifcase = BooleanTerminalNodePacman.createRandom();
                break;
            case 1:
                this.elsecase = BooleanTerminalNodePacman.createRandom();
                break;
            case 2:
                this.ifcase = createRandomBooleanTarget();
                break;
            case 3:
                this.elsecase = createRandomBooleanTarget();
                break;
        }
    }

    private void mutate_numerical() {
        switch (Utils.RANDOM.nextInt(4)) {
            case 0:
                this.ifcase = NumberTerminalNodePacman.createRandom();
                break;
            case 1:
                this.elsecase = NumberTerminalNodePacman.createRandom();
                break;
            case 2:
                this.ifcase = createRandomNumericalTarget();
                break;
            case 3:
                this.elsecase = createRandomNumericalTarget();
                break;
        }
    }

    @Override
    public List<BehaviorNodePacman> getDirectChildren() {
        return Arrays.asList(this.cond, this.ifcase, this.elsecase);
    }

    @Override
    public void mutate() {
        if (Utils.RANDOM.nextBoolean()) {
            //mutate conclusion
            switch (this.target) {
                case Action:
                    mutate_action();
                    break;
                case Boolean:
                    mutate_boolean();
                    break;
                case Numerical:
                    mutate_numerical();
                    break;
            }
        } else {
            // mutate condition
            if (Utils.RANDOM.nextBoolean()) {
                this.cond = BooleanTerminalNodePacman.createRandom();
            } else {
                this.cond = createRandomBooleanTarget();
            }
        }
    }

    @Override
    public void overrideChildren(int whichChildren, BehaviorNodePacman newSubTree) {
        switch (whichChildren) {
            case 0:
                this.cond = newSubTree;
                break;
            case 1:
                this.ifcase = newSubTree;
                break;
            case 2:
                this.elsecase = newSubTree;
                break;
            default:
                throw new IllegalArgumentException(String.format("%s has no %sth children.", this.getClass().getName(), whichChildren));
        }
    }
}
