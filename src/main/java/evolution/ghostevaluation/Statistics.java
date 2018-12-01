package evolution.ghostevaluation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import evolution.behaviortree.ghosts.BehaviorTree;
import evolution.behaviortree.pacman.BehaviorTreePacman;

public class Statistics {
	
	double[] bestFitnessGhost;
	double[] averageFitnessGhosts;
	double[] bestFitnessPacman;
	double[] averageFitnessPacman;
	List<LinkedList<BehaviorTree>> bestTeam;
	List<BehaviorTreePacman> bestPacman;
	int generationGhosts = 0;
	int generationPacman = 0;
	
	public Statistics (int generations){
		this.bestFitnessGhost = new double[generations];
		this.averageFitnessGhosts = new double[generations];
		this.bestFitnessPacman = new double[generations];
		this.averageFitnessPacman = new double[generations];
		this.bestTeam = new LinkedList<LinkedList<BehaviorTree>>();
		this.bestPacman = new LinkedList<BehaviorTreePacman>();

	}
	
	public void addGenerationGhosts(double fitnessvalue, double averageFitnessValue, LinkedList<BehaviorTree> bestTeam)
	{
		this.bestFitnessGhost[generationGhosts] = fitnessvalue;
		this.averageFitnessGhosts[generationGhosts] = averageFitnessValue;
		this.bestTeam.add(bestTeam);
		generationGhosts++;
	}
	
	public void addGenerationPacman(double fitnessvalue, double averageFitnessValue, BehaviorTreePacman bestPacman)
	{
		this.bestFitnessPacman[generationPacman] = fitnessvalue;
		this.averageFitnessPacman[generationPacman] = averageFitnessValue;
		this.bestPacman.add(bestPacman);
		generationPacman++;
	}
	
	public double getLatestFitnessGhosts(){
		return bestFitnessGhost[generationGhosts-1];
	}
	
	public double getLatestAverageFitnessGhosts(){
		return averageFitnessGhosts[generationGhosts-1];
	}
	
	public double getLatestFitnessPacman(){
		return bestFitnessPacman[generationPacman-1];
	}
	
	public double getLatestAverageFitnessPacman(){
		return averageFitnessPacman[generationPacman-1];
	}
	
	public LinkedList<BehaviorTree> getLatestTeam(){
		return this.bestTeam.get(this.bestTeam.size()-1);
	}
	
	public BehaviorTreePacman getLatestPacman(){
		return this.bestPacman.get(this.bestPacman.size()-1);
	}
	
	public void disp(){
		System.out.println("Generation; fitnessGhosts; averageFitnessGhosts; fitnessPacman; averageFitnessPacman");
		for (int i = 0; i < bestFitnessGhost.length; i++){
			System.out.println("" + i + "; " + bestFitnessGhost[i] + "; " + averageFitnessGhosts[i] + "; " + bestFitnessPacman[i] + "; " + averageFitnessPacman[i]);
		}
	}
	
	public void storeToFile(String file){
		PrintStream stdout = System.out;
		
		// try to store everything into a file
		File yourFile = new File(file);
		
		// create the logging file if it not exists
		try {
			yourFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// forward the output stream
	    try {
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	    this.disp();
	    System.setOut(stdout);  
	}
	
}
