package internal_competition.entrants.pacman.team4;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Class containing the custom controller logic for pacman
 * 
 * @author Daniel Hohmann
 * @author Philipp Kittan
 *
 */
public class MyPacMan extends PacmanController {

    /**
     * The brain of pacman
     */
    private static Brain brain = null;

    public MOVE getMove(Game game, long timeDue) {
	
	// Give pacman a brain if he do not have one
	if (brain == null) {
	    brain = new Brain(game);
	}

	// Brain, do your job
	return brain.update(game);
    }
}