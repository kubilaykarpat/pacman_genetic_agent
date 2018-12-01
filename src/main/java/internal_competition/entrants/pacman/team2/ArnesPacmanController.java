package internal_competition.entrants.pacman.team2;


import pacman.Executor;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.util.Stats;

import java.util.Objects;

/**
 * Pacman Controller by Arne, created by arne on 12.06.17.
 */
public class ArnesPacmanController extends PacmanController {
    /**
     * The instance of behaviour used to choose a move
     */
    private final Behaviour behaviour = new Behaviour();

    public static void main(String[] args) {
        Executor po = new Executor(true, true, true);
        int TICK_THRESHOLD = 50;

        // run a single game with gui
        if (args.length == 0 || Objects.equals(args[0], "single")) {
            po.setDaemon(false);
            po.runGame(new ArnesPacmanController(), new POCommGhosts(TICK_THRESHOLD), true, 30);
        }
        // run n simulations
        else {
            int TRIALS = Integer.parseInt(args[0]);
            Stats[] stats = po.runExperiment(new ArnesPacmanController(), new POCommGhosts(TICK_THRESHOLD), TRIALS, "ATSAI");

            System.out.println("ATSAI:\t" + stats[0].getAverage());
        }
    }

    /**
     * Forward the getMove call to our behaviour instance
     *
     * @param game    current game instance
     * @param timeDue how much ms we have left for calculating the next move
     * @return the move we want ms pacman to execute
     */
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return behaviour.getMove(game);
    }
}
