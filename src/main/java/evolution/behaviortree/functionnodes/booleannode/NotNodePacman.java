package evolution.behaviortree.functionnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.BooleanTerminalNodePacman;
import evolution.behaviortree.terminalnodes.booleannode.StaticBooleanTerminalNodePacman;

import java.util.Collections;
import java.util.List;

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
	public BehaviorNodePacman eval(ExtendedGame extendedgame) {
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
	public List<BehaviorNodePacman> getDirectChildren() {
		return Collections.singletonList(negate);
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
