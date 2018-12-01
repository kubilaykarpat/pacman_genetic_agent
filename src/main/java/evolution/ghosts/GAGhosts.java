package evolution.ghosts;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;

import java.util.EnumMap;

import evolution.behaviortree.ghosts.BehaviorTree;


public class GAGhosts extends MASController {



    public GAGhosts(GAGhost blinky, GAGhost inky, GAGhost pinky, GAGhost sue, int TICK_THRESHOLD){
    	super(true, new EnumMap<GHOST, IndividualGhostController>(GHOST.class));
        controllers.put(GHOST.BLINKY, blinky);
        controllers.put(GHOST.INKY, inky);
        controllers.put(GHOST.PINKY, pinky);
        controllers.put(GHOST.SUE, sue);
    }

	public GAGhosts(BehaviorTree individual, BehaviorTree individual2, BehaviorTree individual3,
			BehaviorTree individual4) {
		super(true, new EnumMap<GHOST, IndividualGhostController>(GHOST.class));
		controllers.put(GHOST.BLINKY, new GAGhost(GHOST.BLINKY, individual));
        controllers.put(GHOST.INKY, new GAGhost(GHOST.INKY, individual2));
        controllers.put(GHOST.PINKY, new GAGhost(GHOST.PINKY, individual3));
        controllers.put(GHOST.SUE, new GAGhost(GHOST.SUE, individual4));
		// TODO Auto-generated constructor stub
	}

}