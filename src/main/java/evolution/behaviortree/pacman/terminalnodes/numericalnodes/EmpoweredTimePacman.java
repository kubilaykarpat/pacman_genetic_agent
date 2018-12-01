package evolution.behaviortree.pacman.terminalnodes.numericalnodes;

import evolution.pacmanevaluation.ExtendedGamePacman;

public class EmpoweredTimePacman extends NumberTerminalNodePacman {

	@Override
	public void disp(int depth) {
		String str = "<EmpoweredTimePacman></EmpoweredTimePacman>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

	@Override
	public double getData(ExtendedGamePacman extendedgame) {
		return extendedgame.edibleTime();
	}

	public EmpoweredTimePacman copy(){
		return new EmpoweredTimePacman();
	}
}
