package entrants.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements a genetic algorithm.
 * Uses roulette wheel selection and mi,lambda selection.
 * @author Florian Bethe
 */
public class GeneticAlgorithm
{
	/**
	 * Computes the factorial from m to n.
	 * E.g. factorial(3, 6) results in 3*4*5*6 = 360.
	 * @param m start of the factorial
	 * @param n end of the factorial
	 * @return result
	 */
	private static int factorial(int m, int n) {
		int res = 1;
		for(int i = m; i <= n; ++i) {
			res *= i;
		}
		return res;
	}
	
	/**
	 * Number of threads used for evaluation.
	 */
	private final int THREAD_COUNT;
	/**
	 * Number of parents selected from the population for reproduction.
	 */
	private final int PARENT_SIZE;
	/**
	 * Chance of an individual to mutate.
	 */
	private final double MUTATION_CHANCE;
	/**
	 * Current population of the genetic algorithm.
	 */
	private List<GAIndividual<Double>> population;
	/**
	 * Best individual in current population.
	 */
	private GAIndividual<Double> bestIndividual;
	/**
	 * Best individual out of all populations.
	 */
	private GAIndividual<Double> bestIndividualTotal;
	private double bestIndividualTotalFitness;
	
	/**
	 * Constructor.
	 * @param population initial population
	 * @param parentSize number of parents to be selected for reproduction
	 * @param mutationChance chance of an individual to mutate
	 * @param threads threads to be used for evaluation
	 */
	public GeneticAlgorithm(List<GAIndividual<Double>> population, int parentSize, double mutationChance,
			int threads) {
		this.THREAD_COUNT = threads;
		this.PARENT_SIZE = parentSize;
		this.MUTATION_CHANCE = mutationChance;
		this.population = population;
		this.bestIndividual = null;
		this.bestIndividualTotal = null;
		this.bestIndividualTotalFitness = Double.MIN_VALUE;
	}
	
	/**
	 * Performs a single cycle of the genetic algorithm.
	 * Order:
	 * 1. Parent selection
	 * 2. Offspring creation
	 * 3. Population selection
	 * 4. Mutation
	 */
	public void performIteration() {
		List<GAIndividual<Double>> parents = selectParents(PARENT_SIZE);
		this.population = this.mutatePopulation(
				selectNewPopulation(
						parents, createChildren(parents)
					));
	}
	
	/**
	 * Updates the fitness for the selection of the best individual manually.
	 * This should be done after the last cycle only, since the fitnesses get updated
	 * as part of the cycle anyway.
	 */
	public void updateFitnesses() {
		List<Double> fitnesses = evaluatePopulation(population);
		int best = 0;
		for(int i = 1; i < fitnesses.size(); ++i) {
			if(fitnesses.get(i) > fitnesses.get(best)) {
				best = i;
				if(fitnesses.get(i) > bestIndividualTotalFitness) {
					bestIndividualTotal = population.get(i);
				}
			}
		}
	}
	
	/**
	 * Gets the best individual from the current population.
	 * @return Best individual
	 */
	public GAIndividual<Double> getBestIndividual() {
		return bestIndividual;
	}
	
	/**
	 * Gets the best overall individual.
	 * @return Best individual
	 */
	public GAIndividual<Double> getBestIndividualTotal() {
		return bestIndividualTotal;
	}
	
