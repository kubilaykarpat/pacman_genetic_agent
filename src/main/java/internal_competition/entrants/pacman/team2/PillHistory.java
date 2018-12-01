package internal_competition.entrants.pacman.team2;

import pacman.game.Game;

import java.util.*;

/**
 * This class ~~passes butter~~ records where we have seen pills.
 */
class PillHistory {
    /**
     * This enum is used to store the state of a pill
     */
    private enum PillState {
        UNKNOWN, GONE, AVAILABLE
    }

    /**
     * Last game instance
     */
    private Game mGame;

    /**
     * Used to recognize if a new maze has been loaded
     */
    private int mMazeId = -1;

    /**
     * Contains the current state for each pill
     */
    private PillState[] mPillState;

    /**
     * Update internal pill representation
     *
     * @param game up to date game instance
     */
    void update(Game game) {
        // assert the game is partially observable, or we won't get a null for unknown pills
        assert game.isGamePo();

        mGame = game;

        // reset for a new maze
        if (game.getMazeIndex() != mMazeId) {
            mMazeId = game.getMazeIndex();
            resetState();
        }

        for (int i = 0; i < mGame.getNumberOfPills(); i++)
            if (mGame.isPillStillAvailable(i) != null)
                mPillState[i] = mGame.isPillStillAvailable(i) ? PillState.AVAILABLE : PillState.GONE;
    }

    /**
     * Sort a map by values into a LinkedHashMap
     *
     * @param map the map to be sorted
     * @return a sorted LinkedHashMap
     */
    private static LinkedHashMap<Integer, Integer> sortByValue(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort(Comparator.comparing(o -> (o.getValue())));

        LinkedHashMap<Integer, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list)
            result.put(entry.getKey(), entry.getValue());
        return result;
    }

    /**
     * Get the node index for the pill nearest to sourceIndex.
     *
     * @param sourceIndex from where you want to go to a pill
     * @return the node index to the closest available pill, or -1 if none is known
     */
    int getClosestAvailablePillIndex(int sourceIndex) {
        // create a mapping of pills to their distance to sourceIndex
        Map<Integer, Integer> availablePills = new HashMap<>();
        for (int i = 0; i < mPillState.length; i++) {
            if (mPillState[i] == PillState.AVAILABLE) {
                int pillNodeIndex = mGame.getPillIndices()[i];
                availablePills.put(pillNodeIndex, mGame.getShortestPathDistance(sourceIndex, pillNodeIndex));
            }
        }

        // i guess this *should* not happen, but this API is tricky and treacherous
        if (availablePills.size() == 0)
            return -1;

        LinkedHashMap<Integer, Integer> sortedByDistance = sortByValue(availablePills);
        return sortedByDistance.entrySet().iterator().next().getKey();
    }

    /**
     * Reset the state of this class to a freshly opened maze
     */
    private void resetState() {
        mPillState = new PillState[mGame.getNumberOfPills()];
        Arrays.fill(mPillState, PillState.UNKNOWN);
    }
}
