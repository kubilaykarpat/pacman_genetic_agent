package entrants.mcts;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of a MCTS node.
 * @author Florian Bethe
 * @param <T> action type
 */
public abstract class Node<T>
{
	/**
	 * Action that brought the node about.
	 */
	protected T move;
	/**
	 * Parent node.
	 */
	private Node<T> parent;
	/**
	 * How often has this node been visited.
	 */
	private int visited;
	/**
	 * Visited child nodes.
	 */
	private Map<T, Node<T>> childrenVisited;
	/**
	 * List of remaining possible actions.
	 */
	protected List<T> possibleMoves;
	
	/**
	 * Constructor.
	 * @param move action
	 * @param parent parent node
	 * @param possibleMoves list of possible actions
	 */
	public Node(T move, Node<T> parent, List<T> possibleMoves) {
		this.move = move;
		this.parent = parent;
		this.visited = 0;
		this.childrenVisited = new HashMap<>();
		this.possibleMoves = possibleMoves;
	}
	
	/**
	 * Gets the move performed to get to this node.
	 * @return move
	 */
	public T getMove() {
		return move;
	}
	
	/**
	 * Checks if the node is a root node.
	 * @return true if root
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	/**
	 * Marks this node as root node by setting the parent to null.
	 */
	public void markAsRoot() {
		parent = null;
		move = null;
	}
	
	/**
	 * Checks if the node is a leaf node.
	 * @return true if leaf
	 */
	public boolean isLeaf() {
		return childrenVisited.isEmpty();
	}
	
	/**
	 * Checks if the node has unexpanded actions left.
	 * @return true if node can be expanded
	 */
	public boolean hasActionsLeft() {
		return !possibleMoves.isEmpty();
	}
	
	/**
	 * Returns the parent node.
	 * @return parent node
	 */
	public Node<T> getParent() {
		return parent;
	}
	
	public Collection<Node<T>> getChildren() {
		return childrenVisited.values();
	}
	
	/**
	 * Returns the child node that would be reached by performing action.
	 * @param action action to be performed
	 * @return child node
	 */
	public Node<T> getChild(T action) {
		return childrenVisited.get(action);
	}
	
	/**
	 * Increases the node's and its parent's visited count.
	 */
	public void increaseVisitedCount() {
		Node<T> curr = this;
		while(curr != null) {
			++curr.visited;
			curr = curr.getParent();
		}
	}
	
	/**
	 * Gets the number of times this node has been visited.
	 * @return visited count
	 */
	public int getVisitedCount() {
		return this.visited;
	}
	
	/**
	 * Expands this node with the given action and child node.
	 * @param action action chosen
	 * @param child child resulting
	 */
	public void expand(T action, Node<T> child) {
		this.childrenVisited.put(action, child);
		this.possibleMoves.remove(action);
	}
	
	/**
	 * Adds a random child from the possible moves to the children.
	 * @return added child node
	 */
	public abstract Node<T> getRandomChild();
	
	/**
	 * Visits this node.
	 */
	public abstract void visit();
	
	/**
	 * Gets the value of the node.
	 * @return node value
	 */
	public abstract double getValue();
}
