package com.beanshogi.model;

public interface Promotable {
    Piece promote();
    Piece demote();
    boolean isPromoted();
}
