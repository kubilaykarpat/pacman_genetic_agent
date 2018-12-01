package internal_competition.entrants.pacman.team2;

import pacman.controllers.MASController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

import java.util.*;

/**
 * A Monte Carlo tree search implementation
 */
class MonteCarlo {
    /**
     * Our game instance
     */
    private final Game game;

    /**
     * Instance of ghosts used for simulating ghost behaviour
     */
    private final MASController ghosts;

    /**
     * An array of how often a single move should be explored. If using the first simulation/lookahead pair does not
     * yield distinct scores, the next level will be explored.
     */
    private static final int SIMULATION_RUNS = 10;

    /**
     * An array of how often far a move should be explored in each simulation. If using the first simulation/lookahead pair does not
     * yield distinct scores, the next level will be explored.
     */
    private static final int LOOKAHEAD = 4;

    /**
     * If not null, this contains the move to the next available pill. If null, there is no requirement to move to some
     * pill.
     */
    private Constants.MOVE moveToTargetedPill = null;

    /**
     * Creates a new Monte Carlo tree search instance. To get the best move according to a monte-carlo search, call {@link #getBestMove()}.
     *
     * @param game              the game instance we want to work on
     * @param targetedNodeIndex index of a node that still has a pill
     */
    MonteCarlo(Game game, int targetedNodeIndex) {
        this.ghosts = new POCommGhosts(50);
        GameInfo info = game.getPopulatedGameInfo();
        info.fixGhosts((ghost -> new Ghost(ghost, game.getCurrentMaze().lairNodeIndex, -1, -1, Constants.MOVE.NEUTRAL)));

        // required to be able to forward the game
        this.game = game.getGameFromInfo(info);

        // store how to get where we want to
        if (targetedNodeIndex >= 0)
            moveToTargetedPill = new Helper(game).getMoveToNode(targetedNodeIndex);
    }

    /**
     * Get the best currently available move
     */
    Constants.MOVE getBestMove() {
        Helper hlp = new Helper(game);
        EnumMap<Constants.MOVE, Double> scoreMap = new EnumMap<>(Constants.MOVE.class);

        // run simulations for each move
        for (Constants.MOVE move : hlp.getAvailableMoves())
            scoreMap.put(move, simulateLookAhead(move, hlp));

        return hlp.getRandomBestMoveFromScoreMap(scoreMap);
    }

    /**
     * Advance to the next junction
     *
     * @param game   the game that should be advanced
     * @param helper the helper instance for game
     */
    private void proceedToNextJunction(Game game, Helper helper) {
        while (!helper.isAtJunction()) {
            helper.update(game);
            game.advanceGame(Behaviour.getNonMonteCarloMove(helper), ghosts.getMove(game, 40));
        }
    }

    /**
     * Simulate a lookahead into the future of this game
     *
     * @param startMove the move to simulate
     * @param hlp       helper instance to use
     * @return average score of simulations
     */
    private double simulateLookAhead(Constants.MOVE startMove, Helper hlp) {
        double[] simulationScores = new double[SIMULATION_RUNS];

        // for # of simulations to do
        for (int i = 0; i < SIMULATION_RUNS; i++) {
            // copy game, forward once with the move we want to simulate
            Game copy = game.copy();
            copy.advanceGame(startMove, ghosts.getMove(copy, 40));
            hlp.update(copy);

            boolean seenGhost = false;

            // execute the actual lookahead
            for (int j = 0; j < LOOKAHEAD; j++) {
                proceedToNextJunction(copy, hlp);
                if (hlp.isAnyGhostVisible())
                    seenGhost = true;
                copy.advanceGame(hlp.getAnythingButBack(), ghosts.getMove(copy, 40));
            }

            // calculate base score
            simulationScores[i] = copy.getScore() + hlp.getPacmanLiveCount() * 500;

            // punishment for seeing ghosts
            if (seenGhost)
                simulationScores[i] -= 5;

            // bonus for moving to the targeted pill (if available)
            if (moveToTargetedPill != null && startMove == moveToTargetedPill)
                simulationScores[i] += 50;
        }

        // return average of simulation scores
        return Arrays.stream(simulationScores).sum() / SIMULATION_RUNS;
    }
}
