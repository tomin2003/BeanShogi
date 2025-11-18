package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.UnPromotedPiece;
import com.beanshogi.pieces.promoted.*;
import com.beanshogi.util.*;

// Lance (香)
public class Lance extends UnPromotedPiece {

    // The Lance only moves up n times relative to position.
    private static final int[][] lanceDirs = {{0,1}};
                                              /*LA*/

    public Lance(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(lanceDirs, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        return getLegalMovesSlider(lanceDirs, true);
    }

    @Override
    protected Class<? extends Piece> getPromotedClass() {
        return PromotedLance.class;
    }

    @Override
    public int value() {
        return 300;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new Lance(this.side, this.boardPosition, this.handPosition, board);
    }
}