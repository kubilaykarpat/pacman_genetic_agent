package evolution;

import evolution.behaviortree.BehaviorNodePacman;
import evolution.behaviortree.BehaviorTreePacman;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import util.Utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static util.Utils.RANDOM;

public class PacmanPopulation {
    private static final Logger LOGGER = Logger.getLogger(PacmanPopulation.class.getName());
    private static final int MAX_LOOP = 100;
    private final int numberOfElites;
    private final int numberOfCrossOvers;
    private List<BehaviorTreePacman> individuals;
    private int population_size;

    public PacmanPopulation(int size) {
        this.individuals = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            individuals.add(BehaviorTreePacman.createRandomBehaviourTreePacman());
        }
        this.population_size = size;
        this.numberOfElites = Math.max(1, size / 3); // We will keep size/3 best individuals
        this.numberOfCrossOvers = numberOfElites; //Math.max(1, (size - numberOfElites) / 2); // We will create size/6 individuals with cross over
        // Rest of the individuals will be created by mutation
    }

    public static void main(String[] args) {
        PacmanPopulation pop = new PacmanPopulation(7);
        pop.getIndividual(0).addFitnessValue(1.0);
        pop.getIndividual(1).addFitnessValue(3.0);
        pop.getIndividual(2).addFitnessValue(-3.0);
        pop.getIndividual(3).addFitnessValue(0.0);
        pop.getIndividual(4).addFitnessValue(7.0);
        pop.getIndividual(5).addFitnessValue(-5.0);
        pop.getIndividual(6).addFitnessValue(2.0);

        Collections.sort(pop.individuals);
        for (BehaviorTreePacman p : pop.individuals)
            System.out.println(p.getFitness());

        //pop.evolve();
    }

    public void evolve() {
        selection();
        crossOver();
        mutation();

        for (BehaviorTreePacman individual : individuals) {
            individual.clearFitness();
        }

    }

    private void selection() {
        // natural selection
        Collections.sort(individuals);

        individuals = individuals.subList(0, numberOfElites);
    }

    private void crossOver() {
        while (this.individuals.size() < numberOfElites + numberOfCrossOvers) {
            BehaviorTreePacman firstTree = this.individuals.get(RANDOM.nextInt(numberOfElites));
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
                BehaviorTreePacman secondTree = this.individuals.get(RANDOM.nextInt(numberOfElites));
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

            /*LOGGER.info("CROSS OVER BABY");
            System.out.println("<------- CROSS OVER STARTED------->");
            System.out.println("Source: ");
            firstTree.toString();
            System.out.println("Selected sub-tree: ");
            suitableSubTree.disp(1);
            System.out.println("Result: ");
            newTree.toString();
            System.out.println("<------- CROSS OVER FINISHED ------->");*/
        }
    }

    private void mutation() {
        BehaviorTreePacman mutation;

        while (this.individuals.size() < population_size) {
            mutation = this.individuals.get(RANDOM.nextInt(numberOfElites)).copy();
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


	/*public List<BehaviorNodePacman> retrieveAllNodes(BehaviorNodePacman node){
		node.getNodes();

	}*/


}
