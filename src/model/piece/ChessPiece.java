package model.piece;

import model.Board;
import model.constants.Color;
import model.constants.Piece;
import model.constants.Spot;

import static model.constants.Piece.ALL;


/**
 * Created by Shahab Shekari on 12/24/14.
 */
public abstract class ChessPiece {

    protected Color color;
    protected Piece type;

    public ChessPiece(Color color) {
        this.color = color;
    }

    protected static long getPiece(long[] positions, Piece piece, Color color)
    {
        return piece.equals(ALL)
                ? positions[ 12 + color.ordinal() ]
                : positions[ color.ordinal() * 6 + piece.ordinal() ];
    }

    public Piece getType()
    {
        return this.type;
    }

    public abstract long getValidMoves(long[] positions, Spot origin);
    public abstract int getBitBoardIndex();

}
