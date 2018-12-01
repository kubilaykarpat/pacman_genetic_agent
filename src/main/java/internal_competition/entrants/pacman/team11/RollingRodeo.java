package internal_competition.entrants.pacman.team11;

import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.util.Stats;
import java.util.*;

/**
 * Configuration of the algorithm
 */
class CONFIG{
    static Random rng = new Random();
    static Constants.MOVE[] moves = new Constants.MOVE[]{Constants.MOVE.UP, Constants.MOVE.DOWN, Constants.MOVE.RIGHT, Constants.MOVE.LEFT};

    static boolean experiment = true;                           //output in main
    static boolean ghost_reaction = true;                       //react to ghosts nearby
    static boolean advanced_pill_search = true;                 //activate bonus when searching for pills
    static boolean ghost_reaction_near_Ghost_corridor = false;  //better ghost reaction
    static boolean rh_advanced_move_selection = false;          //activate another way to choose some a move

    static int too_far_time = 10;                               //describe the ticks without pills until improved version is chosen

    static int rh_time_eps = 10;                                //time that is for the decision and choice of move
    static int rh_population_size = 10;                         //size of the population
    static int rh_individual_size = 6;                          //size of the individual
    static int rh_generations= 10;                              //number of generations for the rolling horizon
    static int rh_mutants = 10;                                 //number of mutants in each iteration
    static int rh_children = 10;                                //number of children in each iteration
    static int rh_crossover_swap_probability = 50;              //the probability of to swap each gene
    static int rh_best_selected =10;                            //select the best individuals in the generation
    static int rh_advanced_move_selection_number = rh_best_selected/2;//number of individuals that are used to decide the move
    static int rh_fitness_weight_life = 200;                    //the value of a life
    static int rh_fitness_weight_not_moved_punishment = 0;      //punishment for not moving
    static int rh_fitness_weight_step_punish =0;                //punishment for every step
    static int rh_fitness_bonus_going_in_the_right_direction = 20;//bonus for a move to the next pill
    static int rh_fitness_weight_return_punishment= 75;         //punishment for going back
}

/**
 * Class of a Pair Fitness Individual
 * so it can be better sorted.
 */
class FitnessIndividual{
    private int mFit;
    private Constants.MOVE[] mIndividual;

    FitnessIndividual(int fit, Constants.MOVE[] move){
        mFit = fit;
        mIndividual = move;
    }
    int fitness(){return mFit;}
    Constants.MOVE[] gene(){return mIndividual;}
}
public class RollingRodeo extends PacmanController {
    /**
     * ghosts that are used for simualtions
     */
    private MASController mGhosts;
    /**
     * map with currently all places that pacman visited
     */
    private HashSet<Integer> mPlaces;
    /**
     * time where no pill was eaten
     */
    private int m_ticks_not_eaten = 0;
    /**
     * boolean if we are to far away from pills with regards to the ticks
     */
    private boolean mTooFar =false;
    /**
     * index of the target node
     */
    private int mToo_far_index = -1;

