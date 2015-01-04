package model.piece;

import model.Board;
import model.constants.Color;
import model.constants.Spot;

import static model.constants.BitBoards.*;
import static model.constants.Color.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/25/14.
 */
public class Queen extends ChessPiece {

    public Queen(Color color)
    {
        super(color);
    }

    public long getValidMoves(long[] positions, Spot spot)
    {
        long rookBlockers = getPiece(positions, ALL, BOTH) & ROOK_OCC_MASK[spot.ordinal()],
                bishopBlockers = getPiece(positions, ALL, BOTH) & BISHOP_OCC_MASK[spot.ordinal()];
        int rookDBIndex = (int)((rookBlockers * MAGIC_ROOK_NUMBER[spot.ordinal()]) >>> MAGIC_ROOK_SHIFT[spot.ordinal()]),
                bishopDBIndex = (int)((bishopBlockers * MAGIC_BISHOP_NUMBER[spot.ordinal()]) >>> MAGIC_BISHOP_SHIFT[spot.ordinal()]);
        return (MAGIC_ROOK_MOVE[spot.ordinal()][rookDBIndex] | MAGIC_BISHOP_MOVE[spot.ordinal()][bishopDBIndex])
                & ~getPiece(positions, ALL, color);
    }

    public int getBitBoardIndex()
    {
        return this.color.equals(WHITE) ? 4 : 10;
    }

    @Override
    public String toString() {
        return this.color + "Q";
    }

}
