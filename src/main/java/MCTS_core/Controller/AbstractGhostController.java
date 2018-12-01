package MCTS_core.Controller;

import java.util.Random;

import MCTS_core.Data.GhostData;
import MCTS_core.Data.MCTSSimulationData;
import MCTS_core.Data.PacmanData;
import MCTS_core.MCTS.GhostMCTSSimulation;
import MCTS_core.MCTS.MCTSNode;
import MCTS_core.Manager.GameManager;
import MCTS_core.Manager.SimulationManager;
import MCTS_core.Policies.Ghost.SearchForPacmanPolicy;
import MCTS_core.Policies.Ghost.SpreadGhostPolicy;
import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.comms.BasicMessage;
import pacman.game.comms.Message;
import pacman.game.comms.Messenger;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.PacMan;
import pacman.game.Game;

/**
 * The individual abstract ghost controller class. This is where all starts, again. 
 * @author Max
 *
 */
public abstract class AbstractGhostController extends IndividualGhostController {

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
	private int[] ghostIndices;
	
	/**
	 * TODO: API
	 */
	private MOVE[] lastGhostMoves;
	
	/**
	 * TODO: API
	 */
	private int[] ghostTicks;	
	
	/**
	 * TODO: API
	 */
	private int initialPacmanIndex;
	
	/**
	 * TODO: API
	 */
	private int lastSeenPacmanIndex;
	
	/**
	 * TODO: API
	 */
	private int lastSeenPacmanTick;
	
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
	private GhostMCTSSimulation ghostMCTSSimulation;
	
	public AbstractGhostController(GHOST ghost) {
		this(ghost, new MCTSSimulationData(), DEFAULT_NEXT_MOVE_TIME_BUFFER);
		
		MCTSSimulationData newGhostSimulationData = this.getMCTSSimulationData();
			newGhostSimulationData.getPolicies().add(new SearchForPacmanPolicy());
			newGhostSimulationData.getPolicies().add(new SpreadGhostPolicy());
			
		this.setMCTSSimulationData(newGhostSimulationData);
	}
	
	public AbstractGhostController(GHOST ghost, MCTSSimulationData mctsSimulationData, int nextMoveTimeBuffer) {
		super(ghost);
		this.mctsSimulationData = mctsSimulationData;
		this.nextMoveTimeBuffer = nextMoveTimeBuffer;
		
		this.initialGhostIndex = -1;
		this.ghostIndices = new int[] {-1, -1, -1, -1};
		this.lastGhostMoves = new MOVE[Constants.NUM_GHOSTS];
		this.ghostTicks = new int[] {-1, -1, -1, -1};
		
		this.initialPacmanIndex = -1;
		this.lastSeenPacmanIndex = -1;
		this.lastSeenPacmanTick = -1;
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		MOVE newMove = MOVE.NEUTRAL;
		Boolean requiresAction = game.doesGhostRequireAction(ghost);
		// can we do something?
		if(requiresAction != null && requiresAction){
			// initialization stuff
				
			GhostMCTSSimulation currentSimulation = this.getGhostMCTSSimulation();
	    	// work on a copy of the current game
			Game gameStateCopy = game.copy();
			// update with the current copy of the game the game manager
			this.updatePacmanIndex(gameStateCopy);
			this.updateGhostMovesAndIndices(gameStateCopy);
	    	
			//this.updatePillIndices(gameStateCopy);
	    	//this.updatePowerPillIndices(gameStateCopy);
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
	    		currentSimulation = new GhostMCTSSimulation(repopulatedGame, this.getMCTSSimulationData(), this.ghost);    		
	    		SimulationManager.getInstance().addSimulation(currentSimulation);    		
	    		SimulationManager.getInstance().move(currentSimulation.getIdentiy(),
	    				repopulatedGame,
	    				newMove);
	    		this.setGhostMCTSSimulation(currentSimulation);
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
	    			 currentSimulation = new GhostMCTSSimulation(repopulatedGame, this.getMCTSSimulationData(), this.ghost);
	    			 SimulationManager.getInstance().updateSimulation(currentSimulationId,
	    					 currentSimulation);
	    		}
	    		else{    			
	    			SimulationManager.getInstance().updateSimulationRoot(currentSimulationId, bestNode);
	    		}    		
	    	}
	    	
