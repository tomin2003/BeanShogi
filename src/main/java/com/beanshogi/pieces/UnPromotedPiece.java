package com.beanshogi.pieces;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

public abstract class UnPromotedPiece extends Piece implements Promotable {

    // Each concrete unpromoted piece must define the class it promotes to
    protected abstract Class<? extends Piece> getPromotedClass();

    public UnPromotedPiece(Sides side, Position position, Board board) {
        super(side, position, board);
    }

    @Override
    public Piece promote() {
        try {
            return getPromotedClass()
                    .getConstructor(Sides.class, Position.class, Board.class)
                    .newInstance(side, position, board);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Piece demote() {
        return this; // already demoted
    }

    @Override
    public boolean isPromoted() {
        return false;
    }
}