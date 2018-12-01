package internal_competition.entrants.pacman.team5;

import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class  MTDfBot extends PacmanController {

    private int maxDepth = 4;

    private Map<Integer, Double> lowerBounds = new HashMap<Integer, Double>();
    private Map<Integer, Double> upperBounds = new HashMap<Integer, Double>();

    public Constants.MOVE getMove(Game game, long timeDue) {
        int myNodeIndex = game.getPacmanCurrentNodeIndex();
        Constants.MOVE[] legalMoves = game.getPossibleMoves(myNodeIndex);

        double maxValue = -Double.MAX_VALUE;
        Constants.MOVE maxMove = Constants.MOVE.NEUTRAL;
    int j = -100;
        if (game.isJunction(myNodeIndex)) {
            for (int i = 0; i < legalMoves.length; i++) {

                Game nextGame = gameForAdvance(game);
                advanceGame(myNodeIndex, new POCommGhosts(50), nextGame, legalMoves[i]);

                double firstguess = 0;

                for (int d = 0; d < maxDepth; d++) {
                    firstguess = MTDF(nextGame.copy(), firstguess, d);
                    System.out.println(d);
                }

                if (firstguess > maxValue) {
                    maxValue = firstguess;
                    maxMove = legalMoves[i];
                    j = i;
                }
            }
            System.out.println(maxMove.toString()+" "+j);
            return maxMove;
        }else{
            Constants.MOVE lastMove = game.getPacmanLastMoveMade();
            if (Arrays.asList(legalMoves).contains(lastMove)){
                return lastMove;
            }

            for (Constants.MOVE move : legalMoves){
                if (move != lastMove.opposite()){
                    return move;
                }
            }
        }
        return Constants.MOVE.NEUTRAL;
    }

    private double MTDF(Game game, double guess, int depth){

        double newGuess = guess;
        double upperBound = Double.MAX_VALUE;
        double lowerBound = -Double.MAX_VALUE;

        double beta;

        while(lowerBound < upperBound){
            if (newGuess == lowerBound){
                beta = newGuess + 1;
            }
            else{
                beta = newGuess;
            }
            newGuess = AlphaBetaWithMemory(game, depth,beta -1 ,beta, true);

            if (newGuess < beta){
                upperBound = newGuess;
            }else{
                lowerBound = newGuess;
            }
        }

        return newGuess;
    }

    private double AlphaBetaWithMemory(Game game, int depth, double alpha, double beta, boolean maximizingPlayer){
        int myNodeIndex = game.getPacmanCurrentNodeIndex();

        if(lowerBounds.containsKey(myNodeIndex) && upperBounds.containsKey(myNodeIndex)) {
            double nodeLowerbound = lowerBounds.get(myNodeIndex);
            double nodeUpperbound = upperBounds.get(myNodeIndex);

            if (nodeLowerbound >= beta)
                return nodeLowerbound;
            if (nodeUpperbound <= alpha)
                return nodeUpperbound;
            alpha = Double.max(alpha, nodeLowerbound);
            beta = Double.min(beta, nodeUpperbound);
        }

        Constants.MOVE[] legalMoves = game.getPossibleMoves(myNodeIndex);

        double value;

        if (depth == 0){
            value = evaluateState(game.copy());
            if(value <= alpha)
                upperBounds.put(myNodeIndex, value);
            if((value > alpha) && (value < beta)){
                upperBounds.put(myNodeIndex, value);
                lowerBounds.put(myNodeIndex, value);
            }
            if(value >= beta)
                lowerBounds.put(myNodeIndex, value);
            return value;
        }

        if (maximizingPlayer) {
            value = -Double.MAX_VALUE;
            double oldAlpha = alpha;

            for (int i = 0; i < legalMoves.length; i++) {

                Game nextGame = gameForAdvance(game);

                advanceGame(myNodeIndex, new POCommGhosts(50), nextGame, legalMoves[i]);

                value = Double.max(value, AlphaBetaWithMemory(nextGame.copy(),depth - 1, alpha, beta,false));
                oldAlpha = Double.max(oldAlpha, value);

                if (value >= beta) {
                    break;
                }
            }
        }else{
            value = Double.MAX_VALUE;
            double oldBeta = beta;

            for (int i = 0; i < legalMoves.length; i++) {

                Game nextGame = gameForAdvance(game);

                advanceGame(myNodeIndex, new POCommGhosts(50), nextGame, legalMoves[i]);

                value = Double.min(value, AlphaBetaWithMemory(nextGame.copy(),depth - 1, alpha, beta, true));
                oldBeta = Double.min(oldBeta, value);

                if (value <= alpha) {
                    break;
                }
            }
        }

        if(value <= alpha)
            upperBounds.put(myNodeIndex, value);
        if((value > alpha) && (value < beta)){
            upperBounds.put(myNodeIndex, value);
            lowerBounds.put(myNodeIndex, value);
        }
        if(value >= beta)
            lowerBounds.put(myNodeIndex, value);
        return value;
    }

    private double evaluateState(Game game){
        return game.getScore() + game.getPacmanNumberOfLivesRemaining()*1000;
    }

    private Game gameForAdvance(Game game){
        GameInfo info = game.getPopulatedGameInfo();
        info.fixGhosts((ghost -> new Ghost(ghost, game.getCurrentMaze().lairNodeIndex, -1, -1, Constants.MOVE.NEUTRAL)));
        return game.getGameFromInfo(info).copy();
    }

    private void advanceGame(int oldGameIndex, MASController ghosts, Game game, Constants.MOVE move){
        while(!game.isJunction(game.getPacmanCurrentNodeIndex()) || (oldGameIndex == game.getPacmanCurrentNodeIndex())) {
            game.advanceGame(move,  ghosts.getMove(game.copy(), 40));
        }
    }
}
