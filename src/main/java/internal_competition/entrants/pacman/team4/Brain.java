package internal_competition.entrants.pacman.team4;


import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Class holding pacman's functions for thinking
 * 
 * @author Daniel Hohmann
 * @author Philipp Kittan
 *
 */
public class Brain {

    /**
     * Class holding target information
     * 
     * @author Daniel Hohmann
     * @author Philipp Kittan
     *
     */
    private static class PossibleTarget {

	/**
	 * Index of the target
	 */
	int index;

	/**
	 * Best move towards the target
	 */
	MOVE moveTowards;

	/**
	 * Distance towards the target
	 */
	int distance = Integer.MAX_VALUE;

	/**
	 * Determines if the target should be chosen
	 */
	boolean restricted;

	/**
	 * Constructs a target
	 * <p>
	 * moveTowards = NEUTRAL<br>
	 * index = -1
	 */
	PossibleTarget() {
	    this(-1, MOVE.NEUTRAL);
	}

	/**
	 * Constructs a target
	 * 
	 * @param i
	 *            index in the maze
	 * @param m
	 *            next move towards the target
	 */
	PossibleTarget(int i, MOVE m) {
	    this(i, m, true);
	}

	PossibleTarget(int i, MOVE m, boolean r) {
	    index = i;
	    moveTowards = m;
	    restricted = r;
	}
    }

    /**
     * Toggles the output of debug information
     */
    public static boolean DEBUG = false;

    /**
     * Array of pill statuses
     * <p>
     * <code>true</code> at index <code>i</code>, if the pill at the linked
     * index is active
     * 
     * @see #pillIndices
     */
    private static boolean[] pillStatus = null;

    /**
     * Array of pill indices
     * <p>
     * <code>integer<code> at index <code>i</code> is the index in the maze of
     * the pill #i
     * 
     * @see #pillStatus
     */
    private static int[] pillIndices = null;

    /**
     * Array of power pill statuses
     * <p>
     * <code>true</code> at index <code>i</code>, if the power pill at the
     * linked index is active
     * 
     * @see #powerPillIndices
     */
    private static boolean[] powerPillStatus = null;

    /**
     * Array of power pill indices
     * <p>
     * <code>integer</code> at index <code>i</code> is the index in the maze of
     * the power pill #i
     * 
     * @see #powerPillStatus
     */
    private static int[] powerPillIndices = null;

    /**
     * Current game level
     */
    private static int level;

    /**
     * Last move made by pacman
     */
    private MOVE lastMove = MOVE.NEUTRAL;

    /**
     * Duration the power pill will be active
     */
    private float powerPillActive = -1.0f;

    /**
     * Constructs a brain for pacman
     * 
     * @param game
     *            current game instance
     */
    public Brain(Game game) {
	initPills(game);
    }

    /**
     * Initialises the resources used by the class<br>
     * Should be called every time when the game level changes
     * 
     * @param game
     *            current game instance
     */
    private void initPills(Game game) {
	int[] pillIndex = game.getPillIndices();
	int[] powerPillIndex = game.getPowerPillIndices();

	// Initialise pills
	if (DEBUG) {
	    System.out.println("Initialising pills");
	}
	pillStatus = new boolean[pillIndex.length];
	for (int i = 0; i < pillStatus.length; i++) {
	    pillStatus[i] = true;
	}
	pillIndices = pillIndex;

	// Initialise power pills
	if (DEBUG) {
	    System.out.println("Initialising power pills");
	}
	powerPillStatus = new boolean[powerPillIndex.length];
	for (int i = 0; i < powerPillStatus.length; i++) {
	    powerPillStatus[i] = true;
	}
	powerPillIndices = powerPillIndex;

	// Initialise level
	level = game.getCurrentLevel();
    }

    /**
     * Updates the brain using the current game state
     * 
     * @param game
     *            current game
     * @return next move to make
     */
    public MOVE update(Game game) {

	// Check if the game level changed
	if (game.getCurrentLevel() != level) {
	    initPills(game);
	}

	// Check if pacman died
	if (game.wasPacManEaten()) {
	    if (DEBUG) {
		System.out.println("*** PACMAN was eaten ***");
	    }
	}

	// Check if a pill was eaten in the last tick
	if (game.wasPillEaten()) {
	    updatePills(game);
	}

	// Check if a power pill was eaten or if ghost are edible
	if (game.wasPowerPillEaten() || powerPillActive > 0) {
	    updatePowerPills(game);
	}

	// Calculate the next move
	calculateMove(game);
	return lastMove;
    }

    /**
     * Updates the pill arrays using the current game state
     * 
     * @param game
     *            current game
     */
    private void updatePills(Game game) {
	int[] pills = game.getPillIndices();
	for (int i = 0; i < pills.length; i++) {
	    Boolean thisPill = game.isPillStillAvailable(i);
	    if (thisPill != null && thisPill == false) {
		pillStatus[i] = false;
	    }
	}
    }

    /**
     * Updates the power pill array and the edible duration using the current
     * game state
     * 
     * @param game
     *            current game
     */
    private void updatePowerPills(Game game) {

	// Update power pill arrays
	int[] powerPills = game.getPowerPillIndices();
	for (int i = 0; i < powerPills.length; i++) {
	    Boolean thisPill = game.isPowerPillStillAvailable(i);
	    if (thisPill != null && thisPill == false) {
		powerPillStatus[i] = false;
	    }
	}
	
	// Update edible time
	if (game.wasPowerPillEaten()) {
	    powerPillActive = Constants.EDIBLE_TIME;
	} else {
	    powerPillActive -= Constants.EDIBLE_TIME_REDUCTION;
	}
    }

