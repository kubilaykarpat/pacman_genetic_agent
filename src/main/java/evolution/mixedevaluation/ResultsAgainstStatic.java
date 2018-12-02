package evolution.mixedevaluation;

import evolution.behaviortree.ghosts.BehaviorTree;
import evolution.behaviortree.pacman.BehaviorTreePacman;
import evolution.ghostevaluation.Statistics;
import evolution.ghosts.GAGhosts;
import evolution.pacmanevaluation.GAPacman;
import evolution.pacmanevaluation.PacmanPopulation;
import examples.StarterPacMan.MyPacMan;
import pacman.Executor;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.util.Stats;

import java.io.File;
import java.util.logging.Logger;

public class ResultsAgainstStatic {
	
	PacmanPopulation poppacman;
		
	public static Logger logger; 
	
	private static final int TESTRUNS = 10;
	private String folder;
	Statistics evolution_statistics;

	
	public ResultsAgainstStatic(String folder){
		Executor po = new Executor.Builder().setPacmanPO(true).setGhostPO(true).setGhostsMessage(true).setGraphicsDaemon(true).build();
        
		int filesPerGhost = (new File(folder + "\\Ghosts")).listFiles().length / 4;
		this.evolution_statistics = new Statistics(filesPerGhost);

		for (int i  = 0; i < filesPerGhost; i++)
		{
			BehaviorTree tree1 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Blinky" + i + ".xml");
			BehaviorTree tree2 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Inky" + i + ".xml");
			BehaviorTree tree3 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Pinky" + i + ".xml");
			BehaviorTree tree4 = BehaviorTree.loadFromFile(folder + "\\Ghosts\\Sue" + i + ".xml");
			
			GAGhosts ghosts = new GAGhosts(tree1, tree2, tree3, tree4);
			
			
			Stats[] stats = po.runExperiment(new MyPacMan(), ghosts, TESTRUNS, "test");
	        double fitness = stats[0].getAverage();
			evolution_statistics.addGenerationGhosts(fitness, fitness, null);

		}
		

		int filesPacman = (new File(folder + "\\Pacman")).listFiles().length;
		for (int i  = 0; i < filesPacman; i++)
		{		
			BehaviorTreePacman treepacman = BehaviorTreePacman.loadFromFile(folder + "\\Pacman\\Pacman" + i+ ".xml");
			GAPacman pacman = new GAPacman(treepacman);

	        Stats[] stats = po.runExperiment(pacman, new POCommGhosts(50), TESTRUNS, "test");
	        double fitness = stats[0].getAverage();
			evolution_statistics.addGenerationPacman(fitness, fitness, null);

		}

		evolution_statistics.storeToFile(this.folder + "\\BestAgainstSimple.csv");
		
	}
	
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args){

		ResultsAgainstStatic mixedpop = new ResultsAgainstStatic("evaluationmixed\\exp7");


		
	}
}
