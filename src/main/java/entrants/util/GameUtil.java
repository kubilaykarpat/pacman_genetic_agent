package entrants.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Utility class for getting moves from the game.
 * @author Florian Bethe
 */
public class GameUtil {
	/**
	 * Returns a list of possible moves in the given game from the given index.
	 * @param game current game state
	 * @param index node index
	 * @return list of possible moves
	 */
	public static List<MOVE> getPossibleMoves(Game game, int index) {
		return new ArrayList<>(Arrays.asList(game.getPossibleMoves(index)));
	}
	
	/**
	 * Returns a list of possible moves for pacman in the given game.
	 * @param game current game state
	 * @return list of possible moves
	 */
	public static List<MOVE> getPacmanPossibleMoves(Game game) {
		return getPossibleMoves(game, game.getPacmanCurrentNodeIndex());
	}

	/**
	 * Returns a list of possible moves for the given ghost in the given game.
	 * @param game current game state
	 * @param ghost ghost ID
	 * @return list of possible moves
	 */
	public static List<MOVE> getGhostPossibleMoves(Game game, GHOST ghost) {
		return getPossibleMoves(game, game.getGhostCurrentNodeIndex(ghost));
	}

	/**
	 * Gets the next straight (if possible) move for a given index.
	 * If the index is facing a wall, return the first possible move. Otherwise
	 * return its move last made with preference for moves not opposing the
	 * last one.
	 * @param game current game state
	 * @param index node index
	 * @param last move to be assumed last made
	 * @return next move
	 */
	private static MOVE getStraightNextMove(Game game, int index, MOVE last) {
		List<MOVE> possible = getPossibleMoves(game, index);
		
		// Check if it's actually possible to make the last move
		if(possible.contains(last)) {
			return last;
		}
		
		// Otherwise return the first legal move (preferably not turning around)
		for(MOVE move : possible) {
			if(move != last.opposite()) {
				return move;
			}
		}
		
		// Fallback (only for dead end)
		return last.opposite();
	}
	
	/**
	 * Gets the next straight (if possible) move for pacman.
	 * If pacman is facing a wall, return the first possible move. Otherwise
	 * return its move last made with preference for moves not opposing the
	 * last one.
	 * @param game current game state
	 * @return next move
	 */
	public static MOVE getPacmanStraightNextMove(Game game) {
		return getStraightNextMove(game, game.getPacmanCurrentNodeIndex(),
				game.getPacmanLastMoveMade());
	}
	
	/**
	 * Gets the next straight (if possible) move for the given ghost.
	 * If the ghost is facing a wall, return the first possible move. Otherwise
	 * return its move last made with preference for moves not opposing the
	 * last one.
	 * @param game current game state
	 * @param ghost ghost ID
	 * @return next move
	 */
	public static MOVE getGhostStraightNextMove(Game game, GHOST ghost) {
		return getStraightNextMove(game, game.getGhostCurrentNodeIndex(ghost),
				game.getGhostLastMoveMade(ghost));
	}
}
