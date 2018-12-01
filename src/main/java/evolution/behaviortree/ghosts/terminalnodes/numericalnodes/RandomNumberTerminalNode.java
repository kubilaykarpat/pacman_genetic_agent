package evolution.behaviortree.ghosts.terminalnodes.numericalnodes;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class RandomNumberTerminalNode extends NumberTerminalNode {

	
	public RandomNumberTerminalNode(){
	}

	public double getData(ExtendedGameGhosts game, Constants.GHOST ghosttype){
		return BehaviorNode.RANDOM.nextDouble();
	}


	@Override
	public void disp(int depth) {
		String str = "<RandomNumberTerminalNode></RandomNumberTerminalNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	public RandomNumberTerminalNode copy(){
		return new RandomNumberTerminalNode();
	}
}
