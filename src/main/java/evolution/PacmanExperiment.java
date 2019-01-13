package evolution;

import evolution.behaviortree.BehaviorTreePacman;
import org.apache.commons.lang3.Range;
import pacman.Executor;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.util.Stats;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PacmanExperiment {

    private static final int POPULATION_SIZE = 1000;
    private static final int NUMBER_OF_FITNESS_EVALUATIONS = 1;
    private static final int NUMBER_OF_TEST_RUNGS = 3;
    private static final int NUMBER_OF_GENERATIONS = 50;

    private static Range<Float> CROSS_OVER_RANGE = Range.between(0.1f, 0.3f);
    private static Range<Integer> TOURNAMENT_SIZE_RANGE = Range.between(2, 5);
    private static float MUTATION_RATE = 0.1f;
    private static float ELITISIM_RATE = 0.3f;

    private static Logger logger;
    private Statistics evolution_statistics;
    private PacmanPopulation pacmanPopulation;
    private String folder;


    public PacmanExperiment(String folder) {
        //count the number of files and create a new experiment folder
        int files = (new File(folder)).listFiles().length;
        boolean success = (new File(folder + File.separator + "exp" + (files + 1))).mkdirs();
        if (!success) {
            System.out.println("you have no power here!");
        }

        success = (new File(folder + File.separator + "exp" + (files + 1) + File.separator + "Pacman")).mkdirs();
        if (!success) {
            System.out.println("you have no power here!");
        }

        success = (new File(folder + File.separator + "exp" + (files + 1) + File.separator + "FinalResult")).mkdirs();
        if (!success) {
            System.out.println("you have no power here!");
        }

        this.folder = folder + File.separator + "exp" + (files + 1);


        logger = Logger.getLogger("MyLog");
        FileHandler fh;

        // try to store everything into a file
        String filename = this.folder + File.separator + "MyLogFile.log";
        File yourFile = new File(filename);

        // create the logging file if it not exists
        try {
            yourFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(filename);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pacmanPopulation = new PacmanPopulation(POPULATION_SIZE);
    }

    public static void simulate(String folder) {
        int nrfiles = (new File(folder + "\\Pacman")).listFiles().length;

        BehaviorTreePacman tree = BehaviorTreePacman.loadFromFile(folder + "\\Pacman\\Pacman" + (nrfiles - 1) + ".xml");
        GAPacman pacman = new GAPacman(tree);

        Executor po = new Executor.Builder()
                .setPacmanPO(true)
                .setGhostPO(true)
                .setGhostsMessage(true)
                .setGraphicsDaemon(true).build();

        po.runGame(pacman, new POCommGhosts(50), 40);
    }

    public String getFolder() {
        return this.folder;
    }

    /**
     * Get appropriate number from given range proportioned by given normalizedRatio
     *
     * @param rateRange       Any rate range
     * @param normalizedRatio between 0 and 1
     * @return A float within rateRange
     */
    public static float scaleWithRateFloatRange(Range<Float> rateRange, float normalizedRatio) {
        float diff = rateRange.getMaximum() - rateRange.getMinimum();
        return rateRange.getMinimum() + diff * normalizedRatio;
    }

    /**
     * Get appropriate number from given range proportioned by given normalizedRatio
     *
     * @param rateRange       Any rate range
     * @param normalizedRatio between 0 and 1
     * @return A double within rateRange
     */
    public static int scaleWithRateIntRange(Range<Integer> rateRange, float normalizedRatio) {
        int diff = rateRange.getMaximum() - rateRange.getMinimum();
        return Math.round(rateRange.getMinimum() + diff * normalizedRatio);
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            PacmanExperiment PacmanExperiment = new PacmanExperiment("pacmanevaluation");
            PacmanExperiment.evolve();

            PacmanExperiment.evolution_statistics.disp();
        }

		/*
		Scanner input = new Scanner(System.in);
		System.out.println("type 'yes' in case you want to see the result");
	    String answer = input.nextLine();

		if (answer.equals("yes")){
			simulate(PacmanExperiment.folder);
		}
		 */
    }

    public void evolve() {
        this.evolve(NUMBER_OF_GENERATIONS, true);
    }

    public void determineFitness(Statistics evolution_statistics) {
        double bestFitness = 0;
        double[] fitnessValues = new double[NUMBER_OF_FITNESS_EVALUATIONS * pacmanPopulation.getSize()];

        BehaviorTreePacman bestPacman = BehaviorTreePacman.createRandomBehaviourTreePacman();

        Executor po = new Executor.Builder()
                .setPacmanPO(true)
                .setGhostPO(true)
                .setGhostsMessage(true)
                .setGraphicsDaemon(true).build();
        POCommGhosts ghosts = new POCommGhosts();
        GAPacman pacman;

        for (int i = 0; i < NUMBER_OF_FITNESS_EVALUATIONS; i++) {
            pacmanPopulation.shuffle();

            for (int j = 0; j < pacmanPopulation.getSize(); j++) {
                pacman = new GAPacman(pacmanPopulation.getIndividual(j));

                Stats[] stats = po.runExperiment(pacman, ghosts, NUMBER_OF_TEST_RUNGS, "test");
                double fitness = stats[0].getAverage();
                fitnessValues[j + i * pacmanPopulation.getSize()] = fitness;

                pacmanPopulation.getIndividual(j).addFitnessValue(fitness);

                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestPacman = pacmanPopulation.getIndividual(j);
                }
            }
        }
        double sum = 0;
        for (int i = 0; i < fitnessValues.length; i++)
            sum += fitnessValues[i];
        double averagefitness = sum / (NUMBER_OF_FITNESS_EVALUATIONS * pacmanPopulation.getSize());

        evolution_statistics.addGenerationPacman(bestFitness, averagefitness, bestPacman);
    }

    public void evolve(int generations, boolean storeFinalResult) {
        int numberOfMutations = (int) (POPULATION_SIZE * MUTATION_RATE);
        int numberOfElites = (int) (POPULATION_SIZE * ELITISIM_RATE);

        this.evolution_statistics = new Statistics(generations);

        for (int i = 0; i < generations; i++) {
            float generationProgressRation = (float) (i + 1) / generations;
            logger.info("--------------------------------------------");
            logger.info("Evolution: " + i);

            float currentCrossOverRate = scaleWithRateFloatRange(CROSS_OVER_RANGE, 1 - generationProgressRation);
            int numberOfCrossOvers = (int) (POPULATION_SIZE * currentCrossOverRate);
            logger.info("Number of cross overs: " + numberOfCrossOvers);
            pacmanPopulation.crossOver(numberOfCrossOvers);

            logger.info("Number of mutations: " + numberOfMutations);
            pacmanPopulation.mutation(numberOfMutations);

            logger.info("Current population count: " + pacmanPopulation.getSize());

            logger.info("Determine Fitness");
            determineFitness(evolution_statistics);
            logger.info("Best Fitness: " + evolution_statistics.getLatestBestFitnessPacman());

            int currentTournamenSize = scaleWithRateIntRange(TOURNAMENT_SIZE_RANGE, generationProgressRation);
            logger.info("Current tournament count: " + pacmanPopulation.getSize());
            pacmanPopulation.selection(numberOfElites, currentTournamenSize);

            //record the best team from this generation
            logger.info("Store replay");
            BehaviorTreePacman bestPacman = evolution_statistics.getLatestPacman();

            logger.info("Store bestTeam");
            bestPacman.storeToFile(this.folder + File.separator + "Pacman" + File.separator + "Pacman" + i + ".xml");

            logger.info("Selection and Mutation");

        }
        evolution_statistics.storeToFile(this.folder + File.separator + "Statistic.csv");

        if (storeFinalResult) {
            logger.info("storeFinalResult");

            for (int i = 0; i < pacmanPopulation.getSize(); i++) {
                pacmanPopulation.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Pacman" + i + ".xml");
            }
        }
    }

    public void evolve() {
        this.evolve(NUMBER_OF_GENERATIONS, true);
    }

    public void determineFitness(Statistics evolution_statistics) {
        double bestFitness = 0;
        double[] fitnessValues = new double[NUMBER_OF_FITNESS_EVALUATIONS * pacmanPopulation.getSize()];

        BehaviorTreePacman bestPacman = BehaviorTreePacman.createRandomBehaviourTreePacman();

        Executor po = new Executor.Builder()
                .setPacmanPO(true)
                .setGhostPO(true)
                .setGhostsMessage(true)
                .setGraphicsDaemon(true).build();
        POCommGhosts ghosts = new POCommGhosts();
        GAPacman pacman;

        for (int i = 0; i < NUMBER_OF_FITNESS_EVALUATIONS; i++) {
            pacmanPopulation.shuffle();

            for (int j = 0; j < pacmanPopulation.getSize(); j++) {
                pacman = new GAPacman(pacmanPopulation.getIndividual(j));

                Stats[] stats = po.runExperiment(pacman, ghosts, NUMBER_OF_TEST_RUNGS, "test");
                double fitness = stats[0].getAverage();
                fitnessValues[j + i * pacmanPopulation.getSize()] = fitness;

                pacmanPopulation.getIndividual(j).addFitnessValue(fitness);

                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestPacman = pacmanPopulation.getIndividual(j);
                }
            }
        }
        double sum = 0;
        for (int i = 0; i < fitnessValues.length; i++)
            sum += fitnessValues[i];
        double averagefitness = sum / (NUMBER_OF_FITNESS_EVALUATIONS * pacmanPopulation.getSize());

        evolution_statistics.addGenerationPacman(bestFitness, averagefitness, bestPacman);
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        for (int i = 0; i < 8; i++) {
            PacmanExperiment PacmanExperiment = new PacmanExperiment("pacmanevaluation");
            PacmanExperiment.evolve();

            PacmanExperiment.evolution_statistics.disp();
        }

		/*
		Scanner input = new Scanner(System.in);
		System.out.println("type 'yes' in case you want to see the result");
	    String answer = input.nextLine();

		if (answer.equals("yes")){
			simulate(PacmanExperiment.folder);
		}
		 */
    }
}
