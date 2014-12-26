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
public class Rook implements Piece {

    public long getValidMoves(Board board, Color color, Spot spot)
    {
        long blockers = board.getPiece(ALL, BOTH) & ROOK_OCC_MASK[spot.ordinal()];
        int databaseIndex = (int)((blockers * MAGIC_ROOK_NUMBER[spot.ordinal()]) >>> MAGIC_ROOK_SHIFT[spot.ordinal()]);
        return MAGIC_ROOK_MOVE[spot.ordinal()][databaseIndex] & ~board.getPiece(ALL, color);
    }

}
