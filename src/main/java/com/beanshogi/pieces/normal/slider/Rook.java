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

    public Rook(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public Set<Position> getLegalMoves() {
        return getLegalMovesSlider(rookDirs);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedRook.class;
    }
}