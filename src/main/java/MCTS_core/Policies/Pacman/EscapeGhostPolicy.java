package MCTS_core.Policies.Pacman;

import java.util.Collection;

import MCTS_core.Data.GhostData;
import MCTS_core.MCTS.AbstractMCTSSimulation;
import MCTS_core.MCTS.MCTSNode;
import MCTS_core.Manager.GameManager;
import MCTS_core.Policies.IPolicy;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * Policy class which propagates behavior for moving away from a ghost or closer if he is edible. 
 * @author Max
 *
 */
public class EscapeGhostPolicy implements IPolicy {

	/**
	 * TODO: API
	 */
	private static final int DEFAULT_CLOSER_PENALTY_VALUE = 600;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_AWAY_REWARD_VALUE = 600;
	
	/**
	 * TODO: API
	 */
	private int closerToGhostPenalty;
	
	/**
	 * TODO: API
	 */
	private int awayFromGhostReward;
	
	/**
	 * Default constructor
	 */
	public EscapeGhostPolicy(){
		this(DEFAULT_CLOSER_PENALTY_VALUE, DEFAULT_AWAY_REWARD_VALUE);
	}
	
	/**
	 * 
	 * @param penalty
	 * @param reward
	 */
	public EscapeGhostPolicy(int closerToGhostPenalty, int awayFromGhostReward){
		this.closerToGhostPenalty = closerToGhostPenalty;
		this.awayFromGhostReward = awayFromGhostReward;
				 
	}
	
	private int getCloserToGhostPenalty() {
		return this.closerToGhostPenalty;
	}
	
	private int getAwayFromGhostReward() {
		return this.awayFromGhostReward;
	}

	@Override
	public void evaluateSimulationReward(AbstractMCTSSimulation simulation) {
		AbstractMCTSSimulation currentSimulation = simulation;
		Collection<MCTSNode> children = currentSimulation.getChildren();

		if(children == null){
			return;
		}
		
		Game repopulatedGameState = currentSimulation.getRepopulatedGameState();
		MOVE moveAwakFromGhost = this.getMoveAwakFromGhosts(repopulatedGameState);
		MOVE moveCloserToGhost = this.getMoveCloserToGhost(repopulatedGameState);
		
		
		if(moveAwakFromGhost != MOVE.NEUTRAL){
			this.addBonusScoreToChild(children, moveAwakFromGhost, this.getAwayFromGhostReward());
		}		
		
		if(moveCloserToGhost != MOVE.NEUTRAL){
			this.addBonusScoreToChild(children, moveCloserToGhost, -this.getCloserToGhostPenalty());
		}		
	}
	
