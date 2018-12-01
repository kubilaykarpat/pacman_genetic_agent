package MCTS_core.Policies.Ghost;

import java.util.Collection;

import MCTS_core.Data.PacmanData;
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
 * Policy class which propagates behavior for moving closer to the last known position of pacman 
 * or away from pacman if ghost is edible. 
 * @author Max
 *
 */
public class SearchForPacmanPolicy implements IPolicy {

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
	private int closerToPacmanReward;
	
	/**
	 * TODO: API
	 */
	private int awayFromPacmanPenalty;
	
	/**
	 * 
	 * @param closerToPacmanReward
	 * @param awayFromPacmanPenalty
	 */
	public SearchForPacmanPolicy(int closerToPacmanReward, int awayFromPacmanPenalty){
		this.closerToPacmanReward = closerToPacmanReward;		
		this.awayFromPacmanPenalty = awayFromPacmanPenalty;
	}
	
	public SearchForPacmanPolicy(){
		this(DEFAULT_ClOSER_REWARD_VALUE, DEFAULT_AWAY_PENALTY_VALUE);		
	}
	
	private int getCloserToPacmanReward() {
		return this.closerToPacmanReward;
	}
	
	private int getAwayFromPacmanPenalty() {
		return this.awayFromPacmanPenalty;
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
			try {
				GhostMCTSSimulation currentGhostSimulation = (GhostMCTSSimulation) currentSimulation;
				GHOST currentGhost = currentGhostSimulation.getCurrentGhost();
				
				MOVE moveCloserToPacman = this.getMoveCloserToPacman(repopulatedGameState, currentGhost);
				MOVE moveAwayFromPacman = this.getMoveAwayFromPacman(repopulatedGameState, currentGhost);
				
				if(!repopulatedGameState.isGhostEdible(currentGhost)) {
					this.addBonusScoreToChild(children, moveCloserToPacman, this.getCloserToPacmanReward());
					this.addBonusScoreToChild(children, moveAwayFromPacman, -this.getAwayFromPacmanPenalty());
				}
				// your edible, so escape from pacman
				else{
					this.addBonusScoreToChild(children, moveCloserToPacman, -this.getAwayFromPacmanPenalty());
					this.addBonusScoreToChild(children, moveAwayFromPacman, this.getCloserToPacmanReward());
				}
			} catch (Exception ex) {
			} 
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
	private MOVE getMoveCloserToPacman(Game gameState, GHOST ghost){
		Game repopulatedGameState = gameState;
		GHOST currentGhost = ghost;	
		MOVE lastGhostMove = repopulatedGameState.getGhostLastMoveMade(currentGhost);
		int currentGhostNodeIndex = repopulatedGameState.getGhostCurrentNodeIndex(currentGhost);
		
		PacmanData currentPacmanData = GameManager.getInstance().getGhostRelatedPacmanData();
		int currentPacmanNodeIndex = currentPacmanData.getLastIndex();
	
		if(lastGhostMove == null){
			return MOVE.NEUTRAL;
		}else{
			return repopulatedGameState.getNextMoveTowardsTarget(currentGhostNodeIndex,
					currentPacmanNodeIndex, lastGhostMove, DM.PATH);
		}
	}
	
	/**
	 * TODO: API
	 * @param gameState
	 * @param ghost
	 * @return
	 */
	private MOVE getMoveAwayFromPacman(Game gameState, GHOST ghost){
		Game repopulatedGameState = gameState;
		GHOST currentGhost = ghost;	
		MOVE lastGhostMove = repopulatedGameState.getGhostLastMoveMade(currentGhost);
		int currentGhostNodeIndex = repopulatedGameState.getGhostCurrentNodeIndex(currentGhost);
		
		PacmanData currentPacmanData = GameManager.getInstance().getGhostRelatedPacmanData();
		int currentPacmanNodeIndex = currentPacmanData.getLastIndex();
		
		if(lastGhostMove == null){
			return MOVE.NEUTRAL;
		}else{
			return repopulatedGameState.getNextMoveAwayFromTarget(currentGhostNodeIndex,
					currentPacmanNodeIndex, lastGhostMove, DM.PATH);
		}
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
