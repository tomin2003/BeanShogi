package com.beanshogi.pieces.promoted;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.PromotedPiece;
import com.beanshogi.pieces.normal.GoldGeneral;
import com.beanshogi.pieces.normal.slider.*;
import com.beanshogi.util.*;

// Promoted Lance (杏)
public class PromotedLance extends PromotedPiece {
    // Link to the Lance
    private static final Class<? extends Piece> demotedClass = Lance.class;

    // Normal promoted pieces behave like the Gold General, so they composite it
    private GoldGeneral goldDelegate;

    public PromotedLance(Colors color, Position position, Board board) {
        super(color, position, board);
        goldDelegate = new GoldGeneral(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        goldDelegate.setPosition(this.position);
        return goldDelegate.getLegalMoves();
    }

    @Override
    protected Class<? extends Piece> getDemotedClass() {
        return Lance.class;
    }
}
