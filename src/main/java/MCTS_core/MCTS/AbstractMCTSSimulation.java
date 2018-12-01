package MCTS_core.MCTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import MCTS_core.Data.MCTSSimulationData;
import MCTS_core.Policies.IPolicy;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * The abstract class for monte-carlo-tree-search simulations.
 * @author Max
 *
 */
public abstract class AbstractMCTSSimulation {

	/**
	 * TODO: API
	 */
	private MCTSSimulationData mctsSimulationData;
	
	/**
	 * TODO: API
	 */
	private MCTSNode root;	
	
	/**
	 * TODO: API
	 */
	private Set<Integer> activePowerPills;
	
	/**
	 * The new populated game the simulation is working on.
	 */
	private Game repopulatedGameState;
	
	/**
	 * TODO: API
	 */
	private Stack<Game> repopulatedGameStates;
	
	/**
	 * TODO: API
	 * @param gameState
	 * @param mctsSimulationData
	 */
	public AbstractMCTSSimulation(Game gameState, MCTSSimulationData mctsSimulationData){
		this.repopulatedGameState = gameState;
		this.mctsSimulationData = mctsSimulationData;
		
		this.repopulatedGameStates = new Stack<Game>();
		this.root = new MCTSNode();		
		this.activePowerPills = new HashSet<Integer>();		
	}	
	
	public int getIdentiy(){
		return this.getClass().getName().hashCode();
	}	
	
	protected MCTSSimulationData getMCTSSimulationData() {
		return this.mctsSimulationData;
	}

	protected MCTSNode getRoot() {
		return this.root;
	}

	public void setRoot(MCTSNode root) {
		this.root = root;
	}

	public Game getRepopulatedGameState() {
		return this.repopulatedGameState;
	}

	/**
	 * Sets a new game state for the simulation.
	 * This function is only called from a controller class. 
	 * @param repopulatedGameState the new game state to set
	 */
	public void setRepopulatedGameState(Game repopulatedGameState) {
		this.repopulatedGameState = repopulatedGameState;
	}
	
	protected Set<Integer> getActivePowerPills() {
		return this.activePowerPills;
	}

	private Stack<Game> getRepopulatedGameStates() {
		return this.repopulatedGameStates;
	}

	/**
	 * Returns the best node available by selection the one with the highest average score.  
	 * @return the best node available
	 */
	public MCTSNode getBestAvailableNode() {
		MCTSNode bestAvailableNode = null;
		MCTSNode searchNode = this.getRoot();
		double currentScore;
		double currentMaxScore = Double.NEGATIVE_INFINITY;
		if (searchNode.getChildrenCollection() != null) {
			for (MCTSNode child : searchNode.getChildrenCollection()) {
				currentScore = child.getAverageScore();
				// choose the node with the highest score, a move we can perform
				// and it's not the opposite move
				if ((currentScore > currentMaxScore)) {
					currentMaxScore = currentScore;
					bestAvailableNode = child;
				}
			}
		}
		return bestAvailableNode;
	}
	
	/**
	 * Returns the nodes, i.e. the moves, that can be made.
	 * @return the nodes which can be moved to 
	 */
	public Collection<MCTSNode> getChildren(){
		Collection<MCTSNode> children = this.getRoot().getChildrenCollection();
		if(children != null){
			return children;
		}
		else{
			return new ArrayList<MCTSNode>();
		}	
	}
	
	/**
	 * Returns the best move available. 
	 * @return the best move available
	 */
	public MOVE getBestAvailableMove(){
		Map<MOVE, Double> moveToScoreMap = new HashMap<MOVE, Double>();
		// sum score over all childrens children
		for(MCTSNode rootChild : this.getRoot().getChildrenCollection()){
			if(rootChild.isLeaf()){
				continue;
			}
			
			for(MCTSNode child : rootChild.getChildrenCollection()){
				MOVE childMove = child.getMove();
				
				double averageScore = child.getAverageScore();
				Double totalScore = moveToScoreMap.get(childMove);
				if(totalScore == null){
					moveToScoreMap.put(childMove, averageScore);
				}
				else{
					moveToScoreMap.put(childMove, totalScore + averageScore);
				}
			}	
		}
		// get the move with the best score 
		double bestScore = Double.NEGATIVE_INFINITY;
		MOVE bestAvailableMove = MOVE.NEUTRAL;
		for(Map.Entry<MOVE, Double> mapEntry : moveToScoreMap.entrySet()){
			double value = mapEntry.getValue();
			if(value > bestScore){
				bestScore = value;
				bestAvailableMove = mapEntry.getKey();		
			}
		}		
		return bestAvailableMove;
	}
	
	/**
	 * TODO: API
	 */
	public void runAdditionalPolicies(){
		List<IPolicy> policies = this.getMCTSSimulationData().getPolicies();
		if(policies != null){
			for(IPolicy policy : policies){
				policy.evaluateSimulationReward(this);
			}
		}
	}
		
	/**
	 * Run the simulation.
	 */
	public abstract void runSimulation();
		
	/**
	 * TODO: API
	 * @param gameState
	 * @param edibleGhostScore
	 * @return
	 */
	public abstract boolean isDecisionNode(int edibleGhostScore);
		
	/**
	 * TODO: API
	 * @param move
	 */
	public abstract void move(Game gameState, MOVE move);
	
	/**
	 * Indicates if the simulated object ran into a wall based on the previous direction.
	 * @param gameState the repopulated state of the game
	 * @return true if simulated object ran into a wall, otherwise false
	 */
	protected abstract boolean isRunningAgainstWall(Game gameState);	
	
	/**
	 * Advances the game to the next node, i.e. the next decision point. 
	 * @param gameState the repopulated state of the game 
	 */
	protected abstract void advanceGame(Game gameState);
	
	
	/**
	 * Pushes the currently constructed game states onto the stack.
	 * @param gameState the repopulated state of the game
	 * @return the pushed constructed game state
	 */
	public Game pushGameState(Game gameState){
		Game repopulatedGameState = gameState;
		this.getRepopulatedGameStates().push(repopulatedGameState);
		return repopulatedGameState;
	}

	/**
	 * Pops the first repopulated game states from the stack.
	 */
	public void popGameState(){
		Game popRepopulatedGameState = this.getRepopulatedGameStates().pop();
		this.setRepopulatedGameState(popRepopulatedGameState);		
	}
	
	/**
	 * TODO: API
	 * @param currentActivePowerPillIndices
	 */
	protected void updateActivePowerPills(List<Integer> currentActivePowerPillIndices){
		for(int powerPillIndex : currentActivePowerPillIndices){
			this.getActivePowerPills().add(powerPillIndex);
		}
	}
	
	/**
	 * TODO: API
	 * @param gameState
	 * @param edibleGhostScore
	 * @return
	 */
	protected boolean wasGhostEaten(Game gameState, int edibleGhostScore){
		Game repopulatedGameState = gameState;
		int currentEdibleGhostScore = edibleGhostScore;		
		return (repopulatedGameState.getGhostCurrentEdibleScore() != currentEdibleGhostScore);
	}
}
