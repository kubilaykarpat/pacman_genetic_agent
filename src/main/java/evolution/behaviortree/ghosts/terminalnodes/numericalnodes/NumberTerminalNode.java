package evolution.behaviortree.ghosts.terminalnodes.numericalnodes;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.behaviortree.ghosts.terminalnodes.TerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants.GHOST;

public abstract class NumberTerminalNode extends TerminalNode{

	
	public static NumberTerminalNode createRandom(){
		NumberTerminalNode node;
		switch (BehaviorNode.RANDOM.nextInt(6)){	
			case 0: node = new DoubleTerminalNode();
					break;
					
			case 1: node = new RandomNumberTerminalNode();
					break;
					
			case 2: node = new EdibleTime();
					break;
					
			case 3: node = new EstimatedDistanceOptimistic();
					break;
					
			case 4: node = new EstimatedDistancePessimistic();
					break;
			
			case 5: node = new IntegerTerminalNode();
					break;
			
			case 6: node = new DistanceToOtherGhosts();
					break;
					
			default: 
					System.out.println("NumberTerminalNode unknown case");
					return null;
					
		}
		
		return node;
	}

	public abstract double getData(ExtendedGameGhosts extendedgame, GHOST ghosttype);

}
