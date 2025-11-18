package com.beanshogi.pieces.promoted.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.PromotedPiece;
import com.beanshogi.pieces.normal.slider.*;
import com.beanshogi.util.*;

// Promoted Rook/Dragon King (龍)
public class PromotedRook extends PromotedPiece {
    
    // The Promoted Rook behaves like a slider piece and a normal piece.
    private static final int[][] promotedRookDirs = {          {0,1},
                                                        {-1,0},/*PB*/{1,0},
                                                               {0,-1}};

    private static final int[][] promotedRookOffsets = {{-1,1},/* */{1,1},
                                                               /*PB*/
                                                        {-1,-1},/**/{1,-1}};

    public PromotedRook(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        List<Position> moves = getLegalMovesSlider(promotedRookDirs, false);
        moves.addAll(getLegalMovesNormal(promotedRookOffsets, false));
        return moves;
    }

    @Override
    public List<Position> getAttackMoves() {
        List<Position> moves = getLegalMovesSlider(promotedRookDirs, true);
        moves.addAll(getLegalMovesNormal(promotedRookOffsets, true));
        return moves;
    }

    @Override
    protected Class<? extends Piece> getDemotedClass() {
        return Rook.class;
    }

    @Override
    public int value() {
        return 1500;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new PromotedRook(this.side, this.boardPosition, this.handPosition, board);
    }
}