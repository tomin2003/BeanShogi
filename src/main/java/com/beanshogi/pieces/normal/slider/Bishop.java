package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.UnPromotedPiece;
import com.beanshogi.pieces.promoted.slider.*;
import com.beanshogi.util.*;

// Bishop (角)
public class Bishop extends UnPromotedPiece {
    // Link the promoted version of Bishop
    private static final Class<? extends Piece> promotedClass = PromotedBishop.class;

    private static final int[][] bishopDirs = {{-1,1},/* */{1,1},
                                                      /*BI*/
                                               {-1,-1},/**/{1,-1}};
    
    public Bishop(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(bishopDirs);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedBishop.class;
    }
}