package evolution.behaviortree.terminalnodes.numericalnodes;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;

public class RandomNumberTerminalNodePacman extends NumberTerminalNodePacman {

	
	public RandomNumberTerminalNodePacman(){
	}

	public double getData(ExtendedGame game) {
		return BehaviorNodePacman.RANDOM.nextDouble();
	}


	@Override
	public void disp(int depth) {
		String str = "<RandomNumberTerminalNode></RandomNumberTerminalNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	public RandomNumberTerminalNodePacman copy(){
		return new RandomNumberTerminalNodePacman();
	}
}
