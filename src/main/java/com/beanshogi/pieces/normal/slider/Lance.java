package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.promoted.PromotedLance;
import com.beanshogi.util.*;

// Lance (香)
public class Lance extends Piece implements Promotable {
    // Links the promoted verson of Lance
    private static final Class<? extends Piece> promotedClass = PromotedLance.class;

    // The Lance only moves up n times relative to position.
    private static final int[][] lanceDirs = {{0,1}};
                                              /*LA*/

    public Lance(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(lanceDirs);
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