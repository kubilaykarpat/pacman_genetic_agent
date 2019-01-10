package evolution;

import evolution.behaviortree.BehaviorTreePacman;
import pacman.Executor;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.util.Stats;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PacmanExperiment {

	PacmanPopulation pacmanindividuals;
	public Statistics evolution_statistics;
	
	Random RANDOM = new Random();
	
	public static Logger logger; 
	
	private static final int POPULATIONSIZE = 1000;
	private static final int FITNESSEVLUATIONS = 1;
	private static final int TESTRUNS = 3;
	private static final int NR_GENERATIONS = 50;
	private String folder;
	
	
	public PacmanExperiment(String folder){
		//count the number of files and create a new experiment folder
		int files = (new File(folder)).listFiles().length;
		boolean success = (new File(folder + File.separator +"exp"+(files+1))).mkdirs();
		if (!success) {
		    System.out.println("you have no power here!");
		}
		
		success = (new File(folder + File.separator +"exp"+(files+1)+ File.separator + "Pacman")).mkdirs();
		if (!success) {
		    System.out.println("you have no power here!");
		}
		
		success = (new File(folder + File.separator +"exp"+(files+1)+ File.separator + "FinalResult")).mkdirs();
		if (!success) {
		    System.out.println("you have no power here!");
		}
		
		this.folder = folder + File.separator + "exp"+(files+1);
		
		
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

	    pacmanindividuals = new PacmanPopulation(POPULATIONSIZE);
	}
	
	
	public String getFolder(){
		return this.folder;
	}
	
	public void evolve(int generations, boolean storeFinalResult){
		this.evolution_statistics = new Statistics(generations);
		
		for (int i = 0; i < generations; i++){
		    logger.info("Evolution: " + i);  
	
		    logger.info("determine Fitness");
			determineFitness(evolution_statistics);
			logger.info("best Fitness: "+ evolution_statistics.getLatestFitnessPacman());
	
			//record the best team from this generation
			logger.info("store replay");
			BehaviorTreePacman bestPacman = evolution_statistics.getLatestPacman();
//			GAGhosts ghosts = new GAGhosts(bestTeam.get(0),bestTeam.get(1),bestTeam.get(2),bestTeam.get(3));
	        
			//Executor po = new Executor(true, true, true);
	        //po.setDaemon(true);		  
			//po.runGameTimedRecorded(new MyPacMan(), ghosts, false, this.folder + File.separator + "replay"+ i + ".rpl");

			logger.info("store bestTeam");
			bestPacman.storeToFile(this.folder + File.separator + "Pacman" + File.separator + "Pacman" + i + ".xml");
			
			logger.info("Selection and Mutation");
			pacmanindividuals.evolve();

		}
		evolution_statistics.storeToFile(this.folder + File.separator + "Statistic.csv");
			
		if(storeFinalResult){
			logger.info("storeFinalResult");
			
			for (int i = 0; i < pacmanindividuals.getSize(); i++){
				pacmanindividuals.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Pacman" + i + ".xml");
			}
		}
	}
	
	public void evolve(){
		this.evolve(NR_GENERATIONS, true);
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

	public void determineFitness(Statistics evolution_statistics){
		double bestFitness = 0;
		double[] fitnessvalues = new double[FITNESSEVLUATIONS * pacmanindividuals.getSize()];

        BehaviorTreePacman bestPacman = BehaviorTreePacman.createRandomBehaviourTreePacman();

		Executor po = new Executor.Builder()
				.setPacmanPO(true)
				.setGhostPO(true)
				.setGhostsMessage(true)
				.setGraphicsDaemon(true).build();
        POCommGhosts ghosts = new POCommGhosts();
        GAPacman pacman;

		for (int i = 0; i < FITNESSEVLUATIONS; i++){
			pacmanindividuals.shuffle();

			for (int j = 0; j < pacmanindividuals.getSize(); j++){
				pacman = new GAPacman(pacmanindividuals.getIndividual(j));

				Stats[] stats = po.runExperiment(pacman, ghosts, TESTRUNS, "test");
		        double fitness = stats[0].getAverage();
		        fitnessvalues[j + i*pacmanindividuals.getSize()] = fitness;

				pacmanindividuals.getIndividual(j).addFitnessValue(fitness);

				if (fitness > bestFitness){
		        	bestFitness = fitness;
		        	bestPacman = pacmanindividuals.getIndividual(j);
		        }
			}
		}
		double sum = 0;
		for (int i = 0; i < fitnessvalues.length; i++)
			sum += fitnessvalues[i];
		double averagefitness = sum/(FITNESSEVLUATIONS * pacmanindividuals.getSize());

		evolution_statistics.addGenerationPacman(bestFitness, averagefitness, bestPacman);
	}
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args){
		for (int i = 0; i < 8; i++){
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
