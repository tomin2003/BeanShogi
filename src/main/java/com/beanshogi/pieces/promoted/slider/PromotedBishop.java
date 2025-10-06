package com.beanshogi.pieces.promoted.slider;

import java.util.*;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

// Promoted Bishop/Dragon Horse (馬)
public class PromotedBishop extends Piece {
    // The Promoted Rook behaves like a slider piece and a normal piece in one.
    private static final int[][] promotedBishopDirs = {{-1,1},/* */{1,1},
                                                               /*PB*/
                                                        {-1,-1},/**/{1,-1}};
                                                        
    private static final int[][] promotedBishopOffsets = {       {0,1},
                                                          {-1,0},/*PB*/{1,0},
                                                                {0,-1}};

    public PromotedBishop(Colors color, Position position, Board board) {
        super(color, position, board);
    }

    @Override
    public List<Position> getLegalMoves() {
        List<Position> moves = getLegalMovesSlider(promotedBishopDirs);
        moves.addAll(getLegalMovesNormal(promotedBishopOffsets));
        return moves;
    }
}