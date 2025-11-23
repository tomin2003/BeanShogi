package com.beanshogi.core.game;

import com.beanshogi.core.pieces.Piece;

/**
 * Represents a check event where a piece is attacking a king.
 */
public class CheckEvent {
    private final Piece attacker;
    private final Piece king;

    public CheckEvent(Piece attacker, Piece king) {
        this.attacker = attacker;
        this.king = king;
    }

    public Piece getAttacker() { 
        return attacker; 
    }

    public Piece getKing() {
         return king; 
    }
}
