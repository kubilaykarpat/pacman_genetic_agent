package evolution.behaviortree;

import evolution.ExtendedGame;
import evolution.behaviortree.functionnodes.FunctionNodePacman.TargetPacman;
import evolution.behaviortree.functionnodes.IfElseNodePacman;
import evolution.behaviortree.functionnodes.IfLessThanElseNodePacman;
import evolution.behaviortree.functionnodes.booleannode.AndNodePacman;
import evolution.behaviortree.functionnodes.booleannode.NotNodePacman;
import evolution.behaviortree.functionnodes.booleannode.OrNodePacman;
import evolution.behaviortree.functionnodes.booleannode.XorNodePacman;
import evolution.behaviortree.terminalnodes.actionnodes.ActionTerminalNodePacman;
import evolution.behaviortree.terminalnodes.actionnodes.ActionTerminalNodePacman.ActionTerminalPacman;
import evolution.behaviortree.terminalnodes.booleannode.*;
import evolution.behaviortree.terminalnodes.numericalnodes.*;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class BehaviorNodePacman {

	protected static final Random RANDOM = new Random();

	public abstract BehaviorNodePacman eval(ExtendedGame game);
	
	public abstract void disp(int depth);

	public abstract List<BehaviorNodePacman> getDirectChildren();
	
	public abstract List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list);
	
	public abstract List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list);
	
	public abstract void mutate();

	public abstract BehaviorNodePacman copy();
	
	public static BehaviorNodePacman loadFromFile(Scanner in){
		BehaviorNodePacman node;
		TargetPacman target;
		String line = "";
		String nextLine;
		
		if (in.hasNextLine()){
			line =  in.nextLine().trim();
			switch (line){
				case "<IfLessThanElseNode>": 
					target = TargetPacman.valueOf(in.nextLine().trim());
					node = new IfLessThanElseNodePacman(loadFromFile(in), loadFromFile(in), loadFromFile(in), loadFromFile(in), target);
					nextLine = in.nextLine().trim();
					if (in.hasNextLine() &&  nextLine.startsWith("</IfLessThanElseNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<IfElseNode>": 
					target = TargetPacman.valueOf(in.nextLine().trim());
					node = new IfElseNodePacman(loadFromFile(in), loadFromFile(in), loadFromFile(in), target);
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</IfElseNode>"))
						return node;
					break;
					
				case "<AndNode>":
					node = new AndNodePacman(loadFromFile(in), loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</AndNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<OrNode>":
					node = new OrNodePacman(loadFromFile(in), loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</OrNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<XorNode>":
					node = new XorNodePacman(loadFromFile(in), loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</XorNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<NotNode>":
					node = new NotNodePacman(loadFromFile(in));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</NotNode>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<ActionTerminalNodePacman>":
					node = new ActionTerminalNodePacman(ActionTerminalPacman.valueOf(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</ActionTerminalNodePacman>"))
						return node;
					System.out.println("closing incorrect");

					break;
					
				case "<AmICloseToPower>":
					node = new AmICloseToPower(new Integer(in.nextLine().trim()));
					nextLine = in.nextLine().trim();
					if (in.hasNextLine() && nextLine.startsWith("</AmICloseToPower>"))
						return node;
					System.out.println("closing incorrect");
					
					return node;
					
					
				case "<IsEmpoweredPacman></IsEmpoweredPacman>":
					node = new IsEmpoweredPacman();
					return node;
					
			
				case "<IsGhostClosePacman>":
					node = new IsGhostClosePacman(new Integer(in.nextLine().trim()));
					nextLine = in.nextLine().trim();
					if (in.hasNextLine() && nextLine.startsWith("</IsGhostClosePacman>"))
						return node;
					System.out.println("closing incorrect");
					break;
					
				case "<IsPowerPillStillAvailable></IsPowerPillStillAvailable>":
					node = new IsPowerPillStillAvailablePacman();
					return node;
					
				case "<SeeingGhosts></SeeingGhosts>":
					node = new SeeingGhosts();
					return node;
					
				case "<StaticBooleanTerminalNode>":
					node = new StaticBooleanTerminalNodePacman(Boolean.getBoolean(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</StaticBooleanTerminalNode>"))
						return node;
					System.out.println("closing incorrect");
					break;
					
				case "<DistanceToClosestGhostPacman></DistanceToClosestGhostPacman>":
					node = new DistanceToClosestGhostPacman();
					return node;
					
				case "<DistanceToSecondClosestGhostPacman></DistanceToSecondClosestGhostPacman>":
					node = new DistanceToSecondClosestGhostPacman();
					return node;
					
				case "<DistanceToThirdClosestGhostPacman></DistanceToThirdClosestGhostPacman>":
					node = new DistanceToThirdClosestGhostPacman();
					return node;
					
				case "<DistanceToFourthClosestGhostPacman></DistanceToFourthClosestGhostPacman>":
					node = new DistanceToFourthClosestGhostPacman();
					return node;
					
				case "<DoubleTerminalNode>":
					node = new DoubleTerminalNodePacman(new Double(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</DoubleTerminalNode>"))
						return node;
					System.out.println("closing incorrect");
					break;
					
				case "<EmpoweredTimePacman></EmpoweredTimePacman>":
					node = new EmpoweredTimePacman();
					return node;
					
				case "<IntegerTerminalNode>":
					node = new DoubleTerminalNodePacman(new Double(in.nextLine().trim()));
					nextLine = in.nextLine().trim();

					if (in.hasNextLine() && nextLine.startsWith("</IntegerTerminalNode>"))
						return node;
					System.out.println("closing incorrect");
					break;
					
				case "<RandomNumberTerminalNode></RandomNumberTerminalNode>":
					node = new RandomNumberTerminalNodePacman();
					return node;
				
					
				default:
					break;
					
			}
		}
		//you shouldn't reach this
		System.out.println("Invalid Line:" + line);
		return null;
	}

	public abstract TargetPacman getTarget();
}
