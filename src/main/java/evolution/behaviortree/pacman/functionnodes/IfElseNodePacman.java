package evolution.behaviortree.pacman.functionnodes;

import java.util.List;

import evolution.behaviortree.pacman.BehaviorNodePacman;
import evolution.behaviortree.pacman.terminalnodes.actionnodes.ActionTerminalNodePacman;
import evolution.behaviortree.pacman.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.pacman.terminalnodes.numericalnodes.NumberTerminalNodePacman;
import evolution.pacmanevaluation.ExtendedGamePacman;

public class IfElseNodePacman extends FunctionNodePacman {

	private BehaviorNodePacman cond;
	private BehaviorNodePacman ifcase;
	private BehaviorNodePacman elsecase;
	
	public IfElseNodePacman(TargetPacman target){
		this.cond = BooleanTerminalNodePacman.createRandom();
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
				System.out.println("IfElseNode enum unknown");
				this.ifcase = null;
				this.elsecase = null;
				break; 
		}
	}
	
	
	public IfElseNodePacman (BehaviorNodePacman cond, BehaviorNodePacman ifcase, BehaviorNodePacman elsecase, TargetPacman target){
		this.cond = cond;
		this.ifcase = ifcase;
		this.elsecase = elsecase;
		this.target = target;
	}
	
	@Override
	public IfElseNodePacman copy(){
		return new IfElseNodePacman(this.cond.copy(), this.ifcase.copy(), this.elsecase.copy(), this.target);
	}
	
	
	public BehaviorNodePacman eval(ExtendedGamePacman extendedgame) {
		if (((BooleanTerminalNodePacman)this.cond.eval(extendedgame)).getData(extendedgame)) 
			return this.ifcase.eval(extendedgame);
		//else 
		return this.elsecase.eval(extendedgame);
	}

//	public static BehaviorNode createRandom(StaticBooleanTerminalNode cond, ActionTerminalNode ifcase, ActionTerminalNode elsecase, Target target){
//		return new IfElseNode(cond, ifcase, elsecase, target);
//	}

	@Override
	public void disp(int depth) {
		String str = "<IfElseNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		padded = String.format("%1$" + (4*(depth+1) + target.toString().length()) + "s", target.toString());
		System.out.println(padded);
		
		cond.disp(depth+1);
		ifcase.disp(depth+1);
		elsecase.disp(depth+1);
		
		str = "</IfElseNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

	@Override
	public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.cond.getNodes(list);
		this.ifcase.getNodes(list);
		this.elsecase.getNodes(list);
		return list;
	}

	@Override
	public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.cond.getMutableNodes(list);
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
			this.ifcase = FunctionNodePacman.createRandomActionTarget();
			break;
		case 3:
			this.elsecase = FunctionNodePacman.createRandomActionTarget();
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
			this.ifcase = FunctionNodePacman.createRandomBooleanTarget();
			break;
		case 3:
			this.elsecase = FunctionNodePacman.createRandomBooleanTarget();
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
			this.ifcase = FunctionNodePacman.createRandomNumericalTarget();
			break;
		case 3:
			this.elsecase = FunctionNodePacman.createRandomNumericalTarget();
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
			if (BehaviorNodePacman.RANDOM.nextBoolean()){
				this.cond = BooleanTerminalNodePacman.createRandom();
			}
			else {
				this.cond = FunctionNodePacman.createRandomBooleanTarget();
			}
		}
	}


}
