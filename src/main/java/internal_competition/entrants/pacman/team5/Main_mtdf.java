package internal_competition.entrants.pacman.team5;


import internal_competition.entrants.pacman.team5.AlphaBetaPruningBot;
import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;

import internal_competition.entrants.pacman.team5.MTDfBot;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.*;

import java.util.EnumMap;

public class Main_mtdf {

    public static void main(String[] args) {

        Executor executor = new Executor(true, true, true);

        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);

        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
        executor.setDaemon(false);
        executor.runGame(new MTDfBot(), new MASController(controllers), true, 10);
    }
}
