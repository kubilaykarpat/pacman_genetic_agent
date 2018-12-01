package MCTS_core.MCTS;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * 
 * @author Max
 *
 */
public class MCTSNode {

	/**
	 * TODO: API
	 */
	private MCTSNode parent;
	
	/**
	 * TODO: API
	 */
	private Map<Object, MCTSNode> children;
	
	/**
	 * TODO: API
	 */
	private int id;
	
	/**
	 * TODO: API
	 */
	private int totalScore;
	
	/**
	 * TODO: API
	 */
	private int bonusScore;
	
	/**
	 * TODO: API
	 */
	private int visitCount;
	
	/**
	 * TODO: API
	 */
	private double meanScore;	
	
	/**
	 * TODO: API
	 */
	private long sumOfSquares;
	
	/**
	 * TODO: API
	 */
	private MOVE move;
	
	/**
	 * TODO: API
	 */
	private boolean havePillsEaten;
	
	/**
	 * TODO: API
	 */
	private boolean hasPowerPillEaten;
	
	/**
	 * TODO: API
	 */
	private int[] lastSeenGhostPositionIndices;	
	
	/**
	 * TODO: API
	 */
	private int lastSeenPacmanPositionIndex;
	
	/**
	 * Default constructor
	 */
	public MCTSNode(){
		this(null,  MOVE.NEUTRAL);	
	}	
	
	/**
	 * TODO: API
	 * @param parent
	 * @param move
	 */
	public MCTSNode(MCTSNode parent, MOVE move){
		this(parent, move, -1, 0, 0, 0);
	}
	
	/**
	 * TODO: API
	 * @param parent
	 * @param move
	 * @param id
	 * @param totalScore
	 * @param bonusScore
	 * @param visitCount
	 */
	public MCTSNode(MCTSNode parent, MOVE move, 
			int id, int totalScore, int bonusScore, int visitCount){
		this.setParent(parent);
		this.setMove(move);
		
		this.setId(id);
		this.setTotalScore(totalScore);
		this.setBonusScore(bonusScore);
		this.setVisitCount(visitCount);
		
		this.lastSeenPacmanPositionIndex = -1;
		this.lastSeenGhostPositionIndices = new int[] { -1, -1, -1, -1};		
	}
	
	/**
	 * TODO: API
	 * @return
	 */
	public MCTSNode getParent() {
		return this.parent;
	}

	private void setParent(MCTSNode parent) {
		this.parent = parent;
	}
	
	private Map<Object, MCTSNode> getChildren() {
		return this.children;
	}

	private void setChildren(Map<Object, MCTSNode> children) {
		this.children = children;
	}
	
