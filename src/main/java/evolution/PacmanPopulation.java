package evolution;

import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.BehaviorTreePacman;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static util.Utils.RANDOM;

public class PacmanPopulation {
    private static final Logger LOGGER = Logger.getLogger(PacmanPopulation.class.getName());
    private static final int MAX_LOOP = 100;


    private List<BehaviorTreePacman> individuals;
    private int population_size;

    public PacmanPopulation(int size) {
        this.individuals = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            individuals.add(BehaviorTreePacman.createRandomBehaviourTreePacman());
        }
        this.population_size = size;
    }


    public void selection(int numberOfElites, int tournamentSize) {
        // natural selection
        Collections.sort(individuals);

        List<BehaviorTreePacman> candidates = individuals.subList(numberOfElites, individuals.size()); // Then deal with the rest
        individuals = new ArrayList<>(individuals.subList(0, numberOfElites)); // Always pick the elites

        int numberOfOpenPositions = population_size - numberOfElites;

        for (int i = 0; i < numberOfOpenPositions; i++) {
            List<BehaviorTreePacman> tournamentNodes = RANDOM.ints(tournamentSize, 0, candidates.size()).mapToObj(candidates::get).collect(Collectors.toList());
            BehaviorTreePacman winner = null;
            for (BehaviorTreePacman tournamentNode : tournamentNodes) {
                if (winner == null || tournamentNode.getFitness() > winner.getFitness())
                    winner = tournamentNode;
            }
            individuals.add(winner);
            candidates.remove(winner);
        }
    }

    public void crossOver(int numberOfNewBreeds) {
        int lastPopulationSize = this.individuals.size();

        for (int i = 0; i < numberOfNewBreeds; i++) {
            BehaviorTreePacman firstTree = this.individuals.get(RANDOM.nextInt(lastPopulationSize));
            BehaviorTreePacman newTree = firstTree.copy();
            List<BehaviorNodePacman> nodesOfFirstTree = newTree.getNodes();
            BehaviorNodePacman parentOfCutPoint = Utils.pickRandomElementFromList(nodesOfFirstTree);

            if (!(parentOfCutPoint instanceof FunctionNodePacman)) continue;

            List<BehaviorNodePacman> children = parentOfCutPoint.getDirectChildren();
            int cutPoint = RANDOM.nextInt(children.size());
            BehaviorNodePacman cutNode = children.get(cutPoint);


            BehaviorNodePacman suitableSubTree = null;

            // We might not find a suitable sub tree from a randomly selected individual tree
            // So we should keep looking in other trees
            int count = 0;
            while (count < MAX_LOOP) {
                BehaviorTreePacman secondTree = this.individuals.get(RANDOM.nextInt(lastPopulationSize));
                // One might think that first and second tree should not be the same individual
                // yet it still provides diversity! So we will not check whether those 2 trees are same or not

                List<BehaviorNodePacman> nodesOfSecondTree = secondTree.getNodes();

                suitableSubTree = Utils.filterAndPickRandomElementFromList(nodesOfSecondTree,
                        (node) -> node.getTarget() == cutNode.getTarget());
                if (suitableSubTree != null) {
                    suitableSubTree = suitableSubTree.copy(); // We should not alter the original tree
                }
                count++;
            }

            if (suitableSubTree == null) continue;

            ((FunctionNodePacman) parentOfCutPoint).overrideChildren(cutPoint, suitableSubTree);
            individuals.add(newTree);
        }
    }

    public void mutation(int numberOfNewBreeds) {
        int lastPopulationSize = this.individuals.size();

        for (int i = 0; i < numberOfNewBreeds; i++) {
            BehaviorTreePacman mutation = this.individuals.get(RANDOM.nextInt(lastPopulationSize)).copy();
            mutation.mutate();
            mutation.mutate();
            mutation.mutate();
            this.individuals.add(mutation);
        }
    }

    public List<BehaviorTreePacman> shuffle() {
        java.util.Collections.shuffle(individuals);
        return individuals;
    }

    public int getSize() {
        return individuals.size();
    }

    public BehaviorTreePacman getIndividual(int i) {
        return individuals.get(i);
    }

}
