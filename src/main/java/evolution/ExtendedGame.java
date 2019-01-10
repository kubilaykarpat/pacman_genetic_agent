package evolution;

import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.Map;

import static pacman.game.Constants.LEVEL_RESET_REDUCTION;

//Stores information that is needed during the tree evaluation
public class ExtendedGame {
	private boolean[] pillIsStillAvailable = null;
    private boolean[] powerPillIsStillAvailable = null;
    private Map<Constants.GHOST, Integer> ghostPositions = new EnumMap<Constants.GHOST, Integer>(Constants.GHOST.class);
    private Map<Constants.GHOST, Integer> tickSeen = new EnumMap<Constants.GHOST, Integer>(Constants.GHOST.class);
    public Map<Constants.GHOST, Boolean> edible = new EnumMap<Constants.GHOST, Boolean>(Constants.GHOST.class);
    private double[] distances;
    
    private int mazeIndex = -1;

    private int TICK_THRESHOLD;
    
    private static final int EDIBLE_TIME = 200;
    private static final float EDIBLE_TIME_REDUCTION = 0.9f; 
    
    private int lastPowerPillTick = -EDIBLE_TIME;

    public Game game;

	public ExtendedGame(int TICK_THRESHOLD) {
    	this.TICK_THRESHOLD = TICK_THRESHOLD;
    	this.distances = new double[4];
    	
		for (Constants.GHOST ghosttype : Constants.GHOST.values()){
			edible.put(ghosttype, false);
			tickSeen.put(ghosttype, -1);
			ghostPositions.put(ghosttype, -1);
		}
    }

	
	public void updateGame(Game game){
		this.game = game;
		
		if (mazeIndex != game.getMazeIndex()){
			this.resetData(game);
		}

		//set ghosts
		int currentTick = game.getCurrentLevelTime();
		for (Constants.GHOST ghosttype : Constants.GHOST.values()) {
			if (game.getGhostCurrentNodeIndex(ghosttype) != -1 && currentTick - tickSeen.get(ghosttype) >= TICK_THRESHOLD) {
	        	this.setGhostPosition(-1, ghosttype);
	            tickSeen.put(ghosttype, -1);
	        }
		}
	    this.updateDistances();
		
		//set pills
		int[] powerPills = this.game.getPowerPillIndices();
		int[] pills = this.game.getPillIndices();
		
		if (game.wasPowerPillEaten()){
			lastPowerPillTick = game.getCurrentLevelTime();
			for (Constants.GHOST ghosttype : Constants.GHOST.values()){
				edible.put(ghosttype, true);
			}
		}
			
		if (this.edibleTime() < 0 || game.getCurrentLevelTime() < 2){
			for (Constants.GHOST ghosttype : Constants.GHOST.values()){
				edible.put(ghosttype, false);
			}
		}
		
		for (int i = 0; i < powerPills.length; i++) {
            Boolean thisPill = this.game.isPowerPillStillAvailable(i);
            
            if (thisPill != null && thisPill == false){	            
                this.powerPillIsStillAvailable[i] = false;
            }
        }
		
		for (int i = 0; i < pills.length; i++)
		{
			 Boolean thisPill = this.game.isPillStillAvailable(i);
	            
            if (thisPill != null && thisPill == false){	            
                this.pillIsStillAvailable[i] = false;
            }
		}
		
	}
	
	
	private void resetData(Game game){
		this.resetGhostData();
		this.resetPowerPills();
		this.resetPills();
		this.mazeIndex = game.getMazeIndex();
	}
	
	private void resetGhostData(){
		if (ghostPositions.size() == 0){
			for (GHOST ghosttype : Constants.GHOST.values()){
				ghostPositions.put(ghosttype, game.getGhostInitialNodeIndex());
			}
		}
	}
	
	private void updateDistances(){
		int i = 0;
		for (Constants.GHOST ghosttype : Constants.GHOST.values()){
			if (getGhostPosition(ghosttype) != -1){
				distances[i] = game.getDistance(game.getPacmanCurrentNodeIndex(), getGhostPosition(ghosttype), DM.PATH);
			}
			else {
				distances[i] = -1;
			}
			i++;
		}
	}
	
	public double[] getEstimatedGhostDistances(){
		return this.distances;
	}
	
	private void resetPowerPills()
	{
		if (powerPillIsStillAvailable == null){
			this.powerPillIsStillAvailable = new boolean[game.getPowerPillIndices().length];
			for (int i = 0; i < this.powerPillIsStillAvailable.length; i++){
				this.powerPillIsStillAvailable[i] = true;
			}
		}
	}
	
	private void resetPills()
	{
		this.pillIsStillAvailable = new boolean[game.getPillIndices().length];
		for (int i = 0; i < this.pillIsStillAvailable.length; i++){
			this.pillIsStillAvailable[i] = true;
		}
	}
    
	public boolean isPowerPillStillAvailable()
	{
		if (this.powerPillIsStillAvailable != null){
			for (int i = 0; i < this.powerPillIsStillAvailable.length; i++){
				if (this.powerPillIsStillAvailable[i] == true)
					return true;
			}
		}
		return false;
	}
	
	public boolean isPowerPillStillAvailable(int powerPillIndex){
		return this.powerPillIsStillAvailable[powerPillIndex];
	}
	
	public boolean isPillStillAvailable(int pillIndex){
		return this.pillIsStillAvailable[pillIndex];
	}


	public void setGhostPosition(int position, GHOST ghosttype) {
		this.ghostPositions.put(ghosttype, position);
	}
	
	public int getGhostPosition(GHOST ghosttype){
		return this.ghostPositions.get(ghosttype);
	}

	public int edibleTime(){
		int edibletime = this.game.getCurrentLevelTime() - this.lastPowerPillTick - 
				(int) (EDIBLE_TIME * (Math.pow(EDIBLE_TIME_REDUCTION, this.game.getCurrentLevel() % LEVEL_RESET_REDUCTION)));
		 
		return Math.max(0, edibletime);
	}
}
