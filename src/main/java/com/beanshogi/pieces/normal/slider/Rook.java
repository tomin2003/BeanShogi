package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.promoted.PromotedLance;
import com.beanshogi.util.*;

// Rook (飛)
public class Rook extends Piece implements Promotable {
    // Links the promoted verson of Lance
    private static final Class<? extends Piece> promotedClass = PromotedLance.class;

    private static final int[][] rookDirs = {         {0,1},
                                               {-1,0},/*RO*/{1,0},
                                                     {-1,-1}};

    public Rook(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(rookDirs);
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