package com.beanshogi.model;

import java.util.*;
import java.util.stream.Collectors;

import com.beanshogi.pieces.normal.King;
import com.beanshogi.util.*;

public class Board {
    // A map for handling pieces with their positions
    private final Map<Position, Piece> board = new HashMap<>();

    // A map for handling only kings for quick lookups
    private final Map<Colors, King> kings = new HashMap<>();

    // --Methods-- //
    public Piece getPiece(Position pos) {
        return board.get(pos);
    }

    public King getKing(Colors color) {
        return kings.get(color);
    }

    public void setPiece(Position pos, Piece piece) {
        board.put(pos, piece);
        if (piece != null) {
            piece.setPosition(pos);
        }
        if (piece instanceof King) {
            kings.put(piece.getColor(), (King)piece);
        }
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
        return board.values().stream()
                    .filter(p -> p.getColor() == color)
                    .collect(Collectors.toList());
    }

    @Override // Shallow copy!
    public Board clone() {
        Board clonedBoard = new Board();
        clonedBoard.board.putAll(this.board);
        clonedBoard.kings.putAll(this.kings);
        return clonedBoard;
    }

    // Generic search for piece type with stream
    public <T extends Piece> T findPiece(Class<T> pieceClass, Colors pieceColor) {
        return getPiecesByColor(pieceColor).stream()
                        .filter(pieceClass::isInstance)
                        .map(pieceClass::cast)
                        .findFirst()
                        .orElse(null);
    }
}