package Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rob on 11/4/2016.
 */
public class TableGenerator {
    /**
     * This class generate all possible moves
     * and their results
     * we will usa a hashmap where
     * Key = state of a line
     * value = result of a merge down the line
     *
     * i.e. {0, 1, 1, 2} -> (0, 0, 2, 2)
     *
     */

    public static long[] leftMoves = new long[65535];
    public static long[] rightMoves = new long[65535];
    public static HashMap<Long, Long> upMoves = new HashMap<>();
    public static HashMap<Long, Long> downMoves = new HashMap<>();

    public static HashMap<Long, List<Long>> addTwoTiles = new HashMap<>();

    public static long[][] masks = {
            {0xF000000000000000L, 0x0F00000000000000L, 0x00F0000000000000L, 0x000F000000000000L},
            {0x0000F00000000000L, 0x00000F0000000000L, 0x000000F000000000L, 0x0000000F00000000L},
            {0x00000000F0000000L, 0x000000000F000000L, 0x0000000000F00000L, 0x00000000000F0000L},
            {0x000000000000F000L, 0x0000000000000F00L, 0x00000000000000F0L, 0x000000000000000FL}
    };

    public static long[][] shifts = {
            {60, 56, 52, 48},
            {44, 40, 36, 32},
            {28, 24, 20, 16},
            {12,  8,  4,  0}
    };

    public static long[] rowCombinations = {
            0x0000000000000000L,
            0x0000000000000001L,
            0x0000000000000010L,
            0x0000000000000100L,
            0x0000000000001000L,
            0x0000000000000011L,
            0x0000000000000101L,
            0x0000000000001001L,
            0x0000000000000110L,
            0x0000000000001010L,
            0x0000000000001100L,
            0x0000000000000111L,
            0x0000000000001110L,
            0x0000000000001101L,
            0x0000000000001011L,
            0x0000000000001111L

    };


    static {
        enumerateMoves();
    }

    public static void enumerateMoves(){

        for(long i = 0; i < 65535; i++){
            long row = i;
            long result = mergeRow((i & 0x000000000000F000L), (i & 0x0000000000000F00L), (i & 0x00000000000000F0L), (i & 0x000000000000000FL));
            rightMoves[(int)row] = result;
            leftMoves[(int)reverse(row)] = reverse(result);
            upMoves.put(rotateUp(row), rotateUp(result));
            downMoves.put(rotateDown(row), rotateDown(result));
        }
    }


    public static long rotateUp(long row){
        long result =     (row & 0x000000000000000FL) << 48;
        result = result | (row & 0x00000000000000F0L) << 28;
        result = result | (row & 0x0000000000000F00L) << 8;
        result = result | (row & 0x000000000000F000L) >>> 12;
        return result;
    }
    public static long rotateDown(long row){
        long result =     (row & 0x000000000000000FL);
        result = result | (row & 0x00000000000000F0L) << 12;
        result = result | (row & 0x0000000000000F00L) << 24;
        result = result | (row & 0x000000000000F000L) << 36;
        return result;
    }
    public static long reverse(long row){
        long result = (row & 0x000000000000000FL) << 12;
        result = result | ((row & 0x00000000000000F0L) << 4);
        result = result | ((row & 0x0000000000000F00L) >>> 4);
        result = result | ((row & 0x000000000000F000L) >>> 12);
        return result;
    }


    public static long mergeRow(long p4, long p3, long p2, long p1){

        //first we condense the line, putting all zeroes to the left and values to the right
        for(int i = 0; i < 4; i++) {
            if (p1 == 0) {
                p1 = p2 >>> 4;
                p2 = p3 >>> 4;
                p3 = p4 >>> 4;
                p4 = 0;
            }
            if (p2 == 0) {
                p2 = p3 >>> 4;
                p3 = p4 >>> 4;
                p4 = 0;
            }
            if (p3 == 0) {
                p3 = p4 >>> 4;
                p4 = 0;
            }

        }
        //mrege first tiles
        if((p1 == (p2 >>> 4)) && p1 != 0){
            p1 = p1 + 1;
            p2 = p3 >>> 4;
            p3 = p4 >>> 4;
            p4 = 0;
        }

        //second and third
        if((p2 == (p3 >>> 4) && p2 != 0)){
            p2 = ((p2 >>> 4) + 1) << 4;
            p3 = p4 >>> 4;
            p4 = 0;
        }

        //third and 4th
        if((p3 == (p4 >>> 4) && p3 != 0)){
            p3 = ((p3 >>> 8) + 1) << 8;
            p4 = 0;
        }

        return p4 | p3 | p2 | p1;
    }

    public static double[] heuristicsTable(){
        double[] heuristics = new double[65536];

        for(int row = 0; row < 65535; row++){
            int[] tiles = {
                    (row >>> 0) & (0xF),
                    (row >>> 4) & 0xF,
                    (row >>> 8) & 0xF,
                    (row >>> 12) & 0xF
            };
            double score = 0.0;
            int prev = 0;
            int max = 0;
            int maxIndex = 0;
            for(int i = 0; i < 4; i++){
                if(tiles[i] == 0){
                    score += 2000.0;
                }else if(tiles[i] == prev){
                    score += 3000.0;
                }
                if(tiles[i] > max){
                    max = tiles[i];
                    maxIndex = i;
                }
                prev = tiles[i];
            }
            if(maxIndex == 0 || maxIndex == 3){
                score += 50000;
            }else{
                score -= 10000;
            }

            heuristics[row] = score;
        }

        return heuristics;
    }



}
