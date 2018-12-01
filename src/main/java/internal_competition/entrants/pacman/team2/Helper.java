package internal_competition.entrants.pacman.team2;

import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Helper class to abstract the game interface hopefully making the core logic an easier read. At least if you have an
 * IDE that can follow functions...
 */
class Helper {
    /**
     * Game instance used for all requests to the API
     */
    private Game mGame;

    /**
     * Update the internal state of this Helper instance. Needs to be called at the beginning of each update cycle
     * for all other functions to make sense. Probably most calls to this are not required, because the game instance
     * does not change in between getMove calls, still better to be sure.
     */
    void update(Game game) {
        mGame = game;
    }

    /**
     * Create a new Helper instance, initialising it with the game provided.
     */
    Helper(Game game) {
        update(game);
    }

    /**
     * Returns id of the node pacman currently is at.
     */
    int getCurrentNode() {
        return mGame.getPacmanCurrentNodeIndex();
    }

    /**
     * Returns true if pacman is currently at a junction.
     */
    boolean isAtJunction() {
        return mGame.isJunction(getCurrentNode());
    }

    /**
     * Returns the last move executed by pacman
     */
    Constants.MOVE getLastMove() {
        return mGame.getPacmanLastMoveMade();
    }

    /**
     * Returns the opposite of the last move, which would move pacman back.
     */
    Constants.MOVE getBackMove() {
        return getLastMove().opposite();
    }

    /**
     * Returns an array of currently available moves.
     */
    Constants.MOVE[] getAvailableMoves() {
        return mGame.getPossibleMoves(getCurrentNode());
    }

    /**
     * Return the number of lives pacman has available
     */
    int getPacmanLiveCount() {
        return mGame.getPacmanNumberOfLivesRemaining();
    }

    /**
     * Returns true if the given ghost is visible
     */
    private boolean isGhostVisible(Constants.GHOST ghost) {
        return mGame.getGhostCurrentNodeIndex(ghost) != -1;
    }

    /**
     * Returns true if a ghost with the specified visibility is edible
     */
    boolean isAnyGhostVisible() {
        for (Constants.GHOST g : Constants.GHOST.values())
            if (isGhostVisible(g) && isInedible(g))
                return true;
        return false;
    }

    /**
     * Returns the move you would need to do to move to this ghost
     *
     * @param ghost the ghost you want to get to
     * @return the move you would need to do to move to this ghost
     */
    private Constants.MOVE getMoveToGhost(Constants.GHOST ghost) {
        return getMoveToNode(mGame.getGhostCurrentNodeIndex(ghost));
    }

    /**
     * Returns the move you would need to do to move to this node
     *
     * @param nodeIndex index of the node you want to go to
     * @return the move you would need to do to move to this node
     */
    Constants.MOVE getMoveToNode(int nodeIndex) {
        return mGame.getNextMoveTowardsTarget(
                getCurrentNode(),
                nodeIndex,
                Constants.DM.MANHATTAN);
    }

    /**
     * Returns the distance of pacman to the ghost
     *
     * @param ghost ghost we want the distance to
     * @return manhattan-distance
     */
    private double distanceToGhost(Constants.GHOST ghost) {
        return mGame.getDistance(getCurrentNode(), mGame.getGhostCurrentNodeIndex(ghost), Constants.DM.MANHATTAN);
    }

    /**
     * Returns true if ghost is inedible
     */
    private boolean isInedible(Constants.GHOST ghost) {
        return !mGame.isGhostEdible(ghost);
    }

    /**
     * Returns true if move would bring us closer to any visible and inedible ghost within a distance of 5
     */
    boolean isTowardsGhost(Constants.MOVE move) {
        for (Constants.GHOST g : Constants.GHOST.values())
            if (isGhostVisible(g) && isInedible(g) && distanceToGhost(g) < 5 && getMoveToGhost(g) == move)
                return true;
        return false;
    }

    /**
     * This function takes in a mapping from move to score and returns a random choice from the moves with the best
     * score to avoid being stuck in loops
     *
     * @param scoreMap mapping from move to score
     * @return random best move
     */
    Constants.MOVE getRandomBestMoveFromScoreMap(EnumMap<Constants.MOVE, Double> scoreMap) {
        // get the maximum available value
        Double max = Collections.max(scoreMap.values());

        // get a list of moves that produce this value
        List<Constants.MOVE> bestMoveList = scoreMap.entrySet()
                .stream().filter(entry -> Objects.equals(entry.getValue(), max)).map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // return one of them
        return bestMoveList.get(new Random().nextInt(bestMoveList.size()));
    }

    /**
     * Returns true if no pill is currently visible and available
     *
     * @return true if no pill is currently visible and available, false otherwise
     */
    boolean isNoPillVisible() {
        return getVisiblePillCount() == 0;
    }

    /**
     * Get the number of pills currently visible and available
     *
     * @return the number of pills currently visible and available
     */
    private int getVisiblePillCount() {
        int visible = 0;
        for (int i = 0; i < mGame.getNumberOfPills(); i++)
            if (mGame.isPillStillAvailable(i) != null && mGame.isPillStillAvailable(i))
                visible++;
        return visible;
    }

    /**
     * Returns true only if the last move can be done again.
     */
    boolean isLastMoveRepeatable() {
        return Arrays.asList(getAvailableMoves()).contains(getLastMove());
    }

    /**
     * Find a random move that is not moving in the opposite direction of the last move.
     */
    Constants.MOVE getAnythingButBack() {
        List<Constants.MOVE> notBack = new ArrayList<>();
        for (Constants.MOVE move : getAvailableMoves())
            if (move != getBackMove())
                notBack.add(move);
        return notBack.get(new Random().nextInt(notBack.size()));
    }
}
