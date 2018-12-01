package MCTS_core.Controller;

import java.util.Collection;
import java.util.Random;

import MCTS_core.Data.GhostData;
import MCTS_core.Data.MCTSSimulationData;
import MCTS_core.Data.PillData;
import MCTS_core.Data.PowerPillData;
import MCTS_core.MCTS.MCTSNode;
import MCTS_core.MCTS.PacmanMCTSSimulation;
import MCTS_core.Manager.GameManager;
import MCTS_core.Manager.SimulationManager;
import MCTS_core.Policies.Pacman.StillActivePowerPillPolicy;
import MCTS_core.Policies.Pacman.EscapeGhostPolicy;
import MCTS_core.Policies.Pacman.SearchForPillPolicy;
import MCTS_core.Policies.Pacman.AwayGhostsPowerPillPolicy;
import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

/**
 * The individual abstract pacman controller class. This is where all starts ;-)
 * @author Max
 *
 */
public abstract class AbstractPacmanController extends PacmanController {

	/**
	 * TODO: API
	 */
	private static final int DEFAULT_NEXT_MOVE_TIME_BUFFER = 2;
	
	/**
	 * TODO: API
	 */
	private int initialGhostIndex;
	
	/**
	 * TODO: API
	 */
	private int[] lastSeenGhostIndices;
	
	/**
	 * TODO: API
	 */
	private int[] lastSeenGhostTicks;	
	
	/**
	 * TODO: API
	 */
	private int nextMoveTimeBuffer;
	
	/**
	 * TODO: API
	 */
	private int lastEdibleScore;
    
	/**
	 * TODO: API
	 */
	private MCTSSimulationData mctsSimulationData;
	
	/**
	 * TODO: API
	 */
	private PacmanMCTSSimulation pacmanMCTSSimulation;
	
	/**
	 * TODO: API
	 */
	public AbstractPacmanController(){		
		this(new MCTSSimulationData(), 
				DEFAULT_NEXT_MOVE_TIME_BUFFER);
		
		MCTSSimulationData newPacmanSimulationData = this.getMCTSSimulationData();
			newPacmanSimulationData.getPolicies().add(new EscapeGhostPolicy());
			newPacmanSimulationData.getPolicies().add(new SearchForPillPolicy());
			newPacmanSimulationData.getPolicies().add(new AwayGhostsPowerPillPolicy());
			newPacmanSimulationData.getPolicies().add(new StillActivePowerPillPolicy());
		
		this.setMCTSSimulationData(newPacmanSimulationData);
	}
	
	/**
	 * TODO: API
	 * @param mctsData
	 */
	public AbstractPacmanController(MCTSSimulationData mctsSimulationData, int nextMoveTimeBuffer){
		this.mctsSimulationData = mctsSimulationData;
		this.nextMoveTimeBuffer = nextMoveTimeBuffer;
		
		this.initialGhostIndex = -1;
		this.lastSeenGhostIndices = new int[] {-1, -1, -1, -1};
		this.lastSeenGhostTicks = new int[] {-1, -1, -1, -1};
	}	
	
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// initialization stuff
		MOVE newMove = MOVE.NEUTRAL;		
		PacmanMCTSSimulation currentSimulation = this.getPacmanMCTSSimulation();
    	// work on a copy of the current game
		Game gameStateCopy = game.copy();
		// update with the current copy of the game the game manager
		this.updateGhostIndices(gameStateCopy);
    	this.updatePillIndices(gameStateCopy);
    	this.updatePowerPillIndices(gameStateCopy);
    	// grab the patchy game info from the copy 
		GameInfo patchyGameInfo = gameStateCopy.getPopulatedGameInfo();
		// create a new populated game info
		GameInfo repopulatedGameInfo = this.createRepopulatedGameInfo(gameStateCopy, patchyGameInfo);
		// create a new populated game from the populated game info
		// therewith you we can start the simulation
		Game repopulatedGame = gameStateCopy.getGameFromInfo(repopulatedGameInfo).copy();

		// create a new simulation ... 
		if(currentSimulation == null){
    		
			// first pick a random move because it doesn't matter
    		Random random = new Random();
    		int currentPacmanNodeIndex = repopulatedGame.getPacmanCurrentNodeIndex();
    		MOVE[] currentPossibleMoves = repopulatedGame.getPossibleMoves(currentPacmanNodeIndex); 
    		newMove = currentPossibleMoves[random.nextInt(currentPossibleMoves.length)];
    		// first set the edible score 
    		this.setLastEdibleScore(repopulatedGame.getGhostCurrentEdibleScore());
    		
    		// start a new monte-carlo pacman simulation
    		currentSimulation = new PacmanMCTSSimulation(repopulatedGame, this.getMCTSSimulationData());    		
    		SimulationManager.getInstance().addSimulation(currentSimulation);    		
    		SimulationManager.getInstance().move(currentSimulation.getIdentiy(),
    				repopulatedGame,
    				newMove);
    		this.setPacmanMCTSSimulation(currentSimulation);
    	}
		// or update the current simulation with the new game state
    	else{    		
    		SimulationManager.getInstance().updateSimulationGameState(currentSimulation.getIdentiy(),
    				repopulatedGame);
    	}    	
    	
