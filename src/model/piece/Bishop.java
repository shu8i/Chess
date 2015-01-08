package model.piece;

import model.constants.Color;
import model.constants.Spot;

import static model.constants.BitBoards.*;
import static model.constants.Color.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/25/14.
 */
public class Bishop extends ChessPiece {

    public Bishop(Color color)
    {
        super(color);
        type = BISHOP;
    }

    public long getValidMoves(long[] positions, Spot spot)
    {
        long blockers = getPiece(positions, ALL, BOTH) & BISHOP_OCC_MASK[spot.ordinal()];
        int databaseIndex = (int)((blockers * MAGIC_BISHOP_NUMBER[spot.ordinal()]) >>> MAGIC_BISHOP_SHIFT[spot.ordinal()]);
        return MAGIC_BISHOP_MOVE[spot.ordinal()][databaseIndex] & ~getPiece(positions, ALL, color);
    }

    public int getBitBoardIndex()
    {
        return this.color.equals(WHITE) ? 3 : 9;
    }

    @Override
    public String toString() {
        return this.color + "B";
    }

}
