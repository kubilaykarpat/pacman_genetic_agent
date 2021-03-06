package evolution.behaviortree;

import evolution.ExtendedGame;
import evolution.Genotype;
import evolution.behaviortree.functionnodes.FunctionNodePacman;
import evolution.behaviortree.terminalnodes.actionnodes.ActionTerminalNodePacman;
import pacman.game.Constants.MOVE;
import util.Utils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class BehaviorTreePacman implements Genotype, Comparable<BehaviorTreePacman> {

    BehaviorNodePacman root;
    private List<Double> fitnessList;

    private BehaviorTreePacman(BehaviorNodePacman root) {
        this.root = root;
        this.fitnessList = new LinkedList<>();
    }


    public static BehaviorTreePacman createRandomBehaviourTreePacman() {
        return new BehaviorTreePacman(FunctionNodePacman.createRandomActionTarget());
    }

    private static double calculateAverage(List<Double> marks) {
        Double sum = new Double(0);
        if (!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    public static BehaviorTreePacman loadFromFile(String filename) {
        Scanner in;
        try {
            in = new Scanner(new FileReader(filename));
            if (in.hasNextLine()) {
                if (in.nextLine().startsWith("<BehaviorTreePacman>")) {
                    BehaviorNodePacman root = BehaviorNodePacman.loadFromFile(in);
                    return new BehaviorTreePacman(root);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        System.out.println("invalid File");
        return null;


    }

    public BehaviorTreePacman copy() {
        return new BehaviorTreePacman(this.root.copy());
    }

    public MOVE eval(ExtendedGame game) {
        return ((ActionTerminalNodePacman) this.root.eval(game)).getAction(game);
    }

    public String toString() {
        System.out.println("<BehaviorTreePacman>");

        this.root.disp(1);

        System.out.println("</BehaviorTreePacman>");
        return "";
    }

    public List<BehaviorNodePacman> getNodes() {
        List<BehaviorNodePacman> nodelist = new LinkedList<BehaviorNodePacman>();
        this.root.getNodes(nodelist);
        return nodelist;
    }

    public List<BehaviorNodePacman> getNonterminalNodes() {
        List<BehaviorNodePacman> nodelist = new LinkedList<BehaviorNodePacman>();
        this.root.getMutableNodes(nodelist);
        return nodelist;
    }

    public void mutate() {
        List<BehaviorNodePacman> nodes = this.getNonterminalNodes();
        nodes.get(Utils.RANDOM.nextInt(nodes.size())).mutate();
    }

    public BehaviorNodePacman getRandomNode() {
        List<BehaviorNodePacman> nodes = this.getNodes();
        return nodes.get(Utils.RANDOM.nextInt(nodes.size()));
    }

    @Override
    public double getFitness() {
        return calculateAverage(fitnessList);
    }

    public void clearFitness() {
        this.fitnessList.clear();
    }

    @Override
    public int compareTo(BehaviorTreePacman tree) {
        Double myFitness = this.getFitness();
        Double oFitness = tree.getFitness();
        return oFitness.compareTo(myFitness);
    }

    @Override
    public void addFitnessValue(double fitness) {
        fitnessList.add(fitness);

    }

    public void storeToFile(String file) {
        PrintStream stdout = System.out;

        // try to store everything into a file
        File yourFile = new File(file);

        // create the logging file if it not exists
        try {
            yourFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // forward the output stream
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), true));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(this);
        System.setOut(stdout);
    }
}
