package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.UnPromotedPiece;
import com.beanshogi.pieces.promoted.slider.*;
import com.beanshogi.util.*;

// Bishop (角)
public class Bishop extends UnPromotedPiece {

    private static final int[][] bishopDirs = {{-1,1},/* */{1,1},
                                                      /*BI*/
                                               {-1,-1},/**/{1,-1}};
    
    public Bishop(Sides side, Position position, Board board) {
        super(side, position, board);
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
        return new Bishop(this.side, this.position, board);
    }
}