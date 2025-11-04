package com.beanshogi.pieces.promoted;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.PromotedPiece;
import com.beanshogi.pieces.normal.*;
import com.beanshogi.util.*;

// Promoted Knight (圭)
public class PromotedKnight extends PromotedPiece {

    // Normal promoted pieces behave like the Gold General, so they composite it
    private GoldGeneral goldDelegate;

    public PromotedKnight(Colors color, Position position, Board board) {
        super(color, position, board);
        goldDelegate = new GoldGeneral(color, position, board);
    }

    @Override
    public Set<Position> getLegalMoves() {
        goldDelegate.setPosition(this.position);
        return goldDelegate.getLegalMoves();
    }

    @Override
    protected Class<? extends Piece> getDemotedClass() {
        return Knight.class;
    }
}
