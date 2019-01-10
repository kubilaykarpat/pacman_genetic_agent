package evolution.behaviortree.functionnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.StaticBooleanTerminalNodePacman;


public class AndNodePacman extends BiVariateBooleanOperatorPacman {

	public AndNodePacman()
	{
		this(BooleanTerminalNodePacman.createRandom(), BooleanTerminalNodePacman.createRandom());
	}
	
	@Override
	public AndNodePacman copy(){
		return new AndNodePacman(this.firstCond.copy(), this.secondCond.copy());
	}
	
	public AndNodePacman(BehaviorNodePacman firstCond, BehaviorNodePacman secondCond){
		this.firstCond = firstCond;
		this.secondCond = secondCond;
	}
	
	@Override
    public BehaviorNodePacman eval(ExtendedGame extended_game) {
		if (((BooleanTerminalNodePacman)firstCond.eval( extended_game)).getData(extended_game) && 
				((BooleanTerminalNodePacman)secondCond.eval(extended_game)).getData(extended_game))
			return StaticBooleanTerminalNodePacman.trueNode;
		return StaticBooleanTerminalNodePacman.falseNode;
	}

	@Override
	public void disp(int depth) {
		String str = "<AndNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		firstCond.disp(depth+1);
		secondCond.disp(depth+1);
		
		str = "</AndNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

}
