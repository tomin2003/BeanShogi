package com.beanshogi.pieces.promoted;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.PromotedPiece;
import com.beanshogi.pieces.normal.GoldGeneral;
import com.beanshogi.pieces.normal.slider.*;
import com.beanshogi.util.*;

// Promoted Lance (杏)
public class PromotedLance extends PromotedPiece {
    
    // Normal promoted pieces behave like the Gold General, so they composite it
    private GoldGeneral goldDelegate;

    public PromotedLance(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
        goldDelegate = new GoldGeneral(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        goldDelegate.setBoardPosition(this.boardPosition);
        goldDelegate.setHandPosition(this.handPosition);
        return goldDelegate.getLegalMoves();
    }

    @Override
    public List<Position> getAttackMoves() {
        goldDelegate.setBoardPosition(this.boardPosition);
        goldDelegate.setHandPosition(this.handPosition);
        return goldDelegate.getAttackMoves();
    }

    @Override
    protected Class<? extends Piece> getDemotedClass() {
        return Lance.class;
    }

    @Override
    public int value() {
        return goldDelegate.value();
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new PromotedLance(this.side, this.boardPosition, this.handPosition, board);
    }
}
