package evolution.behaviortree.ghosts.terminalnodes.numericalnodes;

import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;

public class DoubleTerminalNode extends NumberTerminalNode {

	public double number;
	
	public DoubleTerminalNode(double number){
		this.number = number;
	}


	public DoubleTerminalNode() {
		this.number = RANDOM.nextDouble();
	}

	@Override
	public double getData(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype){
		return this.number;
	}


	@Override
	public void disp(int depth) {
		String str = "<DoubleTerminalNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		Double d = this.number;
		padded = String.format("%1$" + (4*(depth+1) +  d.toString().length()) + "s", d.toString());
		System.out.println(padded);
				
		str = "</DoubleTerminalNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
	}
	
	@Override
	public DoubleTerminalNode copy(){
		return new DoubleTerminalNode(this.number);
	}
	
}
