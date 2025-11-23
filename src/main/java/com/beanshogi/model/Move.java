package com.beanshogi.model;

import java.util.Objects;
import com.beanshogi.game.*;
import com.beanshogi.util.Position;

/**
 * Represents an act of movement.
 * @param player the player who initiated the move
 * @param from the square from which the move was made
 * @param to the square to which the move is made
 * @param movedPiece the piece that was moved
 * @param capturedPiece the piece that was caputured as a result (null if didn't capture any)
 * @param isPromotion flag, true when the move promoted the piece
 */
public class Move {
    private final Player player;
    private final Position from;
    private final Position to;
    private Piece movedPiece;
    private Piece capturedPiece;
    private final boolean isPromotion;
    private final boolean isDrop;
    private boolean capturedWasPromoted;

    public Move(Player player, Position from, Position to, Piece movedPiece, Piece capturedPiece, boolean isPromotion, boolean isDrop) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isPromotion = isPromotion;
        this.isDrop = isDrop;
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

    public void setCapturedPiece(Piece capturedPiece) { 
        this.capturedPiece = capturedPiece;
    }

    public void setCapturedWasPromoted(boolean wasPromoted) {
        this.capturedWasPromoted = wasPromoted;
    }

    public boolean wasCapturedPromoted() {
        return capturedWasPromoted;
    }
    
    public boolean isPromotion() { 
        return isPromotion; 
    }

    public boolean isDrop() { 
        return isDrop; 
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Move)) return false;
        Move other = (Move) obj;
        return isDrop == other.isDrop &&
               Objects.equals(from, other.from) &&
               Objects.equals(to, other.to) &&
               movedPiece.getClass() == other.movedPiece.getClass() &&
               movedPiece.getSide() == other.movedPiece.getSide();
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, isDrop);
    }
}