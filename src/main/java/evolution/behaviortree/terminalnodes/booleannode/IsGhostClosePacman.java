package evolution.behaviortree.terminalnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import pacman.game.Constants;

import java.util.List;

public class IsGhostClosePacman extends BooleanTerminalNodePacman {

    private final static int DEFAULT_PROXIMITY = 15;
    private int proximity;

    public IsGhostClosePacman() {
        this(DEFAULT_PROXIMITY);
    }

    public IsGhostClosePacman(int proximity) {
        this.proximity = proximity;
    }

    @Override
    public boolean getData(ExtendedGame extendedgame) {
        return isClose(extendedgame);
    }

    @Override
    public void disp(int depth) {
        String str = "<IsGhostClosePacman>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);

        Integer i = this.proximity;
        padded = String.format("%1$" + (4 * (depth + 1) + i.toString().length()) + "s", i.toString());
        System.out.println(padded);

        str = "</IsGhostClosePacman>";
        padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

    private boolean isClose(ExtendedGame extendedgame) {

        int pacmanNodeIndex = extendedgame.game.getPacmanCurrentNodeIndex();

        for (Constants.GHOST ghosttype : Constants.GHOST.values()) {
            if (extendedgame.getGhostPosition(ghosttype) != -1 &&
                    pacmanNodeIndex != -1 &&
                    extendedgame.getGhostPosition(ghosttype) != extendedgame.game.getCurrentMaze().lairNodeIndex) {
                if (extendedgame.game.getShortestPathDistance(extendedgame.getGhostPosition(ghosttype), pacmanNodeIndex) < this.proximity) {
                    return true;
                }
            }
        }

        return false;

    }

    @Override
    public IsGhostClosePacman copy() {
        return new IsGhostClosePacman(this.proximity);
    }

    @Override
    public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
        list.add(this);
        return list;
    }

    @Override
    public void mutate() {
        this.proximity = RANDOM.nextInt(25);
    }
}