    	int currentEdibleScore = this.getLastEdibleScore();
    	int currentSimulationId = currentSimulation.getIdentiy();
    	
    	// give the simulation of the additional policies some time
    	if(SimulationManager.getInstance().isDecisionNode(currentSimulationId, currentEdibleScore)){
    		timeDue -= 20;
    	}
    	
    	// run simulation until we are out of time
    	while(System.currentTimeMillis() < timeDue - this.getNexMoveTimeBuffer()){
    		SimulationManager.getInstance().runSimulation(currentSimulationId);
    	}
    	
    	// make a decision
    	if(SimulationManager.getInstance().isDecisionNode(currentSimulationId, currentEdibleScore)
    			&& newMove == MOVE.NEUTRAL){
    		
    		//let other evaluators add their 'opinion'
    		SimulationManager.getInstance().runAdditionalPolicies(currentSimulationId);
    	
    		// pick node with the best score
    		MCTSNode bestNode = SimulationManager.getInstance().getBestAvailableNode(currentSimulationId);
    		if(bestNode == null){
    			newMove = SimulationManager.getInstance().getBestAvailableMove(currentSimulationId);
    		}
    		else{
    			newMove = bestNode.getMove();
    		}
    		// start a fresh simulation
    		if(bestNode == null){
    			 currentSimulation = new PacmanMCTSSimulation(repopulatedGame, this.getMCTSSimulationData());
    			 SimulationManager.getInstance().updateSimulation(currentSimulationId,
    					 currentSimulation);
    		}
    		else{    			
    			SimulationManager.getInstance().updateSimulationRoot(currentSimulationId, bestNode);
    		}    		
    	}
    	
