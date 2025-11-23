package com.beanshogi.core.pieces;

/**
 * Interface defining the promotion linkage of each piece
 */
public interface Promotable {
    Piece promote();
    Piece demote();
    boolean isPromoted();
}
