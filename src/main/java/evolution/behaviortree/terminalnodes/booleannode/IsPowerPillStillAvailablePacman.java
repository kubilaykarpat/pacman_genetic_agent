package evolution.behaviortree.terminalnodes.booleannode;

import evolution.ExtendedGame;

public class IsPowerPillStillAvailablePacman extends BooleanTerminalNodePacman {


    @Override
    public boolean getData(ExtendedGame extendedgame) {
        return isAvailable(extendedgame);
    }

    @Override
    public void disp(int depth) {
        String str = "<IsPowerPillStillAvailable></IsPowerPillStillAvailable>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);
    }

    private boolean isAvailable(ExtendedGame extendedgame) {
        return extendedgame.isPowerPillStillAvailable();
    }

    @Override
    public IsPowerPillStillAvailablePacman copy() {
        return new IsPowerPillStillAvailablePacman();
    }
}
