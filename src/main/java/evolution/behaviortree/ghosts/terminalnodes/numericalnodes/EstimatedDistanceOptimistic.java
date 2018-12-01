package evolution.behaviortree.ghosts.terminalnodes.numericalnodes;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;
import pacman.game.Constants.DM;

public class EstimatedDistanceOptimistic extends NumberTerminalNode {

	public double getData(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype){
		int pacman = extendedgame.getLastPacmanIndex();
		
		if (pacman != -1)
		{
			int myself = extendedgame.game.getGhostCurrentNodeIndex(ghosttype);
			return extendedgame.game.getDistance(pacman, myself, DM.PATH);
		}
		return 1000;
	}
	
	@Override
	public void disp(int depth) {
		String str = "<EstimatedDistanceOptimistic></EstimatedDistanceOptimistic>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	@Override
	public EstimatedDistanceOptimistic copy(){
		return new EstimatedDistanceOptimistic();
	}

	

}
