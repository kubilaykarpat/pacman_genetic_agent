package MCTS_core.Data;

/**
 * Abstract container class for all pill related game data.
 * @author Max
 */
public abstract class AbstractPillData{
	
	/**
	 * TODO: API
	 */
	private int pillIndex;
	
	/**
	 * TODO: API
	 */
	private boolean isActive;
	
	/**
	 * 
	 * @param pillIndex
	 * @param hasEaten
	 */
	public AbstractPillData(int pillIndex, boolean isActive){
		this.pillIndex = pillIndex;
		this.isActive = isActive;			
	}

	public int getPillIndex() {
		return this.pillIndex;
	}

	public void setPillIndex(int pillIndex) {
		this.pillIndex = pillIndex;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
}
