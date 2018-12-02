package entrants;

import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.Arrays;


/**
 * Created by glas on 29.06.17.
 */
public class History {
    int maxHistoryEntries;
    public HistoryEntry[] history;
    public HistoryEntry lastEntry;
    private PacmanController mpm;
    public int[] lastGhostsNodeIndex;//[0][] -> nodeIndex; [1][] -> time
    int h;

    public void updateHistory(Game game, Constants.MOVE m) {
        history[h].lastMove = m;
        history[h].lastNodeIndex = game.getPacmanCurrentNodeIndex();
        lastEntry = history[h];
        h = ++h % maxHistoryEntries;
        for(int i = 0; i < Constants.NUM_GHOSTS; i++)
            if(game.getGhostCurrentNodeIndex(Constants.GHOST.values()[i]) != -1)
                lastGhostsNodeIndex[i] = game.getGhostCurrentNodeIndex(Constants.GHOST.values()[i]);
    }

    public void resetHistory(){
        Arrays.setAll(lastGhostsNodeIndex, i -> -1);
        for(int i = 0; i < maxHistoryEntries;i++)
            history[h].reset();
    }

    public int[]  shortestPathDistancesFromPacManToGhosts(Game game){

        int[] lastSeenGhostNodeDistance = new int[Constants.NUM_GHOSTS];
        for(int i = 0; i < Constants.NUM_GHOSTS; i++)
            lastSeenGhostNodeDistance[i] =  lastGhostsNodeIndex[i] != -1 ?
                    game.getShortestPath(game.getPacmanCurrentNodeIndex(),lastGhostsNodeIndex[i]).length :
                    Integer.MAX_VALUE;
        return lastSeenGhostNodeDistance;
    }

    public double[] euclideanDistancesFromPacManToGhosts(Game game){
        double[] lastSeenGhostDistance = new double[Constants.NUM_GHOSTS];
        for(int i = 0; i < Constants.NUM_GHOSTS; i++) //TODO zu ende machen
            lastSeenGhostDistance[i] = lastGhostsNodeIndex[i] != -1 ?
                    game.getEuclideanDistance(game.getPacmanCurrentNodeIndex(),lastGhostsNodeIndex[i]) :
                    Integer.MAX_VALUE;
        return lastSeenGhostDistance;
    }

    public History(int length, PacmanController myPacMan) {
        this.mpm = myPacMan;
        h = 0;
        maxHistoryEntries = length;
        history = new HistoryEntry[maxHistoryEntries];
        for (int i = 0; i < maxHistoryEntries; i++)
            history[i] = new HistoryEntry();
        lastEntry = history[h];
        lastGhostsNodeIndex = new int[]{-1,-1,-1,-1};
        //lastSeenGhostNodeDistance = new double[]{Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE};
    }

}
