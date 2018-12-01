package evolution.behaviortree.ghosts.terminalnodes.numericalnodes;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants.GHOST;

public class EdibleTime extends NumberTerminalNode {

	@Override
	public void disp(int depth) {
		String str = "<EdibleTime></EdibleTime>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

	@Override
	public double getData(ExtendedGameGhosts extendedgame, GHOST ghosttype) {
		return extendedgame.game.getGhostEdibleTime(ghosttype);
	}

	public EdibleTime copy(){
		return new EdibleTime();
	}
}
