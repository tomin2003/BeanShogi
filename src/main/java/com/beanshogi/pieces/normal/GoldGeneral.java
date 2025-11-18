package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

// Gold General (金)
public class GoldGeneral extends Piece {
    // The transformation matrix for the current X and Y 
    private static final int[][] goldOffsets = {{-1,1},{0,1},{1,1},
                                                {-1,0},/*GG*/{1,0},
                                                       {0,-1}};

    public GoldGeneral(Sides side, Position boardPosition, Position handPosition, Board board) {
        super(side, boardPosition, handPosition, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(goldOffsets, false);
    }

    @Override
    public List<Position> getAttackMoves() {
        return getLegalMovesNormal(goldOffsets, true);
    }

    @Override
    public int value() {
        return 600;
    }

    @Override
    public Piece cloneForBoard(Board board) {
        return new GoldGeneral(this.side, this.boardPosition, this.handPosition, board);
    }
}