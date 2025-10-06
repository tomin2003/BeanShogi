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

    public GoldGeneral(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(goldOffsets);
    }
}