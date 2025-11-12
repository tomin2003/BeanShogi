package com.beanshogi.pieces.promoted;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.*;
import com.beanshogi.pieces.normal.*;
import com.beanshogi.util.*;

// Promoted Silver General (全)
public class PromotedSilverGeneral extends PromotedPiece {
    
    // Normal promoted pieces behave like the Gold General, so they composite it
    private GoldGeneral goldDelegate;

    public PromotedSilverGeneral(Sides side, Position position, Board board) {
        super(side, position, board);
        goldDelegate = new GoldGeneral(side, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        goldDelegate.setPosition(this.position);
        return goldDelegate.getLegalMoves();
    }

    @Override
    public List<Position> getAttackMoves() {
        goldDelegate.setPosition(this.position);
        return goldDelegate.getLegalMoves();
    }

    @Override
    protected Class<? extends Piece> getDemotedClass() {
        return SilverGeneral.class;
    }

    @Override
    public int value() {
        return goldDelegate.value();
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new PromotedSilverGeneral(this.side, this.position, board);
    }
}
