package com.beanshogi.pieces.normal.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

// Lance (香)
public class Lance extends Piece {
    // The lance only moves up n times relative to position.
    private static final int[][] lanceDirs = {{0,1}};
                                              /*LA*/

    public Lance(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        return getLegalMovesSlider(lanceDirs);
    }
}