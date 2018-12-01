package evolution.behaviortree.ghosts.terminalnodes.booleannode;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants.GHOST;

public class IsEdible extends BooleanTerminalNode {

	@Override
	public void disp(int depth) {
		String str = "<IsEdible></IsEdible>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

	@Override
	public boolean getData(ExtendedGameGhosts extended_game, GHOST ghosttype) {
		return  extended_game.game.isGhostEdible(ghosttype);
	}
	
	@Override
	public IsEdible copy(){
		return new IsEdible();
	}

}
