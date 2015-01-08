package model;

import model.constants.Color;
import model.constants.Piece;
import model.constants.Spot;
import model.piece.*;

import static model.constants.BitBoards.*;
import static model.constants.Color.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/23/14.
 */
public class Board {

    private long[] position = POSITION;
    private long[] checkInfo = new long[15];
    private static final ChessPiece[] WHITE_PIECES = {
            new Pawn(WHITE), new Rook(WHITE), new Knight(WHITE),
            new Bishop(WHITE), new Queen(WHITE), new King(WHITE)
    };
    private static final ChessPiece[] BLACK_PIECES = {
            new Pawn(BLACK), new Rook(BLACK), new Knight(BLACK),
            new Bishop(BLACK), new Queen(BLACK), new King(BLACK)
    };
    private ChessPiece[] board = new ChessPiece[64];
    private Color turn = WHITE;
    public enum State {CHECK, CHECKMATE, STALEMATE, DEFAULT, NO_KING_MOVES}
    private State state;

    public Board()
    {
        createNewBoard();
    }

    public boolean move(Spot origin, Spot target)
    {
        //origin has to be a piece of the same color as current turn
        if ((PIECE[origin.ordinal()] & getPiece(ALL, turn)) == 0)
            return false;

        //target cannot be a piece of the same color
        if ((PIECE[target.ordinal()] & getPiece(ALL, turn)) != 0)
            return false;

        //piece can actually move from origin to target.
        if ((board[origin.ordinal()].getValidMoves(position, origin) & PIECE[target.ordinal()]) == 0)
            return false;

        //piece is allowed to move... update bitboards and board
        int positionIndex = board[origin.ordinal()].getBitBoardIndex();
        position[positionIndex] = (~PIECE[origin.ordinal()] & position[positionIndex]) ^ PIECE[target.ordinal()];
        if (board[target.ordinal()] != null)
        {
            positionIndex = board[target.ordinal()].getBitBoardIndex();
            position[positionIndex] &= ~PIECE[target.ordinal()];
            positionIndex = turn.equals(WHITE) ? ALL_BLACKS : ALL_WHITES;
            position[positionIndex] &= ~PIECE[target.ordinal()];
        }

        positionIndex = turn.equals(WHITE) ? ALL_WHITES : ALL_BLACKS;
        position[positionIndex] = (~PIECE[origin.ordinal()] & position[positionIndex]) ^ PIECE[target.ordinal()];
        position[ALL_PIECES] = position[ALL_WHITES] | position[ALL_BLACKS];
        board[target.ordinal()] = board[origin.ordinal()];
        board[origin.ordinal()] = null;

        //change turns
        turn = turn.equals(WHITE) ? BLACK : WHITE;

        indicateStalemate();
        int numAttackers = indicateCheck(position.clone());

        if (numAttackers > 0 && this.state == State.STALEMATE)
            this.state = State.CHECKMATE;
        else if (numAttackers > 0)
        {
            if (numAttackers > 1 && this.state == State.NO_KING_MOVES)
            //if it's a double check and the king can't move, it's automatically a checkmate.
            {
                this.state = State.CHECKMATE;
            }
            indicateCheckmate();
        }

        System.out.println(this.state);

        return true;
    }

    private void resetCheckInfo()
    {
        int baseIndex = turn.equals(WHITE) ? WHITE_PAWNS : BLACK_PAWNS;
        for (int i = baseIndex; i < baseIndex+6; i++) this.checkInfo[i] = 0L;
        this.checkInfo[turn.equals(WHITE) ? ALL_WHITES : ALL_BLACKS]  = 0L;
        this.checkInfo[ALL_PIECES] = this.checkInfo[ALL_WHITES] | this.checkInfo[ALL_BLACKS];
    }

