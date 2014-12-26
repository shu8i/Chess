import model.BitBoardFactory;
import model.Board;
import model.constants.BitBoards;

/**
 * Created by Shahab Shekari on 12/23/14.
 */
public class Chess {

    public static void main(String[] args) {
        BitBoardFactory.prepareDatabase();
        Board board = new Board();
        long test = board.test();
        Board.printBitBoard(test);
//        for (final long bitboard : BitBoards.POSITION)
//        {
//            Board.printBitBoard(bitboard);
//        }
    }

}
