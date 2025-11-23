package com.beanshogi.core.pieces.promoted;

import java.util.*;

import com.beanshogi.core.board.Board;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.pieces.PromotedPiece;
import com.beanshogi.core.pieces.normal.GoldGeneral;
import com.beanshogi.core.pieces.normal.slider.*;
import com.beanshogi.core.util.*;

// Promoted Lance (杏)
public class PromotedLance extends PromotedPiece {
    
    // Normal promoted pieces behave like the Gold General, so they composite it
    private GoldGeneral goldDelegate;

    public PromotedLance(Sides side, Position boardPosition, Position handPosition, Board board) {
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
