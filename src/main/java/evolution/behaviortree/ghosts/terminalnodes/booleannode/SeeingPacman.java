package evolution.behaviortree.ghosts.terminalnodes.booleannode;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class SeeingPacman extends BooleanTerminalNode {
	

	@Override
	public void disp(int depth) {
		String str = "<SeeingPacman></SeeingPacman>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	@Override
	public boolean getData(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype){
		if (extendedgame.game.getPacmanCurrentNodeIndex() != -1)
			return true;
		else
			return false;
	}
	
	@Override
	public SeeingPacman copy(){
		return new SeeingPacman();
	}

}
