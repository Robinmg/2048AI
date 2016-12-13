package Environment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BitBoard {

    /**
     * This class represents a compact 2048 board, including methods for efficient computations across the board.
     * Our 2048 bitboard will be represented by a 64bit long.
     *
     * Each tile of the 2048 board will be represented by 4 bits in our bitboard ( 4 bits per tile * 16 tiles = 64bits )
     * For example below is a standard 2048 board, containing powers of 2's as tiles:
     *
     * { 0, 2, 4, 8  }
     * { 0, 2, 8, 16 }
     * { 0, 4, 8, 16 }
     * { 0, 8, 8, 32 }
     *
     * Inorder to convert this into a bitboard we first reduce each tile to its power of 2 (2^0 = 1 will represent empty tiles)
     * resulting in:
     * {0, 1, 2, 3}
     * {0, 1, 3, 4}
     * (0, 2, 3, 4)
     * (0, 3, 3, 5)
     *
     * Now we can convert it into 4 bit binary. NOTE: This means that a limitation of this approach
     * is that each tile may only have a maximum value of (2 ^ 15) = 32768
     *
     * { 0000, 0001, 0010, 0011 }
     * { 0000, 0001, 0011, 0100 }
     * { 0000, 0010, 0011, 0100 }
     * { 0000, 0011, 0011, 0101 }
     *
     * Next pretend the above is a 4x4 2-d array (so board[0][0] is the top left square)
     * The highest 4 bits will be board[0][0], then board[0][1] , then board[0][2] (decreasing row-wise)
     *
     * Therefore our 64 bit long will be the following (with a space inbetween each 4 bits)
     *
     * 0000 0001 0010 0011 0000 0001 0011 0100 0000 0010 0011 0100 0000 0011 0011 0101
     *
     * for ease of readability we will use hexadecimal where each 4 bits is a single hex digit
     * therefore our board becomes:
     *
     * 0x0123013402340335L
     *
     **/




    /**
     *
     * Below are the appropriate masks to extract a single tile from the board.
     * the mask[n][m] corresponds to tile [n][m] if we visualize our board as a 4x4 2d array
     *
     * For example the bitwise AND of a board state and masks[0][0] will leave only the leftmost 4 bits,
     * which must then be shifted right 60 bits to extract the actual value.
     *
     * board & masks[0][0] = 0xN000000000000000L
     * ( board & masks[0][0] ) >>> 60 = N
     *
     **/
    public static long[][] masks = {
            {0xF000000000000000L, 0x0F00000000000000L, 0x00F0000000000000L, 0x000F000000000000L},
            {0x0000F00000000000L, 0x00000F0000000000L, 0x000000F000000000L, 0x0000000F00000000L},
            {0x00000000F0000000L, 0x000000000F000000L, 0x0000000000F00000L, 0x00000000000F0000L},
            {0x000000000000F000L, 0x0000000000000F00L, 0x00000000000000F0L, 0x000000000000000FL}
    };


    /**
     *
     * The row masks are the same as above, however they extract an entire row
     * instead of a single value
     *
     */

    public static long[] rowMasks = {
            0xFFFF000000000000L,
            0x0000FFFF00000000L,
            0x00000000FFFF0000L,
            0x000000000000FFFFL
    };

    /**
     * The shifts represent how many bits you must right shift a board
     * to extract the value at [row][col]
     *
     * for example:
     *
     *  ( board & masks[0][0] ) >>> shifts[0][0] will give you the value in the leftmost 4 bits of the board
     *
     * These also represent how many bits to left shift a 4bit number
     * from the rightmost 4 bits to place it at [row][col]
     */
    public static long[][] shifts = {
                    {60, 56, 52, 48},
                    {44, 40, 36, 32},
                    {28, 24, 20, 16},
                    {12,  8,  4,  0}
    };


    /**
     * These are our precomputed move tables for left and right merges
     * The index of the table is the row you want to merge, and the value at
     * that index is the resultant row.
     *
     * for example to merge row 0 from board = 0x1122000000000000L
     * we first extract the row
     *
     * row = board & rowmasks[0] >>> shifts[0][3]
     * newRow = leftMoves[row] = 0x0023L
     *
     * Then we just shift the row back into its original position
     *
     * We have a table with left and right moves to reduce the number of board rotations
     * (ex. to move UP we rotate the board clockwise then do a right merge then re-rotate
     *      back into the original position)
     *
     */
    public static long[] leftMoves = TableGenerator.leftMoves;
    public static long[] rightMoves = TableGenerator.rightMoves;


    /**
     * Used for adding tiles
     */
    public static Random rng = new Random();


    /**
     * Performs a left merge on a 2048 bitboard.
     *
     * @param board The bitboard state
     * @return the resultant board after a left merge
     */
    public static long moveLeft(long board){
        long r1 = leftMoves[(int)((board & rowMasks[0]) >>> 48)] << 48;
        long r2 = leftMoves[(int)((board & rowMasks[1]) >>> 32)] << 32;
        long r3 = leftMoves[(int)((board & rowMasks[2]) >>> 16)] << 16;
        long r4 = leftMoves[(int) (board & rowMasks[3])];

        return r1 | r2 | r3 | r4;
    }

    /**
     * Performs a right merge on a 2048 bitboard
     *
     * @param board The bitboard state
     * @return the resultant board after a right merge
     */
    public static long moveRight(long board){
        long row1 = rightMoves[(int)( ( board & rowMasks[0] ) >>> 48 )] << 48;
        long row2 = rightMoves[(int)( ( board & rowMasks[1] ) >>> 32 )] << 32;
        long row3 = rightMoves[(int)( ( board & rowMasks[2] ) >>> 16 )] << 16;
        long row4 = rightMoves[(int)  ( board & rowMasks[3] )];

        return row1 | row2 | row3 | row4;
    }

    /**
     * Performs an upward merge on a 2048 bitboard.
     * We achieve this by:
     * 1.) Rotate the board clockwise
     * 2.) Perform a right merge
     * 3.) Rotate the board counter clockwise into its original orientation
     *
     * @param board target bitboard
     * @return the resultant board after an upwards mergs
     */
    public static long moveUp( long board ) {
        return rotateCounterClockwise( moveRight( rotateClockwise( board ) ) );
    }

    /**
     * Performs an downward merge on a 2048 bitboard.
     * We achieve this by:
     * 1.) Rotate the board counter clockwise
     * 2.) Perform a right merge
     * 3.) Rotate the board clockwise into its original orientation
     *
     * @param board target bitboard
     * @return the resultant board after a downwards merge
     */
    public static long moveDown( long board ) {
        return rotateClockwise( moveRight( rotateCounterClockwise( board ) ) );
    }

    public static long rotateClockwise( long board ){
        long result = 0L;
        result = result | ( ( board & masks[0][0] ) >>> 12 ) | ( ( board & masks[0][1] ) >>> 24 ) | ( ( board & masks[0][2] ) >>> 36)  | ( ( board & masks[0][3] ) >>> 48 );
        result = result | ( ( board & masks[1][0] ) <<   8 ) | ( ( board & masks[1][1] ) >>>  4 ) | ( ( board & masks[1][2] ) >>> 16)  | ( ( board & masks[1][3] ) >>> 28 );
        result = result | ( ( board & masks[2][0] ) <<  28 ) | ( ( board & masks[2][1] ) <<  16 ) | ( ( board & masks[2][2] ) <<   4 ) | ( ( board & masks[2][3] ) >>>  8 );
        result = result | ( ( board & masks[3][0] ) <<  48 ) | ( ( board & masks[3][1] ) <<  36 ) | ( ( board & masks[3][2] ) <<  24 ) | ( ( board & masks[3][3] ) <<  12 );
        return result;
    }
    public static long rotateCounterClockwise( long board ){
        long result = 0L;
        result = result | ( ( board & masks[0][3] ) << 12  ) | ( ( board & masks[1][3] ) << 24  ) | ( ( board & masks[2][3] ) << 36  ) | ( ( board & masks[3][3] ) << 48  );
        result = result | ( ( board & masks[0][2] ) >>> 8  ) | ( ( board & masks[1][2] ) << 4   ) | ( ( board & masks[2][2] ) << 16  ) | ( ( board & masks[3][2] ) << 28  );
        result = result | ( ( board & masks[0][1] ) >>> 28 ) | ( ( board & masks[1][1] ) >>> 16 ) | ( ( board & masks[2][1] ) >>> 4  ) | ( ( board & masks[3][1] ) << 8   );
        result = result | ( ( board & masks[0][0] ) >>> 48 ) | ( ( board & masks[1][0] ) >>> 36 ) | ( ( board & masks[2][0] ) >>> 24 ) | ( ( board & masks[3][0] ) >>> 12 );
        return result;
    }

    public static boolean isFull( long board ){
        board |= ( board >> 2 ) & 0x3333333333333333L;
        board |= ( board >> 1 );
        board = ~board & 0x1111111111111111L;
        if( board == 0 ){
            return true;
        }else{
            return false;
        }
    }

    public static int emptySpaces( long board ){
        int emptySpaces = 0;
        for( int row = 0; row < 4; row++ ){
            for( int col = 0; col < 4; col++ ){
                if( ( board & masks[row][col] ) == 0){
                    emptySpaces++;
                }
            }
        }
        return emptySpaces;
    }

    public static long addTile(long board){
        long tile = 1L;
        long ranVal = rng.nextInt(10) + 1;
        if(ranVal == 10){
            tile = 2L;
        }
        long shiftRow = 0L;
        long shiftCol = 0L;
        int emptySpaces = 0;
        for(int row = 0; row < 4; row++){
            for(int col = 0; col < 4; col++){
                if((board & masks[row][col]) == 0){
                    shiftRow = (shiftRow << 4) | row;
                    shiftCol = (shiftCol << 4) | col;
                    emptySpaces++;
                }
            }
        }
        if(emptySpaces == 0){
            return board;
        }
        int choice = rng.nextInt(emptySpaces);
        return (board | (tile << shifts[(int)((shiftRow >>> (choice * 4)) & 0x000000000000000FL)][(int)((shiftCol >>> (choice * 4)) & 0x000000000000000FL)]));
    }


    public static boolean isTerminal(long board){

        if(!isFull(board)){
            return false;
        }

        if(board != moveLeft(board)){
            return false;
        } else if(board != moveUp(board)){
            return false;
        }else if(board != moveDown(board)){
            return false;
        }else if(board != moveRight( board)){
            return false;
        }else{
            return true;
        }
    }


    /**
     *
     * Utility Methods
     *
     */


    public static void printBoard(long board) {
        String result = "";
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                String tile = Integer.toString(convertToNumber(board, row, col));
                for (int i = 0; i < 6 - tile.length(); i++) {
                    result += " ";
                }
                result += tile + "|";
            }
            result += "\n";
        }
        System.out.println(result);
    }
    public static int convertToNumber(long board, int row, int col){
        return (int)Math.pow(2,((board & masks[row][col]) >>> shifts[row][col]));
    }

    public static int getLargestTile(long board){
        int max = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int temp = convertToNumber(board, row, col);
                if(temp > max){
                    max = temp;
                }
            }
        }
        return max;
    }
    public static int[][] getBoardAsIs(long board){
        int[][] b = new int[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int tile = convertToNumber(board, row, col);
                if(tile == 1){
                    tile = 0;
                }
                b[row][col] = tile;
            }
        }
        return b;
    }

    /*
 * @board 2-D int array containing the powers of 2 at each 2048 square (0 = empty square)
 * @return bitboard representation
 *
 * we convert the 4 bit value at each square to its long value by:
 * shifting the 4 bits to the left into their appropriate position
 */
    public static long powersToBitboard(int[][] board){
        long result = 0L;

        for(int row = 0; row < 4; row++){                  //Iterate all rows and columns
            for(int col = 0; col < 4; col++){
                long tileValue = board[row][col];          //get the 4 bit power at each square
                tileValue = tileValue << shifts[row][col];
                result += tileValue;
            }
        }
        return result;
    }
}