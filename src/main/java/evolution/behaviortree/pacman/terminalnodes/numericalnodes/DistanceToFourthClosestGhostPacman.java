package evolution.behaviortree.pacman.terminalnodes.numericalnodes;

import java.util.Arrays;

import evolution.pacmanevaluation.ExtendedGamePacman;

public class DistanceToFourthClosestGhostPacman extends NumberTerminalNodePacman {

	public double getData(ExtendedGamePacman extendedgame){
		
		double[] distances = extendedgame.getEstimatedGhostDistances();
		Arrays.sort(distances);
		if (distances[3] != -1)
		{
			return distances[3];
		}
		else 
			return 100;
	}
	
	
	@Override
	public void disp(int depth) {
		String str = "<DistanceToFourthClosestGhostPacman></DistanceToFourthClosestGhostPacman>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	@Override
	public DistanceToFourthClosestGhostPacman copy(){
		return new DistanceToFourthClosestGhostPacman();
	}

	
}
