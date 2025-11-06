package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.UnPromotedPiece;
import com.beanshogi.pieces.promoted.slider.*;
import com.beanshogi.util.*;

// Rook (飛)
public class Rook extends UnPromotedPiece {

    private static final int[][] rookDirs = {         {0,1},
                                               {-1,0},/*RO*/{1,0},
                                                     {-1,-1}};

    public Rook(Sides side, Position position, Board board) {
        super(side, position, board);
    }

    @Override
    public Set<Position> getLegalMoves() {
        return getLegalMovesSlider(rookDirs);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedRook.class;
    }

    @Override
    public int value() {
        return 900;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new Rook(this.side, this.position, this.board);
    }
}