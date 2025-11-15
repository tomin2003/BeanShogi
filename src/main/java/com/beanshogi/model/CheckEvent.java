package com.beanshogi.model;

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
