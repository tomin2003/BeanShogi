package com.beanshogi.core.pieces.normal.slider;

import java.util.*;

import com.beanshogi.core.board.Board;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.pieces.UnPromotedPiece;
import com.beanshogi.core.pieces.promoted.slider.*;
import com.beanshogi.core.util.*;

// Bishop (角)
public class Bishop extends UnPromotedPiece {

    private static final int[][] bishopDirs = {{-1,1},/* */{1,1},
                                                      /*BI*/
                                               {-1,-1},/**/{1,-1}};
    
    public Bishop(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(bishopDirs, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        return getLegalMovesSlider(bishopDirs, true);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedBishop.class;
    }

    @Override
    public int value() {
        return 800;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new Bishop(this.side, this.boardPosition, this.handPosition, board);
    }
}