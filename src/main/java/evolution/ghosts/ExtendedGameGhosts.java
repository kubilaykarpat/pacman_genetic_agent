package evolution.ghosts;

import java.util.EnumMap;
import java.util.Map;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.comms.BasicMessage;
import pacman.game.comms.Message;
import pacman.game.comms.Messenger;

//Stores information that is needed during the tree evaluation
public class ExtendedGameGhosts {
    private int lastPacmanIndex = -1;
    private boolean[] powerPillIsStillAvailable = null;
    private Map<Constants.GHOST, Integer> ghostPositions = new EnumMap<Constants.GHOST, Integer>(Constants.GHOST.class);
    private int mazeIndex = -1;
    private int tickSeen = -1;
    private int TICK_THRESHOLD;
    private Constants.GHOST ghosttype;

    public Game game;
    
    public ExtendedGameGhosts(int TICK_THRESHOLD, Constants.GHOST ghosttype){
    	this.TICK_THRESHOLD = TICK_THRESHOLD;
    	this.ghosttype = ghosttype;
    }

	public int getLastPacmanIndex() {
		return lastPacmanIndex;
	}

	public void setLastPacmanIndex(int lastPacmanIndex) {
		this.lastPacmanIndex = lastPacmanIndex;
	}
	
	public void updateGame(Game game){
		this.game = game;
		
		if (mazeIndex != game.getMazeIndex()){
			this.resetData(game);
		}

        processMessages();
        
        
		int[] powerPills = this.game.getPowerPillIndices();

		for (int i = 0; i < powerPills.length; i++) {
            Boolean thisPill = this.game.isPowerPillStillAvailable(i);
            
            if (thisPill != null && thisPill == false){	            
                this.powerPillIsStillAvailable[i] = false;
            }
        }
	}
	
	private void processMessages(){
		int currentTick = game.getCurrentLevelTime();
        if (currentTick <= 2 || currentTick - tickSeen >= TICK_THRESHOLD) {
        	this.setLastPacmanIndex(-1);
            tickSeen = -1;
        }
        
        // Can we see PacMan? If so tell people and update our info
        int pacmanIndex = game.getPacmanCurrentNodeIndex();

		Messenger messenger = game.getMessenger();
        if (pacmanIndex != -1) {
        	this.setLastPacmanIndex(pacmanIndex);
            tickSeen = game.getCurrentLevelTime();
            if (messenger != null) {
                messenger.addMessage(new BasicMessage(ghosttype, null, BasicMessage.MessageType.PACMAN_SEEN, pacmanIndex, game.getCurrentLevelTime()));
            }
        }

        messenger.addMessage(new BasicMessage(ghosttype, null, BasicMessage.MessageType.I_AM, game.getGhostCurrentNodeIndex(ghosttype), game.getCurrentLevelTime()));

        
        // Has anybody else seen PacMan if we haven't?
        if (pacmanIndex == -1 && game.getMessenger() != null) {
            for (Message message : messenger.getMessages(ghosttype)) {
                if (message.getType() == BasicMessage.MessageType.PACMAN_SEEN) {
                    if (message.getTick() > tickSeen && message.getTick() < currentTick) { // Only if it is newer information
                    	this.setLastPacmanIndex(message.getData());
                        tickSeen = message.getTick();
                    }
                }
                if (message.getType() == BasicMessage.MessageType.I_AM) {
                	if (message.getTick() < currentTick + 5) {
                    	this.setGhostPosition(message.getData(), message.getSender());
                    }
                } 
            }
        }

	}
	
	private void resetData(Game game){
		this.resetGhostData();
		this.resetPowerPills();
		this.mazeIndex = game.getMazeIndex();
	}
	
	private void resetGhostData(){
		if (ghostPositions.size() == 0){
			for (GHOST ghosttype : Constants.GHOST.values()){
				ghostPositions.put(ghosttype, game.getGhostInitialNodeIndex());
			}
		}
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

	public void setGhostPosition(int position, GHOST ghosttype) {
		this.ghostPositions.put(ghosttype, position);
	}
	
	public int getGhostPosition(GHOST ghosttype){
		return this.ghostPositions.get(ghosttype);
	}

}
