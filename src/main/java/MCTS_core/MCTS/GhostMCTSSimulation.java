package MCTS_core.MCTS;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import MCTS_core.Data.GhostData;
import MCTS_core.Data.MCTSSimulationData;
import MCTS_core.Data.MCTSSimulationData.UCBSelectionPolicy;
import MCTS_core.Manager.GameManager;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.info.GameInfo;
import pacman.game.internal.PacMan;
import pacman.game.Game;

public class GhostMCTSSimulation extends AbstractMCTSSimulation {

	/**
	 * The ghost used for the simulation.
	 */
	private GHOST currentGhost;
	
	/**
	 * TODO: API
	 * @param gameState
	 * @param mctsSimulationData
	 */
	public GhostMCTSSimulation(Game gameState, MCTSSimulationData mctsSimulationData, GHOST ghost) {
		super(gameState, mctsSimulationData);	
		this.currentGhost = ghost;
	}
	
	@Override
	public void runSimulation() {
		Game repopulatedGameState = this.getRepopulatedGameState();		
		// saves the current constructed game state
		this.pushGameState(repopulatedGameState);
		try {
			List<MCTSNode> visitedNodes = new ArrayList<MCTSNode>();				
			MCTSSimulationData currentSimulationData = this.getMCTSSimulationData();
			UCBSelectionPolicy currentSelectionPolicy = currentSimulationData.getUCBSelectionPolicy();			
			int currentPacmanLives = repopulatedGameState.getPacmanNumberOfLivesRemaining();	
			
			// first node is the root node
			MCTSNode node = this.getRoot();
			visitedNodes.add(node);
			
			this.advanceGame(repopulatedGameState);		
			
			// walk through the tree until a leaf is reached 
			while(!node.isLeaf()){
				
				// select node according to the highest UCB value
				node = currentSelectionPolicy.getChild(node);
				
				if(node == null){
					return;
				}
				// save the nodes to update their scores
				visitedNodes.add(node);
				this.move(repopulatedGameState, node.getMove());
				
				this.advanceGame(repopulatedGameState);
			}
			// expand the node by picking one of this children 
			if(node.getVisitCount() >= currentSimulationData.getExpansionThreshold()
					|| (node == this.getRoot())){
				
				node.expandNode(repopulatedGameState, repopulatedGameState.getPacmanCurrentNodeIndex());

				// evaluate first by running a simulation for all children
				for(MCTSNode child : node.getChildrenCollection()){
					//System.out.println("Child with move: " +child.getMove());
					
					int oldPowerPillCount = repopulatedGameState.getNumberOfActivePowerPills();
					int oldPillCount = repopulatedGameState.getNumberOfActivePills();
					int oldLevel = repopulatedGameState.getCurrentLevel();
					
					// save the current state of the game
					this.pushGameState(repopulatedGameState);
					// and make some simulations
					this.move(repopulatedGameState, child.getMove());
					this.advanceGame(repopulatedGameState);
					
					// mark if a power pill has been eaten
					if(repopulatedGameState.getNumberOfActivePowerPills() < oldPowerPillCount){
						child.setHasPowerPillEaten(true);
					}
					// mark if pills have been eaten
					if(repopulatedGameState.getNumberOfActivePills() < oldPillCount){
						child.setHavePillsEaten(true);
					}
					
					int newScore = 0;
					// bonus for completing a level
					if(repopulatedGameState.getCurrentLevel() > oldLevel){
						newScore -= currentSimulationData.getCompletionPenalty();
					}
				
					// calculate the new score for this node 
					newScore += this.runGhostSimulation(repopulatedGameState, 
							currentSimulationData,
							visitedNodes,
							currentPacmanLives);
					// update the score of the child node		
					child.updateNodeScore(newScore);	
					// reset the game state and start all over again
					this.popGameState();					
				}		
				// apply UCB selection policy to get the next child node
				node = currentSelectionPolicy.getChild(node);
				if(node == null){
					return;
				}
				
				visitedNodes.add(node);
				// advance game with the selected node's move
				this.move(repopulatedGameState, node.getMove());	
			}
			
			this.runGhostSimulation(repopulatedGameState, 
					currentSimulationData,
					visitedNodes,
					currentPacmanLives);
			
		} finally {
			// reset game state
			this.popGameState();
		}
	}

	@Override
	public boolean isDecisionNode(int edibleGhostScore) {
		Game repopulatedGameState = this.getRepopulatedGameState();
		GHOST currentGhost = this.getCurrentGhost();
		int currentGhostNodeIndex = repopulatedGameState.getGhostCurrentNodeIndex(currentGhost);
		
		int currentEdibleGhostScore = edibleGhostScore;
		
		boolean value = false;
		value = repopulatedGameState.gameOver()
				|| repopulatedGameState.isJunction(currentGhostNodeIndex)
				|| this.isRunningAgainstWall(repopulatedGameState)
				|| this.wasGhostEaten(repopulatedGameState, currentEdibleGhostScore);		
		return value;
	}

	@Override
	public void move(Game gameState, MOVE move) {
		Game repopulatedGameState = gameState;
		MOVE currentMove = move;

		repopulatedGameState = this.fixLastPacamanMove(repopulatedGameState);		
		EnumMap<GHOST, MOVE> lastGhostMoves = this.getLastGhostMoves(currentMove);		
		repopulatedGameState.advanceGame(
				this.getMCTSSimulationData().getPacman().getMove(repopulatedGameState, 0),
				lastGhostMoves);
	
	}

