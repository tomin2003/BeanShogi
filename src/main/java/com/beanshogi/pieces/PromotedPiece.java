package com.beanshogi.pieces;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

/**
 * Defines the promoted piece of a specific shogi piece.
 */
public abstract class PromotedPiece extends Piece implements Promotable {
    protected abstract Class<? extends Piece> getDemotedClass();

    public PromotedPiece(Sides side, Position position, Board board) {
        super(side, position, board);
    }

    @Override
    public Piece promote() {
        return this; // already promoted
    }

    @Override
    public Piece demote() {
        try {
            return getDemotedClass()
                    .getConstructor(Sides.class, Position.class, Board.class)
                    .newInstance(side, position, board);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPromoted() {
        return true;
    }
}