    	currentEdibleScore = repopulatedGame.getGhostCurrentEdibleScore();
    	this.setLastEdibleScore(currentEdibleScore);
        return newMove;
	}
	
	/**
	 * Updates the indices of the positions the ghosts were last seen. 
	 * The implementation is similar to the code from the {@code AbstractPacmanController} class.	 * 
	 * @param gameState the current copy of state of the game
	 */
	private void updateGhostIndices(Game gameState){
		Game gameStateCopy = gameState;

        // we just started a game
        if(this.getInitialGhostIndex() == -1){
        	this.setInitialGhostIndex(gameStateCopy.getGhostInitialNodeIndex());
        }        
        
        // iterate over all ghosts 
        for(GHOST ghost : GHOST.values()){
        	int ghostOrdinal = ghost.ordinal();
        	int currentLastSeenGhostIndex = this.getLastSeenGhostIndices()[ghostOrdinal];
        	int currentLastSeenGhostTick = this.getLastSeenGhostTicks()[ghostOrdinal];
            
            // can we see a ghost? If so tell update our info
            int currentGhostIndex = gameStateCopy.getGhostCurrentNodeIndex(ghost);
            if (currentGhostIndex != -1) {
            	currentLastSeenGhostIndex = currentGhostIndex;
            	currentLastSeenGhostTick = gameStateCopy.getCurrentLevelTime();
            }
            
            // everything gone, reset ghost to start index :( 
            if(gameStateCopy.wasPacManEaten()){    
            	currentLastSeenGhostIndex = this.getInitialGhostIndex();
            	currentLastSeenGhostTick = 0;
            }
        	this.getLastSeenGhostIndices()[ghostOrdinal] = currentLastSeenGhostIndex;
        	this.getLastSeenGhostTicks()[ghostOrdinal] = currentLastSeenGhostTick;
        }
        
        GameManager.getInstance().updatePacmanRelatedGhostData(
        		this.getInitialGhostIndex(),
        		this.getLastSeenGhostIndices(),
        		this.getLastSeenGhostTicks());
	}
	
	/**
	 * Updates the indices and active state of the pills pacman has seen or eaten.	 * 
	 * @param gameState the current copy of state of the game
	 */
	private void updatePillIndices(Game gameState){
		Game gameStateCopy = gameState;
		// first: add all not-yet-seen pills
		int[] currentActivePillIndices = gameStateCopy.getActivePillsIndices();
		GameManager.getInstance().updatePillDataIndices(currentActivePillIndices);
		// second: update whenever a pill was eaten
		if(gameStateCopy.wasPillEaten()){
			// current node index of pacman is similar to the index of pill that was eaten 
			int currentPacmanNodeIndex = gameStateCopy.getPacmanCurrentNodeIndex();
			GameManager.getInstance().updatePillDataActivState(currentPacmanNodeIndex);
		}
	}
	
	/**
	 * Updates the indices and active state of the power pills pacman has seen or eaten.
	 * @param gameState the current copy of state of the game
	 */
	private void updatePowerPillIndices(Game gameState){
		Game gameStateCopy = gameState;
		// first: add all not-yet-seen pills
		int[] currentActivePowerPillIndices = gameStateCopy.getActivePowerPillsIndices();
		GameManager.getInstance().updatePowerPillDataIndices(currentActivePowerPillIndices);
		
		// second: update whenever a pill was eaten
		if(gameStateCopy.wasPowerPillEaten()){
			// current node index of pacman is similar to the index of the power pill that was eaten
			int currentPacmanNodeIndex = gameStateCopy.getPacmanCurrentNodeIndex();
			GameManager.getInstance().updatePowerPillDataActivState(currentPacmanNodeIndex);
		}
	}

	/**
	 * Returns a new populated game info based on the current game state, the current game info and 
	 * the data stored in the {@code GameManager}. 
	 * @param gameState the copy of the state of the game
	 * @param gameInfo the patchy game info
	 * @return the new populated game info
	 */
	private GameInfo createRepopulatedGameInfo(Game gameState, GameInfo gameInfo){
		Game gameStateCopy = gameState;
		GameInfo patchyGameInfo = gameInfo;
		int currentLairNodeIndex = gameStateCopy.getCurrentMaze().lairNodeIndex; 
				
		// updating the ghost related data
		GhostData[] currentGhostData = GameManager.getInstance().getPacmanRelatedGhostData();		
		int i = 0;
		for(GHOST ghost : GHOST.values()){
			int currentGhostNodeIndex = currentGhostData[i].getLastIndex();
			int currentGhostEdibleTime = gameStateCopy.getGhostEdibleTime(ghost);
			MOVE currentGhostLastMoveMade = gameStateCopy.getGhostLastMoveMade(ghost);			
			// is can actually happen 
			if(currentGhostLastMoveMade == null){
				currentGhostLastMoveMade = MOVE.NEUTRAL;
			}			
			patchyGameInfo.setGhost(ghost,
					new Ghost(ghost,
							currentGhostNodeIndex,
							currentGhostEdibleTime, 
							currentLairNodeIndex, 
							currentGhostLastMoveMade));
			i++;
		}
		
		// updating the pill related data
		Collection<PillData> currentPillData = GameManager.getInstance().getCurrentPillData().values();
		for(PillData pill : currentPillData){
			patchyGameInfo.setPillAtIndex(pill.getPillIndex(), pill.isActive());
		}
		// updating the power pill related data
		Collection<PowerPillData> currentPowerPillData = GameManager.getInstance().getCurrentPowerPillData().values();
		for(PowerPillData powerPill : currentPowerPillData){
			patchyGameInfo.setPowerPillAtIndex(powerPill.getPillIndex(), powerPill.isActive());
		}
		return patchyGameInfo;
	}	
	
	private int getInitialGhostIndex() {
		return this.initialGhostIndex;
	}
	
	private void setInitialGhostIndex(int initialGhostIndex) {
		this.initialGhostIndex = initialGhostIndex;
	}
	
	private int[] getLastSeenGhostIndices() {
		return this.lastSeenGhostIndices;
	}

	private int[] getLastSeenGhostTicks() {
		return this.lastSeenGhostTicks;
	}

    private int getNexMoveTimeBuffer() {
		return this.nextMoveTimeBuffer;
	}

    private int getLastEdibleScore() {
		return this.lastEdibleScore;
	}

    private void setLastEdibleScore(int lastEdibleScore) {
		this.lastEdibleScore = lastEdibleScore;
	}

    private MCTSSimulationData getMCTSSimulationData() {
		return this.mctsSimulationData;
	}

    private void setMCTSSimulationData(MCTSSimulationData mctsSimulationData) {
		this.mctsSimulationData = mctsSimulationData;
	}
    
    private PacmanMCTSSimulation getPacmanMCTSSimulation() {
		return this.pacmanMCTSSimulation;
	}

    private void setPacmanMCTSSimulation(PacmanMCTSSimulation pacmanMCTSSimulation) {
		this.pacmanMCTSSimulation = pacmanMCTSSimulation;
	}
}
