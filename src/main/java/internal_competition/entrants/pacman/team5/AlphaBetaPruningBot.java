package internal_competition.entrants.pacman.team5;

import java.util.Arrays;

import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;

import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.Constants.MOVE;

public class AlphaBetaPruningBot extends PacmanController {

    private int maxDepth = 4;

    public MOVE getMove(Game game, long timeDue) {
        int myNodeIndex = game.getPacmanCurrentNodeIndex();
        MOVE[] legalMoves = game.getPossibleMoves(myNodeIndex);

        double maxValue = -Double.MAX_VALUE;
        MOVE maxMove = MOVE.NEUTRAL;

        if (game.isJunction(myNodeIndex)) {
            for (int i = 0; i < legalMoves.length; i++) {

                Game nextGame = gameForAdvance(game);

                advanceGame(myNodeIndex, new POCommGhosts(50), nextGame, legalMoves[i]);

                double tmpValue = AlphaBetaPruning(nextGame.copy(), this.maxDepth, -Double.MAX_VALUE, Double.MAX_VALUE, true);

                if (tmpValue > maxValue) {
                    maxValue = tmpValue;
                    maxMove = legalMoves[i];
                }
            }
            return maxMove;
        }else{
            MOVE lastMove = game.getPacmanLastMoveMade();
            if (Arrays.asList(legalMoves).contains(lastMove)){
                return lastMove;
            }

            for (MOVE move : legalMoves){
                if (move != lastMove.opposite()){
                    return move;
                }
            }
        }
        return MOVE.NEUTRAL;
    }

    private double AlphaBetaPruning(Game game, int depth, double alpha, double beta, boolean maximizingPlayer){
        if (depth == 0){
            return evaluateState(game.copy());
        }

        int myNodeIndex = game.getPacmanCurrentNodeIndex();
        MOVE[] legalMoves = game.getPossibleMoves(myNodeIndex);

        if (maximizingPlayer) {
            for (int i = 0; i < legalMoves.length; i++) {

                Game nextGame = gameForAdvance(game);

                advanceGame(myNodeIndex, new POCommGhosts(50), nextGame, legalMoves[i]);

                alpha = Double.max(alpha, AlphaBetaPruning(nextGame.copy(),depth - 1, alpha, beta,false));
                if (beta <= alpha) {
                    break;
                }
            }
            return alpha;
        }else{
            for (int i = 0; i < legalMoves.length; i++) {

                Game nextGame = gameForAdvance(game);

                advanceGame(myNodeIndex, new POCommGhosts(50), nextGame, legalMoves[i]);

                beta = Double.min(beta, AlphaBetaPruning(nextGame.copy(),depth - 1, alpha, beta, true));
                if (beta <= alpha) {
                    break;
                }
            }
            return beta;
        }
    }


    private double evaluateState(Game game){
        return game.getScore() + game.getPacmanNumberOfLivesRemaining()*100;
    }

    private Game gameForAdvance(Game game){
        GameInfo info = game.getPopulatedGameInfo();
        info.fixGhosts((ghost -> new Ghost(ghost, game.getCurrentMaze().lairNodeIndex, -1, -1, MOVE.NEUTRAL)));
        return game.getGameFromInfo(info).copy();
    }

    private void advanceGame(int oldGameIndex, MASController ghosts, Game game, MOVE move){
        while(!game.isJunction(game.getPacmanCurrentNodeIndex()) || (oldGameIndex == game.getPacmanCurrentNodeIndex())) {
            game.advanceGame(move,  ghosts.getMove(game.copy(), 40));
        }
    }
}