    private int indicateCheck(long[] pretendPositions)
    {
        resetCheckInfo();
        int kingIndex = turn.equals(WHITE) ? WHITE_KING : BLACK_KING;
        Color oppositeColor = turn.equals(WHITE) ? BLACK : WHITE;
        Spot kingPosition = Spot.values()[Long.numberOfTrailingZeros(position[kingIndex])];
        int numAttackers = 0;

        ChessPiece[] allPieces = turn.equals(WHITE) ? WHITE_PIECES : BLACK_PIECES;
        long moves, attackers;
        pretendPositions[kingIndex] = 0;

        for (int i = 0; i < 6; i++)
        {
            //turn off king position that we added previously
            if (i > 0) pretendPositions[allPieces[i-1].getBitBoardIndex()] &= ~PIECE[kingPosition.ordinal()];
            //add king position to the new piece type
            pretendPositions[allPieces[i].getBitBoardIndex()] ^= PIECE[kingPosition.ordinal()];
            //find valid moves from the king position
            moves = allPieces[i].getValidMoves(pretendPositions, kingPosition);
            //iff it collides with opposite pieces OF THE SAME TYPE, we're in check.
            if ((attackers = (moves & getPiece(allPieces[i].getType(), oppositeColor))) != 0)
            {
                numAttackers += Long.bitCount(attackers);
                this.checkInfo[allPieces[i].getBitBoardIndex()] = attackers;
                this.checkInfo[turn == WHITE ? ALL_WHITES : ALL_BLACKS] |= attackers;
            }
        }

        this.checkInfo[ALL_PIECES] = this.checkInfo[ALL_WHITES] | this.checkInfo[ALL_BLACKS];

        return numAttackers;
    }

    private boolean indicateCheckmate()
    {
        int kingIndex = turn.equals(WHITE) ? WHITE_KING : BLACK_KING;
        Spot kingPosition = Spot.values()[Long.numberOfTrailingZeros(position[kingIndex])];
        ChessPiece[] allPieces = turn.equals(WHITE) ? WHITE_PIECES : BLACK_PIECES;

        int baseIndex = turn.equals(WHITE) ? WHITE_PAWNS : BLACK_PAWNS;
        int pieceIndex;
        Spot attackerSpot;
        long attacker;

        for (int i = baseIndex; i < baseIndex+6; i++)
        {
            attacker = this.checkInfo[i];
            if (attacker == 0) continue;
            pieceIndex = Long.numberOfTrailingZeros(attacker);
            attackerSpot = Spot.values()[pieceIndex];
            if (defensible(allPieces, baseIndex, kingPosition, attackerSpot))
                return false;
        }

        this.state = State.CHECKMATE;
        return true;
    }

    private boolean defensible(ChessPiece[] allPieces, int baseIndex, Spot kingPosition, Spot attackerPosition)
    {
        long defenderBoard;
        long[] pretendPositions = position.clone();
        int defenderIndex;
        int pieceTypeIndex = 0;

        for (int i = baseIndex; i < baseIndex+5; i++, pieceTypeIndex++)
        {
            defenderBoard = pretendPositions[i];
            while (defenderBoard != 0)
            {
                defenderIndex = Long.numberOfTrailingZeros(defenderBoard);
                defenderBoard &= ~PIECE[defenderIndex];
                //is this piece pinned?
                if (pinned(i, defenderIndex)) continue;
                //can this piece capture the attacker?
                if (capturable(allPieces[pieceTypeIndex], Spot.values()[defenderIndex], attackerPosition.ordinal()))
                    return false;
                //can't block a knight...
                if (i == WHITE_KNIGHTS || i == BLACK_KNIGHTS) continue;
                //can this piece block the attack ray?
                if (blockable(allPieces[pieceTypeIndex], Spot.values()[defenderIndex], kingPosition, attackerPosition))
                    return false;
            }
        }

        this.state = State.CHECKMATE;
        return true;
    }

    private boolean pinned(int boardIndex, int pieceIndex)
    {
        long[] pretendPositions = position.clone();
        pretendPositions[boardIndex] &= ~PIECE[pieceIndex];
        pretendPositions[turn == WHITE ? ALL_WHITES : ALL_BLACKS] &= ~PIECE[pieceIndex];
        pretendPositions[ALL_PIECES] = pretendPositions[ALL_WHITES] | pretendPositions[ALL_BLACKS];

        return indicateCheck(pretendPositions) > 1; //1 because we're already under check
    }

    private boolean capturable(ChessPiece piece, Spot origin, int attackerIndex)
    {
        return (piece.getValidMoves(position, origin) & PIECE[attackerIndex]) != 0;
    }

    private boolean blockable(ChessPiece piece, Spot origin, Spot king, Spot attacker)
    {
        long attackRay = getAttackRay(king, attacker);
        return (piece.getValidMoves(position, origin) & attackRay) != 0;
    }

    private long getAttackRay(Spot origin, Spot attacker)
    {
        int from = Math.min(origin.ordinal(), attacker.ordinal()),
                to = Math.max(origin.ordinal(), attacker.ordinal());
        int difference = to - from;
        boolean isRook = difference % 8 == 0 || (to / 8 == from / 8);
        long ray = 0L;
        int increment;

        if (isRook)
        {
            if (difference == 0 || difference == 8) return 0L;
            else if (difference < 8) increment = 1;
            else increment = 8;
        }
        else
        {
            int toRankIndex = to % 8, fromRankIndex = from % 8;
            if (difference == 7 || difference == 9) return 0L;
            if (toRankIndex > fromRankIndex) increment = 9; //attackray is northeast
            else increment = 7; //attackray is northwest
        }

        while (to > from)
        {
            to -= increment;
            from += increment;
            ray |= PIECE[to] | PIECE[from];
        }

        return ray;
    }

