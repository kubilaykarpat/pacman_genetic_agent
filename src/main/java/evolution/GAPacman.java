package evolution;

import evolution.behaviortree.BehaviorTreePacman;
import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.Random;

public class GAPacman extends PacmanController {

    private BehaviorTreePacman behavior;
    Random rnd = new Random();
    private ExtendedGame extendedgame;

    public GAPacman(BehaviorTreePacman individual) {
        this.behavior = individual;
        this.extendedgame = new ExtendedGame(40);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        // Housekeeping - throw out old info
        this.extendedgame.updateGame(game);

        return this.behavior.eval(extendedgame);


    }


}
