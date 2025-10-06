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
                                            
    public King(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(kingOffsets);
    }
}