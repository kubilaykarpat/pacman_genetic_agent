package evolution.behaviortree.terminalnodes.numericalnodes;

import evolution.ExtendedGame;

import java.util.Arrays;

public class DistanceToSecondClosestGhostPacman extends NumberTerminalNodePacman {

    public double getData(ExtendedGame extendedgame) {

        double[] distances = extendedgame.getEstimatedGhostDistances();
        Arrays.sort(distances);
        if (distances[1] != -1) {
            return distances[1];
        } else
            return 100;
    }


    @Override
    public void disp(int depth) {
        String str = "<DistanceToSecondClosestGhostPacman></DistanceToSecondClosestGhostPacman>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

    @Override
    public DistanceToSecondClosestGhostPacman copy() {
        return new DistanceToSecondClosestGhostPacman();
    }


}
