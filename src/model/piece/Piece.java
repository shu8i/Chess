package model.piece;

import model.Board;
import model.constants.Color;
import model.constants.Spot;


/**
 * Created by Shahab Shekari on 12/24/14.
 */
public interface Piece {

    public long getValidMoves(Board board, Color color, Spot spot);

}
