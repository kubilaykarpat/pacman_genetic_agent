package evolution.behaviortree.terminalnodes.booleannode;

import evolution.ExtendedGame;
import pacman.game.Constants;

public class IsEmpoweredPacman extends BooleanTerminalNodePacman {

    @Override
    public void disp(int depth) {
        String str = "<IsEmpoweredPacman></IsEmpoweredPacman>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

    @Override
    public boolean getData(ExtendedGame extendedgame) {
        for (Constants.GHOST ghosttype : Constants.GHOST.values()) {
            if (extendedgame.edible.get(ghosttype)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IsEmpoweredPacman copy() {
        return new IsEmpoweredPacman();
    }

}
