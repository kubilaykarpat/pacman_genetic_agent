package evolution.ghostevaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import evolution.behaviortree.ghosts.BehaviorTree;

public class Population {

	List<BehaviorTree> individuals;
	int population_size;
	
	
	private int ELITISMDEGREE;
	
	Random RANDOM = new Random();
	
	public Population(int size){
		this.individuals = new LinkedList<BehaviorTree>();
		for (int i = 0; i<size; i++){
			individuals.add(new BehaviorTree());
		}
		this.population_size = size;
		this.ELITISMDEGREE = Math.max(1, size/3);
	}
	
	
	public void evolve(){
		selection();
		mutation();
		
		for (BehaviorTree individual : individuals){
			individual.clearFitness();
		}
		
	}
	
	private void selection(){
		// natural selection
		Collections.sort(individuals);

		individuals = individuals.subList(0, ELITISMDEGREE);
	}
	
	private void mutation(){
		BehaviorTree mutation;
		
		while (this.individuals.size() < population_size){
			mutation = this.individuals.get(RANDOM.nextInt(ELITISMDEGREE)).copy();
			mutation.mutate();
			mutation.mutate();
			mutation.mutate();
			this.individuals.add(mutation);
		}
	}
	
	public List<BehaviorTree> shuffle(){
		java.util.Collections.shuffle(individuals);
		return individuals;
	}
	
	public int getSize(){
		return individuals.size();
	}
	
	public BehaviorTree getIndividual (int i){
		return individuals.get(i);
	}
	
	public static void main(String[] args){
		Population pop = new Population(7);
		pop.individuals.get(0).addFitnessValue(1.0);
		pop.individuals.get(1).addFitnessValue(3.0);
		pop.individuals.get(2).addFitnessValue(-3.0);
		pop.individuals.get(3).addFitnessValue(0.0);
		pop.individuals.get(4).addFitnessValue(7.0);
		pop.individuals.get(5).addFitnessValue(-5.0);
		pop.individuals.get(6).addFitnessValue(2.0);

		pop.evolve();
	}
	


}
