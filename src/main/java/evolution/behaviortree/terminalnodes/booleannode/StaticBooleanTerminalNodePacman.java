package evolution.behaviortree.terminalnodes.booleannode;

import evolution.ExtendedGame;
import evolution.behaviortree.BehaviorNodePacman;

public class StaticBooleanTerminalNodePacman extends BooleanTerminalNodePacman {

	private boolean bool;
	
	public final static BooleanTerminalNodePacman trueNode = new StaticBooleanTerminalNodePacman(true);
	public final static BooleanTerminalNodePacman falseNode = new StaticBooleanTerminalNodePacman(false);
	
	
	public StaticBooleanTerminalNodePacman(boolean bool){
		this.bool = bool;
	}
	
	public StaticBooleanTerminalNodePacman() {
		this.bool = BehaviorNodePacman.RANDOM.nextBoolean();
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
	public boolean getData(ExtendedGame extendedgame) {
		return this.bool;
	}
	
	@Override
	public StaticBooleanTerminalNodePacman copy(){
		return new StaticBooleanTerminalNodePacman(this.bool);
	}
}
