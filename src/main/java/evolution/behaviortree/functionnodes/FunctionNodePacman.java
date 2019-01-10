package evolution.behaviortree.functionnodes;

import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.functionnodes.booleannode.AndNodePacman;
import evolution.behaviortree.functionnodes.booleannode.NotNodePacman;
import evolution.behaviortree.functionnodes.booleannode.OrNodePacman;
import evolution.behaviortree.functionnodes.booleannode.XorNodePacman;

public abstract class FunctionNodePacman extends BehaviorNodePacman {

	public enum TargetPacman {Action, Numerical, Boolean	};
	protected TargetPacman target;
	
	public static FunctionNodePacman createRandomBooleanTarget(){
		switch (BehaviorNodePacman.RANDOM.nextInt(6)){
			case 0: return new AndNodePacman();
			case 1: return new NotNodePacman();
			case 2: return new OrNodePacman();
			case 3: return new XorNodePacman();
			case 4: return new IfElseNodePacman(TargetPacman.Boolean);
			case 5: return new IfLessThanElseNodePacman(TargetPacman.Boolean);
			default:
                System.out.println("you screwed up mate, createRandomBooleanTarget");
				return null;	
		}
	}
	
	public static FunctionNodePacman createRandomNumericalTarget(){
		switch (BehaviorNodePacman.RANDOM.nextInt(2)){
			case 0: return new IfElseNodePacman(TargetPacman.Numerical);
			case 1: return new IfLessThanElseNodePacman(TargetPacman.Numerical);
			default:
                System.out.println("you screwed up mate, createRandomNumericalTarget");
				return null;	
		}
	}
	
	public static FunctionNodePacman createRandomActionTarget(){
		if (BehaviorNodePacman.RANDOM.nextBoolean()){
			return new IfElseNodePacman(TargetPacman.Action);
		}
		else {
			return new IfLessThanElseNodePacman(TargetPacman.Action);
		}
	}

	public TargetPacman getTarget() {
		return this.target;
	}

}
