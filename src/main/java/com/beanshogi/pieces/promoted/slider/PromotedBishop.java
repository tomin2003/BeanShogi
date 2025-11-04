package com.beanshogi.pieces.promoted.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.PromotedPiece;
import com.beanshogi.pieces.normal.slider.*;
import com.beanshogi.util.*;

// Promoted Bishop/Dragon Horse (馬)
public class PromotedBishop extends PromotedPiece {

    // The Promoted Rook behaves like a slider piece and a normal piece.
    private static final int[][] promotedBishopDirs = {{-1,1},/* */{1,1},
                                                              /*PB*/
                                                       {-1,-1},/**/{1,-1}};
                                                        
    private static final int[][] promotedBishopOffsets = {       {0,1},
                                                          {-1,0},/*PB*/{1,0},
                                                                {0,-1}};

    public PromotedBishop(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public Set<Position> getLegalMoves() {
        Set<Position> moves = getLegalMovesSlider(promotedBishopDirs);
        moves.addAll(getLegalMovesNormal(promotedBishopOffsets));
        return moves;
    }

    @Override 
    protected Class<? extends Piece> getDemotedClass() {
        return Bishop.class;
    }
}