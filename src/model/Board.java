package model;

import model.constants.Color;
import model.constants.Piece;
import model.constants.Spot;
import model.piece.*;

import static model.constants.Spot.*;
import static model.constants.BitBoards.*;
import static model.constants.Color.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/23/14.
 */
public class Board {

    private long[] position = POSITION;
    private ChessPiece[] board = new ChessPiece[64];
    private Color turn = WHITE;

    public Board()
    {
        createNewBoard();
    }

    public boolean move(Spot origin, Spot target)
    {
        //origin has to be a piece of the same color as current turn
        if ((PIECE[origin.ordinal()] & getPiece(ALL, turn)) == 0)
            return false;

        //target cannot be a piece of the same color
        if ((PIECE[target.ordinal()] & getPiece(ALL, turn)) != 0)
            return false;

        //piece can actually move from origin to target.
        if ((board[origin.ordinal()].getValidMoves(this, origin) & PIECE[target.ordinal()]) == 0)
            return false;

        //piece is allowed to move... update bitboards and board
        int positionIndex = board[origin.ordinal()].getBitBoardIndex();
        position[positionIndex] = (~PIECE[origin.ordinal()] & position[positionIndex]) ^ PIECE[target.ordinal()];

        positionIndex = turn.equals(WHITE) ? ALL_WHITES : ALL_BLACKS;
        position[positionIndex] = (~PIECE[origin.ordinal()] & position[positionIndex]) ^ PIECE[target.ordinal()];
        position[ALL_PIECES] = position[ALL_WHITES] | position[ALL_BLACKS];
        board[target.ordinal()] = board[origin.ordinal()];
        board[origin.ordinal()] = null;

        //change turns
        turn = turn.equals(WHITE) ? BLACK : WHITE;

        return true;
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

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(8 + "\t");

        int idx = 0;
        for (int rank = 7; rank >= 0; rank--)
        {
            for (int file = 0; file < 8; file++)
            {
                idx = rank*8+file;
                buffer.append(
                        board[idx] == null
                                ? (
                                (idx / 8) % 2 == 0
                                        ? (idx % 2 == 0 ? "x" : ".")
                                        : (idx % 2 == 0 ? "." : "x")
                        ) : board[idx]).append("\t");
            }
            buffer.append("\n" + ((idx / 8) != 0 ? (idx / 8) + "\t" : "\t"));
        }

        for (final char file : "abcdefgh".toCharArray()) {
            buffer.append(file + "\t");
        }

        return buffer.toString();
    }

    private void createNewBoard()
    {
        board[8] = board[9] = board[10] = board[11]
                = board[12] = board[13] = board[14] = board[15] = new Pawn(WHITE);
        board[0] = board[7] = new Rook(WHITE);
        board[1] = board[6] = new Knight(WHITE);
        board[2] = board[5] = new Bishop(WHITE);
        board[3] = new Queen(WHITE);
        board[4] = new King(WHITE);

        board[48] = board[49] = board[50] = board[51]
                = board[52] = board[53] = board[54] = board[55] = new Pawn(BLACK);
        board[56] = board[63] = new Rook(BLACK);
        board[57] = board[62] = new Knight(BLACK);
        board[58] = board[61] = new Bishop(BLACK);
        board[59] = new Queen(BLACK);
        board[60] = new King(BLACK);


    }

}
