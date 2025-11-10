package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.pieces.UnPromotedPiece;
import com.beanshogi.pieces.promoted.*;
import com.beanshogi.util.*;

// Silver General (銀)
public class SilverGeneral extends UnPromotedPiece {
    // Link the promoted version of Silver General
    public static final Class<? extends Piece> promotedClass = PromotedSilverGeneral.class;

    // The transformation matrix for the current X and Y 
    private static final int[][] silverOffsets = {{-1,1},{0,1},{1,1},
                                                         /*SG*/
                                                  {-1,-1},     {1,-1}};

    public SilverGeneral(Sides side, Position position, Board board) {
        super(side, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(silverOffsets);
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
        return new SilverGeneral(this.side, this.position, board);
    }
}