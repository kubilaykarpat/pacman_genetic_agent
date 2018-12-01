package evolution.behaviortree.ghosts.functionnodes;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.AndNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.NotNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.OrNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.XorNode;

public abstract class FunctionNode extends BehaviorNode {

	public enum Target {Action, Numerical, Boolean	};
	protected Target target;
	
	public static FunctionNode createRandomBooleanTarget(){
		switch (BehaviorNode.RANDOM.nextInt(6)){
			case 0: return new AndNode();
			case 1: return new NotNode();
			case 2: return new OrNode();
			case 3: return new XorNode();
			case 4: return new IfElseNode(Target.Boolean);
			case 5: return new IfLessThanElseNode(Target.Boolean);
			default: 
				System.out.println("you fucked up mate, createRandomBooleanTarget");
				return null;	
		}
	}
	
	public static FunctionNode createRandomNumericalTarget(){
		switch (BehaviorNode.RANDOM.nextInt(2)){
			case 0: return new IfElseNode(Target.Numerical);
			case 1: return new IfLessThanElseNode(Target.Numerical);
			default:
				System.out.println("you fucked up mate, createRandomNumericalTarget");
				return null;	
		}
	}
	
	public static FunctionNode createRandomActionTarget(){
		if (BehaviorNode.RANDOM.nextBoolean()){
			return new IfElseNode(Target.Action);
		}
		else {
			return new IfLessThanElseNode(Target.Action);
		}
	}

}
