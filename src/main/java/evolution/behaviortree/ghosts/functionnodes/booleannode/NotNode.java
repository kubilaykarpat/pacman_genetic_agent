package evolution.behaviortree.ghosts.functionnodes.booleannode;

import java.util.List;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.behaviortree.ghosts.functionnodes.FunctionNode;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.BooleanTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.StaticBooleanTerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class NotNode extends FunctionNode {

	private BehaviorNode negate;
	
	public NotNode(BehaviorNode negate){
		this.negate = negate;
		this.target = Target.Boolean;
	}
	
	public NotNode(){
		this(BooleanTerminalNode.createRandom());
	}
	
	@Override
	public NotNode copy(){
		return new NotNode(this.negate.copy());
	}
	
	@Override
	public BehaviorNode eval(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype) {
		if (((BooleanTerminalNode)negate.eval(extendedgame, ghosttype)).getData(extendedgame, ghosttype))
			return StaticBooleanTerminalNode.falseNode;
		return StaticBooleanTerminalNode.trueNode;
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
	public List<BehaviorNode> getNodes(List<BehaviorNode> list) {
		list.add(this);
		this.negate.getNodes(list);
		return list;
	}
	
	@Override
	public List<BehaviorNode> getMutableNodes(List<BehaviorNode> list) {
		list.add(this);
		this.negate.getMutableNodes(list);
		return list;
	}

	@Override
	public void mutate() {
		if (BehaviorNode.RANDOM.nextBoolean()){
			this.negate = BooleanTerminalNode.createRandom();
		}
		else {
			this.negate = FunctionNode.createRandomBooleanTarget();
		}
	}
}