    /**
     * take the example ghosts as opponents
     */
    public RollingRodeo(){
        mGhosts = new POCommGhosts();
    }
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        try {

        //save visited places
        if(CONFIG.advanced_pill_search) {
            //keep a map for visited places
            if (game.getCurrentLevelTime() == 0) {
                mPlaces = new HashSet<>();
                mPlaces.add(game.getPacmanCurrentNodeIndex());
            } else {
                mPlaces.add(game.getPacmanCurrentNodeIndex());
            }
            // update ticks and Fitness values
            if(game.wasPillEaten() || game.wasPowerPillEaten()){
                m_ticks_not_eaten = 0;
                mTooFar = false;
            }
            else{
                m_ticks_not_eaten++;
                if(m_ticks_not_eaten > CONFIG.too_far_time){
                    mTooFar = true;
                    double distance = Double.POSITIVE_INFINITY;
                    //search the nearest target pill
                    for(Integer n :game.getCurrentMaze().pillIndices){
                        if(mPlaces.contains(n) && game.getDistance(game.getPacmanCurrentNodeIndex(),n, Constants.DM.MANHATTAN) < distance){
                            mToo_far_index = n;
                            distance = game.getDistance(game.getPacmanCurrentNodeIndex(),n, Constants.DM.MANHATTAN);
                        }
                    }
                }
            }
        }

        //handle behavior at junctions
        if(game.isJunction(game.getPacmanCurrentNodeIndex())){
            return getRollingHorizonMove(game,timeDue- CONFIG.rh_time_eps);
        }
        //handle behavior if we are not in a junction and a ghost is nearby
        else if(game.getPopulatedGameInfo().getGhosts().keySet().size() > 0 && CONFIG.ghost_reaction){
            //look for the worst ghost
            if(CONFIG.ghost_reaction_near_Ghost_corridor){
                for(Constants.GHOST g : game.getPopulatedGameInfo().getGhosts().keySet()){
                    if(Arrays.asList(game.getNeighbouringNodes(game.getPacmanCurrentNodeIndex())).contains(game.getGhostCurrentNodeIndex(g))){
                        //then the ghost ist the worst, and we need to take definetly the other direction
                        Constants.MOVE notAllowed = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(g), Constants.DM.MANHATTAN);
                        //choose another one
                        for(Constants.MOVE m : game.getPossibleMoves(game.getPacmanCurrentNodeIndex())){
                            if(m != notAllowed){
                                return m;
                            }
                        }
                    }
                }
            }
            return getRollingHorizonMove(game,timeDue-CONFIG.rh_time_eps);
        }
        //if no special case appears follow the corridor
        return getCorridorMove(game);
        }catch (Exception e){
            //catch rare exceptions I could not locate
            //e.printStackTrace();
            return Constants.MOVE.LEFT;
        }
    }



    /**
     * choose a move using an horizan evolutionary algorithm
     * @param g Current game state
     * @return Move that should be made
     */
    private Constants.MOVE getRollingHorizonMove(Game g, long timeDue){
        //Prepare Game for the later simulations
        Game tmp_Game;
        GameInfo inf = g.getPopulatedGameInfo();
        inf.fixGhosts((ghost) -> new Ghost(
                ghost,
                g.getCurrentMaze().lairNodeIndex,
                -1,
                -1,
                Constants.MOVE.NEUTRAL
        ));
        tmp_Game = g.getGameFromInfo(inf);

        //init Population
        LinkedList<Constants.MOVE> moves = new LinkedList<>();
        //distribute first move equally over individuals
        int start_per_move = CONFIG.rh_population_size/g.getNeighbouringNodes(g.getPacmanCurrentNodeIndex()).length;
        for(Constants.MOVE m: g.getPossibleMoves(g.getPacmanCurrentNodeIndex())){
            for(int k = 0; k< start_per_move; ++k){
                moves.add(m);
            }
        }

        //fill the rest to population size
        while(moves.size() < CONFIG.rh_population_size)moves.add(CONFIG.moves[CONFIG.rng.nextInt(CONFIG.moves.length)]);

        List<Constants.MOVE[]> population = new ArrayList<>();
        for(int i = 0 ; i < CONFIG.rh_population_size; ++i){
            Constants.MOVE[] tmp = new Constants.MOVE[CONFIG.rh_individual_size];
            //first move
            tmp[0] = moves.pop();
            //fill rest at random
            for(int k = 1; k < tmp.length; ++k){
                tmp[k] = CONFIG.moves[CONFIG.rng.nextInt(CONFIG.moves.length)];
            }
            population.add(tmp);
        }


        List<Integer> distinctChoice = new ArrayList<>();
        for(int i = 0; i <CONFIG.rh_population_size; ++i) distinctChoice.add(i);

        for(int gen = 0; gen < CONFIG.rh_generations && System.currentTimeMillis() < timeDue; ++gen){
            //Mutating
            for(int i = 0 ; i < CONFIG.rh_mutants; ++i){
                population.add(mutate(population.get(CONFIG.rng.nextInt(CONFIG.rh_population_size))));
            }
            //Crossover
            for(int i = 0; i < CONFIG.rh_children; ++i){
                Collections.shuffle(distinctChoice);
                population.add(crossover(population.get(distinctChoice.get(0)),population.get(distinctChoice.get(2))));
            }
            //generate new Generation by fitness
            population = selection(population,tmp_Game);

        }
        //eval best move to use
        Constants.MOVE result = population.get(0)[0];

        if(CONFIG.rh_advanced_move_selection){
            //find the argmax for the best moves and take it as move
            Constants.MOVE[] possibleMoves = g.getPossibleMoves(g.getPacmanCurrentNodeIndex());
            int[] count = new int[possibleMoves.length];
            for(int i = 0;i < CONFIG.rh_advanced_move_selection_number; ++i){
                for(int k = 0; k < possibleMoves.length; ++k){
                    if(possibleMoves[k]==population.get(i)[0]){
                        count[k] ++;
                    }
                }
            }
            //choose argmax
            int index = 0;
            for(int i= 1; i< count.length; ++i){
                if(count[index]< count[i])
                    index =i;
            }
            result = g.getPossibleMoves(g.getPacmanCurrentNodeIndex())[index];
        }
        return result;
    }



    /**
     * mutate by changing one move in the row
     * @param m individual to mutate
     * @return mutated individual
     */
    private Constants.MOVE[] mutate(Constants.MOVE[] m){
        Constants.MOVE[] result = m.clone();
        result[CONFIG.rng.nextInt(m.length)] = CONFIG.moves[CONFIG.rng.nextInt(CONFIG.moves.length)];
        return result;
    }



    /**
     * perform a uniform crossover
     * @param i1 individual1
     * @param i2 individual2
     * @return child
     */
    private Constants.MOVE[] crossover(Constants.MOVE[] i1, Constants.MOVE[] i2){
        Constants.MOVE[] result = i1.clone();
        for(int i = 0 ; i < result.length; ++i){
            if(CONFIG.rng.nextInt(100) < CONFIG.rh_crossover_swap_probability){
                result[i] = i2[i];
            }
        }
        return result;
    }



    /**
     * performs the fitness evaluation
     * after that select the best regarding the configuration and the rest randomly
     * @param pop current population
     * @return population
     */
    private List<Constants.MOVE[]> selection(List<Constants.MOVE[]> pop,Game g){
        List<FitnessIndividual> fitpop = new ArrayList<>();

        for(Constants.MOVE[] m : pop){
            fitpop.add(new FitnessIndividual(fitness(m,g.copy()),m));
        }

        fitpop.sort((fitnessIndividual, t1) -> Integer.compare(t1.fitness(),fitnessIndividual.fitness()));

        List<Constants.MOVE[]> result = new ArrayList<>();
        //select best
        for(int i = 0; i < CONFIG.rh_best_selected; i++){
            result.add(fitpop.get(i).gene());
        }
        //select random the rest of the population
        List<Integer> distinct = new ArrayList<>();
        for(int i = result.size(); i < fitpop.size(); ++i){distinct.add(i);}
        Collections.shuffle(distinct);
        for(int k = 0; k < CONFIG.rh_population_size - CONFIG.rh_best_selected; ++k){
            result.add(fitpop.get(distinct.get(k)).gene());
        }
        return result;
    }



    /**
     * evaluate the fitness of the individual to a given game
     * @param m individual
     * @param g game copy
     * @return fitness
     */
    private int fitness(Constants.MOVE[] m, Game g){
        //start simulating
        int notAllowedMoves = 0;
        int step = 1;
        int goodDirection = 0;
        int returns = 0;

        //first step to get away from junction and handle stuff for the fitness
        if(Arrays.asList(g.getPossibleMoves(g.getPacmanCurrentNodeIndex())).contains(m[0])){
            if(mTooFar && g.getNextMoveTowardsTarget(g.getPacmanCurrentNodeIndex(),mToo_far_index,
                            g.getPacmanLastMoveMade(), Constants.DM.MANHATTAN)== m[0])
                goodDirection++;
            if(m[0].opposite() == g.getPacmanLastMoveMade()){
                returns++;
            }

            g.advanceGame(m[0],mGhosts.getMove(g,40));
            step++;
        }
        else{
            notAllowedMoves++;
        }

        for(int i = 1; i < m.length; ++i){
            //got through corridor
            while(!g.isJunction(g.getPacmanCurrentNodeIndex())){
                g.advanceGame(getCorridorMove(g),mGhosts.getMove(g, 40));
                step++;
            }
            //if it is a possible move make it and handle stuff for the fitness
            if(Arrays.asList(g.getPossibleMoves(g.getPacmanCurrentNodeIndex())).contains(m[i])){

                if(mTooFar && g.getNextMoveTowardsTarget(g.getPacmanCurrentNodeIndex(),mToo_far_index,
                        g.getPacmanLastMoveMade(), Constants.DM.MANHATTAN)== m[i])
                    goodDirection++;
                if(m[i].opposite() == g.getPacmanLastMoveMade()){
                    returns++;
                }

                g.advanceGame(m[i],mGhosts.getMove(g,40));
                step++;
            }
            else{
                notAllowedMoves++;
            }
        }
        //go through the last move to the next junction
        while(!g.isJunction(g.getPacmanCurrentNodeIndex())){
            g.advanceGame(getCorridorMove(g),mGhosts.getMove(g, 40));
            step++;
        }
        //evaluate fitness
        int fit = g.getScore()
                + g.getPacmanNumberOfLivesRemaining() * CONFIG.rh_fitness_weight_life
                - notAllowedMoves * CONFIG.rh_fitness_weight_not_moved_punishment
                - step * CONFIG.rh_fitness_weight_step_punish
                + goodDirection * CONFIG.rh_fitness_bonus_going_in_the_right_direction
                - returns * CONFIG.rh_fitness_weight_return_punishment;
        return fit;
    }



    /**
     * Function from the example to follow the corridor in which pacman is
     * @param g The current game
     * @return move following the corridor
     */
    private static Constants.MOVE getCorridorMove(Game g){
        // get the current position of PacMan (returns -1 in case you can't see PacMan)
        int myNodeIndex = g.getPacmanCurrentNodeIndex();

        // get all possible moves at the queried position
        Constants.MOVE[] myMoves = g.getPossibleMoves(myNodeIndex);

        Constants.MOVE lastMove = g.getPacmanLastMoveMade();
        if (Arrays.asList(myMoves).contains(lastMove)){
            return lastMove;
        }

        // don't go back (corner)
        for (Constants.MOVE move : myMoves){
            if (move != lastMove.opposite()){
                return move;
            }
        }

        // default
        return lastMove.opposite();
    }



//=========================================================================END==========================================
    public static void main(String[] args) {

        Executor executor = new Executor(true, true);
        executor.setScaleFactor(4);


        EnumMap<Constants.GHOST, IndividualGhostController> controllers = new EnumMap<>(Constants.GHOST.class);

        controllers.put(Constants.GHOST.INKY, new Inky());
        controllers.put(Constants.GHOST.BLINKY, new Blinky());
        controllers.put(Constants.GHOST.PINKY, new Pinky());
        controllers.put(Constants.GHOST.SUE, new Sue());


        if (CONFIG.experiment) {
            Stats[] status = executor.runExperiment(new RollingRodeo(), new MASController(controllers), 50, "Jahuusa");
                System.out.println("AVG : "+status[0].getAverage());
                System.out.println("MAX : "+status[0].getMax());
                System.out.println("MIN : "+status[0].getMin());
                System.out.println("AVG_TICKS : "+ status[1].getAverage());
        } else {
            executor.runGameTimed(new RollingRodeo(), new MASController(controllers), true);
        }
    }


}