	/**
	 * TODO: API
	 * @param gameState
	 * @return
	 */
	private MOVE getMoveAwakFromGhosts(Game gameState){
		Game repopulatedGameState = gameState;
		int currentPacmanNodeIndex = repopulatedGameState.getPacmanCurrentNodeIndex();
		MOVE lastPacmanMove = repopulatedGameState.getPacmanLastMoveMade();
		GhostData[] currentGhostData = GameManager.getInstance().getPacmanRelatedGhostData();
		
		MOVE move = MOVE.NEUTRAL;	
		int ghostNodeIndex;
		
		int minNotEdibleGhostDistance = Integer.MAX_VALUE;	
		int closestNotEdibleGhostNodeIndex = -1;			
		int distanceToNotEdibleGhost;
		
		boolean isGhostEdible = false;
		int minEdibleGhostDistance = Integer.MAX_VALUE;
		int closestEdibleGhostNodeIndex = -1;			
		int distanceToEdibleGhost;
		
		int i = 0;
		for(GHOST ghost : GHOST.values()){
			// search for the nearest ghost
			ghostNodeIndex = currentGhostData[i].getLastIndex();
			if(repopulatedGameState.getGhostEdibleTime(ghost) > 0){
				isGhostEdible = true;			
				
				distanceToEdibleGhost = repopulatedGameState.getShortestPathDistance(currentPacmanNodeIndex, ghostNodeIndex);
				if(distanceToEdibleGhost < minEdibleGhostDistance){
					minEdibleGhostDistance = distanceToEdibleGhost;
					closestEdibleGhostNodeIndex = ghostNodeIndex;
				}
				
			}else{
				distanceToNotEdibleGhost = repopulatedGameState.getShortestPathDistance(currentPacmanNodeIndex, ghostNodeIndex);
				if(distanceToNotEdibleGhost < minNotEdibleGhostDistance){
					minNotEdibleGhostDistance = distanceToNotEdibleGhost;
					closestNotEdibleGhostNodeIndex = ghostNodeIndex;
				}
			}
			i++;
		}
		
		if(closestNotEdibleGhostNodeIndex > -1){
			// escape the ghost if he is not edible ...
			if(!isGhostEdible){
				move = repopulatedGameState.getNextMoveAwayFromTarget(currentPacmanNodeIndex,
						closestNotEdibleGhostNodeIndex, lastPacmanMove, DM.PATH);
			}
			// but if he is, than move closer towards him
			else{
				move = repopulatedGameState.getNextMoveTowardsTarget(currentPacmanNodeIndex,
						closestEdibleGhostNodeIndex, lastPacmanMove, DM.PATH);
			}
		}		
		return move;
	}
	
	/**
	 * TODO: API
	 * @param gameState
	 * @return
	 */
	private MOVE getMoveCloserToGhost(Game gameState){
		Game repopulatedGameState = gameState;
		MOVE lastPacmanMove = repopulatedGameState.getPacmanLastMoveMade();
		int currentPacmanNodeIndex = repopulatedGameState.getPacmanCurrentNodeIndex();
		GhostData[] currentGhostData = GameManager.getInstance().getPacmanRelatedGhostData();
				
		MOVE move = MOVE.NEUTRAL;	
		int ghostNodeIndex;
		
		int minNotEdibleGhostDistance = Integer.MAX_VALUE;	
		int closestNotEdibleGhostNodeIndex = -1;			
		int distanceToNotEdibleGhost;
		
		boolean isGhostEdible = false;
		int minEdibleGhostDistance = Integer.MAX_VALUE;
		int closestEdibleGhostNodeIndex = -1;			
		int distanceToEdibleGhost;
		
		int i = 0;
		for(GHOST ghost : GHOST.values()){
			// search for the nearest ghost
			ghostNodeIndex = currentGhostData[i].getLastIndex();
			if(repopulatedGameState.getGhostEdibleTime(ghost) > 0){
				isGhostEdible = true;			
				
				distanceToEdibleGhost = repopulatedGameState.getShortestPathDistance(currentPacmanNodeIndex, ghostNodeIndex);
				if(distanceToEdibleGhost < minEdibleGhostDistance){
					minEdibleGhostDistance = distanceToEdibleGhost;
					closestEdibleGhostNodeIndex = ghostNodeIndex;
				}
				
			}else{
				distanceToNotEdibleGhost = repopulatedGameState.getShortestPathDistance(currentPacmanNodeIndex, ghostNodeIndex);
				if(distanceToNotEdibleGhost < minNotEdibleGhostDistance){
					minNotEdibleGhostDistance = distanceToNotEdibleGhost;
					closestNotEdibleGhostNodeIndex = ghostNodeIndex;
				}
			}
			i++;
		}
		
		if(closestNotEdibleGhostNodeIndex > -1){
			if(!isGhostEdible){
				move = repopulatedGameState.getNextMoveTowardsTarget(currentPacmanNodeIndex,
						closestNotEdibleGhostNodeIndex, lastPacmanMove, DM.PATH);
			}
			else{
				move = repopulatedGameState.getNextMoveAwayFromTarget(currentPacmanNodeIndex,
						closestEdibleGhostNodeIndex, lastPacmanMove, DM.PATH);
			}
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
