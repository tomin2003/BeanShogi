package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.UnPromotedPiece;
import com.beanshogi.pieces.promoted.*;
import com.beanshogi.util.*;


// Pawn (歩)
public class Pawn extends UnPromotedPiece {
    
    // The pawn only moves up relative to position.
    private static final int[][] pawnOffset = {{0,1}};
                                               /*PA*/

    public Pawn(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(pawnOffset);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedPawn.class;
    }
}