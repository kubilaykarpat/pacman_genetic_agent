package entrants.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Evaluator class.
 * Stores weights and produces weighted sum.
 * @author Florian Bethe
 */
public class Evaluator
{
	/**
	 * Weights.
	 */
	private double weights[];
	
	/**
	 * Constructor.
	 * Creates evaluator with n weights of value 1.
	 * @param size number of weights
	 */
	public Evaluator(int size) {
		this.weights = new double[size];
		for(int i = 0; i < size; ++i) {
			weights[i] = 1.0;
		}
	}
	
	/**
	 * Constructor.
	 * @param weights evaluation weights
	 */
	public Evaluator(double... weights) {
		this.weights = weights;
	}
	
	/**
	 * Gets the weight at the specified index.
	 * Does not perform any validity checks.
	 * @param index weight index
	 * @return nth weight
	 */
	public double getWeight(int index) {
		return weights[index];
	}
	
	/**
	 * Randomizes the weights in bounds from 0 to width.
	 * Does not perform any validity checks!
	 * @param widths widths for randomization
	 * @return this after randomization
	 */
	public Evaluator randomize(double...widths) {
		for(int i = 0; i < weights.length; ++i) {
			weights[i] = ThreadLocalRandom.current().nextDouble(0, widths[i]);
		}
		return this;
	}
	
	/**
	 * Computes the weighted sum for the given vector.
	 * Only computes the sum up to the minimum length of both vector and weights.
	 * Does NOT normalize the result with the weight sum.
	 * @param ds value vector
	 * @return weighted sum
	 */
	public double evaluate(double... ds) {
		double sum = 0.0;
		for(int i = 0; i < Math.min(ds.length, weights.length); ++i) {
			sum += ds[i] * weights[i];
		}
		return sum;
	}
	
	/**
	 * Combines this evaluator with the given one.
	 * Performs 1-point crossover with a randomly chosen split location.
	 * @param eval evaluator to combine with
	 * @return new combined {@link Evaluator Evaluator}
	 */
	public Evaluator combine(Evaluator eval) {
		double weights[] = new double[this.weights.length];
		int split = ThreadLocalRandom.current().nextInt(weights.length);
		for(int i = 0; i < split; ++i) {
			weights[i] = this.weights[i];
		}
		for(int i = split; i < weights.length; ++i) {
			weights[i] = eval.weights[i - split];
		}
		return new Evaluator(weights);
	}
	
	/**
	 * Mutates this evaluator.
	 * Chooses a weight at random and adds a gaussian weighted with 1/10th
	 * of the weights magnitude.
	 */
	public void mutate() {
		int index = ThreadLocalRandom.current().nextInt(weights.length);
		weights[index] += ThreadLocalRandom.current().nextGaussian() * weights[index] / 10.0;
	}
	
	@Override
	public String toString() {
		String res = "";
		for(int i = 0; i < weights.length; ++i) {
			res += weights[i] + " ";
		}
		return res;
	}
}