	/**
	 * Evaluates the given population.
	 * @param pop Population
	 * @return List of fitness values
	 */
	private List<Double> evaluatePopulation(List<GAIndividual<Double>> pop) {
		List<Double> fitness = new ArrayList<>();
		
		FitnessEvaluator evals[] = new FitnessEvaluator[THREAD_COUNT];
		Thread threads[] = new Thread[THREAD_COUNT];
		for(int i = 0; i < THREAD_COUNT; ++i) {
			evals[i] = new FitnessEvaluator(pop,
					pop.size() * i / THREAD_COUNT,
					pop.size() * (i + 1) / THREAD_COUNT);
			threads[i] = new Thread(evals[i]);
			threads[i].start();
		}

		try {
			for(int i = 0; i < THREAD_COUNT; ++i) {
				threads[i].join();
				fitness.addAll(evals[i].getFitness());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return fitness;
	}
	
	/**
	 * Selects N parents from the current population.
	 * Uses roulette wheel selection. Also updates the best individuals.
	 * @param parentSize Number of parents to select
	 * @return List of selected individuals
	 */
	private List<GAIndividual<Double>> selectParents(int parentSize) {
		List<Double> fitnesses = evaluatePopulation(population);
		
		// Check for the best individual
		int best = 0;
		for(int i = 1; i < fitnesses.size(); ++i) {
			if(fitnesses.get(i) > fitnesses.get(best)) {
				bestIndividual = this.population.get(i);
				best = i;
				if(fitnesses.get(i) > bestIndividualTotalFitness) {
					bestIndividualTotal = population.get(i);
				}
			}
		}
		
		return rouletteWheelSelection(this.population, fitnesses, parentSize);
	}
	
	/**
	 * Creates new offsprings by combining every parent with every parent.
	 * @param parents Parent individual
	 * @return List of offsprings
	 */
	private List<GAIndividual<Double>> createChildren(List<GAIndividual<Double>> parents) {
		List<GAIndividual<Double>> children = new ArrayList<>(factorial(parents.size() - 2, parents.size()));
		
		// Create the offsprings by combining every parent with every parent (even itself)
		for(GAIndividual<Double> parent1 : parents) {
			for(GAIndividual<Double> parent2 : parents) {
				children.add(parent1.createOffspring(parent2));
			}
		}
		
		return children;
	}
	
	/**
	 * Selects the new population from both parents and children.
	 * Uses roulette wheel selection and mi,lambda (ignores parents).
	 * To change this override the method.
	 * @param parents List of parent individuals
	 * @param children List of offspring individuals
	 * @return List of individuals representing the new population
	 */
	private List<GAIndividual<Double>> selectNewPopulation(List<GAIndividual<Double>> parents, List<GAIndividual<Double>> children) {
		//List<Evaluable<Double>> pop = new ArrayList<>(POPULATION_SIZE);
		return rouletteWheelSelection(children, evaluatePopulation(children), population.size());
	}
	
	/**
	 * Mutates the given population.
	 * Each individual has a chance to be mutated (uniform selection).
	 * @param pop Population
	 * @return Mutated population
	 */
	private List<GAIndividual<Double>> mutatePopulation(List<GAIndividual<Double>> pop) {
		for(GAIndividual<Double> individual : pop) {
			if(ThreadLocalRandom.current().nextDouble() < MUTATION_CHANCE) {
				individual.mutate();
			}
		}
		return pop;
	}
	
	/**
	 * Selects n individuals from the given population by using roulette wheel selection.
	 * @param individuals Population
	 * @param fitnesses Fitnesses of the individuals
	 * @param n Number of individuals to be selected
	 * @return List of selected individuals
	 */
	private static List<GAIndividual<Double>> rouletteWheelSelection(List<GAIndividual<Double>> individuals, List<Double> fitnesses, int n) {
		n = Math.min(individuals.size(), Math.max(0, n));
		
		List<GAIndividual<Double>> selection = new ArrayList<>(n);
		
		// Compute total fitness to get proper random number
		double totalFitness = 0;
		for(Double fitness : fitnesses) {
			totalFitness += fitness;
		}
		
		// Select n individuals
		for(int i = 0; i < n; ++i) {
			// Get random fitness value
			double prob = ThreadLocalRandom.current().nextDouble(totalFitness);
			
			// Find individual on the roulette wheel part with the drawn fitness
			for(int j = 0; j < fitnesses.size(); ++j) {
				prob -= fitnesses.get(j);
				if(prob <= 0) {
					selection.add(individuals.get(j));
					break;
				}
			}
		}
		
		return selection;
	}
	
	private static class FitnessEvaluator implements Runnable {
		private List<Double> fitness;
		private List<GAIndividual<Double>> population;
		private int start;
		private int end;
		
		public FitnessEvaluator(List<GAIndividual<Double>> population, 
								int start, int end) {
			this.population = population;
			this.start = start;
			this.end = end;
		}
		
		@Override
		public void run() {
			fitness = new ArrayList<>(end - start);
			
			for(int i = start; i < end; ++i) {
				fitness.add(population.get(i).evaluate());
			}
		}
		
		public List<Double> getFitness() {
			return fitness;
		}
	}
}
