package evolution.behaviortree.pacman.terminalnodes.numericalnodes;

import evolution.behaviortree.pacman.BehaviorNodePacman;
import evolution.pacmanevaluation.ExtendedGamePacman;

public class RandomNumberTerminalNodePacman extends NumberTerminalNodePacman {

	
	public RandomNumberTerminalNodePacman(){
	}

	public double getData(ExtendedGamePacman game){
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
