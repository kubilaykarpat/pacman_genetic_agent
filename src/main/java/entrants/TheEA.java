package entrants;

import entrants.pacman.username.MyPacMan;
import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;

import java.util.EnumMap;

public class TheEA extends AbstractProblem {

    int numberOfActions;
    int numberOfCriteria;
    int numberOfDecisionLength;
    int numberOfMaxParameters ;
    int numberOfNodes;
    int best;
    public TheEA( int numberOfMaxParameters, int numberOfCriteria, int numberOfDecisionLength, int numberOfActions){
                super(((int)Math.pow(2, numberOfDecisionLength ) - 1)*(numberOfMaxParameters+1),1);
        this.numberOfDecisionLength = numberOfDecisionLength;
        this.numberOfCriteria = numberOfCriteria;
        this.numberOfMaxParameters = numberOfMaxParameters;
        this.numberOfActions = numberOfActions;
        this.numberOfNodes = (int)Math.pow(2, numberOfDecisionLength ) - 1*(numberOfMaxParameters+1);
        best = Integer.MIN_VALUE;
    }
    @Override
    public void evaluate(Solution solution) {
        Executor executor = new Executor(true, true);

        EnumMap<Constants.GHOST, IndividualGhostController> controllers = new EnumMap<>(Constants.GHOST.class);

        controllers.put(Constants.GHOST.INKY, new Inky());
        controllers.put(Constants.GHOST.BLINKY, new Blinky());
        controllers.put(Constants.GHOST.PINKY, new Pinky());
        controllers.put(Constants.GHOST.SUE, new Sue());
        executor.setScaleFactor(1.5);

//        executor.runGameTimed(new MyPacMan(solution,numberOfMaxParameters), new MASController(controllers), true);
        //double obj = (double)executor.runGame(new MyPacMan(solution,numberOfMaxParameters,numberOfDecisionLength,numberOfActions), new MASController(controllers), true,10);

        solution.setObjective(0, -1);
    }
    @Override
    public Solution newSolution() {
        Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives());
        int numberOfLeafs = (int)Math.pow(2, numberOfDecisionLength-1);
        int startIndexForLeaf = getNumberOfVariables() - (numberOfLeafs) * (numberOfMaxParameters + 1);
        for(int i = 0; i < startIndexForLeaf;i++)
            if(i % (numberOfMaxParameters+1) == 0)
                solution.setVariable(i, EncodingUtils.newInt(0, numberOfCriteria -1));
            else
                solution.setVariable(i, EncodingUtils.newReal(0, 1));

        for(int i = 0;
            i < numberOfLeafs * (numberOfMaxParameters+1);
            i++, startIndexForLeaf++)
            if(startIndexForLeaf % (numberOfMaxParameters +1) == 0) {
                solution.setVariable(startIndexForLeaf, EncodingUtils.newInt(0 - numberOfActions, -1));
                //System.out.println("NodeIndex: " + startIndexForLeaf + " NumberOfNodes: " + getNumberOfVariables());
            }
            else
            {

                solution.setVariable(startIndexForLeaf, EncodingUtils.newReal(0, 1));
            }
        return solution;
    }
}
