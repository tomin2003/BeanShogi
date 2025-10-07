package com.beanshogi.model;

import com.beanshogi.util.Position;

// A class representing the act of movement for a given piece (immutable)
public class Move {
    // Initiate the move from (x,y)
    private final Position from;
    // Initiate the move to (x,y)
    private final Position to;
    // The moved piece
    private final Piece movedPiece;
    // The captured piece (if any)
    private final Piece capturedPiece;
    // If this move will promote the piece
    private final boolean isPromotion;

    // A move that involves capture
    public Move(Position from, Position to, Piece movedPiece, Piece capturedPiece, boolean isPromotion) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isPromotion = isPromotion;
    }

    // A move that does not involve capture
    public Move(Position from, Position to, Piece movedPiece, boolean isPromotion) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = null;
        this.isPromotion = isPromotion;
    }

    public Position getFrom() { 
        return from;
    }

    public Position getTo() { 
        return to; 
    }

    public Piece getMovedPiece() { 
        return movedPiece;
    }

    public Piece getCapturedPiece() { 
        return capturedPiece;
    }
    
    public boolean isPromotion() { 
        return isPromotion; 
    }
}