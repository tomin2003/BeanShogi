package com.beanshogi.util;

/**
 * Represents the two player sides in shogi (Black/White color equivalent in chess)
 * Sente - lit. "First hand" - the side that moves first. Perspective: the pieces facing upward.
 * Gote - lit. "Second hand" - the side that moves second. Perspective: the pieces facing downward.
 */
public enum Sides {
    SENTE(1),
    GOTE(-1);

    private final int alignFactor;
    
    private Sides(int alignFactor) { 
        this.alignFactor = alignFactor; 
    }

    public int getAlignFactor() { 
        return alignFactor; 
    }

    public Sides getOpposite() {
        return this == Sides.SENTE ? Sides.GOTE : Sides.SENTE;
    }
}