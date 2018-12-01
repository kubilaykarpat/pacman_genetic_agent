package MCTS_core.Manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MCTS_core.Data.GhostData;
import MCTS_core.Data.PacmanData;
import MCTS_core.Data.PillData;
import MCTS_core.Data.PowerPillData;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * Game manager singleton class. 
 * @author Max
 *
 */
public class GameManager {

	/**
	 * The game manager instance;
	 */
	private static GameManager gameManager;
	
	/**
	 * The ghost related pacman data, 
	 * i.e. the game data of pacman created from the knowledge of the ghosts.
	 */
	private PacmanData ghostRelatedPacmanData;
	
	/**
	 * The ghost related ghost data,
	 * i.e. the game data of the ghosts created from the knowledge over each other.
	 */
	private GhostData[] ghostRelatedGhostData;
	
	/**
	 * The pacman related ghost data,
	 * i.e. the game data of the ghosts created from pacmans knowledge.
	 */
	private GhostData[] pacmanRelatedGhostData;
		
	/**
	 * Stores the current data of seen pills in the game.
	 */
	private Map<Integer, PillData> currentPillData;
	
	/**
	 * Stores the current data of seen power pills in the game.
	 */
	private Map<Integer, PowerPillData> currentPowerPillData;
	
	/**
	 * The hidden constructor. 
	 */
	private GameManager(){
		this.currentPillData = new HashMap<Integer, PillData>();
		this.currentPowerPillData = new HashMap<Integer, PowerPillData>();
	}
	
	/**
	 * Returns the singleton instance of the class.
	 * @return the instance of the class
	 */
	public static GameManager getInstance(){
		// lazy 
		if(gameManager == null){
			gameManager = new GameManager();
		}
		return gameManager;
	}
		
	/**
	 * Updates the data of the ghosts in the game. This function will by only called from pacman.  
	 * @param initialGhostIndex the initial index the positions of the ghosts 
	 * @param lastSeenGhostIndices the indices of the positions the ghosts were was last seen
	 * @param lastSeenGhostTicks the time ticks the ghosts were was last seen
	 */
	public void updatePacmanRelatedGhostData(int initialGhostIndex,
			int[] lastSeenGhostIndices,
			int[] lastSeenGhostTicks){
		GhostData[] currentPacmanRelatedGhostData = this.getPacmanRelatedGhostData();
		
		if(currentPacmanRelatedGhostData == null){
			this.setPacmanRelatedGhostData(new GhostData[Constants.NUM_GHOSTS]);
		}
		
		for(GHOST ghost : GHOST.values()){
			int ghostOrdinal = ghost.ordinal();
			int currentGhostIndex = lastSeenGhostIndices[ghostOrdinal];
			int currentGhostTick = lastSeenGhostTicks[ghostOrdinal];			
			GhostData currentData = this.getPacmanRelatedGhostData()[ghostOrdinal];
			
			// init ghosts with there initial position indices
			if(currentData == null){
				currentData = new GhostData(initialGhostIndex, 0);
			}
			else{
				if(currentGhostIndex != -1 
						&& currentData.getLastIndex() != currentGhostIndex 
						&& currentData.getLastTick() != currentGhostTick ){
					currentData.setLastIndex(currentGhostIndex);
					currentData.setLastTick(currentGhostTick);					
				}
			}	
			this.getPacmanRelatedGhostData()[ghostOrdinal] = currentData;
		}
	}
	
	/**
	 * Updates the data of pacman in the game. This function will by only called from a ghost.
	 * @param initialPacmanIndex the initial index of pacmans position  
	 * @param lastSeenPacmanIndex the index of the postion pacman was last seen
	 * @param lastSeenPacmanTick the time tick pacman was last seen
	 */
	public void updateGhostRelatedPacmanData(int initialPacmanIndex, 
			int lastSeenPacmanIndex,
			int lastSeenPacmanTick){
		PacmanData currentGhostRelatedPacmanData = this.getGhostRelatedPacmanData();
		
		// init pacman with his initial position index
		if(currentGhostRelatedPacmanData == null){
			currentGhostRelatedPacmanData = new PacmanData(initialPacmanIndex, 0);
		}
		else{
			if(lastSeenPacmanIndex != -1 
					&& currentGhostRelatedPacmanData.getLastIndex() != lastSeenPacmanIndex 
					&& currentGhostRelatedPacmanData.getLastTick() != lastSeenPacmanTick){
				currentGhostRelatedPacmanData.setLastIndex(lastSeenPacmanIndex);
				currentGhostRelatedPacmanData.setLastTick(lastSeenPacmanTick);
			}	
		}	
		this.setGhostRelatedPacmanData(currentGhostRelatedPacmanData);
	}
	
