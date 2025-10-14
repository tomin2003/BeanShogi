package com.beanshogi.model;

import com.beanshogi.game.Player;
import com.beanshogi.util.Colors;
import com.beanshogi.util.Position;
import java.util.*;
import java.util.stream.Collectors;

public class Board {
    // A 2D 9x9 array for board management.
    private final Map<Position, Piece> board = new HashMap<>();

    // --Methods-- //
    public Piece getPiece(Position pos) {
        return board.get(pos);
    }

    public void setPiece(Position pos, Piece piece) {
        board.put(pos, piece);
        piece.setPosition(pos);
    }

    public void removePiece(Position pos) {
        board.remove(pos);
    }
    
    public boolean isEmptyAt(Position pos) {
        return !board.containsKey(pos);
    }
    
    public void clear() {
        board.clear();
    }

    // Get pieces via stream
    public Collection<Piece> getAllPieces() {
        return board.values();
    }

    // Get pieces via stream and also filter for color
    public Collection<Piece> getPiecesByColor(Colors color) {
        return board.values()
                    .stream()
                    .filter(p -> p.getColor() == color)
                    .collect(Collectors.toList());
    }
}