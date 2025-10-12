package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.UnPromotedPiece;
import com.beanshogi.pieces.promoted.*;
import com.beanshogi.util.*;


// Knight (桂)
public class Knight extends UnPromotedPiece {
    
    // The transformation matrix for the current X and Y 
    private static final int[][] knightOffsets = {{-1,2},  {1,2}
                                                         //
                                                         //
                                                       /*KN*/};

    public Knight(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(knightOffsets);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedKnight.class;
    }
}