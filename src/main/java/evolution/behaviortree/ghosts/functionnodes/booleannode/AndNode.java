package evolution.behaviortree.ghosts.functionnodes.booleannode;

import java.util.List;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.behaviortree.ghosts.functionnodes.FunctionNode;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.BooleanTerminalNode;
import evolution.behaviortree.ghosts.terminalnodes.booleannode.StaticBooleanTerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;


public class AndNode extends FunctionNode {

	private BehaviorNode firstCond;
	private BehaviorNode secondCond;
	
	public AndNode()
	{
		this(BooleanTerminalNode.createRandom(), BooleanTerminalNode.createRandom());
	}
	
	@Override
	public AndNode copy(){
		return new AndNode(this.firstCond.copy(), this.secondCond.copy());
	}
	
	public AndNode(BehaviorNode firstCond, BehaviorNode secondCond){
		this.firstCond = firstCond;
		this.secondCond = secondCond;
	}
	
	@Override
	public BehaviorNode eval(ExtendedGameGhosts extended_game, Constants.GHOST ghosttype) {
		if (((BooleanTerminalNode)firstCond.eval( extended_game, ghosttype)).getData(extended_game, ghosttype) && 
				((BooleanTerminalNode)secondCond.eval(extended_game, ghosttype)).getData(extended_game, ghosttype))
			return StaticBooleanTerminalNode.trueNode;
		return StaticBooleanTerminalNode.falseNode;
	}

	@Override
	public void disp(int depth) {
		String str = "<AndNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		firstCond.disp(depth+1);
		secondCond.disp(depth+1);
		
		str = "</AndNode>";
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
