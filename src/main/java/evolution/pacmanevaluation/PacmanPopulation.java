package evolution.pacmanevaluation;

import evolution.behaviortree.pacman.BehaviorTreePacman;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PacmanPopulation {

	public List<BehaviorTreePacman> individuals;
	int population_size;
	
	
	private int ELITISMDEGREE;
	
	Random RANDOM = new Random();
	
	public PacmanPopulation(int size){
        this.individuals = new LinkedList<>();
		for (int i = 0; i<size; i++){
            individuals.add(BehaviorTreePacman.createRandomBehaviourTreePacman());
		}
		this.population_size = size;
		this.ELITISMDEGREE = Math.max(1, size/3);
	}
	
	
	public void evolve(){
		selection();
		mutation();
		
		for (BehaviorTreePacman individual : individuals){
			individual.clearFitness();
		}
		
	}
	
	private void selection(){
		// natural selection
		Collections.sort(individuals);

		individuals = individuals.subList(0, ELITISMDEGREE);
	}
	
	private void mutation(){
		BehaviorTreePacman mutation;
		
		while (this.individuals.size() < population_size){
			mutation = this.individuals.get(RANDOM.nextInt(ELITISMDEGREE)).copy();
			mutation.mutate();
			mutation.mutate();
			mutation.mutate();
			this.individuals.add(mutation);
		}
	}
	
	public List<BehaviorTreePacman> shuffle(){
		java.util.Collections.shuffle(individuals);
		return individuals;
	}
	
	public int getSize(){
		return individuals.size();
	}
	
	public BehaviorTreePacman getIndividual (int i){
		return individuals.get(i);
	}
	
	public static void main(String[] args){
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
	


}
