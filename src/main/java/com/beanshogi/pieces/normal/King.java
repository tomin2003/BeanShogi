package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

// King (王/玉)
public class King extends Piece {
    // The transformation matrix for the current X and Y 
    private static final int[][] kingOffsets = {{-1,1},{0,1},{1,1},
                                                {-1,0},/*KI*/{1,0},
                                               {-1,-1},{0,-1},{1,-1}};
                                            
    public King(Sides side, Position position, Board board) {
        super(side, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(kingOffsets, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        // Not relevant for king as it can't give check - returns regular legal moves
        return getLegalMovesNormal(kingOffsets, false);
    }

    @Override
    public int value() {
        return 20000;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new King(this.side, this.position, board);
    }
}