	/**
	 * Updates the data of the ghosts in the game. This function will by only called from the ghosts.  
	 * @param initialGhostIndex the initial index the positions of the ghosts 
	 * @param ghostIndices the indices of the positions the ghosts 
	 * @param ghostMoves the last moves the ghosts have performed 
	 * @param ghostTicks the time ticks the ghosts have send their position
	 */
	public void updateGhostRelatedGhostData(int initialGhostIndex,
			int[] ghostIndices,
			MOVE[] ghostMoves,
			int[] ghostTicks){
		GhostData[] currentGhostRelatedGhostData = this.getGhostRelatedGhostData();
		
		if(currentGhostRelatedGhostData == null){
			this.setGhostRelatedGhostData(new GhostData[Constants.NUM_GHOSTS]);
		}
		
		for(GHOST ghost : GHOST.values()){
			int ghostOrdinal = ghost.ordinal();
			int currentGhostIndex = ghostIndices[ghostOrdinal];	
			int currentGhostTick = ghostTicks[ghostOrdinal];	
			MOVE lastGhostMove = ghostMoves[ghostOrdinal];
					
			GhostData currentData = this.getGhostRelatedGhostData()[ghostOrdinal];
			
			// init ghosts with there initial position indices
			if(currentData == null){
				currentData = new GhostData(initialGhostIndex, 0);
			}
			else{
				if(currentGhostIndex != -1 
						&& currentData.getLastIndex() != currentGhostIndex 
						&& currentData.getLastTick() != currentGhostTick ){
					currentData.setLastIndex(currentGhostIndex);
					currentData.setLastMove(lastGhostMove);
					currentData.setLastTick(currentGhostTick);					
				}
			}	
			this.getGhostRelatedGhostData()[ghostOrdinal] = currentData;
		}
	}
	
	/**
	 * Updates the pill data with possible not-yet-seen pill indices.
	 * @param activePillIndices the currently active and visible pills in the game
	 */
	public void updatePillDataIndices(int[] activePillIndices){
		int[] currentActivePillIndices = activePillIndices;
		Map<Integer, PillData> pillDataMap = this.getCurrentPillData();
		Set<Integer> pillDataIndices = pillDataMap.keySet();
		
		// FIXME: this is a ugly workaround for this silly java problem
		// https://stackoverflow.com/questions/15664396/java-containsall-does-not-return-true-when-given-lists
		List<Integer> dummyList = new ArrayList<Integer>();
		for(int index : currentActivePillIndices){
			dummyList.add(index);
		}
		
		if(pillDataIndices.containsAll(dummyList)){
			// all pills are already in there, so nothing to do
			return;
		}
		// now we have to search
		else{
			for(int index : currentActivePillIndices){
				if(!pillDataMap.containsKey(index)){
					pillDataMap.put(index, new PillData(index, true));
				}
				//System.out.println("Active Pills: " +index);
			}	
		}
	}
	
	/**
	 * Updates the active status of the pill data.
	 * @param inActivePillIndex the index of the pill which was eaten
	 */
	public void updatePillDataActivState(int inActivePillIndex){
		Map<Integer, PillData> pillDataMap = this.getCurrentPillData();
		// is pill part of the seen pills?
		if(pillDataMap.containsKey(inActivePillIndex)){
			PillData inActivePill = pillDataMap.get(inActivePillIndex);
			inActivePill.setIsActive(false);
		}
		else{
			// pill eaten but never seen... should not be happening otherwise we are in big trouble
		}
	}	
	
	/**
	 * Returns the indices of the current seen and active pills.
	 * @return the indices of the power pills
	 */
	public List<Integer> getActivePillIndices(){
		Collection<PillData> pillData = this.getCurrentPillData().values();
		List<Integer> currentAcitvePillIndices = new ArrayList<Integer>();
		for(PillData pill : pillData){
			if(pill.isActive()){
				currentAcitvePillIndices.add(pill.getPillIndex());
			}
		}
		return currentAcitvePillIndices;
	}
	
