package MCTS_core.Data;

import pacman.game.Constants.MOVE;

/**
 * Container class for the ghost related game data. 
 * @author Max
 *
 */
public class GhostData extends AbstractControllerData{

	/**
	 * The last move the ghost has performed.
	 */
	private MOVE lastMove;
	
	/**
	 * TODO: API
	 * @param lastIndex
	 * @param lastTick
	 */
	public GhostData(int lastIndex, int lastTick) {
		this(lastIndex, lastTick, MOVE.NEUTRAL);
	}
	
	/**
	 * TODO: API
	 * @param lastIndex
	 * @param lastTick
	 * @param move
	 */
	public GhostData(int lastIndex, int lastTick, MOVE lastMove) {
		super(lastIndex, lastTick);
		this.lastMove = lastMove;
	}
	
	/**
	 * Returns the last move the ghost has performed.
	 * @return the last move
	 */
	public MOVE getLastMove(){
		return this.lastMove;
	}
	
	/**
	 * Sets the last move the ghost has performed.
	 * @param lastMove the last move
	 */
	public void setLastMove(MOVE lastMove){
		this.lastMove = lastMove;
	}
}
