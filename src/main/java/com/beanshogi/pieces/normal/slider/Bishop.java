package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

// Bishop (角)
public class Bishop extends Piece {
    private static final int[][] bishopDirs = {{-1,1},/* */{1,1},
                                                      /*BI*/
                                               {-1,-1},/**/{1,-1}};
    
    public Bishop(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(bishopDirs);
    }
}