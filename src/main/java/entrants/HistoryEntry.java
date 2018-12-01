package entrants;

import pacman.game.Constants.*;

/**
 * Created by glas on 28.06.17.
 */
public class HistoryEntry {
    public MOVE lastMove;
    public int lastNodeIndex;
    //public
    public HistoryEntry(){
        this.lastNodeIndex = 0;
        this.lastMove = MOVE.NEUTRAL;
    }
    public void reset(){
        this.lastNodeIndex = 0;
        this.lastMove = MOVE.NEUTRAL;
    }

}
