package evolution.behaviortree.ghosts;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import evolution.behaviortree.ghosts.functionnodes.FunctionNode.Target;
import evolution.behaviortree.ghosts.functionnodes.IfElseNode;
import evolution.behaviortree.ghosts.functionnodes.IfLessThanElseNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.AndNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.NotNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.OrNode;
import evolution.behaviortree.ghosts.functionnodes.booleannode.XorNode;
import evolution.behaviortree.ghosts.terminalnodes.actionnode.ActionTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.actionnode.ActionTerminalNode.ActionTerminal;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.IsEdible;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.IsPacManClose;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.IsPowerPillStillAvailable;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.SeeingPacman;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.StaticBooleanTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.numericalnodes.DistanceToOtherGhosts;
import evolution.behaviortree.ghosts.terminalnodes.numericalnodes.DoubleTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.numericalnodes.EdibleTime;
import evolution.behaviortree.ghosts.terminalnodes.numericalnodes.EstimatedDistanceOptimistic;
import evolution.behaviortree.ghosts.terminalnodes.numericalnodes.EstimatedDistancePessimistic;
import evolution.behaviortree.ghosts.terminalnodes.numericalnodes.RandomNumberTerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public abstract class BehaviorNode {

	protected static final Random RANDOM = new Random();

	public abstract BehaviorNode eval(ExtendedGameGhosts game, Constants.GHOST ghosttype);
	
	public abstract void disp(int depth);
	
	public abstract List<BehaviorNode> getNodes(List<BehaviorNode> list);
	
	public abstract List<BehaviorNode> getMutableNodes(List<BehaviorNode> list);
	
	public abstract void mutate();

	public abstract BehaviorNode copy();
	
	public static BehaviorNode loadFromFile(Scanner in){
		BehaviorNode node;
		Target target;
		String line = "";
		String nextLine;
		
		if (in.hasNextLine()){
			line =  in.nextLine().trim();
			switch (line){
				case "<IfLessThanElseNode>": 
					target = Target.valueOf(in.nextLine().trim());
					node = new IfLessThanElseNode(loadFromFile(in), loadFromFile(in), loadFromFile(in), loadFromFile(in), target);
					nextLine = in.nextLine().trim();
					if (in.hasNextLine() &&  nextLine.startsWith("</IfLessThanElseNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<IfElseNode>": 
					target = Target.valueOf(in.nextLine().trim());
					node = new IfElseNode(loadFromFile(in), loadFromFile(in), loadFromFile(in), target);
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</IfElseNode>"))
						return node;
					break;
					
				case "<AndNode>":
					node = new AndNode(loadFromFile(in), loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</AndNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<OrNode>":
					node = new OrNode(loadFromFile(in), loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</OrNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<XorNode>":
					node = new XorNode(loadFromFile(in), loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</XorNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<NotNode>":
					node = new NotNode(loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</NotNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<ActionTerminalNode>":
					node = new ActionTerminalNode(ActionTerminal.valueOf(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</ActionTerminalNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<IsEdible></IsEdible>":
					node = new IsEdible();
					return node;
					
				case "<IsPacManClose>":
					node = new IsPacManClose(new Integer(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</IsPacManClose>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<IsPacManCloseToPower>":
					node = new IsPacManClose(new Integer(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</IsPacManCloseToPower>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<IsPowerPillStillAvailable></IsPowerPillStillAvailable>":
					node = new IsPowerPillStillAvailable();
					return node;
					
				case "<SeeingPacman></SeeingPacman>":
					node = new SeeingPacman();
					return node;
					
				case "<StaticBooleanTerminalNode>":
					node = new StaticBooleanTerminalNode(Boolean.getBoolean(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</StaticBooleanTerminalNode>"))
						return node;
					System.out.println("closing incorrect");
					break;
					
				case "<EdibleTime></EdibleTime>":
					node = new EdibleTime();
					return node;
					
				case "<EstimatedDistanceOptimistic></EstimatedDistanceOptimistic>":
					node = new EstimatedDistanceOptimistic();
					return node;
					
				case "<EstimatedDistancePessimistic></EstimatedDistancePessimistic>":
					node = new EstimatedDistancePessimistic();
					return node;
					
				case "<DoubleTerminalNode>":
					node = new DoubleTerminalNode(new Double(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</DoubleTerminalNode>"))
						return node;
					System.out.println("closing incorrect");
					break;
				
				case "<IntegerTerminalNode>":
					node = new DoubleTerminalNode(new Double(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</IntegerTerminalNode>"))
						return node;
					System.out.println("closing incorrect");
					break;
					
				case "<RandomNumberTerminalNode></RandomNumberTerminalNode>":
					node = new RandomNumberTerminalNode();
					return node;
					
				case "<DistanceToOtherGhosts></DistanceToOtherGhosts>":
					node = new DistanceToOtherGhosts();
					return node;
					
				default:
					break;
					
			}
		}
		//you shouldn't reach this
		System.out.println("Invalid Line:" + line);
		return null;
	}
}
