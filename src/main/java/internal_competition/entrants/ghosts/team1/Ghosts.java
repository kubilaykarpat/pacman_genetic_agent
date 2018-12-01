package internal_competition.entrants.ghosts.team1;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;

import java.util.EnumMap;


public class Ghosts extends MASController {

    public Ghosts(){
    	super(true, new EnumMap<GHOST, IndividualGhostController>(GHOST.class));
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
    }

}