	    	currentEdibleScore = repopulatedGame.getGhostCurrentEdibleScore();
	    	this.setLastEdibleScore(currentEdibleScore);	
		}
		return newMove;
	}
	
	/**
	 * Updates the index of position pacman was last seen.  
	 * The implementation is based on the code from the {@code IndividualGhostController} class.
	 * 
	 * @param gameState the current copy state of the game
	 */
	private void updatePacmanIndex(Game gameState){
		Game gameStateCopy = gameState;
		
		// we just started a game
		if(this.getInitialPacmanIndex() == -1){
			this.setInitialPacmanIndex(gameStateCopy.getPacManInitialNodeIndex());
		}		
		
        int currentTick = gameStateCopy.getCurrentLevelTime();        
        int pacmanIndex = gameStateCopy.getPacmanCurrentNodeIndex();
        Messenger messenger = gameStateCopy.getMessenger();
        // can we see PacMan? If so tell people and update our info
        if (pacmanIndex != -1) {
        	this.setLastSeenPacmanIndex(pacmanIndex);
        	this.setLastSeenPacmanTick(gameStateCopy.getCurrentLevelTime());
            if (messenger != null) {
                messenger.addMessage(
                		new BasicMessage(ghost, 
                				null,
                				BasicMessage.MessageType.PACMAN_SEEN,
                				this.getLastSeenPacmanIndex(),
                				this.getLastSeenPacmanTick()));
            }
        }         
        // has anybody else seen PacMan if we haven't?
        if (pacmanIndex == -1 && gameStateCopy.getMessenger() != null) {
            for (Message message : messenger.getMessages(ghost)) {
                if (message.getType() == BasicMessage.MessageType.PACMAN_SEEN) {
                    if (message.getTick() > this.getLastSeenPacmanTick() && message.getTick() < currentTick) { // Only if it is newer information
                    	this.setLastSeenPacmanIndex(message.getData());
                        this.setLastSeenPacmanTick(message.getTick());
                    }
                }
            }
        }
        
        // we made it, we eat pacman so reset pacman to start index :) 
        if(gameStateCopy.wasPacManEaten()){    
        	this.setLastSeenPacmanIndex(this.getInitialPacmanIndex());
        	this.setLastSeenPacmanTick(0);
        }
                
        GameManager.getInstance().updateGhostRelatedPacmanData(
        		this.getInitialPacmanIndex(),
        		this.getLastSeenPacmanIndex(),
        		this.getLastSeenPacmanTick());
    }

	/**
	 * Updates the moves and the indices of the ghosts. 
	 * The implementation is similar to the code from the {@code AbstractPacmanController} class.	  
	 * @param gameState the current copy of state of the game
	 */
	private void updateGhostMovesAndIndices(Game gameState){
		Game gameStateCopy = gameState;

        // we just started a game
        if(this.getInitialGhostIndex() == -1){
        	this.setInitialGhostIndex(gameStateCopy.getGhostInitialNodeIndex());
        } 
		
		int currentGhostNodeIndex = gameStateCopy.getGhostCurrentNodeIndex(ghost);
		MOVE currentLastMove = gameStateCopy.getGhostLastMoveMade(ghost);
		int currentGhostTick =   gameStateCopy.getCurrentLevelTime();  
		// update our own data
		int currentGhostOrdinal = ghost.ordinal();
		this.getGhostIndices()[currentGhostOrdinal] = currentGhostNodeIndex;
		this.getLastGhostMoves()[currentGhostOrdinal] = currentLastMove;
		this.getGhostTicks()[currentGhostOrdinal] = currentGhostTick;
				
		Messenger messenger = gameStateCopy.getMessenger();
		if(messenger != null){
			// tell the other ghosts our location
			messenger.addMessage(new BasicMessage(ghost,
					null,
					BasicMessage.MessageType.I_AM,
					this.getGhostIndices()[currentGhostOrdinal], 
					this.getGhostTicks()[currentGhostOrdinal]));	
			// hijack I_AM_HEADING for telling the the other ghost the last move the ghost has performed
			// by using the enums oridnal. If you think this is cheating, come and get me :P 
			messenger.addMessage(new BasicMessage(ghost,
					null,
					BasicMessage.MessageType.I_AM_HEADING,
					this.getLastGhostMoves()[currentGhostOrdinal].ordinal(), 
					this.getGhostTicks()[currentGhostOrdinal]));
			
			for (Message message : messenger.getMessages(ghost)) {
				// update positions from other
				if (message.getType() == BasicMessage.MessageType.I_AM) {
					int ghostOrdinal = message.getSender().ordinal();
					this.getGhostIndices()[ghostOrdinal] = message.getData();
					this.getGhostTicks()[ghostOrdinal] = message.getTick();					
				}
				// update moves from other by decoding ordinal 
				else if (message.getType() == BasicMessage.MessageType.I_AM_HEADING){
					int ghostOrdinal = message.getSender().ordinal();
					MOVE lastGhostMove = MOVE.values()[message.getData()];
					this.getLastGhostMoves()[ghostOrdinal] =  lastGhostMove;	
				}
				
			}
		}		
        // everything gone, reset ghost to start index :( 
        if(gameStateCopy.wasPacManEaten()){    
        	for(GHOST ghost : GHOST.values()){
        		int ghostOrdinal = ghost.ordinal();
        		this.getGhostIndices()[ghostOrdinal] = this.getInitialGhostIndex();
        		this.getLastGhostMoves()[ghostOrdinal] = MOVE.NEUTRAL;
        		this.getGhostTicks()[ghostOrdinal] = 0;	
        	}
        }	
        
        GameManager.getInstance().updateGhostRelatedGhostData(
        		this.getInitialGhostIndex(),
        		this.getGhostIndices(),
        		this.getLastGhostMoves(),
        		this.getGhostTicks());
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
		// update the pacman related data	
		PacmanData currentPacmanData = GameManager.getInstance().getGhostRelatedPacmanData();
		int currentPacmanNodeIndex = currentPacmanData.getLastIndex();
		MOVE currentLastPacamanMove = gameStateCopy.getPacmanLastMoveMade();
		int currentPacmanLives = gameStateCopy.getPacmanNumberOfLivesRemaining();
		
		patchyGameInfo.setPacman(new PacMan(currentPacmanNodeIndex,
				currentLastPacamanMove,
				currentPacmanLives,
				false));
				
		// updating the ghost related data
		GhostData[] currentGhostData = GameManager.getInstance().getGhostRelatedGhostData();		
		int i = 0;
		for(GHOST ghost : GHOST.values()){
			int currentGhostNodeIndex = currentGhostData[i].getLastIndex();
			// use our own index 
			if(ghost == this.ghost){
				currentGhostNodeIndex = gameStateCopy.getGhostCurrentNodeIndex(this.ghost);
			}

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
		
//		// updating the pill related data
//		Collection<PillData> currentPillData = GameManager.getInstance().getCurrentPillData().values();
//		for(PillData pill : currentPillData){
//			patchyGameInfo.setPillAtIndex(pill.getPillIndex(), pill.isActive());
//		}
//		// updating the power pill related data
//		Collection<PowerPillData> currentPowerPillData = GameManager.getInstance().getCurrentPowerPillData().values();
//		for(PowerPillData powerPill : currentPowerPillData){
//			patchyGameInfo.setPowerPillAtIndex(powerPill.getPillIndex(), powerPill.isActive());
//		}
		return patchyGameInfo;
	}	
		
	private int getInitialGhostIndex() {
		return this.initialGhostIndex;
	}
	
	private void setInitialGhostIndex(int initialGhostIndex) {
		this.initialGhostIndex = initialGhostIndex;
	}
	
	private int[] getGhostIndices() {
		return this.ghostIndices;
	}
	
	private MOVE[] getLastGhostMoves() {
		return this.lastGhostMoves;
	}

	private int[] getGhostTicks() {
		return this.ghostTicks;
	}	
	
	private int getInitialPacmanIndex() {
		return this.initialPacmanIndex;
	}
	
	private void setInitialPacmanIndex(int initialPacmanIndex) {
		this.initialPacmanIndex = initialPacmanIndex;
	}
	
	private int getLastSeenPacmanIndex() {
		return this.lastSeenPacmanIndex;
	}

	private void setLastSeenPacmanIndex(int lastPacmanIndex) {
		this.lastSeenPacmanIndex = lastPacmanIndex;
	}

	private int getLastSeenPacmanTick() {
		return this.lastSeenPacmanTick;
	}

	private void setLastSeenPacmanTick(int lastPacmanSeenTick) {
		this.lastSeenPacmanTick = lastPacmanSeenTick;
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
    
    private GhostMCTSSimulation getGhostMCTSSimulation() {
		return this.ghostMCTSSimulation;
	}

    private void setGhostMCTSSimulation(GhostMCTSSimulation ghostMCTSSimulation) {
		this.ghostMCTSSimulation = ghostMCTSSimulation;
	}
}
