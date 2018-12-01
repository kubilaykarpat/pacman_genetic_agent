package MCTS_core.Policies.Pacman;

import java.util.Collection;

import MCTS_core.Data.GhostData;
import MCTS_core.MCTS.AbstractMCTSSimulation;
import MCTS_core.MCTS.MCTSNode;
import MCTS_core.Manager.GameManager;
import MCTS_core.Policies.IPolicy;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Policy class which propagates behavior for moving closer to a known pill or power pill.  
 * @author Max
 *
 */
public class SearchForPillPolicy implements IPolicy {

	/**
	 * TODO: API
	 */
	private static final int DEFAULT_GHOST_SCORE_VALUE = 400;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_PILL_SCORE_VALUE = 200;
	
	/**
	 * TODO: API
	 */
	private int ghostScore;
	
	/**
	 * TODO: API
	 */
	private int pillScore;
	
	
	/**
	 * 
	 * @param ghostScore
	 * @param pillScore
	 */
	public SearchForPillPolicy(int ghostScore, int pillScore){
		this.ghostScore = ghostScore;		
		this.pillScore = pillScore;
	}
	
	public SearchForPillPolicy(){
		this(DEFAULT_GHOST_SCORE_VALUE, DEFAULT_PILL_SCORE_VALUE);
	}
	
	private int getGhostScore() {
		return this.ghostScore;
	}

	private int getPillScore() {
		return this.pillScore;
	}

	@Override
	public void evaluateSimulationReward(AbstractMCTSSimulation simulation) {
		AbstractMCTSSimulation currentSimulation = simulation;
		
		Collection<MCTSNode> children = currentSimulation.getChildren();
		if(children == null){
			return;
		}
		
		Game repopulatedGameState = currentSimulation.getRepopulatedGameState();
		MOVE moveToNearestGhost = this.getMoveCloserToEdibleGhost(repopulatedGameState);
		if(moveToNearestGhost != MOVE.NEUTRAL){
			this.addBonusScoreToChild(children, moveToNearestGhost, this.getGhostScore());
		}
		
		MOVE moveToNearestPill = this.getMoveToNearestPill(repopulatedGameState);
		this.addBonusScoreToChild(children, moveToNearestPill, this.getPillScore());
	}

	/**
	 * TODO: API
	 * @param gameState
	 * @return
	 */
	private MOVE getMoveCloserToEdibleGhost(Game gameState){
		Game repopulatedGameState = gameState;
		MOVE lastPacmanMove = repopulatedGameState.getPacmanLastMoveMade();
		int currentPacmanNodeIndex = repopulatedGameState.getPacmanCurrentNodeIndex();
		
		MOVE move = MOVE.NEUTRAL;
		int minDistance = Integer.MAX_VALUE;
		int closestGhostNodeIndex = -1;
		
		int ghostNodeIndex;
		int distanceToGhost;
		
		int i = 0;
		GhostData[] currentGhostData = GameManager.getInstance().getPacmanRelatedGhostData();
		for(GHOST ghost : GHOST.values()){
			if(repopulatedGameState.getGhostEdibleTime(ghost) > 0){
				ghostNodeIndex = currentGhostData[i].getLastIndex();
				distanceToGhost = repopulatedGameState.getShortestPathDistance(currentPacmanNodeIndex, ghostNodeIndex);
				if(distanceToGhost < minDistance){
					minDistance = distanceToGhost;
					closestGhostNodeIndex = ghostNodeIndex;
				}
			}
			i++;
		}
		
		if(closestGhostNodeIndex > -1){
			move = repopulatedGameState.getNextMoveTowardsTarget(currentPacmanNodeIndex,
					closestGhostNodeIndex, lastPacmanMove, DM.PATH);
		}
		return move;
	}

	/**
	 * TODO: API
	 * @param gameState
	 * @return
	 */
	private MOVE getMoveToNearestPill(Game gameState){
		Game repopulatedGameState = gameState;
		int currentPacmanNodeIndex = gameState.getPacmanCurrentNodeIndex();
		MOVE move = MOVE.NEUTRAL;
		
		int[] activePillNodeIndices = repopulatedGameState.getActivePillsIndices();
		int closestPillNodeIndex = repopulatedGameState.getClosestNodeIndexFromNodeIndex(currentPacmanNodeIndex,
				activePillNodeIndices, DM.PATH);
		move = repopulatedGameState.getNextMoveTowardsTarget(currentPacmanNodeIndex, closestPillNodeIndex, DM.PATH);
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
