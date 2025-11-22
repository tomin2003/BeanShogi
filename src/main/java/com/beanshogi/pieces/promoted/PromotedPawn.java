package com.beanshogi.pieces.promoted;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.PromotedPiece;
import com.beanshogi.pieces.normal.*;
import com.beanshogi.util.*;

// Promoted Pawn (と)
public class PromotedPawn extends PromotedPiece {

    // Normal promoted pieces behave like the Gold General, so they composite it
    private GoldGeneral goldDelegate;

    public PromotedPawn(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
        goldDelegate = new GoldGeneral(side, boardPosition, handPosition, board);
    }

    @Override
    public void setBoard(Board board) {
        super.setBoard(board);
        if (goldDelegate == null) {
            goldDelegate = new GoldGeneral(side, boardPosition, handPosition, board);
        } else {
            goldDelegate.setBoard(board);
        }
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
        return Pawn.class;
    }

    @Override
    public int value() {
        return goldDelegate.value();
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new PromotedPawn(this.side, this.boardPosition, this.handPosition, board);
    }
}
