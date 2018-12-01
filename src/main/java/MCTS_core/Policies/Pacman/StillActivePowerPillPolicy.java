package MCTS_core.Policies.Pacman;

import java.util.Collection;

import MCTS_core.MCTS.AbstractMCTSSimulation;
import MCTS_core.MCTS.MCTSNode;
import MCTS_core.Policies.IPolicy;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

/**
 * Policy class which penalizes behavior for eating a power pill if there is still one active. 
 * @author Max
 *
 */
public class StillActivePowerPillPolicy implements IPolicy {

	/**
	 * TODO: API
	 */
	private static final int DEFAULT_PENALTY_VALUE = 600;
	
	/**
	 * TODO: API
	 */
	private int penalty;
	
	/**
	 * 
	 * @param minimumDistance
	 * @param penalty
	 */
	public StillActivePowerPillPolicy(int penalty){
		this.penalty = penalty;		
	}
	
	/**
	 * 
	 */
	public StillActivePowerPillPolicy(){
		this(DEFAULT_PENALTY_VALUE);
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
		
		if(this.isPowerPillActive(simulation.getRepopulatedGameState())){
			for(MCTSNode child : children){
				if(child.hasPowerPillEaten()){
					child.addBonusScore(- this.getPenalty());
				}
			}
		}		
	}
	
	/**
	 * TODO: API
	 * @param gameState
	 * @return
	 */
	private boolean isPowerPillActive(Game gameState){
		Game repopulatedGameState = gameState;
		int edibleGhostTimeCount = 0;
		for(GHOST ghost : GHOST.values()){
			edibleGhostTimeCount += repopulatedGameState.getGhostEdibleTime(ghost);
		}
		return (edibleGhostTimeCount > 0);
	}
}
