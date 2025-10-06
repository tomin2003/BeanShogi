package com.beanshogi.pieces.normal;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

// Silver General (銀)
public class SilverGeneral extends Piece {
    // The transformation matrix for the current X and Y 
    private static final int[][] silverOffsets = {{-1,1},{0,1},{1,1},
                                                         /*SG*/
                                                  {-1,-1},     {1,-1}};

    public SilverGeneral(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesNormal(silverOffsets);
    }
}