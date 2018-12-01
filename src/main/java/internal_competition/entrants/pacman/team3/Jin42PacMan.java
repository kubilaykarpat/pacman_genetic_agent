package internal_competition.entrants.pacman.team3;


import java.util.ArrayList;
import java.util.HashMap;

import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class Jin42PacMan extends PacmanController {
    private boolean[] pillsAvailable = null;
    private int currentLevel = 0;
    private MOVE lastMove = MOVE.NEUTRAL;
    
    private void updatePills(Game game)
    {
    	for (int i = 0; i < game.getCurrentMaze().pillIndices.length; i++)
    	{
    		Boolean ab = game.isPillStillAvailable(i);
    		if (ab != null)
    			pillsAvailable[i] = ab;
    	}
    }
    
    private MOVE moveToGhost(Game game, GHOST ghost)
    {
    	int pacmanNode = game.getPacmanCurrentNodeIndex();
    	if (game.getGhostCurrentNodeIndex(ghost) != -1 && game.getGhostEdibleTime(ghost) <= 0)
    		return game.getNextMoveTowardsTarget(pacmanNode, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
    	return null;
    }
    
    private MOVE getNearestPill(Game game)
    {
    	int pacmanNode = game.getPacmanCurrentNodeIndex();
    	int aim = -1;
    	double path = -1;
    	ArrayList<MOVE> forbiddenMoves = new ArrayList<MOVE>();
    	MOVE a;
    	if ((a = moveToGhost(game,GHOST.BLINKY)) != null)
    		forbiddenMoves.add(a);
    	if ((a = moveToGhost(game,GHOST.INKY)) != null)
    		forbiddenMoves.add(a);
    	if ((a = moveToGhost(game,GHOST.PINKY)) != null)
    		forbiddenMoves.add(a);
    	if ((a = moveToGhost(game,GHOST.SUE)) != null)
    		forbiddenMoves.add(a);
    	MOVE[] moves = game.getPossibleMoves(pacmanNode, lastMove);
    	ArrayList<MOVE> mo = new ArrayList<MOVE>();
    	if (moves != null)
    	for (int i = 0; i < moves.length; i++)
    		if (!forbiddenMoves.contains(moves[i]))
    				mo.add(moves[i]);
    	if (mo.size() == 1)
    		return mo.get(0);
    	for (int i = 0; i < game.getCurrentMaze().pillIndices.length; i++)
    	{
    		if (pillsAvailable[i])
    		{
    			double distance = game.getDistance(pacmanNode, game.getCurrentMaze().pillIndices[i], DM.PATH);
    			if (distance < path || path == -1)
    			{
    				if (!forbiddenMoves.contains(game.getNextMoveTowardsTarget(pacmanNode, game.getCurrentMaze().pillIndices[i], DM.PATH)))
    				
    				{
    					aim = game.getCurrentMaze().pillIndices[i];
    					path = distance;
    				}
    			}
    		}
    	}
    	if (aim == -1)
    		return MOVE.NEUTRAL;
    	return game.getNextMoveTowardsTarget(pacmanNode, aim, DM.PATH);
    }

    public MOVE getMove(Game game, long timeDue) {
    	if (pillsAvailable == null || game.getCurrentLevel() != currentLevel)
    	{
			pillsAvailable = new boolean[game.getCurrentMaze().pillIndices.length];
			for (int i = 0; i < pillsAvailable.length; i++)
				pillsAvailable[i] = true;
			currentLevel = game.getCurrentLevel();
    	}
    	else
    	{
    		updatePills(game);
    	}
    	MOVE pill = null;
    		pill = getNearestPill(game);
    		lastMove = pill;
    	return pill;
    	
    }
}