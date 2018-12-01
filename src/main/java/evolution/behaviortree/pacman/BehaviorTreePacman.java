package evolution.behaviortree.pacman;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import evolution.behaviortree.pacman.functionnodes.FunctionNodePacman;
import evolution.behaviortree.pacman.terminalnodes.actionnodes.ActionTerminalNodePacman;
import evolution.ghostevaluation.Genotype;
import evolution.pacmanevaluation.ExtendedGamePacman;
import pacman.game.Constants.MOVE;

public class BehaviorTreePacman implements Genotype, Comparable<BehaviorTreePacman> {

	
	BehaviorNodePacman root;
	private static final Random RANDOM = new Random();
	private List<Double> fitnesslist;
	
	public BehaviorTreePacman(){
		this(FunctionNodePacman.createRandomActionTarget()); 
	}
	
	public BehaviorTreePacman copy(){
		return new BehaviorTreePacman(this.root.copy());
	}
	
	BehaviorTreePacman(BehaviorNodePacman root){
		this.root = root;
		this.fitnesslist = new LinkedList<Double>();
	}
	
	public MOVE eval(ExtendedGamePacman game){
		return ((ActionTerminalNodePacman)this.root.eval(game)).getAction(game);
	}
	
	public String toString(){		
		System.out.println("<BehaviorTreePacman>");
		
		this.root.disp(1);
		
		System.out.println("</BehaviorTreePacman>");
		return "";
	}
	
	public List<BehaviorNodePacman> getNodes(){
		List<BehaviorNodePacman> nodelist = new LinkedList<BehaviorNodePacman>();
		this.root.getNodes(nodelist);
		return nodelist;
	}
	
	public List<BehaviorNodePacman> getNonterminalNodes(){
		List<BehaviorNodePacman> nodelist = new LinkedList<BehaviorNodePacman>();
		this.root.getMutableNodes(nodelist);
		return nodelist;
	}
	
	public void mutate(){
		List<BehaviorNodePacman> nodes = this.getNonterminalNodes();
		nodes.get(RANDOM.nextInt(nodes.size())).mutate();;	
	}
	
	public BehaviorNodePacman getRandomNode(){
		List<BehaviorNodePacman> nodes = this.getNodes();
		return nodes.get(RANDOM.nextInt(nodes.size()));
	}

	@Override
	public double getFitness() {
		return calculateAverage(fitnesslist);
	}

	public void clearFitness() {
		this.fitnesslist.clear();
	}

	
	private static double calculateAverage(List <Double> marks) {
		  Double sum = new Double(0);
		  if(!marks.isEmpty()) {
		    for (Double mark : marks) {
		        sum += mark;
		    }
		    return sum.doubleValue() / marks.size();
		  }
		  return sum;
	}

	@Override
	public int compareTo(BehaviorTreePacman tree) {
	    Double myFitness = this.getFitness();
	    Double oFitness = tree.getFitness();
	    return oFitness.compareTo(myFitness);
	}
	
	@Override
	public void addFitnessValue(double fitness) {
		fitnesslist.add(fitness);
		
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
		 
	    System.out.println(this);
	    System.setOut(stdout);  
	}
	
	public static BehaviorTreePacman loadFromFile(String filename){
		Scanner in;
		try {
			in = new Scanner(new FileReader(filename));
			if (in.hasNextLine()){
				if (in.nextLine().startsWith("<BehaviorTreePacman>"))
				{
					BehaviorNodePacman root = BehaviorNodePacman.loadFromFile(in);
					return new BehaviorTreePacman(root);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

		System.out.println("invalid File");
		return null;

		
		
	}

	public static void main(String[] args){
		BehaviorTreePacman tree = new BehaviorTreePacman();
		System.out.println(tree);
		
		List<BehaviorNodePacman> nodelist = tree.getNodes();
		System.out.println("#nodes: " + nodelist.size());
		System.out.println(nodelist);
		System.out.println();
		
		List<BehaviorNodePacman> nonterminalnodelist = tree.getNonterminalNodes();
		System.out.println(nonterminalnodelist);
		System.out.println("#nonterminal nodes: " + nonterminalnodelist.size());
		System.out.println();

		BehaviorNodePacman node = tree.getRandomNode();
		node.disp(0);
		System.out.println();
		
		System.out.println("Mutated tree:");
		for (int i = 0; i < 1000; i++)
			tree.mutate();
		System.out.println(tree);
		
		System.out.println("Copied tree:");
		BehaviorTreePacman copiedtree = tree.copy();
		System.out.println(copiedtree);
		
		
		System.out.println("Check if Deep Copy worked:");
		System.out.println("Mutated tree:");
		for (int i = 0; i < 10; i++)
			tree.mutate();
		System.out.println(tree);
		System.out.println();
		
		System.out.println("Copied Tree");
		System.out.println(copiedtree);
		
		tree.storeToFile("evaluation" + File.separator + "tree.xml");
		loadFromFile("evaluation" + File.separator + "tree.xml");
		//System.out.println(tree);
	}

}
