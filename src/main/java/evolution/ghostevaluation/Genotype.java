package evolution.ghostevaluation;

public interface Genotype {

	public double getFitness();
	
	public void addFitnessValue(double fitness);
	
	public void clearFitness();
		
}
