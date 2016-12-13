package Search;

import Environment.BitBoard;

import java.util.List;

/**
 * Created by Rob on 12/12/2016.
 */
public class ABPruning {

    private int MAX_DEPTH = 8;
    private long TILE_TWO = 0x1L;

    public long decideAction(long board){
        long result = 0;
        double maxValue = -1.0D / 0.0;

        List<Long> actions = MoveGenerator.generateMoves(board);

        for(long action : actions){
            double resultValue = this.minValue(action, -1.0D / 0.0, 1.0D / 0.0, 0);
            if(resultValue > maxValue){
                System.out.println("yes");
                result = action;
                maxValue = resultValue;
            }
        }
        return result;
    }

    public double maxValue(long boardState, double alpha, double beta, int depth){
        if(BitBoard.isTerminal(boardState)){
            return 0.0;
        }
        if(depth == MAX_DEPTH){
            return Evaluator.evaluateBoard(boardState);
        }

        double value = -1.0D / 0.0;

        for(long action : MoveGenerator.generateMoves(boardState)){
            alpha = Math.max(alpha, value);
            value = Math.max(value, this.minValue(action, alpha, beta, depth + 1));
            if(value >= beta){
                return value;
            }

        }

        return value;

    }


    public double minValue(long boardState, double alpha, double beta, int depth){
        if(BitBoard.isTerminal(boardState)){
            return 0.0;
        }
        double value = 1.0D / 0.0;

        if(depth == MAX_DEPTH){
            return Evaluator.evaluateBoard(boardState);
        }
        long mask = 0x000000000000000FL;
        for( int i = 0; i < 16; i++ ) {
            if( ( mask & boardState ) == 0 ) {
                value = Math.min(value, maxValue( (boardState | (   TILE_TWO        << ( i * 4 ) ) ), alpha, beta, depth + 1) );
                value = Math.min(value, maxValue( (boardState | ( ( TILE_TWO << 1 ) << ( i * 4 ) ) ), alpha, beta, depth + 1) );
            }
            if(value <= alpha){
                return value;
            }
            mask = mask << 4;
        }


        for(long action : MoveGenerator.generateMoves(boardState)){
            beta = Math.min(beta, value);
            value = Math.min(value, this.maxValue(action, alpha, beta, depth + 1));
            if(value <= alpha){
                return value;
            }
        }

        return value;

    }

}
