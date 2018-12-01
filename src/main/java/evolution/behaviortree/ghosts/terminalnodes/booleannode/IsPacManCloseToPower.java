package evolution.behaviortree.ghosts.terminalnodes.booleannode;

import java.util.List;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants.GHOST;

public class IsPacManCloseToPower extends BooleanTerminalNode {

	private static final int DEFAULT_PILL_PROXIMITY = 15;
	private int proximity;

	public IsPacManCloseToPower() {
		this(DEFAULT_PILL_PROXIMITY);
	}
	
	public IsPacManCloseToPower(int proximity){
		this.proximity = proximity;
	}
	
	@Override
	public boolean getData(ExtendedGameGhosts extendedgame, GHOST ghosttype) {
		return closeToPower(extendedgame);
	}

	@Override
	public void disp(int depth) {
		String str = "<IsPacManCloseToPower>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		Integer i = this.proximity;
		padded = String.format("%1$" + (4*(depth+1) +  i.toString().length()) + "s", i.toString());
		System.out.println(padded);
				
		str = "</IsPacManCloseToPower>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	 private boolean closeToPower(ExtendedGameGhosts extendedgame) {
	        int[] powerPills = extendedgame.game.getPowerPillIndices();

	        for (int i = 0; i < powerPills.length; i++) {
	            Boolean powerPillStillAvailable = extendedgame.isPowerPillStillAvailable(i);
	            int pacmanNodeIndex = extendedgame.getLastPacmanIndex();
	            if (pacmanNodeIndex == -1) {
	                return false;
	            }
	            if (powerPillStillAvailable && extendedgame.game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < this.proximity) {
	                return true;
	            }
	        }

	        return false;
	   }
	 
	@Override
	public IsPacManCloseToPower copy(){
		return new IsPacManCloseToPower();
	}
	
	@Override
	public List<BehaviorNode> getMutableNodes(List<BehaviorNode> list){
		list.add(this);
		return list;
	}
	
	@Override
	public void mutate(){
		this.proximity = RANDOM.nextInt(100);
	}
}
