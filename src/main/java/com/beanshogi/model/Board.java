package com.beanshogi.model;

import com.beanshogi.util.Position;
import java.util.*;

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
    
    public Collection<Piece> getAllPieces() {
        Collection<Piece> allPieces = new ArrayList<>();
        for (Piece[] rowPieces : board) {
            for (Piece columnPieces : rowPieces) {
                if (columnPieces != null) {
                    allPieces.add(columnPieces);
                }
            }
        }
        return allPieces;
    }

    public void clear() {
        for (Piece[] row : board) {
            Arrays.fill(row, null);
        }
    }
}