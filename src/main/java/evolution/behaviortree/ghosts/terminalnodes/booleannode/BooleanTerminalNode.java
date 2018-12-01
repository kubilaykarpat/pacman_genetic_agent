package evolution.behaviortree.ghosts.terminalnodes.booleannode;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.behaviortree.ghosts.terminalnodes.TerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public abstract class BooleanTerminalNode extends TerminalNode {

	public static BooleanTerminalNode createRandom(){
		
		switch(BehaviorNode.RANDOM.nextInt(6)){
			case 0: return  new StaticBooleanTerminalNode();
			case 1: return  new IsEdible();
			case 2: return  new SeeingPacman();
			case 3: return  new IsPacManCloseToPower();
			case 4: return  new IsPacManClose();
			case 5: return  new IsPowerPillStillAvailable();
			default:
				System.out.println("BooleanTerminalNode, unknown case");
				return null;
		}
		
	}
	
	public abstract boolean getData(ExtendedGameGhosts extended_game, Constants.GHOST ghosttype);
	
}
