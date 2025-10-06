package com.beanshogi.model;

import com.beanshogi.util.Position;
import java.util.*;

public class Board {
    // A Position-Piece pairing for convenient management of squares.
    private Map<Position, Piece> squares;

    // --Constructors-- //
    public Board() {
        this.squares = new HashMap<>();
    }

    // --Methods-- //
    public Piece getPiece(Position pos) {
        return squares.get(pos);
    }
    
    public boolean isEmpty(Position pos) {
        return !squares.containsKey(pos);
    }

    public Collection<Piece> getAllPieces() {
        return squares.values();
    }

    public void setPiece(Position pos, Piece piece) {
        if (piece != null) {
            piece.setPosition(pos);
            squares.put(pos, piece);
        } else {
            squares.remove(pos);
        }
    }

    public void clear() {
        squares.clear();
    }
}