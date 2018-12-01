
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.*;

import java.util.EnumMap;


/**
 * Created by pwillic on 06/05/2016.
 */
public class Ghost_Main {

    public static void main(String[] args) {

        Executor executor = new Executor(true, true);

        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
        
        controllers.put(GHOST.INKY, new entrants.ghosts.max_frick.Inky());
        controllers.put(GHOST.BLINKY, new entrants.ghosts.max_frick.Blinky());
        controllers.put(GHOST.PINKY, new entrants.ghosts.max_frick.Pinky());
        controllers.put(GHOST.SUE, new entrants.ghosts.max_frick.Sue());  
        // runs the ghost AI
        executor.runGameTimed( new examples.StarterPacMan.MyPacMan(), new MASController(controllers), true);
    }
}
