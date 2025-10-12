package com.beanshogi.pieces;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

public abstract class UnPromotedPiece extends Piece implements Promotable {

    // Each concrete unpromoted piece must define the class it promotes to
    protected abstract Class<? extends Piece> getPromotedClass();

    public UnPromotedPiece(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public Piece promote() {
        try {
            return getPromotedClass()
                    .getConstructor(Colors.class, Position.class, Board.class)
                    .newInstance(color, position, board);
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