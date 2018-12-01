package evolution.ghosts;

import java.util.Random;

import evolution.behaviortree.ghosts.BehaviorTree;
import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GAGhost extends IndividualGhostController {

    private BehaviorTree behavior;
    
    Random rnd = new Random();
    private ExtendedGameGhosts extendedgame;

	public GAGhost(Constants.GHOST ghost, BehaviorTree behavior) {
        this(ghost, 5, behavior);
    }

    public GAGhost(Constants.GHOST ghost, int TICK_THRESHOLD, BehaviorTree behavior) {
        super(ghost);
    	this.behavior = behavior;
    	this.extendedgame = new ExtendedGameGhosts(TICK_THRESHOLD, ghost);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
    	// Housekeeping - throw out old info
    	this.extendedgame.updateGame(game);
    	
        

        if (game.doesGhostRequireAction(ghost)){
        	// evaluate tree
        	return this.behavior.eval(extendedgame, this.ghost);
        }
        
    	
        return MOVE.NEUTRAL;
    }

    
  
}