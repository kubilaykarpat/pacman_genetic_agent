package entrants.mcts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements general Monte-Carlo Tree Search.
 * @author Florian Bethe
 * @param <T> action type
 */
public class MCTS<T>
{
	private static Random rng = new Random();
	
	/**
	 * Balances between exploration and exploitation in the UCB1 tree policy.
	 */
	private final double EXPLORATION_TERM;
	protected Node<T> root;
	
	/**
	 * Constructor.
	 * @param root Root node for the tree
	 * @param explorationTerm balance between exploration and exploitation
	 */
	public MCTS(Node<T> root, double explorationTerm) {
		this.root = root;
		this.EXPLORATION_TERM = explorationTerm;
	}
	
	/**
	 * Performs a single simulation on the tree.
	 */
	public void simulate() {
		getNextTreeNode(root).getRandomChild().visit();
	}
	
	/**
	 * Gets the next node to be expanded.
	 * Follows the tree policy given in {@link #followTreePolicy(Node) followTreePolicy}.
	 * @param root node to assume as root
	 * @return next node to expand
	 */
	public Node<T> getNextTreeNode(Node<T> root) {
		Node<T> curr = root;
		
		// Iterate until we find a node that has actions left to choose
		while(!curr.hasActionsLeft()) {
			curr = followTreePolicy(curr);
		}
		
		return curr;
	}
	
	/**
	 * Returns the best action to take given the current tree state.
	 * Also advances the root to take the new node.
	 * @return best action
	 */
	public T getBestAction() {
		List<T> best = new ArrayList<>();
		double bestValue = -Double.MAX_VALUE;
		
		for(Node<T> curr : root.getChildren()) {
			// Evaluate node
			double currValue = curr.getValue();

			// Check if we got equal values or a true better one
			if(currValue == bestValue) {
				best.add(curr.getMove());
			} else if(currValue > bestValue) {
				best.clear();
				bestValue = currValue;
				best.add(curr.getMove());
			}
		}
		
		// Which single action is best?
		T bestAction = null;
		
		if(best.size() > 1) {
			bestAction = actionTieBreaker(best);
		} else if(best.size() == 0) {
			return null;
		} else {
			bestAction = best.get(0);
		}
		
		// Advance the root
		root = root.getChild(bestAction);
		root.markAsRoot();
		
		return bestAction;
	}
	
	/**
	 * Tie breaker in case two or more actions feature the same value.
	 * @param actions list of actions of equal value
	 * @return chosen action among them
	 */
	public T actionTieBreaker(List<T> actions) {
		return actions.get(rng.nextInt(actions.size()));
	}
	
	/**
	 * Selects which node to follow during traversal.
	 * Uses UCB1 by default.
	 * @param node node to decide about
	 * @return next node
	 */
	public Node<T> followTreePolicy(Node<T> node) {
		Node<T> bestNode = null;
		double bestValue = -Double.MAX_VALUE;
		
		// For all children compute UCB1 term and choose best one
		for(Node<T> child : node.getChildren()) {
			double val = child.getValue() + EXPLORATION_TERM*Math.sqrt(Math.log(node.getVisitedCount()) / child.getVisitedCount());
			if(val > bestValue) {
				bestValue = val;
				bestNode = child;
			}
		}
		
		return bestNode;
	}
}
