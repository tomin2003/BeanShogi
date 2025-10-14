package com.beanshogi.model;

import com.beanshogi.util.Colors;
import com.beanshogi.util.Position;
import java.util.*;
import java.util.stream.Collectors;

public class Board {
    // A 2D 9x9 array for board management.
    private final Piece[][] board = new Piece[9][9];

    // --Methods-- //
    public Piece getPiece(Position pos) {
        return board[pos.getX()][pos.getY()];
    }

    public void setPiece(Position pos, Piece piece) {
        board[pos.getX()][pos.getY()] = piece;
        if (piece != null) piece.setPosition(pos);
    }

    public void removePiece(Position pos) {
        board[pos.getX()][pos.getY()] = null;
    }
    
    public boolean isEmpty(Position pos) {
        return board[pos.getX()][pos.getY()] == null;
    }
    
    // Get pieces via stream
    public Collection<Piece> getAllPieces() {
        return Arrays.stream(board)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Get pieces via stream and also filter for color
    public Collection<Piece> getPiecesByColor(Colors color) {
        return Arrays.stream(board)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)  
                .filter(p -> p.getColor() == color)
                .collect(Collectors.toList());
    }

    public void clear() {
        for (Piece[] row : board) {
            Arrays.fill(row, null);
        }
    }
}