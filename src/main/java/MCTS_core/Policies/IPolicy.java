package MCTS_core.Policies;

import MCTS_core.MCTS.AbstractMCTSSimulation;

/**
 * TODO: API
 * @author Max
 *
 */
public interface IPolicy {

	void evaluateSimulationReward(AbstractMCTSSimulation simulation);
	
}
