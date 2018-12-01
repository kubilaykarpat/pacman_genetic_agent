package evolution.mixedevaluation;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import evolution.behaviortree.ghosts.BehaviorTree;
import evolution.behaviortree.pacman.BehaviorTreePacman;
import evolution.ghostevaluation.Population;
import evolution.ghostevaluation.Statistics;
import evolution.ghosts.GAGhosts;
import evolution.pacmanevaluation.GAPacman;
import evolution.pacmanevaluation.PacmanPopulation;
import pacman.Executor;
import pacman.game.util.Stats;

public class Mixedpopulation {

	Population pop1;
	Population pop2;
	Population pop3;
	Population pop4;
	
	PacmanPopulation poppacman;
	
	Statistics evolution_statistics;
	
	public static Logger logger; 
	
	private static final int POPULATIONSIZE = 250;
	private static final int FITNESSEVLUATIONS = 10;
	private static final int TESTRUNS = 3;
	private static final int NR_GENERATIONS = 50;
	private String folder;
	
	
	public Mixedpopulation(String folder){
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

		pop1 = new Population(POPULATIONSIZE);
		pop2 = new Population(POPULATIONSIZE);
		pop3 = new Population(POPULATIONSIZE);
		pop4 = new Population(POPULATIONSIZE);
		
		poppacman = new PacmanPopulation(POPULATIONSIZE*4);
	}
	
	
	public String getFolder(){
		return this.folder;
	}
	
	public void evolve(int generations, boolean storeFinalResult){
		this.evolution_statistics = new Statistics(generations);
		
		for (int i = 0; i < generations; i++){
		    logger.info("Evolution: " + i);  
	
		    logger.info("determine Fitness");
		    if (i == 0){
		    	determineFitnessGhosts(evolution_statistics, poppacman.getIndividual(0));
		    	
		    	LinkedList<BehaviorTree> teamlist= new LinkedList<BehaviorTree>();
		    	teamlist.add(pop1.getIndividual(0));
		    	teamlist.add(pop2.getIndividual(0));
		    	teamlist.add(pop3.getIndividual(0));
		    	teamlist.add(pop4.getIndividual(0));
				determineFitnessPacman(evolution_statistics, teamlist);

		    } else
		    {
		    	determineFitnessGhosts(evolution_statistics, evolution_statistics.getLatestPacman());
				determineFitnessPacman(evolution_statistics, evolution_statistics.getLatestTeam());
		    }
			
			logger.info("best FitnessGhosts: "+ evolution_statistics.getLatestFitnessGhosts());
			logger.info("best FitnessPacman: "+ evolution_statistics.getLatestFitnessPacman());

			//record the best team from this generation
			LinkedList<BehaviorTree> bestTeam = evolution_statistics.getLatestTeam();
			BehaviorTreePacman bestPacman = evolution_statistics.getLatestPacman();

//			GAGhosts ghosts = new GAGhosts(bestTeam.get(0),bestTeam.get(1),bestTeam.get(2),bestTeam.get(3));
	        
			//Executor po = new Executor(true, true, true);
	        //po.setDaemon(true);		  
			//po.runGameTimedRecorded(new MyPacMan(), ghosts, false, this.folder + File.separator + "replay"+ i + ".rpl");

			logger.info("store bestTeam");
			bestTeam.get(0).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Blinky" + i + ".xml");
			bestTeam.get(1).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Inky" + i + ".xml");
			bestTeam.get(2).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Pinky" + i + ".xml");
			bestTeam.get(3).storeToFile(this.folder + File.separator + "Ghosts" + File.separator + "Sue" + i + ".xml");
			
			logger.info("store bestPacman");
			bestPacman.storeToFile(this.folder + File.separator + "Pacman" + File.separator + "Pacman" + i + ".xml");

			
			logger.info("Selection and Mutation");
			pop1.evolve();
			pop2.evolve();
			pop3.evolve();
			pop4.evolve();
			
			poppacman.evolve();
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
			
			for (int i = 0; i < poppacman.getSize(); i++){
				poppacman.getIndividual(i).storeToFile(this.folder + File.separator + "FinalResult" + File.separator + "Pacman" + i + ".xml");
			}
		}
	}
	
	public void evolve(){
		this.evolve(NR_GENERATIONS, true);
	}
	
