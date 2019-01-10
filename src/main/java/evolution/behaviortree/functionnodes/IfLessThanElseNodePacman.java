package evolution.behaviortree.functionnodes;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.terminalnodes.actionnodes.ActionTerminalNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.terminalnodes.numericalnodes.NumberTerminalNodePacman;

import java.util.Arrays;
import java.util.List;

public class IfLessThanElseNodePacman extends FunctionNodePacman {

	private BehaviorNodePacman firstCond;
	private BehaviorNodePacman secondCond;
	private BehaviorNodePacman ifcase;
	private BehaviorNodePacman elsecase;
	
	public IfLessThanElseNodePacman(TargetPacman target){
		this.firstCond = NumberTerminalNodePacman.createRandom();
		this.secondCond = NumberTerminalNodePacman.createRandom();
		this.target = target;
		
		switch(target)
		{
			case Action: 
				this.ifcase = ActionTerminalNodePacman.createRandom();
				this.elsecase = ActionTerminalNodePacman.createRandom();
				break;
			case Boolean: 
				this.ifcase = BooleanTerminalNodePacman.createRandom();
				this.elsecase = BooleanTerminalNodePacman.createRandom();
				break;
			case Numerical: 
				this.ifcase = NumberTerminalNodePacman.createRandom();
				this.elsecase = NumberTerminalNodePacman.createRandom();
				break;
			default:
				System.out.println("IfLessThanElseNode, enum unknown");
				this.ifcase = null;
				this.elsecase = null;
				break; 
		}
	}
	
	@Override
	public IfLessThanElseNodePacman copy(){
		return new IfLessThanElseNodePacman(this.firstCond.copy(), this.secondCond.copy(), this.ifcase.copy(), this.elsecase.copy(), this.target);
	}
		
	public IfLessThanElseNodePacman (BehaviorNodePacman firstCond, BehaviorNodePacman secondCond, BehaviorNodePacman ifcase, BehaviorNodePacman elsecase, TargetPacman target){
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

	public BehaviorNodePacman eval(ExtendedGame extendedgame) {
		if (((NumberTerminalNodePacman)this.firstCond.eval(extendedgame)).getData(extendedgame) < 
				((NumberTerminalNodePacman)this.secondCond.eval(extendedgame)).getData(extendedgame)) 
			return this.ifcase.eval(extendedgame);
		//else 
		return this.elsecase.eval(extendedgame);
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
	public List<BehaviorNodePacman> getDirectChildren() {
		return Arrays.asList(this.firstCond, this.secondCond, this.ifcase, this.elsecase);
	}

	@Override
	public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.firstCond.getNodes(list);
		this.secondCond.getNodes(list);
		this.ifcase.getNodes(list);
		this.elsecase.getNodes(list);
		return list;
	}


	@Override
	public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.firstCond.getMutableNodes(list);
		this.secondCond.getMutableNodes(list);
		this.ifcase.getMutableNodes(list);
		this.elsecase.getMutableNodes(list);
		return list;
	}


	private void mutate_action(){
		switch (BehaviorNodePacman.RANDOM.nextInt(4)){
		case 0: 
			this.ifcase = ActionTerminalNodePacman.createRandom();
			break;
		case 1:
			this.elsecase = ActionTerminalNodePacman.createRandom();
			break;
		case 2:
			this.ifcase = createRandomActionTarget();
			break;
		case 3:
			this.elsecase = createRandomActionTarget();
			break;
		} 
	}
	
	private void mutate_boolean(){
		switch (BehaviorNodePacman.RANDOM.nextInt(4)){
		case 0: 
			this.ifcase = BooleanTerminalNodePacman.createRandom();
			break;
		case 1:
			this.elsecase = BooleanTerminalNodePacman.createRandom();
			break;
		case 2:
			this.ifcase = createRandomBooleanTarget();
			break;
		case 3:
			this.elsecase = createRandomBooleanTarget();
			break;
		} 
	}
	
	private void mutate_numerical(){
		switch (BehaviorNodePacman.RANDOM.nextInt(4)){
		case 0: 
			this.ifcase = NumberTerminalNodePacman.createRandom();
			break;
		case 1:
			this.elsecase = NumberTerminalNodePacman.createRandom();
			break;
		case 2:
			this.ifcase = createRandomNumericalTarget();
			break;
		case 3:
			this.elsecase = createRandomNumericalTarget();
			break;
		} 
	}
	
	@Override
	public void mutate() {
		if (BehaviorNodePacman.RANDOM.nextBoolean()){
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
			switch (BehaviorNodePacman.RANDOM.nextInt(4)){
				case 0: 
					this.firstCond = NumberTerminalNodePacman.createRandom();
					break;
				case 1:
					this.secondCond = NumberTerminalNodePacman.createRandom();
					break;
				case 2:
					this.firstCond = createRandomNumericalTarget();
					break;
				case 3:
					this.secondCond = createRandomNumericalTarget();
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
