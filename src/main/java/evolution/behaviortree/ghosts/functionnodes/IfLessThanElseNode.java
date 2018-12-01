package evolution.behaviortree.ghosts.functionnodes;

import java.util.List;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.behaviortree.ghosts.terminalnodes.actionnode.ActionTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.BooleanTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.numericalnodes.NumberTerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class IfLessThanElseNode extends FunctionNode {

	private BehaviorNode firstCond;
	private BehaviorNode secondCond;
	private BehaviorNode ifcase;
	private BehaviorNode elsecase;
	
	public IfLessThanElseNode(Target target){
		this.firstCond = NumberTerminalNode.createRandom();
		this.secondCond = NumberTerminalNode.createRandom();
		this.target = target;
		
		switch(target)
		{
			case Action: 
				this.ifcase = ActionTerminalNode.createRandom();
				this.elsecase = ActionTerminalNode.createRandom();
				break;
			case Boolean: 
				this.ifcase = BooleanTerminalNode.createRandom();
				this.elsecase = BooleanTerminalNode.createRandom();
				break;
			case Numerical: 
				this.ifcase = NumberTerminalNode.createRandom();
				this.elsecase = NumberTerminalNode.createRandom();
				break;
			default:
				System.out.println("IfLessThanElseNode, enum unknown");
				this.ifcase = null;
				this.elsecase = null;
				break; 
		}
	}
	
	@Override
	public IfLessThanElseNode copy(){
		return new IfLessThanElseNode(this.firstCond.copy(), this.secondCond.copy(), this.ifcase.copy(), this.elsecase.copy(), this.target);
	}
		
	public IfLessThanElseNode (BehaviorNode firstCond, BehaviorNode secondCond, BehaviorNode ifcase, BehaviorNode elsecase, Target target){
		this.firstCond = firstCond;
		this.secondCond = secondCond;
		this.ifcase = ifcase;
		this.elsecase = elsecase;
		this.target = target;
	}
	
//	public IfLessThanElseNode(NumericalTerminalNode firstCond, NumericalTerminalNode secondCond, ActionTerminalNode ifcase, ActionTerminalNode elsecase){
//		this.firstCond = firstCond;
//		this.secondCond = secondCond;
//		this.ifcase = ifcase;
//		this.elsecase = elsecase;
//	}
	
	public BehaviorNode eval(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype) {
		if (((NumberTerminalNode)this.firstCond.eval(extendedgame, ghosttype)).getData(extendedgame, ghosttype) < 
				((NumberTerminalNode)this.secondCond.eval(extendedgame, ghosttype)).getData(extendedgame, ghosttype)) 
			return this.ifcase.eval(extendedgame, ghosttype);
		//else 
		return this.elsecase.eval(extendedgame, ghosttype);
	}


	@Override
	public void disp(int depth) {
		String str = "<IfLessThanElseNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		padded = String.format("%1$" + (4*(depth+1) + target.toString().length()) + "s", target.toString());
		System.out.println(padded);
	
		firstCond.disp(depth+1);
		secondCond.disp(depth+1);
		ifcase.disp(depth+1);
		elsecase.disp(depth+1);
		
		str = "</IfLessThanElseNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}


	@Override
	public List<BehaviorNode> getNodes(List<BehaviorNode> list) {
		list.add(this);
		this.firstCond.getNodes(list);
		this.secondCond.getNodes(list);
		this.ifcase.getNodes(list);
		this.elsecase.getNodes(list);
		return list;
	}


	@Override
	public List<BehaviorNode> getMutableNodes(List<BehaviorNode> list) {
		list.add(this);
		this.firstCond.getMutableNodes(list);
		this.secondCond.getMutableNodes(list);
		this.ifcase.getMutableNodes(list);
		this.elsecase.getMutableNodes(list);
		return list;
	}


	private void mutate_action(){
		switch (BehaviorNode.RANDOM.nextInt(4)){
		case 0: 
			this.ifcase = ActionTerminalNode.createRandom();
			break;
		case 1:
			this.elsecase = ActionTerminalNode.createRandom();
			break;
		case 2:
			this.ifcase = FunctionNode.createRandomActionTarget();
			break;
		case 3:
			this.elsecase = FunctionNode.createRandomActionTarget();
			break;
		} 
	}
	
	private void mutate_boolean(){
		switch (BehaviorNode.RANDOM.nextInt(4)){
		case 0: 
			this.ifcase = BooleanTerminalNode.createRandom();
			break;
		case 1:
			this.elsecase = BooleanTerminalNode.createRandom();
			break;
		case 2:
			this.ifcase = FunctionNode.createRandomBooleanTarget();
			break;
		case 3:
			this.elsecase = FunctionNode.createRandomBooleanTarget();
			break;
		} 
	}
	
	private void mutate_numerical(){
		switch (BehaviorNode.RANDOM.nextInt(4)){
		case 0: 
			this.ifcase = NumberTerminalNode.createRandom();
			break;
		case 1:
			this.elsecase = NumberTerminalNode.createRandom();
			break;
		case 2:
			this.ifcase = FunctionNode.createRandomNumericalTarget();
			break;
		case 3:
			this.elsecase = FunctionNode.createRandomNumericalTarget();
			break;
		} 
	}
	
	@Override
	public void mutate() {
		if (BehaviorNode.RANDOM.nextBoolean()){
			//mutate conclusion
			switch(this.target){
				case Action: 
					mutate_action();
					break;
				case Boolean: 
					mutate_boolean();
					break;
				case Numerical: 
					mutate_numerical();
					break;
			}
		}
		else {
			// mutate condition
			switch (BehaviorNode.RANDOM.nextInt(4)){
				case 0: 
					this.firstCond = NumberTerminalNode.createRandom();
					break;
				case 1:
					this.secondCond = NumberTerminalNode.createRandom();
					break;
				case 2:
					this.firstCond = FunctionNode.createRandomNumericalTarget();
					break;
				case 3:
					this.secondCond = FunctionNode.createRandomNumericalTarget();
					break;
				default:
					System.out.println("default existiert nicht");
					this.firstCond = null;
					this.secondCond = null;
					break;
			}
		}
	}
	
}
