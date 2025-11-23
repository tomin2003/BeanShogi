package com.beanshogi.core.pieces.normal.slider;

import java.util.*;

import com.beanshogi.core.pieces.UnPromotedPiece;
import com.beanshogi.core.pieces.promoted.slider.*;
import com.beanshogi.core.util.*;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.board.Board;
import com.beanshogi.core.pieces.Piece;

// Rook (飛)
public class Rook extends UnPromotedPiece {

    private static final int[][] rookDirs = {         {0,1},
                                               {-1,0},/*RO*/{1,0},
                                                     {0,-1}};

    public Rook(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(rookDirs, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        return getLegalMovesSlider(rookDirs, true);
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
        return new Rook(this.side, this.boardPosition, this.handPosition, board);
    }
}