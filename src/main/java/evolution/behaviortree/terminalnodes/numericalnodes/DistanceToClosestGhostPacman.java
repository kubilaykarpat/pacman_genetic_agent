package evolution.behaviortree.terminalnodes.numericalnodes;

import evolution.ExtendedGame;

import java.util.Arrays;

public class DistanceToClosestGhostPacman extends NumberTerminalNodePacman {


    public double getData(ExtendedGame extendedgame) {

        double[] distances = extendedgame.getEstimatedGhostDistances();
        Arrays.sort(distances);
        if (distances[0] != -1) {
            return distances[0];
        } else
            return 100;
    }

    @Override
    public void disp(int depth) {
        String str = "<DistanceToClosestGhostPacman></DistanceToClosestGhostPacman>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

    @Override
    public DistanceToClosestGhostPacman copy() {
        return new DistanceToClosestGhostPacman();
    }


}