	/**
	 * Updates the power pill data with possible not-yet-seen power pill indices.
	 * @param activePowerPillIndices the currently active and visible power pills in the game
	 */
	public void updatePowerPillDataIndices(int[] activePowerPillIndices){
		int[] currentActivePowerPillIndices = activePowerPillIndices;
		Map<Integer, PowerPillData> powerPillDataMap = this.getCurrentPowerPillData();
		Set<Integer> powerPillDataIndices = powerPillDataMap.keySet();
		
		// FIXME: this is a ugly workaround for this silly java problem
		// https://stackoverflow.com/questions/15664396/java-containsall-does-not-return-true-when-given-lists
		List<Integer> dummyList = new ArrayList<Integer>();
		for(int index : activePowerPillIndices){
			dummyList.add(index);
		}
		
		if(powerPillDataIndices.containsAll(dummyList)){
			// all pills are already in there, so nothing to do
			return;
		}
		// now we have to search for the missing key 
		else {
			for (int index : currentActivePowerPillIndices) {
				if (!powerPillDataMap.containsKey(index)) {
					powerPillDataMap.put(index, new PowerPillData(index, true));
				}
			}
		}
	}
	
	/**
	 * Updates the active status of the power pill data.
	 * @param inActivePowerPillIndex the index of the power pill which was eaten
	 */
	public void updatePowerPillDataActivState(int inActivePowerPillIndex){
		Map<Integer, PowerPillData> powerPillDataMap = this.getCurrentPowerPillData();
		// is pill part of the seen pills?
		if(powerPillDataMap.containsKey(inActivePowerPillIndex)){
			PowerPillData inActivePill = powerPillDataMap.get(inActivePowerPillIndex);
			inActivePill.setIsActive(false);
		}
		else{
			// pill eaten but never seen... should not be happening otherwise we are in big trouble
		}
	}	
	
	/**
	 * Returns the indices of the current seen and active power pills.
	 * @return the indices of the power pills
	 */
	public List<Integer> getActivePowerPillIndices(){
		Collection<PowerPillData> powerPillData = this.getCurrentPowerPillData().values();
		List<Integer> currentAcitvePowerPillIndices = new ArrayList<Integer>();
		for(PowerPillData powerPill : powerPillData){
			if(powerPill.isActive()){
				currentAcitvePowerPillIndices.add(powerPill.getPillIndex());
			}
		}
		return currentAcitvePowerPillIndices;
	}
	
	
	
	/**
	 * Returns the data over pacman from the knowledge of the ghosts.
	 * @return the data
	 */
	public PacmanData getGhostRelatedPacmanData() {
		return this.ghostRelatedPacmanData;
	}

	/**
	 * TODO: API
	 * @param ghostRelatedPacmanData
	 */
	private void setGhostRelatedPacmanData(PacmanData ghostRelatedPacmanData) {
		this.ghostRelatedPacmanData = ghostRelatedPacmanData;
	}
	
	/**
	 * Returns the data over the ghosts from pacman knowledges.
	 * @return the data
	 */
	public GhostData[] getPacmanRelatedGhostData() {
		return this.pacmanRelatedGhostData;
	}

	/**
	 * TODO: API
	 * @param pacmanRelatedGhostData
	 */
	private void setPacmanRelatedGhostData(GhostData[] pacmanRelatedGhostData) {
		this.pacmanRelatedGhostData = pacmanRelatedGhostData;
	}

	/**
	 * Returns the data over the ghosts from the knowledges over each other.
	 * @return the data
	 */
	public GhostData[] getGhostRelatedGhostData() {
		return this.ghostRelatedGhostData;
	}

	/**
	 * TODO: API
	 * @param ghostRelatedGhostData
	 */
	private void setGhostRelatedGhostData(GhostData[] ghostRelatedGhostData) {
		this.ghostRelatedGhostData = ghostRelatedGhostData;
	}	
	
	/**
	 * TODO: API
	 * @return
	 */
	public Map<Integer, PillData> getCurrentPillData() {
		return this.currentPillData;
	}

	/**
	 * TODO: API
	 * @param currentPillData
	 */
	public void setCurrentPillData(Map<Integer, PillData> currentPillData) {
		this.currentPillData = currentPillData;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public Map<Integer, PowerPillData> getCurrentPowerPillData() {
		return this.currentPowerPillData;
	}

	/**
	 * TODO: API
	 * @param currentPowerPillData
	 */
	public void setCurrentPowerPillData(Map<Integer, PowerPillData> currentPowerPillData) {
		this.currentPowerPillData = currentPowerPillData;
	}
}
