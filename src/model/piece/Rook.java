package model.piece;

import model.Board;
import model.constants.Color;
import model.constants.Spot;

import static model.constants.Piece.*;
import static model.constants.Color.*;
import static model.constants.BitBoards.*;


/**
 * Created by Shahab Shekari on 12/24/14.
 */
public class Rook extends ChessPiece {

    public Rook(Color color)
    {
        super(color);
        type = ROOK;
    }

    public long getValidMoves(long[] positions, Spot spot)
    {
        long blockers = getPiece(positions, ALL, BOTH) & ROOK_OCC_MASK[spot.ordinal()];
        int databaseIndex = (int)((blockers * MAGIC_ROOK_NUMBER[spot.ordinal()]) >>> MAGIC_ROOK_SHIFT[spot.ordinal()]);
        return MAGIC_ROOK_MOVE[spot.ordinal()][databaseIndex] & ~getPiece(positions, ALL, color);
    }

    public int getBitBoardIndex()
    {
        return this.color.equals(WHITE) ? 1 : 7;
    }

    @Override
    public String toString() {
        return this.color + "R";
    }

}
