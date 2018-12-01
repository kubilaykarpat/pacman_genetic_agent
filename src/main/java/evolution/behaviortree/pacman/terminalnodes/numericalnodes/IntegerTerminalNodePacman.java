package evolution.behaviortree.pacman.terminalnodes.numericalnodes;

import evolution.pacmanevaluation.ExtendedGamePacman;

public class IntegerTerminalNodePacman extends NumberTerminalNodePacman {

	public int number;
	
	public IntegerTerminalNodePacman(int number){
		this.number = number;
	}


	public IntegerTerminalNodePacman() {
		this.number = RANDOM.nextInt(1000);
	}

	@Override
	public double getData(ExtendedGamePacman extendedgame){
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
	public IntegerTerminalNodePacman copy(){
		return new IntegerTerminalNodePacman(this.number);
	}
	
}