	/**
	 * TODO: API
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	private void setId(int id) {
		this.id = id;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public int getTotalScore() {
		return this.totalScore;
	}

	private void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	private int getBonusScore() {
		return this.bonusScore;
	}

	private void setBonusScore(int bonusScore) {
		this.bonusScore = bonusScore;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public int getVisitCount() {
		return this.visitCount;
	}

	private void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	private double getMeanScore() {
		return this.meanScore;
	}

	private void setMeanScore(double meanScore) {
		this.meanScore = meanScore;
	}	
	
	private long getSumOfSquares() {
		return this.sumOfSquares;
	}

	private void setSumOfSquares(long sumOfSquares) {
		this.sumOfSquares = sumOfSquares;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public boolean havePillsEaten() {
		return this.havePillsEaten;
	}

	/**
	 * TODO: API
	 * @param hasPillEaten
	 */
	public void setHavePillsEaten(boolean havePillsEaten) {
		this.havePillsEaten = havePillsEaten;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public boolean hasPowerPillEaten() {
		return this.hasPowerPillEaten;
	}

	/**
	 * TODO: API
	 * @param hasPowerPillEaten
	 */
	public void setHasPowerPillEaten(boolean hasPowerPillEaten) {
		this.hasPowerPillEaten = hasPowerPillEaten;
	}
	
	/**
	 * TODO: API
	 * @return
	 */
	public MOVE getMove() {
		return this.move;
	}
	
	private void setMove(MOVE move) {
		this.move = move;
	}
	
	/**
	 * TODO: API
	 * @return
	 */
	public int[] getLastSeenGhostPositionIndices() {
		return this.lastSeenGhostPositionIndices;
	}

	/**
	 * TODO: API
	 * @param lastSeenGhostPositionIndices
	 */
	public void setLastSeenGhostPositionIndices(int[] lastSeenGhostPositionIndices) {
		this.lastSeenGhostPositionIndices = lastSeenGhostPositionIndices;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public int getLastSeenPacmanPositionIndex() {
		return this.lastSeenPacmanPositionIndex;
	}

	/**
	 * TODO: API
	 * @param lastSeenPacmanPositionIndex
	 */
	public void setLastSeenPacmanPositionIndex(int lastSeenPacmanPositionIndex) {
		this.lastSeenPacmanPositionIndex = lastSeenPacmanPositionIndex;
	}

	/**
	 * TODO: API
	 * @return
	 */
	public Collection<MCTSNode> getChildrenCollection() {		
		if(this.getChildren() == null){
			return null;
		}		
		return this.getChildren().values();
	}
	
	/**
	 * TODO: API
	 * @param childMove
	 * @return
	 */
	public MCTSNode getChild(MOVE childMove){
		if(this.getChildren() == null){
			return null;
		}
		return this.getChildren().get(childMove);
	}
	
	/**
	 * TODO: API
	 * @return
	 */
	public double getAverageScore(){
		if(this.getVisitCount() > 0){			
			return this.getMeanScore() + this.getBonusScore();
		}
		else{
			return this.getBonusScore();
		}
	}
	
	/**
	 * TODO: API
	 * @return
	 */
	public double getVariance(){
		if(this.getVisitCount() > 1){
			return this.getSumOfSquares() / (this.getVisitCount() - 1);
		}
		else{
			return 0;
		}
	}	
	
	/**
	 * Expands the node (which is always a leaf) by adding children accordingly to possible moves
	 * which can be performed at the current node. 
	 * @param gameState the current state of the game
	 * @param currenNodeIndex the index of the current game node
	 */
	public void expandNode(Game gameState, int currenNodeIndex){
		Game constructedGameState = gameState;
		
		MOVE[] possibleMoves = constructedGameState.getPossibleMoves(currenNodeIndex);
		int movesLength = possibleMoves.length;
		this.setChildren(new HashMap<Object, MCTSNode>(movesLength));
		
		for(int i = 0; i <movesLength; i++){
			MOVE currentMove = possibleMoves[i];
			this.getChildren().put(currentMove, new MCTSNode(this, currentMove));
			// FIXME: maybe add the current positions of the ghosts to the new created node 
		}		
	}
	
	/**
	 * TODO: API
	 * @param score
	 */
	public void updateNodeScore(int score){
		int newTotalScore = this.getTotalScore();
		int newVisitCount = this.getVisitCount();
		
		newTotalScore +=score;
		newVisitCount++;
		
		if(newVisitCount == 1){
			this.setMeanScore(score);
			this.setSumOfSquares(0);
		}else{
			double lastMeanScore = this.getMeanScore();
			double newMeanScore =  this.getMeanScore();
			newMeanScore += (score - lastMeanScore) / newVisitCount;
						
			long newSumOfSquares = this.getSumOfSquares();
			newSumOfSquares += (score - lastMeanScore) *( score - newMeanScore);
			
			this.setMeanScore(newMeanScore);
			this.setSumOfSquares(newSumOfSquares);
		}	
		
		this.setTotalScore(newTotalScore);
		this.setVisitCount(newVisitCount);
	}

	/**
	 * TODO: API
	 * @param bonusScore
	 */
	public void addBonusScore(int bonusScore){
		int newBonusScore = this.getBonusScore();
		newBonusScore +=bonusScore;
		this.setBonusScore(newBonusScore);
	}

	/**
	 * TODO: API
	 * @return
	 */
	public boolean isLeaf(){
		return (this.getChildren() == null);
	}
	
	/**
	 * TODO: API
	 */
	public void incrementVisitCount(){
		int newVisitCount = this.getVisitCount();
		newVisitCount++;
		this.setVisitCount(newVisitCount);
	}

	/**
	 * TODO: API
	 * @return
	 */
	public boolean havePillsEatenOnMove(){
		if(this.getChildren() == null){
			return false;
		}
		
		for(MCTSNode child : this.getChildrenCollection()){
			if(child.havePillsEaten()){
				return true;				
			}
		}
		return false;
	}
}
