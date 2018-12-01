package evolution.behaviortree.ghosts;

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

import evolution.behaviortree.ghosts.functionnodes.FunctionNode;
import evolution.behaviortree.ghosts.terminalnodes.actionnode.ActionTerminalNode;
import evolution.ghostevaluation.Genotype;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;

public class BehaviorTree implements Genotype, Comparable<BehaviorTree> {

	
	BehaviorNode root;
	private static final Random RANDOM = new Random();
	private List<Double> fitnesslist;
	
	public BehaviorTree(){
		this(FunctionNode.createRandomActionTarget()); 
	}
	
	public BehaviorTree copy(){
		return new BehaviorTree(this.root.copy());
	}
	
	BehaviorTree(BehaviorNode root){
		this.root = root;
		this.fitnesslist = new LinkedList<Double>();
	}
	
	public MOVE eval(ExtendedGameGhosts game, Constants.GHOST ghosttype){
		return ((ActionTerminalNode)this.root.eval(game, ghosttype)).getAction(game, ghosttype);
	}
	
	public String toString(){		
		System.out.println("<BehaviorTree>");
		
		this.root.disp(1);
		
		System.out.println("</BehaviorTree>");
		return "";
	}
	
	public List<BehaviorNode> getNodes(){
		List<BehaviorNode> nodelist = new LinkedList<BehaviorNode>();
		this.root.getNodes(nodelist);
		return nodelist;
	}
	
	public List<BehaviorNode> getNonterminalNodes(){
		List<BehaviorNode> nodelist = new LinkedList<BehaviorNode>();
		this.root.getMutableNodes(nodelist);
		return nodelist;
	}
	
	public void mutate(){
		List<BehaviorNode> nodes = this.getNonterminalNodes();
		nodes.get(RANDOM.nextInt(nodes.size())).mutate();;	
	}
	
	public BehaviorNode getRandomNode(){
		List<BehaviorNode> nodes = this.getNodes();
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
	public int compareTo(BehaviorTree tree) {
	    Double myFitness = this.getFitness();
	    Double oFitness = tree.getFitness();
	    return myFitness.compareTo(oFitness);
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
	
	public static BehaviorTree loadFromFile(String filename){
		Scanner in;
		try {
			in = new Scanner(new FileReader(filename));
			if (in.hasNextLine()){
				if (in.nextLine().startsWith("<BehaviorTree>"))
				{
					BehaviorNode root = BehaviorNode.loadFromFile(in);
					return new BehaviorTree(root);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

		System.out.println("invalid File");
		return null;

		
		
	}

	public static void main(String[] args){
		BehaviorTree tree = new BehaviorTree();
		System.out.println(tree);
		
		List<BehaviorNode> nodelist = tree.getNodes();
		System.out.println("#nodes: " + nodelist.size());
		System.out.println(nodelist);
		System.out.println();
		
		List<BehaviorNode> nonterminalnodelist = tree.getNonterminalNodes();
		System.out.println(nonterminalnodelist);
		System.out.println("#nonterminal nodes: " + nonterminalnodelist.size());
		System.out.println();

		BehaviorNode node = tree.getRandomNode();
		node.disp(0);
		System.out.println();
		
		System.out.println("Mutated tree:");
		for (int i = 0; i < 1000; i++)
			tree.mutate();
		System.out.println(tree);
		
		System.out.println("Copied tree:");
		BehaviorTree copiedtree = tree.copy();
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
		System.out.println(tree);
	}

}
