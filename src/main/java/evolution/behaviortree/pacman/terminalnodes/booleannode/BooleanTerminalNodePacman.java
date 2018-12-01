package evolution.behaviortree.pacman.terminalnodes.booleannode;

import evolution.behaviortree.pacman.BehaviorNodePacman;
import evolution.behaviortree.pacman.terminalnodes.TerminalNodePacman;
import evolution.pacmanevaluation.ExtendedGamePacman;

public abstract class BooleanTerminalNodePacman extends TerminalNodePacman {

	public static BooleanTerminalNodePacman createRandom(){
		
		switch(BehaviorNodePacman.RANDOM.nextInt(6)){
			case 0: return  new StaticBooleanTerminalNodePacman();
			case 1: return  new IsEmpoweredPacman();
			case 2: return  new SeeingGhosts();
			case 3: return  new AmICloseToPower();
			case 4: return  new IsGhostClosePacman();
			case 5: return  new IsPowerPillStillAvailablePacman();
			default:
				System.out.println("BooleanTerminalNode, unknown case");
				return null;
		}
		
	}
	
	public abstract boolean getData(ExtendedGamePacman extended_game);
	
}
