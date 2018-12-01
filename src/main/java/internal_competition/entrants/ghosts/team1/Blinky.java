package internal_competition.entrants.ghosts.team1;


import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Blinky extends CommonGhost {
    private int targetNode = 0;

    public Blinky() {
        super(Constants.GHOST.BLINKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
//        System.out.print("left");
//        return Constants.MOVE.LEFT;
//        int currentNode = game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);


        return null;
    }

    public void SetTargetNode(int node){
        this.targetNode = node;
    }

}
