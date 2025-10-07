package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.promoted.slider.PromotedBishop;
import com.beanshogi.util.*;

// Bishop (角)
public class Bishop extends Piece implements Promotable {
    // Link the promoted version of Bishop
    private static final Class<? extends Piece> promotedClass = PromotedBishop.class;

    private static final int[][] bishopDirs = {{-1,1},/* */{1,1},
                                                      /*BI*/
                                               {-1,-1},/**/{1,-1}};
    
    public Bishop(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(bishopDirs);
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