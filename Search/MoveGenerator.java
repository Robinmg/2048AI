package Search;

import Environment.*;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Rob on 11/6/2016.
 */
public class MoveGenerator {


    public static List<Long> generateMoves(long board) {
        List<Long> moves = new ArrayList<>();
        long left = BitBoard.moveLeft(board);
        long right = BitBoard.moveRight(board);
        long up = BitBoard.moveUp(board);
        long down = BitBoard.moveDown(board);
        if(left != board){
            moves.add(left);
        }
        if(right != board){
            moves.add(right);
        }
        if(up != board){
            moves.add(up);
        }
        if(down != board){
            moves.add(down);
        }
        return moves;
    }
}
