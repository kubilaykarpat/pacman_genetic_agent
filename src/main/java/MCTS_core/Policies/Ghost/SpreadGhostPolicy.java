package MCTS_core.Policies.Ghost;

import java.util.Collection;

import MCTS_core.Data.GhostData;
import MCTS_core.MCTS.AbstractMCTSSimulation;
import MCTS_core.MCTS.GhostMCTSSimulation;
import MCTS_core.MCTS.MCTSNode;
import MCTS_core.Manager.GameManager;
import MCTS_core.Policies.IPolicy;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * Policy class which propagates behavior for moving away from nearby ghost. 
 * The ghosts should spread all over the level. 
 * @author Max
 *
 */
public class SpreadGhostPolicy implements IPolicy {

	/**
	 * TODO: API
	 */
	private static final int DEFAULT_ClOSER_REWARD_VALUE = 300;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_AWAY_PENALTY_VALUE = 200;
	
		/**
	 * TODO: API
	 */
	private int closerToGhostPenalty;
	
	/**
	 * TODO: API
	 */
	private int awayFromGhostReward;
	
	
	private int getAwayFromGhostReward() {
		return this.awayFromGhostReward;
	}

	private int getCloserToGhostPenalty() {
		return this.closerToGhostPenalty;
	}
	
	/**
	 * 
	 * @param closerToPacmanReward
	 * @param awayFromPacmanPenalty
	 */
	public SpreadGhostPolicy(int awayFromGhostReward, int closerToGhostPenalty){
		this.awayFromGhostReward = awayFromGhostReward;		
		this.closerToGhostPenalty = closerToGhostPenalty;
	}
		
	public SpreadGhostPolicy(){
		this(DEFAULT_ClOSER_REWARD_VALUE, DEFAULT_AWAY_PENALTY_VALUE);		
	}
	
	@Override
	public void evaluateSimulationReward(AbstractMCTSSimulation simulation) {
		AbstractMCTSSimulation currentSimulation = simulation;
		
		Collection<MCTSNode> children = currentSimulation.getChildren();
		if(children == null){
			return;
		}
		Game repopulatedGameState = currentSimulation.getRepopulatedGameState();
		if(currentSimulation instanceof GhostMCTSSimulation){
			GhostMCTSSimulation currentGhostSimulation = (GhostMCTSSimulation) currentSimulation;
			GHOST currentGhost = currentGhostSimulation.getCurrentGhost();
			
			MOVE moveCloserToNearestGhost = this.getMoveCloserToNearsetGhost(repopulatedGameState, currentGhost);
			MOVE moveAwayFromNearestGhost = this.getMoveAwayFromNearestGhost(repopulatedGameState, currentGhost);
			
			this.addBonusScoreToChild(children, moveCloserToNearestGhost, -this.getCloserToGhostPenalty());
			this.addBonusScoreToChild(children, moveAwayFromNearestGhost, this.getAwayFromGhostReward());
		}
		else{
			return;
		}
	}
	
	/**
	 * TODO: API
	 * @param gameState
	 * @param ghost
	 * @return
	 */
	private MOVE getMoveCloserToNearsetGhost(Game gameState, GHOST ghost){
		Game repopulatedGameState = gameState;
		GHOST currentGhost = ghost;	
		MOVE lastGhostMove = repopulatedGameState.getGhostLastMoveMade(currentGhost);
		int currentGhostNodeIndex = repopulatedGameState.getGhostCurrentNodeIndex(currentGhost);
		
		// get nearest ghost
		MOVE move = MOVE.NEUTRAL;
		int minDistance = Integer.MAX_VALUE;
		int closestGhostNodeIndex = -1;
		
		int ghostNodeIndex;
		int distanceToGhost;
		
		int i = 0;
		GhostData[] currentGhostData = GameManager.getInstance().getGhostRelatedGhostData();
		for(GHOST otherGhost : GHOST.values()){
			if(otherGhost != currentGhost){
				ghostNodeIndex = currentGhostData[i].getLastIndex();
				distanceToGhost = repopulatedGameState.getShortestPathDistance(currentGhostNodeIndex, ghostNodeIndex);
				if(distanceToGhost < minDistance){
					minDistance = distanceToGhost;
					closestGhostNodeIndex = ghostNodeIndex;
				}			
			}
			i++;
		}
		if(closestGhostNodeIndex > -1){
			move = repopulatedGameState.getNextMoveTowardsTarget(currentGhostNodeIndex,
					closestGhostNodeIndex, lastGhostMove, DM.PATH);
		}
		return move;
	}
	
	
	/**
	 * TODO: API
	 * @param gameState
	 * @param ghost
	 * @return
	 */
	private MOVE getMoveAwayFromNearestGhost(Game gameState, GHOST ghost){
		Game repopulatedGameState = gameState;
		GHOST currentGhost = ghost;	
		MOVE lastGhostMove = repopulatedGameState.getGhostLastMoveMade(currentGhost);
		int currentGhostNodeIndex = repopulatedGameState.getGhostCurrentNodeIndex(currentGhost);
		
		// get nearest ghost
		MOVE move = MOVE.NEUTRAL;
		int minDistance = Integer.MAX_VALUE;
		int closestGhostNodeIndex = -1;
		
		int ghostNodeIndex;
		int distanceToGhost;
		
		int i = 0;
		GhostData[] currentGhostData = GameManager.getInstance().getGhostRelatedGhostData();
		for(GHOST otherGhost : GHOST.values()){
			if(otherGhost != currentGhost){
				ghostNodeIndex = currentGhostData[i].getLastIndex();
				distanceToGhost = repopulatedGameState.getShortestPathDistance(currentGhostNodeIndex, ghostNodeIndex);
				if(distanceToGhost < minDistance){
					minDistance = distanceToGhost;
					closestGhostNodeIndex = ghostNodeIndex;
				}			
			}
			i++;
		}
		if(closestGhostNodeIndex > -1){
			move = repopulatedGameState.getNextMoveAwayFromTarget(currentGhostNodeIndex,
					closestGhostNodeIndex, lastGhostMove, DM.PATH);
		}
		return move;
	}
	
	
	/**
	 * TODO: API
	 * @param children
	 * @param move
	 * @param bonus
	 */
	private void addBonusScoreToChild(Collection<MCTSNode> children, MOVE move, int bonus){
		for(MCTSNode child : children){
			if(child.getMove() == move){
				child.addBonusScore(bonus);
				return;
			}
		}
	}
}
