package Search;

import Environment.BitBoard;
import Environment.TableGenerator;

/**
 * Created by Rob on 11/6/2016.
 */
public class Evaluator {

    public static long[] rowMasks = BitBoard.rowMasks;
    public static long[][] shifts = BitBoard.shifts;

    //public static double[] scores = TableGenerator.genHeuristics();
    public static double[] scores = TableGenerator.heuristicsTable();
    public static double evaluateBoard( long board ) {
        return eval( board ) + eval( transpose( board ) );
    }

    public static double eval( long board ){
        return  ( scores[(int)( ( board & rowMasks[0] ) >>> shifts[0][3] ) ] ) +
                ( scores[(int)( ( board & rowMasks[1] ) >>> shifts[1][3] ) ] ) +
                ( scores[(int)( ( board & rowMasks[2] ) >>> shifts[2][3] ) ] ) +
                ( scores[(int)( ( board & rowMasks[3] ) >>> shifts[3][3] ) ] );
    }


    public static long transpose( long board ){
        long result = board & 0xF0000F0000F0000FL;
        result = result | ((board & 0X0F0000F0000F0000L) >>> 12);
        result = result | ((board & 0x00F0000F00000000L) >>> 24);
        result = result | ((board & 0x000F000000000000L) >>> 36);

        result = result | ((board & 0x0000F0000F0000F0L) << 12);
        result = result | ((board & 0x00000000F0000F00L) << 24);
        result = result | ((board & 0x000000000000F000L) << 36);

        return result;
    }

    /**
     *         long result = board & 0xF0000F0000F0000FL;
     result = result | ((board & 0X0F00000000000000L) >>> 12);
     result = result | ((board & 0x00F0000000000000L) >>> 24);
     result = result | ((board & 0x000F000000000000L) >>> 36);

     result = result | ((board & 0xF00000000000L) << 12);
     result = result | ((board & 0x00F000000000L) >>> 12);
     result = result | ((board & 0x000F00000000L) >>> 24);

     result = result | ((board & 0xF0000000L) << 24);
     result = result | ((board & 0x0F000000L) << 12);
     result = result | ((board & 0x000F0000L) >>> 12);

     result = result | ((board & 0xF000L) << 36);
     result = result | ((board & 0x0F00L) << 24);
     result = result | ((board & 0x00F0L) << 12);
     */

}
