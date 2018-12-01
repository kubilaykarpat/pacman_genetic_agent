package entrants.util;

import java.util.BitSet;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

/**
 * Class representing an accumulated game state.
 * Collects information about active pills and scouted pill locations.
 * @author Florian Bethe
 */
public class AccumGameState {
	/**
	 * Bitset indicating still active pills.
	 */
	private BitSet activePills;
	/**
	 * Bitset indicating still active power pills.
	 */
	private BitSet powerPills;
	/**
	 * Bitset tracking seen pill locations.
	 */
	private BitSet pillsSeen;
	/**
	 * Bitset tracking seen power pill locations.
	 */
	private BitSet powerPillsSeen;
	
	/**
	 * Constructor.
	 * Initializes empty state.
	 */
	public AccumGameState() {
		this.activePills = new BitSet();
		this.powerPills = new BitSet();
		this.pillsSeen = new BitSet();
		this.powerPillsSeen = new BitSet();
	}
	
	/**
	 * Updates the state.
	 * Updates observed pill locations and consumption status.
	 * @param game current game state
	 * @return game state populated with accumulated pill status.
	 */
	public Game update(Game game) {
		// Get normal game information and remove ghosts
		GameInfo info = game.getPopulatedGameInfo();
		info.fixGhosts((ghost) -> new Ghost(
                ghost,
                game.getCurrentMaze().lairNodeIndex,
                -1,
                -1,
                MOVE.NEUTRAL
        ));
		
		// Update the pills
		for(int i = info.getPills().nextSetBit(0); i != -1; i = info.getPills().nextSetBit(i + 1)) {
			activePills.set(i);
			pillsSeen.set(i);
		}
		// Update the power pills
		for(int i = info.getPowerPills().nextSetBit(0); i != -1; i = info.getPowerPills().nextSetBit(i + 1)) {
			powerPills.set(i);
			powerPillsSeen.set(i);
		}
		
		// Erase the currently consumed pill from the active pill list
		if(game.getPillIndex(game.getPacmanCurrentNodeIndex()) >= 0) {
			activePills.clear(game.getPillIndex(game.getPacmanCurrentNodeIndex()));
		}
		// Erase the currently consumed power pill from the active power pill list
		if(game.getPowerPillIndex(game.getPacmanCurrentNodeIndex()) >= 0) {
			powerPills.clear(game.getPowerPillIndex(game.getPacmanCurrentNodeIndex()));
		}
		
		// Set pills and power pills previously seen into the game state
		for(int i = activePills.nextSetBit(0); i != -1; i = activePills.nextSetBit(i + 1)) {
			info.setPillAtIndex(i, true);
		}
		for(int i = powerPills.nextSetBit(0); i != -1; i = powerPills.nextSetBit(i + 1)) {
			info.setPowerPillAtIndex(i, true);
		}
		
		return game.getGameFromInfo(info);
	}
	
	/**
	 * Return the number of pill positions seen.
	 * @return count
	 */
	public int getPillPositionsSeen() {
		return pillsSeen.cardinality();
	}

	/**
	 * Return the number of power pill positions seen.
	 * @return count
	 */
	public int getPowerPillPositionsSeen() {
		return powerPillsSeen.cardinality();
	}
	
	/**
	 * Performs a deep copy of the accumulated game state.
	 * @return deep state copy
	 */
	public AccumGameState copy() {
		AccumGameState state = new AccumGameState();
		state.activePills = (BitSet)this.activePills.clone();
		state.powerPills = (BitSet)this.powerPills.clone();
		state.pillsSeen = (BitSet)this.pillsSeen.clone();
		state.powerPillsSeen = (BitSet)this.powerPillsSeen.clone();
		return state;
	}
}
