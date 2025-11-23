package com.beanshogi.core.pieces.normal;

import java.util.*;

import com.beanshogi.core.pieces.UnPromotedPiece;
import com.beanshogi.core.pieces.promoted.*;
import com.beanshogi.core.util.*;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.board.Board;

// Silver General (銀)
public class SilverGeneral extends UnPromotedPiece {
    // Link the promoted version of Silver General
    public static final Class<? extends Piece> promotedClass = PromotedSilverGeneral.class;

    // The transformation matrix for the current X and Y 
    private static final int[][] silverOffsets = {{-1,1},{0,1},{1,1},
                                                         /*SG*/
                                                  {-1,-1},     {1,-1}};

    public SilverGeneral(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(silverOffsets, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        return getLegalMovesNormal(silverOffsets, true);
    }


    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedSilverGeneral.class;
    }

    @Override
    public int value() {
        return 500;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new SilverGeneral(this.side, this.boardPosition, this.handPosition, board);
    }
}