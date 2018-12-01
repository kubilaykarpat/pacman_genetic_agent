package internal_competition.entrants.pacman.team1;

import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

import java.util.*;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.scrublords).
 */
public class MyPacMan extends PacmanController {
    private int[][] stateArray = new int[200][200];
    private MOVE myMove = MOVE.NEUTRAL;
    private int startIndex = 0;
    private ArrayList<Integer> x_train = new ArrayList();
    private ArrayList<Integer> y_train = new ArrayList();
    private ArrayList<Integer> pillsInMaze = new ArrayList();
    private ArrayList<Integer> powerPillsInMaze = new ArrayList();
    private Constants.DM distanceMeasure = Constants.DM.MANHATTAN;
    private int currentTargetNode = 0;
    private ExtendedGame extendedGame = null;
    private static final Random RANDOM = new Random();
    private static final int SIZE = MOVE.values().length;
    private static final List<MOVE> VALUES =
            Collections.unmodifiableList(Arrays.asList(MOVE.values()));
    private int gameLevel = -1;
    private final static int PILL_PROXIMITY = 15;


//    public Maze currentMaze = game.getCurrentMaze();

    public MOVE getMove(Game game, long timeDue) {
        int currentLevel = game.getCurrentLevel();
        if (currentLevel != gameLevel){
            FirstIteration(game);
            this.extendedGame = new ExtendedGame();
            this.extendedGame.initGame(game, this.pillsInMaze);
            this.gameLevel = currentLevel;
        }


        this.extendedGame.updateGame(game);
        GameInfo state = game.getPopulatedGameInfo();
        int pacmanIndex = state.getPacman().currentNodeIndex;
        MOVE lastMove = state.getPacman().lastMoveMade;

        myMove = MOVE.NEUTRAL;

        //check if ghost is present
        if(state.getGhosts().size() > 0){
            ArrayList<MOVE> movesAway = new ArrayList();
            EnumMap<Constants.GHOST, Ghost> ghosts = state.getGhosts();
            ArrayList<Ghost> ghostArr = new ArrayList(ghosts.values());
            //Iterate visible ghosts and choose a move which does not collide with them
            MOVE towardsPowerPill = MOVE.NEUTRAL;

            for(Ghost ghost : ghostArr){
                double distanceToGhost = game.getDistance(pacmanIndex, ghost.currentNodeIndex, distanceMeasure);
                // If ghosts are far enough there is no reason to run
                if (ghost.edibleTime < 5 && distanceToGhost < 35){
                    // Check if power pill is available and in sight
                    int[] visiblePowerPill = game.getActivePowerPillsIndices();
                    int isCloseToPower = closeToPower(game, pacmanIndex);
                    if(visiblePowerPill.length > 1){
                        double shortestDistanse = 9999;
                        int powerPillNodeIndex = 0;
                        //Find shortest path to available power pill
                        for (int powerPillIndex : visiblePowerPill){
                            double currDistance = game.getDistance(pacmanIndex, powerPillIndex, distanceMeasure);
                            if (currDistance < shortestDistanse){
                                shortestDistanse = currDistance;
                                powerPillNodeIndex = powerPillIndex;
                            }
                        }

                        towardsPowerPill = game.getNextMoveTowardsTarget(pacmanIndex, powerPillNodeIndex,
                                distanceMeasure);

                    } else if(visiblePowerPill.length == 1) {
                        towardsPowerPill = game.getNextMoveTowardsTarget(pacmanIndex, visiblePowerPill[0],
                                distanceMeasure);
                    } else if (isCloseToPower != -1){
                        towardsPowerPill = game.getApproximateNextMoveTowardsTarget(pacmanIndex, isCloseToPower,
                                lastMove, distanceMeasure);
                    }

                    //
                    MOVE away = game.getNextMoveAwayFromTarget(pacmanIndex,ghost.currentNodeIndex, distanceMeasure);



                    movesAway.add(away);
                } else if(ghost.edibleTime > 20 && distanceToGhost < 35){
                    MOVE chase = game.getNextMoveTowardsTarget(pacmanIndex, ghost.currentNodeIndex, distanceMeasure);
                    return chase;
                }
            }

            if (movesAway.size() > 1){
                ArrayList<MOVE> runTowards = new ArrayList();
                runTowards.addAll(Arrays.asList(game.getPossibleMoves(pacmanIndex)));
                runTowards.removeAll(movesAway);

                if(runTowards.size() == 0){
                    //Pacman is boxed in, accepts fate and hopes for the best
                    myMove = VALUES.get(RANDOM.nextInt(SIZE));
                    System.out.println("Random move");
                } else {
                    // check better move
                    Boolean acceptableToGoForPowerPill = true;
                    for (MOVE currMove : runTowards){
                        // Check if towards power pill is an acceptable move
                        if (towardsPowerPill == MOVE.NEUTRAL || towardsPowerPill == GetOposite(currMove)){
                            acceptableToGoForPowerPill = false;
                        }
                    }
                    if (acceptableToGoForPowerPill){
                        return towardsPowerPill;
                    }

                    if (runTowards.size() > 1){
                        //Has more than one possible ways out, compute possibilities and tries to select acceptable one
                        System.out.println("Dumb move");
                        System.out.println(runTowards.get(0));

                    }
                    ExtractFeaturesFromState(state, game);
                    MOVE suggestedMove = game.getNextMoveTowardsTarget(pacmanIndex, currentTargetNode,  distanceMeasure);
                    // check for a better move
                    if (runTowards.contains(suggestedMove)){
                        myMove = suggestedMove;
                        System.out.println("Suggested move used: " + myMove.toString());

                    } else {
                        //Perform first acceptable move
                        myMove = runTowards.get(0);

                    }

                }

            } else {
                if (movesAway.size() == 0) {
                    //If pacman hasn't reached his target don't look for another one
                    if (currentTargetNode == 0 || pacmanIndex == currentTargetNode){
                        ExtractFeaturesFromState(state, game);
                    }
                    myMove = game.getNextMoveTowardsTarget(pacmanIndex, currentTargetNode,  distanceMeasure);

                } else {
                    myMove = movesAway.get(0);
                }
            }

        } else {
            // Look for best densest and closest cluster
            ExtractFeaturesFromState(state, game);
            myMove = game.getNextMoveTowardsTarget(pacmanIndex, currentTargetNode, lastMove, distanceMeasure);
        }

        return myMove;
    }

