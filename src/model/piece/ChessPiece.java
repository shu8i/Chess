package model.piece;

import model.Board;
import model.constants.Color;
import model.constants.Spot;


/**
 * Created by Shahab Shekari on 12/24/14.
 */
public abstract class ChessPiece {

    protected Color color;

    public ChessPiece(Color color) {
        this.color = color;
    }
    
    public abstract long getValidMoves(Board board, Spot spot);
    public abstract int getBitBoardIndex();

}
