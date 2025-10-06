package com.beanshogi.model;

import com.beanshogi.util.Position;

public class Move {
    // Initiate the move from (x,y)
    private Position from;
    // Initiate the move to (x,y)
    private Position to;
    // The moved piece
    private Piece piece;
    // If this move will promote the piece
    private boolean isPromoted;
    // If it has been dropped already
    private boolean drop;
}