    private int closeToPower(Game game, int pacmanIndex) {
        int[] powerPills = game.getPowerPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            try{
                if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanIndex) < PILL_PROXIMITY) {
                    return powerPills[i];
                }
            } catch (NullPointerException ex){
                return -1;
            }

        }

        return -1;
    }

    // Used as decision for goal selection
    public void ExtractFeaturesFromState(GameInfo info, Game game) {
        int pacmanIndex = info.getPacman().currentNodeIndex;
        int[] pill_nodes = game.getActivePillsIndices();
        //If no pills are visible look in the stored solution containing all remaining pills
        if (pill_nodes.length == 0){
            int pillIndex = this.extendedGame.goToPill();
            this.currentTargetNode = pillIndex;

        } else {
            // Check distance towards the available pills
            int[] distanses = new int[pill_nodes.length];
            for (int i = 0; i < pill_nodes.length; i ++){
                distanses[i] = game.getManhattanDistance(pacmanIndex, pill_nodes[i]);
            }

            Arrays.sort(distanses);
            //Get closest and furthest distance
            int min = Arrays.stream(distanses).min().getAsInt();
            int max = Arrays.stream(distanses).max().getAsInt();
            // If closest visible pill is further than 30.0 distance consider the global availability of pills
            if (min > 30){
                int pillIndex = this.extendedGame.goToPill();
                this.currentTargetNode = pillIndex;

            } else {
                //Maximize possible gain by going towards furthest point
                //If it's farther than 40 concider going towards the closer gain
                //Useful when in big hallways
                int valueToCompare = max > 40 ? max : min;
                int indexOfMaxa = Arrays.binarySearch(distanses, valueToCompare);
                this.currentTargetNode = pill_nodes[indexOfMaxa];
            }

        }
    }

    // Returns opposite move
    private MOVE GetOposite(MOVE move){
        if (move == MOVE.DOWN){
            return MOVE.UP;
        } else if (move == MOVE.LEFT){
            return MOVE.RIGHT;
        } else if (move == MOVE.RIGHT){
            return MOVE.LEFT;
        } else {
            return MOVE.DOWN;
        }
    }

    // Used to call on each new level to reinitialize the array
    // containing the pills
    public void FirstIteration(Game game) {
        int[] allPills = game.getPillIndices();
        int[] allPowerPills = game.getPowerPillIndices();

        for (Integer pill : allPills){
            pillsInMaze.add(pill);
        }
        for (Integer powerPill : allPowerPills){
            powerPillsInMaze.add(powerPill);
        }
    }

}