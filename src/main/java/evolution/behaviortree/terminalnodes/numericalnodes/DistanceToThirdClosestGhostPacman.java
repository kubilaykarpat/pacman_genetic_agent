package evolution.behaviortree.terminalnodes.numericalnodes;

import evolution.ExtendedGame;

import java.util.Arrays;

public class DistanceToThirdClosestGhostPacman extends NumberTerminalNodePacman {

	public double getData(ExtendedGame extendedgame) {
		
		double[] distances = extendedgame.getEstimatedGhostDistances();
		Arrays.sort(distances);
		if (distances[2] != -1)
		{
			return distances[2];
		}
		else 
			return 100;
	}
	
	@Override
	public void disp(int depth) {
		String str = "<DistanceToThirdClosestGhostPacman></DistanceToThirdClosestGhostPacman>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	@Override
	public DistanceToThirdClosestGhostPacman copy(){
		return new DistanceToThirdClosestGhostPacman();
	}

	
}
