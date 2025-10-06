package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

// Rook (飛)
public class Rook extends Piece {
    private static final int[][] rookDirs = {         {0,1},
                                               {-1,0},/*RO*/{1,0},
                                                     {-1,-1}};

    public Rook(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(rookDirs);
    }
}