package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.promoted.PromotedPawn;
import com.beanshogi.util.*;

// Pawn (歩)
public class Pawn extends Piece implements Promotable {
    // Link the promoted version of Pawn
    public static final Class<? extends Piece> promotedClass = PromotedPawn.class;
    
    // The pawn only moves up relative to position.
    private static final int[][] pawnOffset = {{0,1}};
                                               /*PA*/

    public Pawn(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(pawnOffset);
    }

    @Override
    public Piece promote() {
        try {
            return promotedClass.getConstructor(Colors.class, Position.class, Board.class)
                                 .newInstance(color, position, board);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Piece demote() {
        return this; // already unpromoted
    }

    @Override
    public boolean isPromoted() {
        return false; 
    }
}