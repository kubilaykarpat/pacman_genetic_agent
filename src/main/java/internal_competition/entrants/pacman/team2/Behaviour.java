package internal_competition.entrants.pacman.team2;

import pacman.game.Constants;
import pacman.game.Game;

/**
 * This class contains my getMove, because we need it twice: for simulating, and for executing the behaviour. It also
 * tracks for how long no pill was seen to be able to move to a last known pill.
 */
class Behaviour {
    /**
     * If true, a warning will be displayed if the getMove duration exceeds 40ms
     */
    private static final boolean TIME = true;

    /**
     * The number of {@link #getMove(Game)} calls where no pill was visible
     */
    private int zeroPillHistoryLength = 0;

    /**
     * The instance of {@link PillHistory} used to track where a pill is still available.
     */
    private final PillHistory pillHistory = new PillHistory();

    /**
     * Get the next move to be done; decides between executing a monte-carlo search and just using corridor behaviour.
     */
    Constants.MOVE getMove(Game game) {
        // time execution duration
        long then;
        if (TIME) then = System.currentTimeMillis();

        // keep a history of known pills
        pillHistory.update(game);

        // create a new helper to ease dealing with the game api
        Helper helper = new Helper(game);

        // track number of moves where we saw no pill
        zeroPillHistoryLength = helper.isNoPillVisible() ? zeroPillHistoryLength + 1 : 0;

        Constants.MOVE move;
        // choose between junction or corridor behaviour
        if (helper.isAtJunction()) {
            int nextPillIndex = -1;

            // target the nearest pill if we have not seen one for some time
            if (zeroPillHistoryLength > 5)
                nextPillIndex = pillHistory.getClosestAvailablePillIndex(helper.getCurrentNode());

            move = new MonteCarlo(game, nextPillIndex).getBestMove();
            // corridor behaviour
        } else {
            move = getNonMonteCarloMove(helper);
        }

        // time execution duration
        if (TIME) {
            long duration = System.currentTimeMillis() - then;
            if (duration > 40)
                System.out.println("took " + duration);
        }
        return move;
    }

    /**
     * This is the behaviour executed if the bot is not at a junction, e.g. not using monte carlo
     */
    static Constants.MOVE getNonMonteCarloMove(Helper helper) {
        // avoid moving into ghosts
        if (helper.isLastMoveRepeatable()) {
            if (helper.isTowardsGhost(helper.getLastMove())) {
                //System.out.println("back");
                return helper.getBackMove();
            } else {
                //System.out.println("forward");
                return helper.getLastMove();
            }
        } else {
            //System.out.println("anything");
            return helper.getAnythingButBack();
        }
    }
}
