package model;

import model.constants.Color;
import model.constants.Piece;
import model.piece.Queen;

import static model.constants.Spot.*;
import static model.constants.BitBoards.*;
import static model.constants.Color.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/23/14.
 */
public class Board {

    private long[] position = POSITION;


    public long test()
    {
        Queen queen = new Queen();
        return queen.getValidMoves(this, WHITE, D1);

    }

    public long getPiece(Piece piece, Color color)
    {
        return piece.equals(ALL)
                ? position[ 12 + color.ordinal() ]
                : position[ color.ordinal() * 6 + piece.ordinal() ];
    }

    public static void printBitBoard(long bitboard)
    {
        String padding = "0000000000000000000000000000000000000000000000000000000000000000";
        String paddedBitBoard = padding + Long.toBinaryString(bitboard);
        paddedBitBoard = paddedBitBoard.substring(paddedBitBoard.length() - 64, paddedBitBoard.length());  // take the right

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 8; i++)
        {
            for (int j = 7; j >= 0; j--)
            {
                buffer.append(paddedBitBoard.charAt(i*8+j)).append("\t");
            }
            buffer.append("\n");
        }

        System.out.println(buffer);
    }

}