	public void determineFitnessPacman(Statistics evolution_statistics, LinkedList<BehaviorTree> bestGhostTeam){
  	    double bestFitness = 0;
		double[] fitnessvalues = new double[FITNESSEVLUATIONS * poppacman.getSize()];
		
	    
        Executor po = new Executor(true, true, true);
        po.setDaemon(true);	
        GAPacman pacman;
        
        GAGhosts ghosts = new GAGhosts(bestGhostTeam.get(0), bestGhostTeam.get(1), bestGhostTeam.get(2), bestGhostTeam.get(3));
        
		for (int i = 0; i < FITNESSEVLUATIONS; i++){
			
			for (int j = 0; j < poppacman.getSize(); j++){
				pacman = new GAPacman(poppacman.getIndividual(j));
		        
		        Stats[] stats = po.runExperiment(pacman, ghosts, TESTRUNS, "test");
		        double fitness = stats[0].getAverage();
		        fitnessvalues[j + i*poppacman.getSize()] = fitness;
		        
		        poppacman.getIndividual(j).addFitnessValue(fitness);
		        
		        if (fitness > bestFitness){
		        	bestFitness = fitness;
		        }
			}
		}
		double sum = 0;
		for (int i = 0; i < fitnessvalues.length; i++)
			sum += fitnessvalues[i];
		double averagefitness = sum/(FITNESSEVLUATIONS * poppacman.getSize());
		
		double bestOfTheBest = Double.MAX_VALUE;
		double worstOfTheWorst = Double.MIN_VALUE;
		for (double d : fitnessvalues){
			if (d < bestOfTheBest)
				bestOfTheBest = d;
			if (d > worstOfTheWorst)
				worstOfTheWorst = d;
		}
		System.out.println("best pacman: "+ bestOfTheBest);
		System.out.println("worst pacman: " + worstOfTheWorst);
		
		Collections.sort(poppacman.individuals);
		evolution_statistics.addGenerationPacman(poppacman.getIndividual(0).getFitness(), averagefitness, poppacman.getIndividual(0));

	}
	
	public void determineFitnessGhosts(Statistics evolution_statistics, BehaviorTreePacman bestPacman){
		double bestFitnessGhosts = Double.MAX_VALUE;
		double[] fitnessvaluesGhosts = new double[FITNESSEVLUATIONS * pop1.getSize()];
		
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
		        GAPacman pacman = new GAPacman(bestPacman);
				ghosts = new GAGhosts(pop1.getIndividual(j),pop2.getIndividual(j),pop3.getIndividual(j),pop4.getIndividual(j));
		        
		        Stats[] stats = po.runExperiment(pacman, ghosts, TESTRUNS, "test");
		        double fitness = stats[0].getAverage();
		        fitnessvaluesGhosts[j + i*pop1.getSize()] = fitness;
		        
		        pop1.getIndividual(j).addFitnessValue(fitness);
		        pop2.getIndividual(j).addFitnessValue(fitness);
		        pop3.getIndividual(j).addFitnessValue(fitness);
		        pop4.getIndividual(j).addFitnessValue(fitness);
		        
		        if (fitness < bestFitnessGhosts){
		        	bestFitnessGhosts = fitness;
		        	bestTeam.clear();
		        	bestTeam.add(pop1.getIndividual(j));
		        	bestTeam.add(pop2.getIndividual(j));
		        	bestTeam.add(pop3.getIndividual(j));
		        	bestTeam.add(pop4.getIndividual(j));
		        }
			}
		}
		double sum = 0;
		for (int i = 0; i < fitnessvaluesGhosts.length; i++)
			sum += fitnessvaluesGhosts[i];
		double averagefitness = sum/(FITNESSEVLUATIONS * pop1.getSize());
		
		double bestOfTheBest = Double.MAX_VALUE;
		double worstOfTheWorst = Double.MIN_VALUE;
		for (double d : fitnessvaluesGhosts){
			if (d < bestOfTheBest)
				bestOfTheBest = d;
			if (d > worstOfTheWorst)
				worstOfTheWorst = d;
		}
		System.out.println("best ghost: "+ bestOfTheBest);
		System.out.println("worst ghost: " + worstOfTheWorst);
		evolution_statistics.addGenerationGhosts(bestFitnessGhosts, averagefitness, bestTeam);
	}

	public static void simulate(String folder){
		int filesPerGhost = (new File(folder + "\\Ghosts")).listFiles().length / 4;

		BehaviorTree tree1 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Blinky" + (filesPerGhost -1) + ".xml");
		BehaviorTree tree2 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Inky" + (filesPerGhost -1) + ".xml");
		BehaviorTree tree3 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Pinky" + (filesPerGhost -1) + ".xml");
		BehaviorTree tree4 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Sue" + (filesPerGhost -1) + ".xml");

		int filesPacman = (new File(folder + "\\Pacman")).listFiles().length;
		BehaviorTreePacman treepacman = BehaviorTreePacman.loadFromFile(folder + "\\Pacman\\Pacman" + (filesPacman -1) + ".xml");

		
		GAGhosts ghosts = new GAGhosts(tree1, tree2, tree3, tree4);
		GAPacman pacman = new GAPacman(treepacman);
		
		Executor po = new Executor(true, true, true);
        po.setDaemon(true);	
        po.runGame(pacman, ghosts, true, 40);
	}
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args){
		for (int i = 0; i < 2; i++)
		{
			Mixedpopulation mixedpop = new Mixedpopulation("evaluationmixed");
			mixedpop.evolve();

			mixedpop.evolution_statistics.disp();
		}
		/*
		Scanner input = new Scanner(System.in);
		System.out.println("type 'yes' in case you want to see the result");
	    String answer = input.nextLine();
	    
		if (answer.equals("yes")){
			simulate(mixedpop.folder);
		}
		*/
		
	}
}
