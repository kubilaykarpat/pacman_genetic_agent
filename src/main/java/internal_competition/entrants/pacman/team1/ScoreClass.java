package internal_competition.entrants.pacman.team1;

/**
 * Created by Anton on 01-Jul-17.
 */
public class ScoreClass {
    private double distanceMeasure = 0;
    private int chainSize = 0;
    private double distanceToFirst = 0;
    private double distanceToLast = 0;
    private int firstNodeIndex = 0;
    private int lastNodeIndex = 0;

    public ScoreClass(){}

    public ScoreClass(double distance, int size, double distanceToFirst, double distanceToLast,
                      int firstNodeIndex, int lastNodeIndex){
        this.distanceMeasure = distance;
        this.chainSize = size;
        this.distanceToLast = distanceToLast;
        this.distanceToFirst = distanceToFirst;
        this.firstNodeIndex = firstNodeIndex;
        this.lastNodeIndex = lastNodeIndex;
    }

    public double getDistance(){
        return  this.distanceMeasure;
    }

    public int getSize(){
        return this.chainSize;
    }

    public int getClosestNode(){
        if(this.distanceToFirst > this.distanceToLast){
            return this.firstNodeIndex;
        } else {
            return this.lastNodeIndex;
        }
    }

    public int getFarthestNode(){
        if(this.distanceToFirst > this.distanceToLast){
            return this.lastNodeIndex;
        } else {
            return this.firstNodeIndex;
        }
    }

    //Used to measure the value of each cluster, where each cluster is evaluated as the size of the cluster multiplied
    // by it's contents and is divided based on the node which is furthest away.
    public double getDensity(){
        double closestNode = this.distanceToFirst > this.distanceToLast ? this.distanceToFirst : this.distanceToLast;
        double farthest = this.distanceToFirst > this.distanceToLast ? this.distanceToLast : this.distanceToFirst;

        double score = (closestNode * this.chainSize) / farthest;
//        double score = (closestNode + this.chainSize) / this.chainSize;

//        double density = ((this.distanceToFirst + this.distanceToLast) / this.chainSize - this.distanceToFirst) /
//                (this.distanceToLast - this.distanceToFirst);
        return score;
    }



}
