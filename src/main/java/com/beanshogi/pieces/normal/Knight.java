package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.promoted.PromotedKnight;
import com.beanshogi.util.*;

// Knight (桂)
public class Knight extends Piece implements Promotable {
    // Link the promoted version of Knight
    private static final Class<? extends Piece> promotedClass = PromotedKnight.class;
    
    // The transformation matrix for the current X and Y 
    private static final int[][] knightOffsets = {{-1,2},  {1,2}
                                                         //
                                                         //
                                                       /*KN*/};

    public Knight(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(knightOffsets);
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