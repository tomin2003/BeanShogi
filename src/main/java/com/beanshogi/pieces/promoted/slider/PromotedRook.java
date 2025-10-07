package com.beanshogi.pieces.promoted.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.normal.slider.*;
import com.beanshogi.util.*;

// Promoted Rook/Dragon King (龍)
public class PromotedRook extends Piece implements Promotable {
    // Link to the Rook
    private static final Class<? extends Piece> demotedClass = Rook.class;
    
    // The Promoted Rook behaves like a slider piece and a normal piece.
    private static final int[][] promotedRookDirs = {          {0,1},
                                                        {-1,0},/*PB*/{1,0},
                                                               {0,-1}};

    private static final int[][] promotedRookOffsets = {{-1,1},/* */{1,1},
                                                               /*PB*/
                                                        {-1,-1},/**/{1,-1}};

    public PromotedRook(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        List<Position> moves = getLegalMovesSlider(promotedRookDirs);
        moves.addAll(getLegalMovesNormal(promotedRookOffsets));
        return moves;
    }

    @Override
    public Piece promote() {
        return this; // already promoted
    }

    @Override
    public Piece demote() {
        try {
            return demotedClass.getConstructor(Colors.class, Position.class, Board.class)
                                 .newInstance(color, position, board);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPromoted() {
        return true; 
    }
}