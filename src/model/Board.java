package model;

import model.constants.Color;
import model.constants.Piece;

import static model.constants.Spot.*;
import static model.constants.Board.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/23/14.
 */
public class Board {

    private long[] position = POSITION;

    public Board()
    {

    }

    public long getPiece(Piece piece, Color color)
    {
        return color.equals(ALL)
                ? position[ 12 + color.ordinal() ]
                : position[ color.ordinal() * 6 + piece.ordinal() ];
    }

}
