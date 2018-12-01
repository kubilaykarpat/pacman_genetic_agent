package evolution.behaviortree.ghosts.terminalnodes.booleannode;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class StaticBooleanTerminalNode extends BooleanTerminalNode {

	private boolean bool;
	
	public final static BooleanTerminalNode trueNode = new StaticBooleanTerminalNode(true);
	public final static BooleanTerminalNode falseNode = new StaticBooleanTerminalNode(false);
	
	
	public StaticBooleanTerminalNode(boolean bool){
		this.bool = bool;
	}
	
	public StaticBooleanTerminalNode() {
		this.bool = RANDOM.nextBoolean();
	}


	@Override
	public void disp(int depth) {
		String str = "<StaticBooleanTerminalNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		Boolean b = this.bool;
		padded = String.format("%1$" + (4*(depth+1) +  b.toString().length()) + "s", b.toString());
		System.out.println(padded);
				
		str = "</StaticBooleanTerminalNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}

	@Override
	public boolean getData(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype){
		return this.bool;
	}
	
	@Override
	public StaticBooleanTerminalNode copy(){
		return new StaticBooleanTerminalNode(this.bool);
	}
}
