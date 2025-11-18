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

    public Knight(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(knightOffsets, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        return getLegalMovesNormal(knightOffsets, true);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedKnight.class;
    }

    @Override
    public int value() {
        return 350;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new Knight(this.side, this.boardPosition, this.handPosition, board);
    }
}