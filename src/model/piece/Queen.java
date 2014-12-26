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
public class Queen implements Piece {

    public long getValidMoves(Board board, Color color, Spot spot)
    {
        long rookBlockers = board.getPiece(ALL, BOTH) & ROOK_OCC_MASK[spot.ordinal()],
                bishopBlockers = board.getPiece(ALL, BOTH) & BISHOP_OCC_MASK[spot.ordinal()];
        int rookDBIndex = (int)((rookBlockers * MAGIC_ROOK_NUMBER[spot.ordinal()]) >>> MAGIC_ROOK_SHIFT[spot.ordinal()]),
                bishopDBIndex = (int)((bishopBlockers * MAGIC_BISHOP_NUMBER[spot.ordinal()]) >>> MAGIC_BISHOP_SHIFT[spot.ordinal()]);
        return (MAGIC_ROOK_MOVE[spot.ordinal()][rookDBIndex] | MAGIC_BISHOP_MOVE[spot.ordinal()][bishopDBIndex])
                & ~board.getPiece(ALL, color);
    }

}
