package evolution.pacmanevaluation;

import java.util.Random;

import evolution.behaviortree.pacman.BehaviorTreePacman;
import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Game;

public class GAPacman  extends PacmanController {

    private BehaviorTreePacman behavior;
    Random rnd = new Random();
    private ExtendedGamePacman extendedgame;
    
	public GAPacman(BehaviorTreePacman individual) {
		this.behavior = individual;
		this.extendedgame = new ExtendedGamePacman(40);
	}

	@Override
    public Constants.MOVE getMove(Game game, long timeDue) {
    	// Housekeeping - throw out old info
    	this.extendedgame.updateGame(game);
        
        return this.behavior.eval(extendedgame);
        
    	
    }
	
	
}