    private boolean indicateStalemate()
    {
        int possibleMove = 0;
        this.state = State.DEFAULT;
        long[] pretendPositions = position.clone();
        int kingIndex = turn.equals(WHITE) ? WHITE_KING : BLACK_KING;
        Spot kingPosition = Spot.values()[Long.numberOfTrailingZeros(getPiece(KING, turn))];
        long kingMoves = board[kingPosition.ordinal()].getValidMoves(position, kingPosition);

        pretendPositions[kingIndex] = 0L;
        while (kingMoves != 0)
        {
            possibleMove = Long.numberOfTrailingZeros(kingMoves);
            pretendPositions[kingIndex] = PIECE[possibleMove];

            if (indicateCheck(pretendPositions) == 0)
            //king can move to at least one spot, without being checked...
            {
                this.state = State.DEFAULT;
                return false;
            }

            kingMoves &= ~PIECE[possibleMove];
        }

        //king can't move anywhere, but maybe other pieces can?
        int pieceIndex = turn.equals(WHITE) ? WHITE_PAWNS : BLACK_PAWNS;
        pretendPositions = position.clone();
        for (int i = pieceIndex; i < pieceIndex + 5; i++)
        {
            while (pretendPositions[pieceIndex] != 0)
            {
                possibleMove = Long.numberOfTrailingZeros(pretendPositions[pieceIndex]);
                if (board[possibleMove].getValidMoves(position, Spot.values()[possibleMove]) != 0)
                //at least one of the pieces can move.
                {
                    this.state = State.NO_KING_MOVES;
                    return false;
                }
                pretendPositions[pieceIndex] &= ~PIECE[possibleMove];
            }
        }

        //not a single piece can move.
        this.state = State.STALEMATE;
        return true;
    }

    public long getPiece(Piece piece, Color color)
    {
        return piece.equals(ALL)
                ? position[ 12 + color.ordinal() ]
                : position[ color.ordinal() * 6 + piece.ordinal() ];
    }

    public static void printBitBoard(long bitboard)
    {
        String padding = "0000000000000000000000000000000000000000000000000000000000000000";
        String paddedBitBoard = padding + Long.toBinaryString(bitboard);
        paddedBitBoard = paddedBitBoard.substring(paddedBitBoard.length() - 64, paddedBitBoard.length());  // take the right

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 8; i++)
        {
            for (int j = 7; j >= 0; j--)
            {
                buffer.append(paddedBitBoard.charAt(i*8+j)).append("\t");
            }
            buffer.append("\n");
        }

        System.out.println(buffer);
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(8 + "\t");

        int idx = 0;
        for (int rank = 7; rank >= 0; rank--)
        {
            for (int file = 0; file < 8; file++)
            {
                idx = rank*8+file;
                buffer.append(
                        board[idx] == null
                                ? (
                                (idx / 8) % 2 == 0
                                        ? (idx % 2 == 0 ? "#" : ".")
                                        : (idx % 2 == 0 ? "." : "#")
                        ) : board[idx]).append("\t");
            }
            buffer.append("\n" + ((idx / 8) != 0 ? (idx / 8) + "\t" : "\t"));
        }

        for (final char file : "abcdefgh".toCharArray()) {
            buffer.append(file + "\t");
        }

        return buffer.toString();
    }

    private void createNewBoard()
    {
        board[8] = board[9] = board[10] = board[11]
                = board[12] = board[13] = board[14] = board[15] = new Pawn(WHITE);
        board[0] = board[7] = new Rook(WHITE);
        board[1] = board[6] = new Knight(WHITE);
        board[2] = board[5] = new Bishop(WHITE);
        board[3] = new Queen(WHITE);
        board[4] = new King(WHITE);

        board[48] = board[49] = board[50] = board[51]
                = board[52] = board[53] = board[54] = board[55] = new Pawn(BLACK);
        board[56] = board[63] = new Rook(BLACK);
        board[57] = board[62] = new Knight(BLACK);
        board[58] = board[61] = new Bishop(BLACK);
        board[59] = new Queen(BLACK);
        board[60] = new King(BLACK);
    }

}
