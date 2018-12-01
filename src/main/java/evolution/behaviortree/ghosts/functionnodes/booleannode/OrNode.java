package evolution.behaviortree.ghosts.functionnodes.booleannode;

import java.util.List;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.behaviortree.ghosts.functionnodes.FunctionNode;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.BooleanTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.StaticBooleanTerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;


public class OrNode extends FunctionNode {

	private BehaviorNode firstCond;
	private BehaviorNode secondCond;
	
	public OrNode()
	{
		this(BooleanTerminalNode.createRandom(), BooleanTerminalNode.createRandom());
	}
	
	@Override
	public OrNode copy(){
		return new OrNode(this.firstCond.copy(), this.secondCond.copy());
	}
	
	public OrNode(BehaviorNode firstCond, BehaviorNode secondCond){
		this.firstCond = firstCond;
		this.secondCond = secondCond;
		this.target = Target.Boolean;
	}
	
	@Override
	public BehaviorNode eval(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype) {
		if (((BooleanTerminalNode)firstCond.eval(extendedgame, ghosttype)).getData(extendedgame, ghosttype) || 
				((BooleanTerminalNode)secondCond.eval(extendedgame, ghosttype)).getData(extendedgame, ghosttype))
			return StaticBooleanTerminalNode.trueNode;
		return StaticBooleanTerminalNode.falseNode;
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
	public List<BehaviorNode> getNodes(List<BehaviorNode> list) {
		list.add(this);
		this.firstCond.getNodes(list);
		this.secondCond.getNodes(list);
		return list;
	}
	
	@Override
	public List<BehaviorNode> getMutableNodes(List<BehaviorNode> list) {
		list.add(this);
		this.firstCond.getMutableNodes(list);
		this.secondCond.getMutableNodes(list);
		return list;
	}

	@Override
	public void mutate() {
		switch (BehaviorNode.RANDOM.nextInt(4)){
			case 0: 
				this.firstCond = BooleanTerminalNode.createRandom();
				break;
			case 1:
				this.secondCond = BooleanTerminalNode.createRandom();
				break;
			case 2:
				this.firstCond = FunctionNode.createRandomBooleanTarget();
				break;
			case 3:
				this.secondCond = FunctionNode.createRandomBooleanTarget();
				break;
		}
	}
}
