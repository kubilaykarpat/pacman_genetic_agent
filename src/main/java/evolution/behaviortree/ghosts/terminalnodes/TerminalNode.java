package evolution.behaviortree.ghosts.terminalnodes;

import java.util.List;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public abstract class TerminalNode extends BehaviorNode {

	@Override
	public BehaviorNode eval(ExtendedGameGhosts game, Constants.GHOST ghosttype) {
		return this;
	}
	
	@Override
	public List<BehaviorNode> getNodes(List<BehaviorNode> list) {
		list.add(this);
		return list;
	}
	
	public List<BehaviorNode> getMutableNodes(List<BehaviorNode> list) {
		return list;
	}
	
	public void mutate(){
		System.out.println("Node tries to mutate but can't");
		this.disp(0);
	}
}
