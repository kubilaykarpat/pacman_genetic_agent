package evolution.behaviortree.terminalnodes.booleannode;

import evolution.ExtendedGame;
import pacman.game.Constants;

public class SeeingGhosts extends BooleanTerminalNodePacman {
	

	@Override
	public void disp(int depth) {
		String str = "<SeeingGhosts></SeeingGhosts>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	@Override
	public boolean getData(ExtendedGame extendedgame) {
		for (Constants.GHOST ghosttype : Constants.GHOST.values())
		{
			if (extendedgame.game.getGhostCurrentNodeIndex(ghosttype) != -1){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public SeeingGhosts copy(){
		return new SeeingGhosts();
	}

}
