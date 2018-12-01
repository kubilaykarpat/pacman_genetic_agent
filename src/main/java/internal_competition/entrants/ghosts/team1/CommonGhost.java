package internal_competition.entrants.ghosts.team1;


import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.MOVE;
import pacman.game.comms.BasicMessage;
import pacman.game.comms.Message;
import pacman.game.comms.Messenger;

import java.util.ArrayList;
import java.util.Random;

import internal_competition.entrants.pacman.team1.ExtendedGame;


/**
 * Created by pwillic on 25/02/2016.
 */

public class CommonGhost extends IndividualGhostController {
    private final static float CONSISTENCY = 0.7f;    //attack Ms Pac-Man with this probability

    private final static int PILL_PROXIMITY = 15;        //if Ms Pac-Man is this close to a power pill, back away
    Random rnd = new Random();
    private int TICK_THRESHOLD;
    private int lastPacmanIndex = -1;
    private int tickSeen = -1;
    private int gameLevel = -1;
    private ExtendedGame extendedGame = null;
    private ArrayList<Integer> pillsInMaze = new ArrayList();
    private ArrayList<Integer> powerPillsInMaze = new ArrayList();
    private int targetNode = 0;

    //Ideally each ghost should have target nodes and try to get to them
    public void SetTargetNode(int node, CommonGhost ghost){
        ghost.targetNode = node;
    }
    public int GetTargetNode(CommonGhost ghost){
        return ghost.targetNode;
    }

    public CommonGhost(Constants.GHOST ghost) {
        this(ghost, 5);
        this.targetNode = 0;
    }

    public CommonGhost(Constants.GHOST ghost, int TICK_THRESHOLD) {
        super(ghost);
        this.TICK_THRESHOLD = TICK_THRESHOLD;

    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        // Initial steps for initializing the pill arrays
        int currentLevel = game.getCurrentLevel();
        if (currentLevel != gameLevel){
            FirstIteration(game);
            this.extendedGame = new ExtendedGame();
            this.extendedGame.initGame(game, this.pillsInMaze);
            this.gameLevel = currentLevel;
        }
        this.extendedGame.updateGame(game);

        int currentTick = game.getCurrentLevelTime();
        if (currentTick <= 2 || currentTick - tickSeen >= TICK_THRESHOLD) {
            lastPacmanIndex = -1;
            tickSeen = -1;
        }

//        // Can we see PacMan? If so tell people and update our info
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        Messenger messenger = game.getMessenger();
        if (pacmanIndex != -1) {
            lastPacmanIndex = pacmanIndex;
            tickSeen = game.getCurrentLevelTime();
            if (messenger != null) {
                messenger.addMessage(new BasicMessage(ghost, null, BasicMessage.MessageType.PACMAN_SEEN, pacmanIndex, game.getCurrentLevelTime()));
            }

        }

        // Has anybody else seen PacMan if we haven't?
        if (pacmanIndex == -1 && game.getMessenger() != null) {
            for (Message message : messenger.getMessages(ghost)) {
                if (message.getType() == BasicMessage.MessageType.PACMAN_SEEN) {
                    if (message.getTick() > tickSeen && message.getTick() < currentTick) { // Only if it is newer information
                        lastPacmanIndex = message.getData();
                        tickSeen = message.getTick();
                        if(ghost == Constants.GHOST.BLINKY){
                            // Should be done in the Blinky class
                            Constants.MOVE next = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
                                    lastPacmanIndex, game.getGhostLastMoveMade(ghost), Constants.DM.MANHATTAN);

                            return next;
                        }
                    }
                }
            }
        }
        if (pacmanIndex == -1) {
            pacmanIndex = lastPacmanIndex;
        }

        Boolean requiresAction = true;
        if (requiresAction != null && requiresAction)        //if ghost requires an action
        {
            if (pacmanIndex != -1) {
                if (game.getGhostEdibleTime(ghost) > 0 || closeToPower(game))    //retreat from Ms Pac-Man if edible or if Ms Pac-Man is close to power pill
                {
                    try {
                        return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
                                game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(e);
                        System.out.println(pacmanIndex + " : " + currentIndex);
                    }
                } else {
                    try {
                        Constants.MOVE move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
                                pacmanIndex, game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
                        return move;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(e);
                        System.out.println(pacmanIndex + " : " + currentIndex);
                    }
                }
            } else {
                int position = game.getGhostCurrentNodeIndex(ghost);
                String ghostType = ghost.className;
                int specificIndex = GetGhostSpecificTask(ghost.className);

                int pillIndex = this.extendedGame.goToPositionForGhost(position, specificIndex, lastPacmanIndex);
                if(pillIndex == -1){
                    Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
                    if (possibleMoves.length == 0)
                    	return MOVE.NEUTRAL;
                    return possibleMoves[rnd.nextInt(possibleMoves.length)];
                } else {
                    Constants.MOVE last = game.getGhostLastMoveMade(ghost);
                    return  game.getNextMoveTowardsTarget(position, pillIndex, last, Constants.DM.PATH);
                }
            }
        }
        return null;
    }

    //A trashy way to get each ghost to have a different behavior
    private int GetGhostSpecificTask(String ghostName){
        if (ghostName == Constants.GHOST.BLINKY.name()){
            return 1;
        } else if (ghostName == Constants.GHOST.INKY.name()){
            return 2;
        } else if (ghostName == Constants.GHOST.PINKY.name()){
            return 3;
        } else {
            return 4;
        }

    }

    //This helper function checks if Ms Pac-Man is close to an available power pill
    private boolean closeToPower(Game game) {
        int[] powerPills = game.getPowerPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
            if (pacmanNodeIndex == -1) {
                pacmanNodeIndex = lastPacmanIndex;
            }
            if (powerPillStillAvailable == null || pacmanNodeIndex == -1) {
                return false;
            }
            if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < PILL_PROXIMITY) {
                return true;
            }
        }

        return false;
    }

    //Used to build the pills arrays
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