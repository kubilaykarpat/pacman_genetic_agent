package evolution.behaviortree.terminalnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import util.Utils;

import java.util.List;

public class AmICloseToPower extends BooleanTerminalNodePacman {

    private static final int DEFAULT_PILL_PROXIMITY = 15;
    private int proximity;

    public AmICloseToPower() {
        this(DEFAULT_PILL_PROXIMITY);
    }

    public AmICloseToPower(int proximity) {
        this.proximity = proximity;
    }

    @Override
    public boolean getData(ExtendedGame extendedgame) {
        return closeToPower(extendedgame);
    }

    @Override
    public void disp(int depth) {
        String str = "<AmICloseToPower>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);

        Integer i = this.proximity;
        padded = String.format("%1$" + (4 * (depth + 1) + i.toString().length()) + "s", i.toString());
        System.out.println(padded);

        str = "</AmICloseToPower>";
        padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

    private boolean closeToPower(ExtendedGame extendedgame) {
        int[] powerPills = extendedgame.game.getPowerPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean powerPillStillAvailable = extendedgame.isPowerPillStillAvailable(i);
            int pacmanNodeIndex = extendedgame.game.getPacmanCurrentNodeIndex();
            if (pacmanNodeIndex == -1) {
                return false;
            }
            if (powerPillStillAvailable && extendedgame.game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < this.proximity) {
                return true;
            }
        }

        return false;
    }

    @Override
    public AmICloseToPower copy() {
        return new AmICloseToPower();
    }

    @Override
    public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        return list;
    }

    @Override
    public void mutate() {
        this.proximity = Utils.RANDOM.nextInt(25);
    }
}
