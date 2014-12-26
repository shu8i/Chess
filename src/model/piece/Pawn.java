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
public class Pawn implements Piece {

    public long getValidMoves(Board board, Color color, Spot spot)
    {
        long allPieces = board.getPiece(ALL, BOTH),
                location = PIECE[spot.ordinal()],

                oneStep = color.equals(WHITE) ?
                        (location << 8) & ~allPieces :
                        (location >> 8) & ~allPieces,
                twoStep = color.equals(WHITE) ?
                        ((oneStep & MASK_RANK_3) << 8) & ~allPieces :
                        ((oneStep & MASK_RANK_6) >> 8) & ~allPieces,
                moves = oneStep | twoStep,

                clipFileA = location & CLEAR_FILE_A,
                clipFileH = location & CLEAR_FILE_H,

                leftAttack = color.equals(WHITE) ?
                        clipFileA << 7 : clipFileA >> 7,
                rightAttack = color.equals(WHITE) ?
                        clipFileH << 9 : clipFileH >> 9,
                attacks = leftAttack | rightAttack,
                validAttacks = color.equals(WHITE) ?
                        attacks & board.getPiece(ALL, BLACK) :
                        attacks & board.getPiece(ALL, WHITE);

        return moves | validAttacks;
    }

}
