package model.piece;

/**
 * Created by Shahab Shekari on 12/24/14.
 */
public interface Piece {

    public long getValidMoves(long location, long friendlies);

}
