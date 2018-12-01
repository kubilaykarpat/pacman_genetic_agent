package evolution.behaviortree.pacman.functionnodes.booleannode;

import java.util.List;

import evolution.behaviortree.pacman.BehaviorNodePacman;
import evolution.behaviortree.pacman.functionnodes.FunctionNodePacman;
import evolution.behaviortree.pacman.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.pacman.terminalnodes.booleannode.StaticBooleanTerminalNodePacman;
import evolution.pacmanevaluation.ExtendedGamePacman;

public class NotNodePacman extends FunctionNodePacman {

	private BehaviorNodePacman negate;
	
	public NotNodePacman(BehaviorNodePacman negate){
		this.negate = negate;
		this.target = TargetPacman.Boolean;
	}
	
	public NotNodePacman(){
		this(BooleanTerminalNodePacman.createRandom());
	}
	
	@Override
	public NotNodePacman copy(){
		return new NotNodePacman(this.negate.copy());
	}
	
	@Override
	public BehaviorNodePacman eval(ExtendedGamePacman extendedgame) {
		if (((BooleanTerminalNodePacman)negate.eval(extendedgame)).getData(extendedgame))
			return StaticBooleanTerminalNodePacman.falseNode;
		return StaticBooleanTerminalNodePacman.trueNode;
	}

	@Override
	public void disp(int depth) {
		String str = "<NotNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);

		negate.disp(depth+1);
			
		str = "</NotNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

	@Override
	public List<BehaviorNodePacman> getNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.negate.getNodes(list);
		return list;
	}
	
	@Override
	public List<BehaviorNodePacman> getMutableNodes(List<BehaviorNodePacman> list) {
		list.add(this);
		this.negate.getMutableNodes(list);
		return list;
	}

	@Override
	public void mutate() {
		if (BehaviorNodePacman.RANDOM.nextBoolean()){
			this.negate = BooleanTerminalNodePacman.createRandom();
		}
		else {
			this.negate = FunctionNodePacman.createRandomBooleanTarget();
		}
	}
}
