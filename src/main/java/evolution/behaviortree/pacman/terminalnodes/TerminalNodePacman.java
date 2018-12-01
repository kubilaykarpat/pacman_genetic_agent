package evolution.behaviortree.pacman.terminalnodes;

import java.util.List;

import evolution.behaviortree.pacman.BehaviorNodePacman;
import evolution.pacmanevaluation.ExtendedGamePacman;

public abstract class TerminalNodePacman extends BehaviorNodePacman {

	@Override
	public BehaviorNodePacman eval(ExtendedGamePacman game) {
		return this;
	}
	
	@Override
	public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		return list;
	}
	
	public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
		return list;
	}
	
	public void mutate(){
		System.out.println("Node tries to mutate but can't");
		this.disp(0);
	}
}
