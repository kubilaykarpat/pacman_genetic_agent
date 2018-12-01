package evolution.behaviortree.ghosts.terminalnodes.actionnode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import evolution.behaviortree.ghosts.terminalnodes.TerminalNode;
import evolution.ghosts.ExtendedGameGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;

public class ActionTerminalNode extends TerminalNode {

	public enum ActionTerminal {ToPacman, FromPacman, FromPowerPill, ToClosestPowerPill, Split, Group};
	private static final List<ActionTerminal> VALUES =
		    Collections.unmodifiableList(Arrays.asList(ActionTerminal.values()));
	private static final int SIZE = VALUES.size();
		  
	private ActionTerminal terminal;
	
	public ActionTerminalNode(){
		this.terminal = VALUES.get(RANDOM.nextInt(SIZE));
	}
	
	public static ActionTerminalNode createRandom(){
		return new ActionTerminalNode();
	}
	
	public ActionTerminalNode (ActionTerminal terminal){
		this.terminal = terminal;
	}

	@Override
	public ActionTerminalNode copy() {
		return new ActionTerminalNode(this.terminal);
	}
 
	
	public MOVE getAction(ExtendedGameGhosts extendedgame, Constants.GHOST ghosttype){
		MOVE move = MOVE.NEUTRAL;
        int[] powerPills = extendedgame.game.getPowerPillIndices();
        int shortestDistance = 1000000;
        int nearestPill = powerPills[0];
        Boolean powerPillStillAvailable;
		int oGhostIndex;
		Constants.GHOST closestGhost = null;

		switch (this.terminal){
			case FromPacman:
				move = extendedgame.game.getApproximateNextMoveAwayFromTarget(extendedgame.game.getGhostCurrentNodeIndex(ghosttype),
						extendedgame.getLastPacmanIndex(), extendedgame.game.getGhostLastMoveMade(ghosttype), Constants.DM.PATH);
				break;
				
			case ToPacman:
				move = extendedgame.game.getApproximateNextMoveTowardsTarget(extendedgame.game.getGhostCurrentNodeIndex(ghosttype),
						extendedgame.getLastPacmanIndex(), extendedgame.game.getGhostLastMoveMade(ghosttype), Constants.DM.PATH);
				break;
				
			case FromPowerPill:
				for (int i = 0; i < powerPills.length; i++) {
		            powerPillStillAvailable = extendedgame.isPowerPillStillAvailable(i);
            
		            if (powerPillStillAvailable != null && powerPillStillAvailable && 
		            		extendedgame.game.getShortestPathDistance(powerPills[i], extendedgame.game.getGhostCurrentNodeIndex(ghosttype)) < shortestDistance) {
		            	shortestDistance = extendedgame.game.getShortestPathDistance(powerPills[i], extendedgame.game.getGhostCurrentNodeIndex(ghosttype));
		            	nearestPill = powerPills[i];
		            }
		        }
				move = extendedgame.game.getApproximateNextMoveAwayFromTarget(extendedgame.game.getGhostCurrentNodeIndex(ghosttype),
						nearestPill, extendedgame.game.getGhostLastMoveMade(ghosttype), Constants.DM.PATH);
				break;
					
				
			case ToClosestPowerPill:
				
		        for (int i = 0; i < powerPills.length; i++) {
		        	powerPillStillAvailable= extendedgame.isPowerPillStillAvailable(i);
            
		            if (powerPillStillAvailable!= null && powerPillStillAvailable && 
		            		extendedgame.game.getShortestPathDistance(powerPills[i], extendedgame.game.getGhostCurrentNodeIndex(ghosttype)) < shortestDistance) {
		            	shortestDistance = extendedgame.game.getShortestPathDistance(powerPills[i], extendedgame.game.getGhostCurrentNodeIndex(ghosttype));
		            	nearestPill = powerPills[i];
		            }
		        }
				move = extendedgame.game.getApproximateNextMoveTowardsTarget(extendedgame.game.getGhostCurrentNodeIndex(ghosttype),
						nearestPill, extendedgame.game.getGhostLastMoveMade(ghosttype), Constants.DM.PATH);
				break;
				
			case Split:
				
				for (Constants.GHOST oGhost : Constants.GHOST.values()){
					oGhostIndex= extendedgame.getGhostPosition(oGhost);
					int myself = extendedgame.game.getGhostCurrentNodeIndex(ghosttype);
					if ( extendedgame.game.getCurrentMaze().lairNodeIndex != extendedgame.getGhostPosition(oGhost)  && 
							extendedgame.game.getShortestPathDistance(oGhostIndex, myself) < shortestDistance)
					{
						shortestDistance = extendedgame.game.getShortestPathDistance(oGhostIndex, myself);
						closestGhost = oGhost;
					}
				}
				
				if (extendedgame.game.getGhostLastMoveMade(ghosttype) == MOVE.NEUTRAL  || closestGhost == null){
					return MOVE.NEUTRAL;
				
				} else {
					try {
					return extendedgame.game.getNextMoveAwayFromTarget(extendedgame.game.getGhostCurrentNodeIndex(ghosttype),
							extendedgame.getGhostPosition(closestGhost), extendedgame.game.getGhostLastMoveMade(ghosttype), Constants.DM.PATH);
					}
					catch (Exception e){
						System.out.println(e);
					}
				}
				
				
			case Group:
				
				for (Constants.GHOST oGhost : Constants.GHOST.values()){
					oGhostIndex= extendedgame.getGhostPosition(oGhost);
					int myself = extendedgame.game.getGhostCurrentNodeIndex(ghosttype);
					if ( extendedgame.game.getCurrentMaze().lairNodeIndex != extendedgame.getGhostPosition(oGhost)  && 
							extendedgame.game.getShortestPathDistance(oGhostIndex, myself) < shortestDistance)
					{
						if (extendedgame.game.getShortestPathDistance(oGhostIndex, myself) != -1){
							shortestDistance = extendedgame.game.getShortestPathDistance(oGhostIndex, myself);
							closestGhost = oGhost;
						}
					}
				}
				
				if (extendedgame.game.getGhostLastMoveMade(ghosttype) == MOVE.NEUTRAL  || closestGhost == null){
					return MOVE.NEUTRAL;
				
				} else {
					try {
						return extendedgame.game.getNextMoveAwayFromTarget(extendedgame.game.getGhostCurrentNodeIndex(ghosttype),
							extendedgame.getGhostPosition(closestGhost), extendedgame.game.getGhostLastMoveMade(ghosttype), Constants.DM.PATH);
					}
					catch (Exception e){
						System.out.println(e);
					}
				}
			default:
				
				break;
		}
		return move;
	}

	@Override
	public void disp(int depth) {
		
		String str = "<ActionTerminalNode>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		padded = String.format("%1$" + (4*(depth+1) +  this.terminal.name().length()) + "s", this.terminal.name().toString());
		System.out.println(padded);
				
		str = "</ActionTerminalNode>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
	}


}