    /**
     * Calculates the next move using the current game state
     * @param game current game
     */
    private void calculateMove(Game game) {
	
	// Create possible targets for up, down, left and right
	PossibleTarget[] targets = new PossibleTarget[4];
	for (int i = 0; i < targets.length; i++) {
	    targets[i] = new PossibleTarget();
	}

	// Get the nearest pill in every direction
	int currpos = game.getPacmanCurrentNodeIndex();
	for (int i = 0; i < pillStatus.length; i++) {
	    if (pillStatus[i]) {
		MOVE direction = MOVE.NEUTRAL;
		try {
		    direction = game.getApproximateNextMoveTowardsTarget(currpos, pillIndices[i], lastMove, Constants.DM.PATH);
		} catch (NullPointerException e) {

		}
		int d = game.getShortestPathDistance(currpos, pillIndices[i], lastMove);

		// Target in every direction with shortest path
		switch (direction) {
		case LEFT:
		    if (d < targets[0].distance) {
			targets[0].distance = d;
			targets[0].index = pillIndices[i];
			targets[0].moveTowards = direction;
		    }
		    break;
		case RIGHT:
		    if (d < targets[1].distance) {
			targets[1].distance = d;
			targets[1].index = pillIndices[i];
			targets[1].moveTowards = direction;
		    }
		    break;
		case UP:
		    if (d < targets[2].distance) {
			targets[2].distance = d;
			targets[2].index = pillIndices[i];
			targets[2].moveTowards = direction;
		    }
		    break;
		case DOWN:
		    if (d < targets[3].distance) {
			targets[3].distance = d;
			targets[3].index = pillIndices[i];
			targets[3].moveTowards = direction;
		    }
		    break;
		default:
		    break;
		}
	    }
	}

	// Restrict moves that are not available
	for (MOVE m : game.getPossibleMoves(currpos)) {
	    switch (m) {
	    case LEFT:
		targets[0].restricted = false;
		if (targets[0].index == -1) {
		    targets[0].index = game.getNeighbour(currpos, MOVE.LEFT);
		    targets[0].distance = Integer.MAX_VALUE - 1;
		    targets[0].moveTowards = MOVE.LEFT;
		}
		break;
	    case RIGHT:
		targets[1].restricted = false;
		if (targets[1].index == -1) {
		    targets[1].index = game.getNeighbour(currpos, MOVE.RIGHT);
		    targets[1].distance = Integer.MAX_VALUE - 1;
		    targets[1].moveTowards = MOVE.RIGHT;
		}
		break;
	    case UP:
		targets[2].restricted = false;
		if (targets[2].index == -1) {
		    targets[2].index = game.getNeighbour(currpos, MOVE.UP);
		    targets[2].distance = Integer.MAX_VALUE - 1;
		    targets[2].moveTowards = MOVE.UP;
		}
		break;
	    case DOWN:
		targets[3].restricted = false;
		if (targets[3].index == -1) {
		    targets[3].index = game.getNeighbour(currpos, MOVE.DOWN);
		    targets[3].distance = Integer.MAX_VALUE - 1;
		    targets[3].moveTowards = MOVE.DOWN;
		}
		break;
	    default:
		break;
	    }
	}
	
	// Restrict directions with ghosts
	if (powerPillActive < MyConstants.POWER_PILL_MIN_DURATION) {
	    for (GHOST g : GHOST.values()) {
		int ghostindex = -1;
		if ((ghostindex = game.getGhostCurrentNodeIndex(g)) != -1) {
		    if (game.getShortestPathDistance(currpos, ghostindex, lastMove) < MyConstants.DANGEROUS_DISTANCE_GHOSTS) {
			MOVE moveToGhost = game.getApproximateNextMoveTowardsTarget(currpos, ghostindex, lastMove, Constants.DM.PATH);
			if (DEBUG)
			    System.out.println("seeing ghost " + g + " in direction " + moveToGhost);
			switch (moveToGhost) {
			case LEFT:
			    targets[0].restricted = true;
			    break;
			case RIGHT:
			    targets[1].restricted = true;
			    break;
			case UP:
			    targets[2].restricted = true;
			    break;
			case DOWN:
			    targets[3].restricted = true;
			    break;
			default:
			    break;
			}
		    }
		}
	    }
	} else {
	    // Capture ghosts
	    int ghostDistance = Integer.MAX_VALUE;
	    MOVE direction = MOVE.NEUTRAL;
	    for (GHOST g : GHOST.values()) {
		int ghostindex = -1;
		if ((ghostindex = game.getGhostCurrentNodeIndex(g)) != -1) {
		    if(game.isGhostEdible(g)){
			int d = game.getShortestPathDistance(currpos, ghostindex, lastMove);
			if(d < MyConstants.GHOST_HUNTING_DISTANCE && d < ghostDistance){
			    direction = game.getApproximateNextMoveTowardsTarget(currpos, ghostindex, lastMove, Constants.DM.PATH);
			    ghostDistance = d;
			}
		    }
		}
	    }
	    if(direction != MOVE.NEUTRAL){
		lastMove = direction;
		return;
	    }
	}

	// Get the best non-restricted move with the shortest distance
	int distance = Integer.MAX_VALUE;
	int targetIndex = -1;
	for (int i = 0; i < targets.length; i++) {
	    PossibleTarget target = targets[i];
	    if (!target.restricted && target.distance < distance) {
		targetIndex = i;
		distance = target.distance;
	    }
	}

	// Update the next move
	if (targetIndex >= 0) {
	    try {
		lastMove = targets[targetIndex].moveTowards;
	    } catch (NullPointerException e) {
		System.out.println("path calculation error");
		lastMove = MOVE.NEUTRAL;
	    }
	}
    }

}
