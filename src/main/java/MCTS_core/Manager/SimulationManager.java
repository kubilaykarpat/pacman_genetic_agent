package MCTS_core.Manager;

import java.util.HashMap;
import java.util.Map;

import MCTS_core.MCTS.AbstractMCTSSimulation;
import MCTS_core.MCTS.MCTSNode;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * TODO: API
 * @author Max
 *
 */
public class SimulationManager {
	
	/**
	 * TODO: API
	 */
	private static SimulationManager simulationManager;	
	
	/**
	 * TODO: API
	 */	
	private Map<Integer, AbstractMCTSSimulation> simulations;
	
	/**
	 * TODO: API
	 */
	private SimulationManager(){	
		this.simulations = new HashMap<Integer, AbstractMCTSSimulation>();
	}
	
	/**
	 * 
	 * @return
	 */
	public static SimulationManager getInstance(){
		// lazy 
		if(simulationManager == null){
			simulationManager = new SimulationManager();
		}
		return simulationManager;
	}
	
	/**
	 * TODO: API
	 * @param simulation
	 */
	public boolean addSimulation(AbstractMCTSSimulation simulation){
		boolean result;
		int id = simulation.getIdentiy();
		if(!this.getSimulations().containsKey(id)){
		
			this.getSimulations().put(id, simulation);
			result = true;
		}
		else{
			result = false;
		}		
		return result;
	}
	
	/**
	 * TODO: API
	 * @param id
	 * @return
	 */
	public boolean removeSimulation(int id){
		boolean result;
		if(this.getSimulations().containsKey(id)){
		
			this.getSimulations().remove(id);
			result = true;
		}
		else{
			result = false;
		}		
		return result;
	}
	
	/**
	 * 
	 * @param id
	 * @param gameState
	 * @return
	 */
	public boolean updateSimulation(int id, AbstractMCTSSimulation simulation){
		boolean result;
		if(this.getSimulations().containsKey(id)){
			this.getSimulations().replace(id, simulation);	
			result = true;
		}
		else{
			result = false;
		}		
		return result;
	}
	
	/**
	 * TODO: API
	 * @param id
	 * @param gameState
	 * @return
	 */
	public boolean updateSimulationGameState(int id, Game gameState){
		boolean result;
		if(this.getSimulations().containsKey(id)){
			AbstractMCTSSimulation simulation = this.getSimulations().get(id);
			simulation.setRepopulatedGameState(gameState);			
			result = true;
		}
		else{
			result = false;
		}		
		return result;
	}
	
	/**
	 * TODO: API
	 * @param id
	 * @param root
	 * @return
	 */
	public boolean updateSimulationRoot(int id, MCTSNode root){
		boolean result;
		if(this.getSimulations().containsKey(id)){
			AbstractMCTSSimulation simulation = this.getSimulations().get(id);
			simulation.setRoot(root);			
			result = true;
		}
		else{
			result = false;
		}		
		return result;
	}
	

	/**
	 * Returns the best node from simulation.  
	 * @param id the id of the simulation
	 * @return the best node available
	 */
	public MCTSNode getBestAvailableNode(int id){
		MCTSNode result;
		if(this.getSimulations().containsKey(id)){
			result = this.getSimulations().get(id).getBestAvailableNode();
		}
		else{
			result = null;
		}
		return result;
	}
	
	/**
	 * Returns the best move from simulation. 
	 * @param id the id of the simulation
	 * @return the best move available
	 */
	public MOVE getBestAvailableMove(int id){
		MOVE result;
		if(this.getSimulations().containsKey(id)){
			result = this.getSimulations().get(id).getBestAvailableMove();
		}
		else{
			result = MOVE.NEUTRAL;
		}
		return result;
	}
	
	/**
	 * Run the simulation.
	 * @param id the id of the simulation	 
	 * @return true if simulation was successful, otherwise false 
	 */
	public boolean runSimulation(int id){
		boolean result;
		if(this.getSimulations().containsKey(id)){
			this.getSimulations().get(id).runSimulation();
			result = true;
		}
		else{
			result = false;
		}
		return result;
	}
	
	/**
	 * TODO: API
	 * @param id
	 * @param gameState
	 * @param edibleGhostScore
	 * @return
	 */
	public boolean isDecisionNode(int id, int edibleGhostScore){
		boolean result;
		if(this.getSimulations().containsKey(id)){
			result = this.getSimulations().get(id).isDecisionNode(edibleGhostScore);
		}
		else{
			result = false;
		}
		return result;	
	}	
	
	/**
	 * Performs a move in the simulation.
	 * @param id the id of the simulation 
	 * @param gameState the current state of the game
	 * @param move the move to perform
	 * @return true if performing the move was successful, otherwise false
	 */
	public boolean move(int id, Game gameState, MOVE move){
		boolean result;
		if(this.getSimulations().containsKey(id)){
			this.getSimulations().get(id).move(gameState, move);
			result = true;
		}
		else{
			result = false;
		}
		return result;	
	}
	
	/**
	 * Runs the simulation with the additional policies of the simulation data.
	 * @param id the id of the simulation
	 */
	public boolean runAdditionalPolicies(int id){
		boolean result;
		if(this.getSimulations().containsKey(id)){
			this.getSimulations().get(id).runAdditionalPolicies();
			result = true;
		}
		else{
			result = false;
		}
		return result;	
	}
	
	
	private Map<Integer, AbstractMCTSSimulation> getSimulations() {
		return simulations;
	}
	
	/**
	 * Indicates if ghost ran into a wall based on the previous direction.
	 * @param gameState the current state of the game
	 * @param ghost the ghost who ran
	 * @return true if ghost ran into a wall, otherwise false
	 */
	public boolean isGhostRunAgainstWall(Game gameState, GHOST ghost){		
		Game currentGameState = gameState;
		GHOST currentGhost = ghost;
		int currentGhostNodeIndex = currentGameState.getGhostCurrentNodeIndex(currentGhost);
		
		MOVE lastGhostMove = currentGameState.getGhostLastMoveMade(currentGhost);
		MOVE possibleGhostMoves[] = currentGameState.getPossibleMoves(currentGhostNodeIndex);		
		for(int i = 0; i <possibleGhostMoves.length; i++){
			if(possibleGhostMoves[i] == lastGhostMove){
				return false;
			}
		}		
		return true;
	}
}
