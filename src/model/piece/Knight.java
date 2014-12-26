package model.piece;

import model.Board;
import model.constants.Color;
import model.constants.Spot;

import static model.constants.BitBoards.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/24/14.
 */
public class Knight implements Piece {

    public long getValidMoves(Board board, Color color, Spot spot)
    {
        long friendlies = board.getPiece(ALL, color),
                location = PIECE[spot.ordinal()],
                clipFileAB = location & CLEAR_FILE_A & CLEAR_FILE_B,
                clipFileA = location & CLEAR_FILE_A,
                clipFileGH = location & CLEAR_FILE_G & CLEAR_FILE_H,
                clipFileH = location & CLEAR_FILE_H,

                spot1 = clipFileAB << 6,
                spot2 = clipFileA << 15,
                spot3 = clipFileH << 17,
                spot4 = clipFileGH << 10,
                spot5 = clipFileGH >> 6,
                spot6 = clipFileH >> 15,
                spot7 = clipFileA >> 17,
                spot8 = clipFileAB >> 10,

                moves = spot1 | spot2 | spot3 | spot4 |
                        spot5 | spot6 | spot7 | spot8;

        return moves & ~friendlies;
    }

}
