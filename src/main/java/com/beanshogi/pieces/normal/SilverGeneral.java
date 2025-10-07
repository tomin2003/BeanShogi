package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.promoted.PromotedSilverGeneral;
import com.beanshogi.util.*;

// Silver General (銀)
public class SilverGeneral extends Piece implements Promotable {
    // Link the promoted version of Silver General
    public static final Class<? extends Piece> promotedClass = PromotedSilverGeneral.class;

    // The transformation matrix for the current X and Y 
    private static final int[][] silverOffsets = {{-1,1},{0,1},{1,1},
                                                         /*SG*/
                                                  {-1,-1},     {1,-1}};

    public SilverGeneral(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(silverOffsets);
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