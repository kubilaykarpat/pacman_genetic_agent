package evolution.behaviortree.ghosts.terminalnodes.booleannode;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants.GHOST;

public class IsPowerPillStillAvailable extends BooleanTerminalNode {


	@Override
	public boolean getData(ExtendedGameGhosts extendedgame, GHOST ghosttype) {
		return isAvailable(extendedgame);
	}

	@Override
	public void disp(int depth) {
		String str = "<IsPowerPillStillAvailable></IsPowerPillStillAvailable>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	private boolean isAvailable(ExtendedGameGhosts extendedgame) {
	    return extendedgame.isPowerPillStillAvailable();
	}

	@Override
	public IsPowerPillStillAvailable copy(){
		return new IsPowerPillStillAvailable();
	}
}
