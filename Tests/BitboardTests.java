package Tests; /**
 * Created by Rob on 11/4/2016.
 */
import Environment.BitBoard;
import Search.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class BitboardTests {



    //DECIMAL = 10133253816329028
    //HEX = 24002402242344
    public static int[][] board = {
            {6, 4, 3, 8},        //  0 | 0 | 4  | 16
            {5, 3, 2, 1},        //  0 | 0 | 4  | 16
            {5, 1, 2, 1},        //  0 | 4 | 4  | 16
            {6, 2, 2, 2}         //  0 | 8 | 16 | 16
    };



    public static void main(String[] args){
        testGame();
    }

    public static void testPrint(long board){
        BitBoard.printBoard(board);
    }


    public static void test_ABPruning(){
        long x = BitBoard.addTile(0x0L);
        x = BitBoard.addTile(x);
        ABPruning search = new ABPruning();
        while(!BitBoard.isTerminal(x)){
            x = search.decideAction(x);
            x = BitBoard.addTile(x);
            BitBoard.printBoard(x);
        }
    }


    public static void testCprob(){
         int[][] bd = {
                {7, 6, 5, 2},        //  0 | 0 | 4  | 16
                {5, 4, 2, 1},        //  0 | 0 | 4  | 16
                {2, 2, 0, 0},        //  0 | 4 | 4  | 16
                {0, 0, 0, 0}         //  0 | 8 | 16 | 16
        };
        long p = BitBoard.powersToBitboard(bd);
        float[] probs = {0.1f, 0.01f, 0.001f, 0.00001f, 0.000001f };
        ExpectiMax se = new ExpectiMax();
        for(float prob : probs){
            se.setFilterProbability(prob);
            se.decideAction(p);
        }



    }

    public static void testGame(){
        ExpectiMax s = new ExpectiMax();
        s.setDepth(6);
        Random rng = new Random();
        long x = 0L;
        x = BitBoard.addTile(x);
        x = BitBoard.addTile(x);
        //BitBoard.printBoard(x);
        int move = 0;
        HashMap<Integer, Integer> scores = new HashMap<>();
        scores.put(512, 0);
        scores.put(1024, 0);
        scores.put(2048, 0);
        scores.put(4096, 0);
        scores.put(8192, 0);
        scores.put(16384, 0);
        scores.put(32768, 0);


        //             2147483647
        int numGames = 10;                ;
        for(int i = 0; i < numGames; i++) {
            boolean print = false;
            while (!BitBoard.isTerminal(x)) {
                x = s.decideAction(x);
                x = BitBoard.addTile(x);
            }
            int score = BitBoard.getLargestTile(x);
            System.out.println("Score: " + score );
            System.out.println("Iterations Remaining: " + (numGames - i));
            if(score >= 512){
                int t = scores.get(512);
                scores.replace(512, t, t+1);
                if(score >= 1024){
                    t = scores.get(1024);
                    scores.replace(1024, t, t+1);
                    if(score >= 2048){
                        t = scores.get(2048);
                        scores.replace(2048, t, t+1);
                        if(score >= 4096){
                            t = scores.get(4096);
                            scores.replace(4096, t, t+1);
                            if(score >= 8192){
                                t = scores.get(8192);
                                scores.replace(8192, t, t+1);
                                if(score >= 16384){
                                    t = scores.get(16384);
                                    scores.replace(16384, t, t+1);
                                    if(score >= 32768){
                                        t = scores.get(32768);
                                        scores.replace(32768, t, t+1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            String header =  "Iterations: " + i + "\nScore | num times | percentage";
            String msg = "";
            int[] keys = {512, 1024, 2048, 4096, 8192, 16384, 32768};


           // for(int key : keys){

            //    msg += key;
            //    for(int K = Integer.toString(key).length(); K < 6; K++){
            //       msg += " ";
           //     }
           //     msg += "| ";
          //      int times = scores.get(key);
           //     msg += times;
           //     for(int K = Integer.toString(times).length(); K < 9; K++){
           //         msg += " ";
          //      }
           //     msg += "| " + (((double)times/((double)i + 1)) * 100) + "%\n";
          //  }
            //System.out.println(header);
            //System.out.println(msg);
            x = 0L;
            x = BitBoard.addTile(x);
            x = BitBoard.addTile(x);
            s = new ExpectiMax();
            s.setDepth(6);
        }
        String header =  "Iterations: " + numGames + "\nScore | num times | percentage";
        String msg = "";
        int[] keys = {512, 1024, 2048, 4096, 8192, 16384, 32768};


        for(int key : keys){

            msg += key;
            for(int K = Integer.toString(key).length(); K < 6; K++){
                msg += " ";
            }
            msg += "| ";
            int times = scores.get(key);
            msg += times;
            for(int K = Integer.toString(times).length(); K < 9; K++){
                msg += " ";
            }
            msg += "| " + (((double)times/((double)numGames)) * 100) + "%\n";
        }
        System.out.println(header);
        System.out.println(msg);
    }

    public static void testSearch(){
        long x = BitBoard.powersToBitboard(board);
        //BitBoard.printBoard(x);
        long move = 0;
        ExpectiMax s = new ExpectiMax();
        while(!BitBoard.isTerminal(x)) {
            System.out.println("Moving");
            BitBoard.printBoard(x);
            x = s.decideAction(x);
            x = BitBoard.addTile(x);
        }
        BitBoard.printBoard(x);
        System.out.println("Game over");

    }
}
