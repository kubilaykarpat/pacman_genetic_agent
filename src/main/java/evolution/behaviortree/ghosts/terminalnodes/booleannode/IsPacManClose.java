package evolution.behaviortree.ghosts.terminalnodes.booleannode;

import java.util.List;

import evolution.behaviortree.ghosts.BehaviorNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants.GHOST;

public class IsPacManClose extends BooleanTerminalNode {

	private final static int DEFAULT_PROXIMITY = 15;
	private int proximity;
	
	@Override
	public boolean getData(ExtendedGameGhosts extendedgame, GHOST ghosttype) {
		return isClose(extendedgame, ghosttype);
	}
	
	public IsPacManClose(){
		this(DEFAULT_PROXIMITY);
	}
	
	public IsPacManClose(int proximity)
	{
		this.proximity = proximity;
	}
	
	@Override
	public void disp(int depth) {
		String str = "<IsPacManClose>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		Integer i = this.proximity;
		padded = String.format("%1$" + (4*(depth+1) +  i.toString().length()) + "s", i.toString());
		System.out.println(padded);
				
		str = "</IsPacManClose>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
	}
	
	private boolean isClose(ExtendedGameGhosts extendedgame, GHOST ghosttype) {

        int pacmanNodeIndex = extendedgame.game.getPacmanCurrentNodeIndex();
        if (pacmanNodeIndex == -1) {
            pacmanNodeIndex = extendedgame.getLastPacmanIndex();
        }
        
        if (extendedgame.game.getShortestPathDistance(extendedgame.game.getGhostCurrentNodeIndex(ghosttype), pacmanNodeIndex) < this.proximity) {
            return true;
        }
        return false;

	}
	 
	@Override
	public IsPacManClose copy(){
		return new IsPacManClose(this.proximity);
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
