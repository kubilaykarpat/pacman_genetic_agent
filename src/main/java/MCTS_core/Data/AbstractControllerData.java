package MCTS_core.Data;

/**
 * Abstract container class for controller related game data. 
 * @author Max
 *
 */
public abstract class AbstractControllerData{
	
	/**
	 * TODO: API
	 */
	private int lastIndex;
	
	/**
	 * TODO: API
	 */
	private int lastTick;
	
	/**
	 * TODO: API
	 * @param lastIndex
	 * @param lastTick
	 */
	public AbstractControllerData(int lastIndex, int lastTick){
		this.lastIndex = lastIndex;
		this.lastTick = lastTick;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public int getLastIndex() {
		return this.lastIndex;
	}

	/**
	 * TODO: API
	 * @param lastPacmanIndex
	 */
	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public int getLastTick() {
		return this.lastTick;
	}

	/**
	 * TODO: API
	 * @param lastPacmanSeenTick
	 */
	public void setLastTick(int lastTick) {
		this.lastTick = lastTick;
	}		
}
