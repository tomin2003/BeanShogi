package com.beanshogi.pieces;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

public abstract class UnPromotedPiece extends Piece implements Promotable {

    // Each concrete unpromoted piece must define the class it promotes to
    protected abstract Class<? extends Piece> getPromotedClass();

    public UnPromotedPiece(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition,handPosition, board);
    }

    @Override
    public Piece promote() {
        try {
            return getPromotedClass()
                    .getConstructor(Sides.class, Position.class, Position.class, Board.class)
                    .newInstance(side, boardPosition, handPosition, board);
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