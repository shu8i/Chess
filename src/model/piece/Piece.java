package model.piece;

import model.Board;
import model.constants.Color;


/**
 * Created by Shahab Shekari on 12/24/14.
 */
public interface Piece {

    public long getValidMoves(Board board, Color color, long location);

}
