package internal_competition.entrants.pacman.team1;

import java.util.*;

import pacman.game.Constants;
import pacman.game.Game;

import static oracle.jrockit.jfr.events.Bits.intValue;


public class ExtendedGame {
    private boolean[] pillIsStillAvailable = null;
    private boolean[] powerPillIsStillAvailable = null;
    private int mazeIndex;
    public Game game;
    private Constants.DM distanceMeasure = Constants.DM.MANHATTAN;
    private ArrayList<Integer> pillsInMaze = null;
    private int[] powerPillsInMaze = null;
    Random rnd = new Random();




    public ExtendedGame(){
    }

    // Initialization phase
    public void initGame(Game game, ArrayList<Integer> pillsInMaze){
        this.game = game;
        this.pillsInMaze = pillsInMaze;
        this.powerPillsInMaze = game.getPowerPillIndices();
        resetPills();
        resetPowerPills();
    }

    // Updates the state of saved pills
    public void updateGame(Game game){
        this.game = game;

        if (mazeIndex != game.getMazeIndex()){
            this.resetData(game);
        }

        //set pills
        int[] powerPills = this.game.getPowerPillIndices();
        int[] pills = this.game.getPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean thisPill = this.game.isPowerPillStillAvailable(i);

            if (thisPill != null && thisPill == false){
                this.powerPillIsStillAvailable[i] = false;
            }
        }

