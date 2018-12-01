package evolution.behaviortree.ghosts.terminalnodes.numericalnodes;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class IntegerTerminalNode extends NumberTerminalNode {

	public int number;
	
	public IntegerTerminalNode(int number){
		this.number = number;
	}


	public IntegerTerminalNode() {
		this.number = RANDOM.nextInt(1000);
	}

	@Override
	public double getData(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype){
		return this.number;
	}


	@Override
	public void disp(int depth) {
		String str = "<IntegerTerminalNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		Integer i = this.number;
		padded = String.format("%1$" + (4*(depth+1) +  i.toString().length()) + "s", i.toString());
		System.out.println(padded);
				
		str = "</IntegerTerminalNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
	}
	
	@Override
	public IntegerTerminalNode copy(){
		return new IntegerTerminalNode(this.number);
	}
	
}
