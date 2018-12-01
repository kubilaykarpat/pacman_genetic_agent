package evolution.ghostevaluation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import evolution.behaviortree.ghosts.BehaviorTree;
import evolution.ghosts.GAGhosts;
import examples.StarterPacMan.MyPacMan;
import mcts.MCTSAIPacMan;
import pacman.Executor;
import pacman.game.util.Stats;

public class SinglePopulation {

	Population pop;
	Statistics evolution_statistics;
	
	public static Logger logger; 
	
	private static final int POPULATIONSIZE = 1000;
	private static final int FITNESSEVLUATIONS = 10;
	private static final int TESTRUNS = 3;
	private static final int NR_GENERATIONS = 50;
	private String folder;
	
	
	public SinglePopulation(String folder){
		//count the number of files and create a new experiment folder
		int files = (new File(folder)).listFiles().length;
		boolean success = (new File(folder + File.separator +"exp"+(files+1))).mkdirs();
		if (!success) {
		    System.out.println("you have no power here!");
		}
		
		success = (new File(folder + File.separator +"exp"+(files+1)+ File.separator + "Ghosts")).mkdirs();
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

		pop = new Population(POPULATIONSIZE);
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
			logger.info("best Fitness: "+ evolution_statistics.getLatestFitnessGhosts());
	
			//record the best team from this generation
			logger.info("store replay");
			LinkedList<BehaviorTree> bestTeam = evolution_statistics.getLatestTeam();
//			GAGhosts ghosts = new GAGhosts(bestTeam.get(0),bestTeam.get(1),bestTeam.get(2),bestTeam.get(3));
	        
			//Executor po = new Executor(true, true, true);
	        //po.setDaemon(true);		  
			//po.runGameTimedRecorded(new MyPacMan(), ghosts, false, this.folder + File.separator + "replay"+ i + ".rpl");

			logger.info("store bestTeam");
			bestTeam.get(0).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Blinky" + i + ".xml");
			bestTeam.get(1).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Inky" + i + ".xml");
			bestTeam.get(2).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Pinky" + i + ".xml");
			bestTeam.get(3).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Sue" + i + ".xml");
			
			logger.info("Selection and Mutation");
			pop.evolve();
		}
		evolution_statistics.storeToFile(this.folder + File.separator + "Statistic_SinglePopulation_MCTS.csv");
			
		if(storeFinalResult){
			logger.info("storeFinalResult");
			
			for (int i = 0; i < pop.getSize(); i++){
				pop.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Blinky" + i + ".xml");
				pop.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Inky" + i + ".xml");
				pop.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Pinky" + i + ".xml");
				pop.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Sue" + i + ".xml");
			}
		}
	}
	
	public void evolve(){
		this.evolve(NR_GENERATIONS, true);
	}
	
	public void determineFitness(Statistics evolution_statistics){
		double bestFitness = Double.MAX_VALUE;
		double[] fitnessvalues = new double[FITNESSEVLUATIONS * pop.getSize()];
		
		LinkedList<BehaviorTree> bestTeam = new LinkedList<BehaviorTree>();
	    
        Executor po = new Executor(true, true, true);
        po.setDaemon(true);	
        GAGhosts ghosts;
        
		for (int i = 0; i < FITNESSEVLUATIONS; i++){
			pop.shuffle();
			
			for (int j = 0; j < pop.getSize()/4; j++){
				ghosts = new GAGhosts(pop.getIndividual(j),pop.getIndividual(j+250),pop.getIndividual(j+500),pop.getIndividual(j+750));
		        
		        Stats[] stats = po.runExperiment(new MCTSAIPacMan(), ghosts, TESTRUNS, "test");
		        double fitness = stats[0].getAverage();
		        fitnessvalues[j + i*pop.getSize()] = fitness;
		        
		        pop.getIndividual(j).addFitnessValue(fitness);
		        pop.getIndividual(j+250).addFitnessValue(fitness);
		        pop.getIndividual(j+500).addFitnessValue(fitness);
		        pop.getIndividual(j+750).addFitnessValue(fitness);
		        
		        if (fitness < bestFitness){
		        	bestFitness = fitness;
		        	bestTeam.clear();
		        	bestTeam.add(pop.getIndividual(j));
		        	bestTeam.add(pop.getIndividual(j+250));
		        	bestTeam.add(pop.getIndividual(j+500));
		        	bestTeam.add(pop.getIndividual(j+750));
		        }
			}
		}
		double sum = 0;
		for (int i = 0; i < fitnessvalues.length; i++)
			sum += fitnessvalues[i];
		double averagefitness = sum/(FITNESSEVLUATIONS * pop.getSize()/4);
		
		evolution_statistics.addGenerationGhosts(bestFitness, averagefitness, bestTeam);
	}

	public static void simulate(String folder){
		int filesPerGhost = (new File(folder + "\\Ghosts")).listFiles().length / 4;

		BehaviorTree tree1 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Blinky" + (filesPerGhost -1) + ".xml");
		BehaviorTree tree2 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Inky" + (filesPerGhost -1) + ".xml");
		BehaviorTree tree3 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Pinky" + (filesPerGhost -1) + ".xml");
		BehaviorTree tree4 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Sue" + (filesPerGhost -1) + ".xml");

		GAGhosts ghosts = new GAGhosts(tree1, tree2, tree3, tree4);
		Executor po = new Executor(true, true, true);
        po.setDaemon(true);	
        //po.runGame(new MCTSAIPacMan(), ghosts, true, 40);
        po.runGame(new MCTSAIPacMan(), ghosts, true, 40);

	}
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args){
		for(int i = 0; i < 2; i++)
		{
			SinglePopulation multipop = new SinglePopulation("evaluation");
			multipop.evolve();

			multipop.evolution_statistics.disp();
		}
		
		
//		Scanner input = new Scanner(System.in);
//		System.out.println("type 'yes' in case you want to see the result");
//	    String answer = input.nextLine();
//	    
//		if (answer.equals("yes")){
//			simulate(multipop.folder);
//		}

		//simulate("evaluation\\exp49");
		
	}
}
