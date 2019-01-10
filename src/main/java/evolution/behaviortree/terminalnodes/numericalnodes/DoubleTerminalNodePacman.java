package evolution.behaviortree.terminalnodes.numericalnodes;

import evolution.ExtendedGame;
import util.Utils;

public class DoubleTerminalNodePacman extends NumberTerminalNodePacman {

    public double number;

    public DoubleTerminalNodePacman(double number) {
        this.number = number;
    }


    public DoubleTerminalNodePacman() {
        this.number = Utils.RANDOM.nextDouble();
    }

    @Override
    public double getData(ExtendedGame extendedgame) {
        return this.number;
    }


    @Override
    public void disp(int depth) {
        String str = "<DoubleTerminalNode>";
        String padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);

        Double d = this.number;
        padded = String.format("%1$" + (4 * (depth + 1) + d.toString().length()) + "s", d.toString());
        System.out.println(padded);

        str = "</DoubleTerminalNode>";
        padded = String.format("%1$" + (4 * depth + str.length()) + "s", str);
        System.out.println(padded);

    }

    @Override
    public DoubleTerminalNodePacman copy() {
        return new DoubleTerminalNodePacman(this.number);
    }

}
