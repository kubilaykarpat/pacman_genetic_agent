package MCTS_core.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import MCTS_core.MCTS.MCTSNode;
import MCTS_core.Policies.IPolicy;
import pacman.controllers.Controller;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * TODO: API
 * @author Max
 *
 */
public class MCTSSimulationData {

	/**
	 * TODO: API
	 */
	private static final int DEFAULT_EXPANSION_THRESHOLD = 50;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_MAXIMUM_SIMULATION_CYCLES = 10000000;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_DEATH_PENALTY = 10000;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_COMPLETION_REWARD = 10000;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_DEATH_REWARD = 10000;
	
	/**
	 * TODO: API
	 */
	private static final int DEFAULT_COMPLETION_PENALTY = 10000;
	
	/**
	 * TODO: API
	 */
	private int expansionThreshold;
	
	/**
	 * TODO: API
	 */
	private int maximumSimulationCycles;	

	/**
	 * TODO: API
	 */
	private int deathPenalty;	

	/**
	 * TODO: API
	 */
	private int completionReward;
	
	/**
	 * TODO: API
	 */
	private int deathReward;	

	/**
	 * TODO: API
	 */
	private int completionPenalty;
	
	/**
	 * TODO: API
	 */
	private PacmanController pacman;
	
	/**
	 * TODO: API
	 */
	private Controller<EnumMap<GHOST,MOVE>> ghosts;	
	
	/**
	 * TODO: API
	 */
	private UCBSelectionPolicy ucbSelectionPolicy;
	
	/**
	 * TODO: API
	 */
	private List<IPolicy> policies;
	
	
	public MCTSSimulationData(){
		this(DEFAULT_EXPANSION_THRESHOLD,
				DEFAULT_MAXIMUM_SIMULATION_CYCLES,
				DEFAULT_DEATH_PENALTY,
				DEFAULT_COMPLETION_REWARD,
				DEFAULT_DEATH_REWARD,
				DEFAULT_COMPLETION_PENALTY,
				new StarterPacMan(),
				new StarterGhosts());
	}

	public MCTSSimulationData(int expansionThreshold, 
			int maximumSimulationCycles,
			int deathPenalty,
			int completionReward,
			int deathReward,
			int completionPenalty,
			PacmanController pacmanController,
			Controller<EnumMap<GHOST,MOVE>> ghostControllers){
		
		this.expansionThreshold = expansionThreshold;
		this.maximumSimulationCycles = maximumSimulationCycles;
		this.deathPenalty = deathPenalty;
		this.completionReward = completionReward;
		this.deathReward = deathReward;
		this.completionPenalty = completionPenalty;
		
		this.pacman = pacmanController;
		this.ghosts = ghostControllers;
		
		this.policies = new ArrayList<IPolicy>();
		this.ucbSelectionPolicy = new UCBSelectionPolicy();
	}		
	
	public int getExpansionThreshold() {
		return this.expansionThreshold;
	}

	public int getMaximumSimulationCycles() {
		return this.maximumSimulationCycles;
	}

	public int getDeathPenalty() {
		return this.deathPenalty;
	}
	
	public int getDeathReward(){
		return this.deathReward;
	}

	public int getCompletionReward() {
		return this.completionReward;
	}
	
	public int getCompletionPenalty(){
		return this.completionPenalty;
	}

	public PacmanController getPacman() {
		return this.pacman;
	}

	public List<IPolicy> getPolicies() {
		return this.policies;
	}

	public Controller<EnumMap<GHOST,MOVE>> getGhosts() {
		return this.ghosts;
	}


	public void setGhosts(Controller<EnumMap<GHOST,MOVE>> ghosts) {
		this.ghosts = ghosts;
	}

	public UCBSelectionPolicy getUCBSelectionPolicy() {
		return this.ucbSelectionPolicy;
	}

	public class UCBSelectionPolicy{
		
		/**
		 * TODO: API
		 */
		private static final double DEFAULT_BALANCE = 4000;
		
		/**
		 * TODO: API
		 */
		private double balance;
		
		/**
		 * Default constructor.
		 */
		public UCBSelectionPolicy(){
			this(DEFAULT_BALANCE);
		}
		
		/**
		 * TODO: API
		 * @param balance
		 */
		public UCBSelectionPolicy(double balance){
			this.balance = balance;
		}
	
		/**
		 * TODO: API
		 * @param node
		 * @return
		 */
		public MCTSNode getChild(MCTSNode node)
		{
			Collection<MCTSNode> children = node.getChildrenCollection();
			MCTSNode selectedChild = null;	
			
			if (children == null){
				return null;
			}			
			
			double currentUCB;
			double maxUCB = Double.NEGATIVE_INFINITY;
			for (MCTSNode child : children)
			{
				currentUCB = this.getValue(child);
				
				if (Double.isNaN(currentUCB))
				{
					return null;
				}
				else if (currentUCB > maxUCB)
				{
					maxUCB = currentUCB;
					selectedChild = child;
				}
			}			
			return selectedChild;
		}		
		
		/**
		 * TODO: API
		 */
		private double getValue(MCTSNode node){
			MCTSNode currentNode = node;			
			return (currentNode.getAverageScore() 
					+ (this.getBalance() 
							* Math.sqrt(
									( 
									(Math.log(currentNode.getParent().getVisitCount())) 
									/ currentNode.getVisitCount()
									)
							 )
						)
					);
		}

		/**
		 * TODO: API
		 * @return
		 */
		private double getBalance() {
			return this.balance;
		}
		
	}
}
