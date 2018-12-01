package MCTS_core.Policies.Pacman;

import java.util.Collection;

import MCTS_core.Data.GhostData;
import MCTS_core.MCTS.AbstractMCTSSimulation;
import MCTS_core.MCTS.MCTSNode;
import MCTS_core.Manager.GameManager;
import MCTS_core.Policies.IPolicy;
import pacman.game.Game;
import pacman.game.Constants;
import pacman.game.Constants.DM;

/**
 * Policy class which penalizes behavior for eating a power pill by far away ghosts.  
 * @author Max
 *
 */
public class AwayGhostsPowerPillPolicy implements IPolicy {

	/**
	 * TODO: API
	 */
	private static final double DEFAULT_MINIMUM_DISTANCE = 20;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_PENALTY_VALUE = 300;	
	
	/**
	 * TODO: API
	 */
	private double minimumDistance;
	
	/**
	 * TODO: API
	 */
	private int penalty;

	/**
	 * 
	 * @param minimumDistance
	 * @param penalty
	 */
	public AwayGhostsPowerPillPolicy(double minimumDistance, int penalty){
		this.minimumDistance = minimumDistance;
		this.penalty = penalty;		
	}
	
	/**
	 * 
	 */
	public AwayGhostsPowerPillPolicy(){
		this(DEFAULT_MINIMUM_DISTANCE, DEFAULT_PENALTY_VALUE);
	}
	
	
	private double getMinimumDistance() {
		return this.minimumDistance;
	}

	private int getPenalty() {
		return this.penalty;
	}

	@Override
	public void evaluateSimulationReward(AbstractMCTSSimulation simulation) {
		AbstractMCTSSimulation currentSimulation = simulation;
		Collection<MCTSNode> children = currentSimulation.getChildren();

		if(children == null){
			return;
		}
				
		if(this.getDistanceToNearsetGhost(currentSimulation.getRepopulatedGameState())
				< this.getMinimumDistance()){
			for(MCTSNode child : children){
				if(child.hasPowerPillEaten()){
					child.addBonusScore(- this.getPenalty());
				}
			}
		}		
	}	
	
	/**
	 * TODO: API
	 * @return
	 */
	private double getDistanceToNearsetGhost(Game gameStatus){
		Game repopulatedGameState = gameStatus;
		GhostData[] currentGhostData = GameManager.getInstance().getPacmanRelatedGhostData();
		int[] ghostNodeIndices = new int[Constants.NUM_GHOSTS];
		
		int i = 0;
		for(GhostData ghost : currentGhostData){
			ghostNodeIndices[i] = ghost.getLastIndex();			
			i++;
		}
		int currentPacmanNodeIndex = repopulatedGameState.getPacmanCurrentNodeIndex();
		int closestNodeIndex = repopulatedGameState.getClosestNodeIndexFromNodeIndex(currentPacmanNodeIndex,
				ghostNodeIndices, DM.PATH);
		return repopulatedGameState.getDistance(currentPacmanNodeIndex, closestNodeIndex, DM.PATH);
	}
}