        for (int i = 0; i < pills.length; i++)
        {
            Boolean thisPill = this.game.isPillStillAvailable(i);
            if (thisPill != null && thisPill == false){
                this.pillIsStillAvailable[i] = false;
            }
        }
    }

    // Check the global space with pills
    public int goToPill(){
        ArrayList<Integer> bestChain = new ArrayList();
        ArrayList<ArrayList<Integer>> listOfLengths = new ArrayList();
        int bestCount = 0;

        for(int i = 0; i < this.pillIsStillAvailable.length; i++){
            // iterate over all pills
            // each true chain is stored
            // on false interupt store chain
            // Assumption is that pills clustered together will have sequential indexes
            if (this.pillIsStillAvailable[i] == true){
                bestChain.add(i);
                bestCount ++;
            } else{
                if (bestChain.size() != 0){
                    ArrayList<Integer> arrayToStore = new ArrayList(bestChain);
                    listOfLengths.add(arrayToStore);
                    bestChain = new ArrayList<>();
                }
            }
        }

        // iterate chains and look for longest
        ArrayList<ScoreClass> evaluation = EvaluateChains(listOfLengths, -1);
        int bestTarget = -1;
        if (evaluation.size() > 1){
            bestTarget = GetBestCurrentTarget(evaluation);
        } else if(evaluation.size() != 0) {
            bestTarget = evaluation.get(0).getClosestNode();
        }

        if(bestTarget == -1){
            // When no clusters have been computed make the first available pill the next target
            System.out.print("No clusters");
            int index = 0;
            for (int i = 0; i < this.pillIsStillAvailable.length; i ++){
                if(this.pillIsStillAvailable[i]){
                    index = i;
                    break;
                }
            }
            bestTarget = this.pillsInMaze.get(index);

        }

        // FIx this for end game
        return bestTarget;
    }

    // Checks the global solution and tries to go to pills which have not been collected yet
    // and guard them. Method is used for ghosts.
    public int goToPositionForGhost(int ghostNode, int decisionIndex, int pacmanIndex){
        ArrayList<Integer> bestPosition = new ArrayList();
        ArrayList<ArrayList<Integer>> pointsOfInterest = new ArrayList();
        int bestCount = 0;

        for(int i = 0; i < this.pillIsStillAvailable.length; i++){
            if (this.pillIsStillAvailable[i] == true){
                bestPosition.add(i);
                bestCount ++;
            } else{
                if (bestPosition.size() != 0){
                    int test =1;
                    ArrayList<Integer> arrayToStore = new ArrayList(bestPosition);
                    pointsOfInterest.add(arrayToStore);
                    bestPosition = new ArrayList<>();
                }
            }
        }

        // iterate chains and look for longest
        ArrayList<ScoreClass> evaluation = EvaluateChains(pointsOfInterest, ghostNode);
        int bestTarget = -1;
        if (pacmanIndex != -1 ){
            return pacmanIndex;
        }

        if (evaluation.size() == 0){
            //First ghost just selects a random junction index
            if (decisionIndex < 2){
                int[] junctions = game.getJunctionIndices();
                bestTarget = junctions[rnd.nextInt(junctions.length)];

            } else {
                //If no clusters have been found try and guard the power pills
                Boolean isPowerPillAvailable = this.powerPillIsStillAvailable[decisionIndex -1];
                if (isPowerPillAvailable){
                    bestTarget = this.powerPillsInMaze[decisionIndex - 1];
                } else {
                    Boolean hasActivePowerPills = false;
                    int nodeToUse = -1;
                    for(int t = 0; t< this.powerPillIsStillAvailable.length; t ++){
                        if (this.powerPillIsStillAvailable[t]){
                            nodeToUse = t;
                        }
                    }
                    if (nodeToUse != -1){
                        bestTarget = this.powerPillsInMaze[nodeToUse];
                    }
                }
            }

        }
        if (evaluation.size() > 0){
            bestTarget = GestBestCurrentTargetWithDecision(evaluation, decisionIndex);
        }

        // FIx this for end game
        return bestTarget;
    }

    //After extraction of the available pills, they are being segmented in sub lists, where each segment
    //is used to build a Score of the cluster, which contain the furhest and closest nodes, making assumptions
    // that they are following one sequence. Possible drawback for three of four way junctions.
    private ArrayList<ScoreClass> EvaluateChains(ArrayList<ArrayList<Integer>> listOfLengths, int ghostPosition){
        int bestCount = 0;
        ArrayList<ScoreClass> scores = new ArrayList();
        int stepSize = 25; // Used to determine cluster size

        for(int i = 0; i < listOfLengths.size(); i++){
            ArrayList<Integer> currentList = listOfLengths.get(i);
            // expect chains with more than N pills together
            // select closest chain and score

            if (currentList.size() > stepSize){
                //check distance to first and last pill - use median for scale
                int remaining = currentList.size() % stepSize;
                int iterationCount = (currentList.size() - remaining) / stepSize;

                for(int j = 0; j < iterationCount; j++){
                    int startIndex = j * stepSize;
                    ArrayList<Integer> listToConsider = new ArrayList(currentList.subList(j, j+ stepSize));
                    ScoreClass scoreToAdd = null;
                    if(ghostPosition != -1){
                        scoreToAdd = ExtractScoreFromChainForGhost(listToConsider, ghostPosition);
                    }else {
                        scoreToAdd = ExtractScoreFromChain(listToConsider);
                    }

                    scores.add(scoreToAdd);
                }

                if(remaining > 0){
                    int startIndex = iterationCount * stepSize - 1;
                    ArrayList<Integer> listToConsider = new ArrayList(currentList.subList(startIndex, startIndex + remaining));
                    ScoreClass scoreToAdd = null;
                    if(ghostPosition != -1){
                        scoreToAdd = ExtractScoreFromChainForGhost(listToConsider, ghostPosition);
                    }else {
                        scoreToAdd = ExtractScoreFromChain(listToConsider);
                    }
                    scores.add(scoreToAdd);
                }

            } else{
                ScoreClass newScore = null;
                if(ghostPosition != -1){
                    newScore = ExtractScoreFromChainForGhost(currentList, ghostPosition);
                }else {
                    newScore = ExtractScoreFromChain(currentList);
                }
                scores.add(newScore);
            }
        }

        return scores;
    }

    private int GetBestCurrentTarget(ArrayList<ScoreClass> scores){
        scores.sort(Comparator.comparing(ScoreClass::getDensity));

        return scores.get(0).getClosestNode();
    }

    //Give different targets to each ghost, where each one compares different values
    private int GestBestCurrentTargetWithDecision(ArrayList<ScoreClass> scores, int decisionIndex){
        if (scores.size() == 0){
            return -1;
        } else {
            if (decisionIndex == 1){
                scores.sort(Comparator.comparing(ScoreClass::getDensity));
                return scores.get(0).getFarthestNode();

            } else if (decisionIndex == 2) {
                scores.sort(Comparator.comparing(ScoreClass::getDistance));
                return scores.get(intValue(scores.size() / 2)).getClosestNode();
            } else if (decisionIndex == 3){
                int newRand = rnd.nextInt(scores.size());

                scores.sort(Comparator.comparing(ScoreClass::getDistance));
                return scores.get(newRand).getClosestNode();
            } else {
                int newRand = rnd.nextInt(scores.size());

                scores.sort(Comparator.comparing(ScoreClass::getSize).thenComparing(ScoreClass::getDistance));
                return scores.get(newRand).getClosestNode();
            }
        }
    }

    // Used to construct the ScoreClass object for Mrs. Pacman
    private ScoreClass ExtractScoreFromChain(ArrayList<Integer> currentList){

        int firstElementAsNode = this.pillsInMaze.get(currentList.get(0));
        int lastElementAsNode = this.pillsInMaze.get(currentList.get(currentList.size() - 1));
        double distanceToFirst = this.game.getDistance(this.game.getPacmanCurrentNodeIndex(),
                firstElementAsNode, distanceMeasure);
        double distanceToLast = this.game.getDistance(this.game.getPacmanCurrentNodeIndex(),
                lastElementAsNode, distanceMeasure);

        double distanceMeasure = (distanceToFirst + distanceToLast) / 2;
//        double summedDistances =
        ScoreClass toAdd = new ScoreClass(distanceMeasure, currentList.size(), distanceToFirst, distanceToLast,
                firstElementAsNode, lastElementAsNode);
        return toAdd;
    }

    // Used to construct the ScoreClass object for the ghosts
    private ScoreClass ExtractScoreFromChainForGhost(ArrayList<Integer> currentList, int ghostPosition){

        int firstElementAsNode = this.pillsInMaze.get(currentList.get(0));
        int lastElementAsNode = this.pillsInMaze.get(currentList.get(currentList.size() - 1));
        double distanceToFirst = this.game.getDistance(ghostPosition,
                firstElementAsNode, distanceMeasure);
        double distanceToLast = this.game.getDistance(ghostPosition,
                lastElementAsNode, distanceMeasure);

        double distanceMeasure = (distanceToFirst + distanceToLast) / 2;
//        double summedDistances =
        ScoreClass toAdd = new ScoreClass(distanceMeasure, currentList.size(), distanceToFirst, distanceToLast,
                firstElementAsNode, lastElementAsNode);
        return toAdd;
    }

    private void resetData(Game game){
        this.resetPowerPills();
        this.resetPills();
        this.mazeIndex = game.getMazeIndex();
    }

    private void resetPowerPills()
    {
        if (powerPillIsStillAvailable == null){
            this.powerPillIsStillAvailable = new boolean[game.getPowerPillIndices().length];
            for (int i = 0; i < this.powerPillIsStillAvailable.length; i++){
                this.powerPillIsStillAvailable[i] = true;
            }
        }
    }

    private void resetPills()
    {
        this.pillIsStillAvailable = new boolean[game.getPillIndices().length];
        for (int i = 0; i < this.pillIsStillAvailable.length; i++){
            this.pillIsStillAvailable[i] = true;
        }
    }

    public boolean isPowerPillStillAvailable()
    {
        if (this.powerPillIsStillAvailable != null){
            for (int i = 0; i < this.powerPillIsStillAvailable.length; i++){
                if (this.powerPillIsStillAvailable[i] == true)
                    return true;
            }
        }
        return false;
    }

    public boolean isPowerPillStillAvailable(int powerPillIndex){
        return this.powerPillIsStillAvailable[powerPillIndex];
    }

    public boolean isPillStillAvailable(int pillIndex){
        return this.pillIsStillAvailable[pillIndex];
    }
}