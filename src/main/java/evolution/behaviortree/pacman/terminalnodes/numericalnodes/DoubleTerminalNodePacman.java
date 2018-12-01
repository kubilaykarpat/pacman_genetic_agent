package evolution.behaviortree.pacman.terminalnodes.numericalnodes;

import evolution.pacmanevaluation.ExtendedGamePacman;

public class DoubleTerminalNodePacman extends NumberTerminalNodePacman {

	public double number;
	
	public DoubleTerminalNodePacman(double number){
		this.number = number;
	}


	public DoubleTerminalNodePacman() {
		this.number = RANDOM.nextDouble();
	}

	@Override
	public double getData(ExtendedGamePacman extendedgame){
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
	public DoubleTerminalNodePacman copy(){
		return new DoubleTerminalNodePacman(this.number);
	}
	
}
