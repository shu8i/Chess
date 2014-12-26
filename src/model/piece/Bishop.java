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
public class Bishop implements Piece {

    public long getValidMoves(Board board, Color color, Spot spot)
    {
        long blockers = board.getPiece(ALL, BOTH) & BISHOP_OCC_MASK[spot.ordinal()];
        int databaseIndex = (int)((blockers * MAGIC_BISHOP_NUMBER[spot.ordinal()]) >>> MAGIC_BISHOP_SHIFT[spot.ordinal()]);
        return MAGIC_BISHOP_MOVE[spot.ordinal()][databaseIndex] & ~board.getPiece(ALL, color);
    }

}
