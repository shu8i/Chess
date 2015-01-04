package model.piece;

import model.Board;
import model.constants.Color;
import model.constants.Spot;

import static model.constants.BitBoards.*;
import static model.constants.Color.WHITE;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/24/14.
 */
public class King extends ChessPiece {

    public King(Color color)
    {
        super(color);
    }

    public long getValidMoves(long[] positions, Spot spot)
    {
        long friendlies = getPiece(positions, ALL, color),
                location = PIECE[spot.ordinal()],
                clipFileA = location & CLEAR_FILE_A,
                clipFileH = location & CLEAR_FILE_H,

                spot1 = clipFileA << 7,
                spot2 = location << 8,
                spot3 = clipFileH << 9,
                spot4 = clipFileH << 1,
                spot5 = clipFileH >> 7,
                spot6 = location >> 8,
                spot7 = clipFileA >> 9,
                spot8 = clipFileA >> 1,

                moves = spot1 | spot2 | spot3 | spot4 |
                        spot5 | spot6 | spot7 | spot8;

        return moves & ~friendlies;
    }

    public int getBitBoardIndex()
    {
        return this.color.equals(WHITE) ? 5 : 11;
    }

    @Override
    public String toString() {
        return this.color + "K";
    }

}
