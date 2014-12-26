package model;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import model.constants.Color;
import model.constants.Piece;
import model.piece.Bishop;
import model.piece.Queen;
import model.piece.Rook;

import java.util.Arrays;

import static model.constants.Spot.*;
import static model.constants.BitBoards.*;
import static model.constants.Color.*;
import static model.constants.Piece.*;

/**
 * Created by Shahab Shekari on 12/23/14.
 */
public class Board {

    private long[] position = POSITION;


    public long test()
    {
        Queen queen = new Queen();
        return queen.getValidMoves(this, WHITE, D1);

    }

    public long getPiece(Piece piece, Color color)
    {
        return piece.equals(ALL)
                ? position[ 12 + color.ordinal() ]
                : position[ color.ordinal() * 6 + piece.ordinal() ];
    }

    public static void generateOccupancyVariations(boolean isRook)
    {
        int i, j, bitRef;
        long mask;
        int variationCount;
        int[] setBitsInMask, setBitsInIndex;
        int bitCount;

        for (bitRef = 0; bitRef < 64; bitRef++) {
            mask = isRook ? ROOK_OCC_MASK[bitRef] : BISHOP_OCC_MASK[bitRef];
            setBitsInMask = getSetBits(mask);
            bitCount = Long.bitCount(mask);
            variationCount = (int) (1L << bitCount);
            for (i = 0; i < variationCount; i++) {
                OCCUPANCY_VARIATION[bitRef][i] = 0;

                // find bits set in index "i" and map them to bits in the 64 bit "occupancyVariation"

                setBitsInIndex = getSetBits(i); // an array of integers showing which bits are set
                for (j = 0; setBitsInIndex[j] != -1; j++) {
                    OCCUPANCY_VARIATION[bitRef][i] |= (1L << setBitsInMask[setBitsInIndex[j]]);
                }

                if (isRook) {
                    for (j = bitRef + 8; j <= 55 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j += 8) ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                    for (j = bitRef - 8; j >= 8 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j -= 8) ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                    for (j = bitRef + 1; j % 8 != 7 && j % 8 != 0 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j++)
                        ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                    for (j = bitRef - 1; j % 8 != 7 && j % 8 != 0 && j >= 0 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j--)
                        ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                } else {
                    for (j = bitRef + 9; j % 8 != 7 && j % 8 != 0 && j <= 55 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j += 9)
                        ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                    for (j = bitRef - 9; j % 8 != 7 && j % 8 != 0 && j >= 8 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j -= 9)
                        ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                    for (j = bitRef + 7; j % 8 != 7 && j % 8 != 0 && j <= 55 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j += 7)
                        ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                    for (j = bitRef - 7; j % 8 != 7 && j % 8 != 0 && j >= 8 && (OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) == 0; j -= 7)
                        ;
                    if (j >= 0 && j <= 63) OCCUPANCY_ATTACK_SET[bitRef][i] |= (1L << j);
                }
            }
        }

    }

    public static void generateMoveDatabase(boolean isRook)
    {
        long validMoves;
        int variations, bitCount;
        int bitRef, i, j, magicIndex;

        for (bitRef=0; bitRef<=63; bitRef++)
        {
            bitCount = isRook ? Long.bitCount(ROOK_OCC_MASK[bitRef]) : Long.bitCount(BISHOP_OCC_MASK[bitRef]);
            variations = (int)(1L << bitCount);

            for (i=0; i<variations; i++)
            {
                validMoves = 0;
                if (isRook)
                {
                    magicIndex = (int)((OCCUPANCY_VARIATION[bitRef][i] * MAGIC_ROOK_NUMBER[bitRef]) >>> MAGIC_ROOK_SHIFT[bitRef]);

                    for (j=bitRef+8; j<=63; j+=8) { validMoves |= (1L << j); if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0) break; }
                    for (j=bitRef-8; j>=0; j-=8) { validMoves |= (1L << j); if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0) break; }
                    for (j=bitRef+1; j%8!=0; j++) { validMoves |= (1L << j); if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0) break; }
                    for (j=bitRef-1; j%8!=7 && j>=0; j--) { validMoves |= (1L << j); if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0) break; }

                    MAGIC_ROOK_MOVE[bitRef][magicIndex] = validMoves;
                }
                else
                {
                    magicIndex = (int)((OCCUPANCY_VARIATION[bitRef][i] * MAGIC_BISHOP_NUMBER[bitRef]) >>> MAGIC_BISHOP_SHIFT[bitRef]);

                    for (j=bitRef+9; j%8!=0 && j<=63; j+=9) { validMoves |= (1L << j); if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0) break; }
                    for (j=bitRef-9; j%8!=7 && j>=0; j-=9) { validMoves |= (1L << j); if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0) break; }
                    for (j=bitRef+7; j%8!=7 && j<=63; j+=7) {
                        validMoves |= (1L << j);
                        if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0)
                            break;
                    }
                    for (j=bitRef-7; j%8!=0 && j>=0; j-=7) {
                        validMoves |= (1L << j);
                        if ((OCCUPANCY_VARIATION[bitRef][i] & (1L << j)) != 0)
                            break;
                    }

                    MAGIC_BISHOP_MOVE[bitRef][magicIndex] = validMoves;
                }
            }
        }
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

}
