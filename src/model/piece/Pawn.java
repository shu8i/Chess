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
public class Pawn extends ChessPiece {

    public Pawn(Color color)
    {
        super(color);
    }

    public long getValidMoves(long[] positions, Spot spot)
    {
        long allPieces = getPiece(positions, ALL, BOTH),
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
                        attacks & getPiece(positions, ALL, BLACK) :
                        attacks & getPiece(positions, ALL, WHITE);

        return moves | validAttacks;
    }

    public int getBitBoardIndex()
    {
        return this.color.equals(WHITE) ? 0 : 6;
    }

    @Override
    public String toString() {
        return this.color + "P";
    }

}
