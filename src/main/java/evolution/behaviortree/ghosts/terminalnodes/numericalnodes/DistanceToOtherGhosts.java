package evolution.behaviortree.ghosts.terminalnodes.numericalnodes;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class DistanceToOtherGhosts extends NumberTerminalNode {

	public double getData(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype){
		int oGhostIndex;
		double shortestDistance = Double.MAX_VALUE;
		for (Constants.GHOST oGhost : Constants.GHOST.values()){
			oGhostIndex= extendedgame.getGhostPosition(oGhost);
			int myself = extendedgame.game.getGhostCurrentNodeIndex(ghosttype);
			if (extendedgame.game.getShortestPathDistance(oGhostIndex, myself) < shortestDistance)
			{
				shortestDistance = extendedgame.game.getShortestPathDistance(oGhostIndex, myself);
			}
		}
		return shortestDistance;
	}
	
	@Override
	public void disp(int depth) {
		String str = "<DistanceToOtherGhosts></DistanceToOtherGhosts>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	@Override
	public DistanceToOtherGhosts copy(){
		return new DistanceToOtherGhosts();
	}

	
}
