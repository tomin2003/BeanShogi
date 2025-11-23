package com.beanshogi.core.pieces.normal;

import java.util.*;

import com.beanshogi.core.board.Board;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.pieces.UnPromotedPiece;
import com.beanshogi.core.pieces.promoted.*;
import com.beanshogi.core.util.*;


// Pawn (歩)
public class Pawn extends UnPromotedPiece {
    
    // The pawn only moves up relative to position.
    private static final int[][] pawnOffset = {{0,1}};
                                               /*PA*/

    public Pawn(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(pawnOffset, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        return getLegalMovesNormal(pawnOffset, true);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedPawn.class;
    }

    @Override
    public int value() {
        return 100;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new Pawn(this.side, this.boardPosition, this.handPosition, board);
    }
}