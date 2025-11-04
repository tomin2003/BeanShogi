package com.beanshogi.model;

import com.beanshogi.game.*;
import com.beanshogi.util.Position;

/**
 * Represents an act of movement, immutable
 * @param player the player who initiated the move
 * @param from the square from which the move was made
 * @param to the square to which the move is made
 * @param movedPiece the piece that was moved
 * @param capturedPiece the piece that was caputured as a result (null if didn't capture any)
 * @param isPromotion flag, true when the move promoted the piece
 */
public class Move {
    // The player who moved the piece
    private final Player player;
    // Move from (x,y)
    private final Position from;
    // Move to (x,y)
    private final Position to;
    // The moved piece
    private final Piece movedPiece;
    // The captured piece (if any)
    private final Piece capturedPiece;
    // If this move will promote the piece
    private final boolean isPromotion;

    // Constructor for moves
    public Move(Player player, Position from, Position to, Piece movedPiece, Piece capturedPiece, boolean isPromotion) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isPromotion = isPromotion;
    }

    public Player getPlayer() { 
        return player;
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