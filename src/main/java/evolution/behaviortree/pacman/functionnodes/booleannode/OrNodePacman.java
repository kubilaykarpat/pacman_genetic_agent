package evolution.behaviortree.pacman.functionnodes.booleannode;

import java.util.List;

import evolution.behaviortree.pacman.BehaviorNodePacman;
import evolution.behaviortree.pacman.functionnodes.FunctionNodePacman;
import evolution.behaviortree.pacman.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.pacman.terminalnodes.booleannode.StaticBooleanTerminalNodePacman;
import evolution.pacmanevaluation.ExtendedGamePacman;


public class OrNodePacman extends FunctionNodePacman {

	private BehaviorNodePacman firstCond;
	private BehaviorNodePacman secondCond;
	
	public OrNodePacman()
	{
		this(BooleanTerminalNodePacman.createRandom(), BooleanTerminalNodePacman.createRandom());
	}
	
	@Override
	public OrNodePacman copy(){
		return new OrNodePacman(this.firstCond.copy(), this.secondCond.copy());
	}
	
	public OrNodePacman(BehaviorNodePacman firstCond, BehaviorNodePacman secondCond){
		this.firstCond = firstCond;
		this.secondCond = secondCond;
		this.target = TargetPacman.Boolean;
	}
	
	@Override
	public BehaviorNodePacman eval(ExtendedGamePacman extendedgame) {
		if (((BooleanTerminalNodePacman)firstCond.eval(extendedgame)).getData(extendedgame) || 
				((BooleanTerminalNodePacman)secondCond.eval(extendedgame)).getData(extendedgame))
			return StaticBooleanTerminalNodePacman.trueNode;
		return StaticBooleanTerminalNodePacman.falseNode;
	}

	@Override
	public void disp(int depth) {
		String str = "<OrNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);

		firstCond.disp(depth+1);
		secondCond.disp(depth+1);
		
		str = "</OrNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

	@Override
	public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.firstCond.getNodes(list);
		this.secondCond.getNodes(list);
		return list;
	}
	
	@Override
	public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.firstCond.getMutableNodes(list);
		this.secondCond.getMutableNodes(list);
		return list;
	}

	@Override
	public void mutate() {
		switch (BehaviorNodePacman.RANDOM.nextInt(4)){
			case 0: 
				this.firstCond = BooleanTerminalNodePacman.createRandom();
				break;
			case 1:
				this.secondCond = BooleanTerminalNodePacman.createRandom();
				break;
			case 2:
				this.firstCond = FunctionNodePacman.createRandomBooleanTarget();
				break;
			case 3:
				this.secondCond = FunctionNodePacman.createRandomBooleanTarget();
				break;
		}
	}
}