	@Override
	protected boolean isRunningAgainstWall(Game gameState) {
		Game repopulatedGameState = gameState;
		GHOST currentGhost = this.getCurrentGhost();
		int currentGhostNodeIndex = repopulatedGameState.getGhostCurrentNodeIndex(currentGhost);
		
		MOVE lastGhostMove = repopulatedGameState.getGhostLastMoveMade(currentGhost);
		MOVE possibleGhostMoves[] = repopulatedGameState.getPossibleMoves(currentGhostNodeIndex);		
		for(int i = 0; i <possibleGhostMoves.length; i++){
			if(possibleGhostMoves[i] == lastGhostMove){
				return false;
			}
		}		
		return true;
	}

	@Override
	protected void advanceGame(Game gameState) {
		Game repopulatedGameState = gameState;

		EnumMap<GHOST, MOVE> lastGhostMoves = this.getLastGhostMoves(repopulatedGameState.getGhostLastMoveMade(this.getCurrentGhost()));	

		int currentEdibleGhostScore = repopulatedGameState.getGhostCurrentEdibleScore();
		while(!this.isDecisionNode(currentEdibleGhostScore)){	
			repopulatedGameState.advanceGame(
					this.getMCTSSimulationData().getPacman().getMove(repopulatedGameState, 0),
					lastGhostMoves);
		}
	}

	/**
	 * Runs a ghost simulation for the current state of the game, calculates and applies a score to the visited nodes.
	 * @param gameState the repopulated state of the game
	 * @param simulationData the simulation data
	 * @param visitedNodes the current visited nodes
	 * @param pacmanLives the number of pacman reaming lives
	 * @return
	 */
	private int runGhostSimulation(Game gameState, MCTSSimulationData simulationData,
			List<MCTSNode> visitedNodes, int pacmanLives){
		Game repopulatedGameState = gameState;
		MCTSSimulationData currentSimulationData = simulationData;
		List<MCTSNode> currentVistedNodes = visitedNodes;
		int currentPacmanLives = pacmanLives;
		
		int score = 0;
		if(repopulatedGameState.getPacmanNumberOfLivesRemaining() < currentPacmanLives){
			score += currentSimulationData.getDeathReward();
		}		
		score += this.ghostRollout(repopulatedGameState, currentSimulationData);
		
		// backpropagate the new calculated score to all parent nodes, 
		// i.e. the score of a parent node is the sum of the scores of all his children
		for(MCTSNode node : currentVistedNodes){
			node.updateNodeScore(score);
		}
		return score;
	}
	
	/**
	 * Plays a game and calculates the score until the end of the simulation limit, the end of the level or a game over is reached.
	 * @param gameState the repopulated state of the game
	 * @param simulationData the simulation data
	 * @return the score of the playthrough
	 */
	private int ghostRollout(Game gameState, MCTSSimulationData simulationData){
		Game repopulatedGameState = gameState;
		MCTSSimulationData currentSimulationData = simulationData;
		int oldLevel = repopulatedGameState.getCurrentLevel();
		int i = 0;
		
		while((i++ < currentSimulationData.getMaximumSimulationCycles()) 
				&& repopulatedGameState.gameOver()
				&& (repopulatedGameState.getCurrentLevel() != oldLevel)){
			repopulatedGameState.advanceGame(currentSimulationData.getPacman().getMove(repopulatedGameState, 0),
					currentSimulationData.getGhosts().getMove(repopulatedGameState, 0));
			
		}
		return repopulatedGameState.getScore();
	}
	
	/**
	 * Returns the last moves performed by the ghosts. 
	 * @param move the last move of the current ghost
	 * @return the last moves of the ghosts
	 */
	private EnumMap<GHOST, MOVE> getLastGhostMoves(MOVE move){
		MOVE currentMove = move;
		GhostData[] currentGhostData = GameManager.getInstance().getGhostRelatedGhostData();
		EnumMap<GHOST, MOVE> lastGhostMoves = new EnumMap<GHOST,MOVE>(GHOST.class);
		for(GHOST ghost : GHOST.values()){
			MOVE lastMove = MOVE.NEUTRAL;
			if(ghost == this.getCurrentGhost()){
				lastMove = currentMove;
			}
			else{
				int ghostOrdinal = ghost.ordinal();
				lastMove = currentGhostData[ghostOrdinal].getLastMove();
			}
			lastGhostMoves.put(ghost, lastMove);
		}	
		return lastGhostMoves;
	}
	
	/**
	 * Fix for a pacman move which is null, 
	 * @param gameState the game state with the incorrect ghost moves
	 * @return the fixed game state
	 */
	private Game fixLastPacamanMove(Game gameState){
		Game repopulatedGameState = gameState;
		GameInfo currentGameInfo = repopulatedGameState.getPopulatedGameInfo();
		PacMan currentPacman = currentGameInfo.getPacman();
		if(currentPacman.lastMoveMade == null)
		{
			currentPacman.lastMoveMade = MOVE.NEUTRAL;
		}
		// update game accordingly
		return repopulatedGameState.getGameFromInfo(currentGameInfo);
	}
	
	/**
	 * Returns the ghost the simulation is running on.
	 * @return the ghost
	 */
	public GHOST getCurrentGhost() {
		return this.currentGhost;
	}
}
