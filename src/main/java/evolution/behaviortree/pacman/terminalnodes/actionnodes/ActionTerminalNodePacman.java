package evolution.behaviortree.pacman.terminalnodes.actionnodes;

import evolution.behaviortree.pacman.BehaviorNodePacman;
import evolution.behaviortree.pacman.functionnodes.FunctionNodePacman;
import evolution.behaviortree.pacman.terminalnodes.TerminalNodePacman;
import evolution.pacmanevaluation.ExtendedGamePacman;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ActionTerminalNodePacman extends TerminalNodePacman {

	public enum ActionTerminalPacman {FromClosestGhost, ToClosestEdibleGhost, ToClosestPowerPill, ToClosestPill};
	private static final List<ActionTerminalPacman> VALUES =
		    Collections.unmodifiableList(Arrays.asList(ActionTerminalPacman.values()));
	private static final int SIZE = VALUES.size();
		  
	private ActionTerminalPacman terminal;
	
	public ActionTerminalNodePacman(){
		this.terminal = VALUES.get(BehaviorNodePacman.RANDOM.nextInt(SIZE));
	}
	
	public static ActionTerminalNodePacman createRandom(){
		return new ActionTerminalNodePacman();
	}
	
	public ActionTerminalNodePacman (ActionTerminalPacman terminal){
		this.terminal = terminal;
	}

	@Override
	public ActionTerminalNodePacman copy() {
		return new ActionTerminalNodePacman(this.terminal);
	}

	@Override
	public FunctionNodePacman.TargetPacman getTarget() {
		return FunctionNodePacman.TargetPacman.Action;
	}


	public MOVE getAction(ExtendedGamePacman extendedgame){
		MOVE move = MOVE.NEUTRAL;
        int[] powerPills = extendedgame.game.getPowerPillIndices();
        int[] pills = extendedgame.game.getPillIndices();

        double shortestDistance = 1000000;
        int nearestPill = powerPills[0];
        Boolean powerPillStillAvailable;
        Boolean pillIsStillAvailable;
		Constants.GHOST closestGhost = null;
		double[] distances = extendedgame.getEstimatedGhostDistances();
		int j = 0;
		
		switch (this.terminal){
			case FromClosestGhost:
				for (Constants.GHOST ghosttype : Constants.GHOST.values()){
					if (shortestDistance > distances[j]){
						shortestDistance = distances[j];
						closestGhost = ghosttype;
					}
					j++;
				}
				
				if (closestGhost == null || extendedgame.getGhostPosition(closestGhost) == -1){
					return MOVE.NEUTRAL;
				} else {
					return extendedgame.game.getNextMoveAwayFromTarget(extendedgame.game.getPacmanCurrentNodeIndex(),
							extendedgame.getGhostPosition(closestGhost), Constants.DM.PATH);
					
				}
				
			case ToClosestEdibleGhost:
				j = 0;
				for (Constants.GHOST ghosttype : Constants.GHOST.values()){
					if (extendedgame.edible.get(ghosttype) == true || 
							(extendedgame.game.isGhostEdible(ghosttype) != null && extendedgame.game.isGhostEdible(ghosttype))){
						if (shortestDistance > distances[j]){
							shortestDistance = distances[j];
							closestGhost = ghosttype;
						}
					}
					j++;
				}
				
				if (closestGhost == null || extendedgame.getGhostPosition(closestGhost) == -1){
					return MOVE.NEUTRAL;
				} else {
					return extendedgame.game.getNextMoveTowardsTarget(extendedgame.game.getPacmanCurrentNodeIndex(),
							extendedgame.getGhostPosition(closestGhost), Constants.DM.PATH);
					
				}
				
			case ToClosestPowerPill:
				for (int i = 0; i < powerPills.length; i++) {
		            powerPillStillAvailable = extendedgame.isPowerPillStillAvailable(i);
            
		            if (powerPillStillAvailable != null && powerPillStillAvailable && 
		            		extendedgame.game.isPowerPillStillAvailable(i) != null &&
		            		extendedgame.game.isPowerPillStillAvailable(i) &&
		            		extendedgame.game.getShortestPathDistance(powerPills[i], extendedgame.game.getPacmanCurrentNodeIndex()) < shortestDistance) {
		            	shortestDistance = extendedgame.game.getShortestPathDistance(powerPills[i], extendedgame.game.getPacmanCurrentNodeIndex());
		            	nearestPill = powerPills[i];
		            }
		        }
					move = extendedgame.game.getNextMoveTowardsTarget(extendedgame.game.getPacmanCurrentNodeIndex(),
							nearestPill, Constants.DM.PATH);
				
				break;
					
				
			case ToClosestPill:
				for (int i = 0; i < pills.length; i++) {
		            pillIsStillAvailable = extendedgame.isPillStillAvailable(i);
            
		            if (pillIsStillAvailable != null && pillIsStillAvailable && 
		            		extendedgame.game.getShortestPathDistance(pills[i], extendedgame.game.getPacmanCurrentNodeIndex()) < shortestDistance) {
		            	shortestDistance = extendedgame.game.getShortestPathDistance(pills[i], extendedgame.game.getPacmanCurrentNodeIndex());
		            	nearestPill = pills[i];
		            }
		        }
				move = extendedgame.game.getNextMoveTowardsTarget(extendedgame.game.getPacmanCurrentNodeIndex(),
						nearestPill, Constants.DM.PATH);
				
				break;				
			
			default:
				
				break;
		}
		return move;
	}

	@Override
	public void disp(int depth) {
		
		String str = "<ActionTerminalNodePacman>";
		String padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
		padded = String.format("%1$" + (4*(depth+1) +  this.terminal.name().length()) + "s", this.terminal.name().toString());
		System.out.println(padded);
				
		str = "</ActionTerminalNodePacman>";
		padded = String.format("%1$" + (4*depth + str.length()) + "s", str);
		System.out.println(padded);
		
	}


}
