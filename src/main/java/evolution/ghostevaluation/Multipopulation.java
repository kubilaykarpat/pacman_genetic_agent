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

public class Multipopulation {

	Population pop1;
	Population pop2;
	Population pop3;
	Population pop4;
	Statistics evolution_statistics;
	
	public static Logger logger; 
	
	private static final int POPULATIONSIZE = 250;
	private static final int FITNESSEVLUATIONS = 10;
	private static final int TESTRUNS = 3;
	private static final int NR_GENERATIONS = 50;
	private String folder;
	
	
	public Multipopulation(String folder){
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

		pop1 = new Population(POPULATIONSIZE);
		pop2 = new Population(POPULATIONSIZE);
		pop3 = new Population(POPULATIONSIZE);
		pop4 = new Population(POPULATIONSIZE);
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
			pop1.evolve();
			pop2.evolve();
			pop3.evolve();
			pop4.evolve();
		}
		evolution_statistics.storeToFile(this.folder + File.separator + "Statistic.csv");
			
		if(storeFinalResult){
			logger.info("storeFinalResult");
			
			for (int i = 0; i < pop1.getSize(); i++){
				pop1.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Blinky" + i + ".xml");
				pop2.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Inky" + i + ".xml");
				pop3.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Pinky" + i + ".xml");
				pop4.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Sue" + i + ".xml");
			}
		}
	}
	
	public void evolve(){
		this.evolve(NR_GENERATIONS, true);
	}
	
	public void determineFitness(Statistics evolution_statistics){
		double bestFitness = Double.MAX_VALUE;
		double[] fitnessvalues = new double[FITNESSEVLUATIONS * pop1.getSize()];
		
		LinkedList<BehaviorTree> bestTeam = new LinkedList<BehaviorTree>();
	    
        Executor po = new Executor(true, true, true);
        po.setDaemon(true);	
        GAGhosts ghosts;
        
		for (int i = 0; i < FITNESSEVLUATIONS; i++){
			pop1.shuffle();
			pop2.shuffle();
			pop3.shuffle();
			pop4.shuffle();
			
			for (int j = 0; j < pop1.getSize(); j++){
				ghosts = new GAGhosts(pop1.getIndividual(j),pop2.getIndividual(j),pop3.getIndividual(j),pop4.getIndividual(j));
		        
		        Stats[] stats = po.runExperiment(new MyPacMan(), ghosts, TESTRUNS, "test");
		        double fitness = stats[0].getAverage();
		        fitnessvalues[j + i*pop1.getSize()] = fitness;
		        
		        pop1.getIndividual(j).addFitnessValue(fitness);
		        pop2.getIndividual(j).addFitnessValue(fitness);
		        pop3.getIndividual(j).addFitnessValue(fitness);
		        pop4.getIndividual(j).addFitnessValue(fitness);
		        
		        if (fitness < bestFitness){
		        	bestFitness = fitness;
		        	bestTeam.clear();
		        	bestTeam.add(pop1.getIndividual(j));
		        	bestTeam.add(pop2.getIndividual(j));
		        	bestTeam.add(pop3.getIndividual(j));
		        	bestTeam.add(pop4.getIndividual(j));
		        }
			}
		}
		double sum = 0;
		for (int i = 0; i < fitnessvalues.length; i++)
			sum += fitnessvalues[i];
		double averagefitness = sum/(FITNESSEVLUATIONS * pop1.getSize());
		
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
        po.runGame(new MyPacMan(), ghosts, true, 40);

	}
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args){
		for(int i = 0; i < 5; i++)
		{
			Multipopulation multipop = new Multipopulation("evaluation");
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
