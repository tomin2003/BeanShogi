package com.beanshogi.core.pieces.promoted.slider;

import java.util.*;

import com.beanshogi.core.pieces.PromotedPiece;
import com.beanshogi.core.pieces.normal.slider.*;
import com.beanshogi.core.util.*;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.board.Board;
import com.beanshogi.core.pieces.Piece;

// Promoted Bishop/Dragon Horse (馬)
public class PromotedBishop extends PromotedPiece {

    // The Promoted Rook behaves like a slider piece and a normal piece.
    private static final int[][] promotedBishopDirs = {{-1,1},/* */{1,1},
                                                              /*PB*/
                                                       {-1,-1},/**/{1,-1}};
                                                        
    private static final int[][] promotedBishopOffsets = {       {0,1},
                                                          {-1,0},/*PB*/{1,0},
                                                                {0,-1}};

    public PromotedBishop(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        List<Position> moves = getLegalMovesSlider(promotedBishopDirs, false);
        moves.addAll(getLegalMovesNormal(promotedBishopOffsets, false));
        return moves;
    }

    @Override
    public List<Position> getAttackMoves() {
        List<Position> moves = getLegalMovesSlider(promotedBishopDirs, true);
        moves.addAll(getLegalMovesNormal(promotedBishopOffsets, true));
        return moves;
    }


    @Override 
    protected Class<? extends Piece> getDemotedClass() {
        return Bishop.class;
    }

    @Override
    public int value() {
        return 1200;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new PromotedBishop(this.side, this.boardPosition, this.handPosition, board);
    }
}