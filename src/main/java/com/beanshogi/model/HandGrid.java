package com.beanshogi.model;

import java.util.*;
import java.util.stream.Collectors;

import com.beanshogi.util.Exceptions;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

/**
 * Represents a 4x5 grid for storing captured pieces (hand pieces).
 * Provides both a flat list interface and operations for abstracting underlying grid structure.
 */
public class HandGrid {
    private static final int ROWS = 4;
    private static final int COLS = 5;
    
    private final Piece[][] grid = new Piece[ROWS][COLS];

    /**
     * Add a piece to the next available position in the grid.
     * Sets the piece's hand position and clears its board position.
     * @param piece the piece to add
     */
    public void addPiece(Piece piece) {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (grid[x][y] == null) {
                    grid[x][y] = piece;
                    piece.setHandPosition(new Position(y, x));
                    piece.setBoardPosition(null);
                    return;
                }
            }
        }
        // Should never get called, as the hand can fit up to 20 pieces.
        throw new Exceptions.HandFullException("No more pieces can be added to hand");
    }

    /**
     * Remove a piece from the hand (from any position in the grid)
     * @param piece the piece to remove
     * @return true if piece was found and removed, false otherwise
     */
    public boolean removePiece(Piece piece) {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (grid[x][y] == piece) {
                    grid[x][y] = null;
                    piece.setHandPosition(null);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove a piece from a specific grid position (for rendering/UI purposes)
     * @param x the x index
     * @param y the yumn index
     * @return the removed piece, or null if position was empty
     */
    public Piece removePieceAt(Position pos) {
        if (isValidPosition(pos)) {
            Piece piece = grid[pos.x][pos.y];
            grid[pos.x][pos.y] = null;
            return piece;
        }
        return null;
    }

    /**
     * Get a piece at a specific grid position (for rendering/UI purposes)
     * @param x the x index
     * @param y the y index
     * @return the piece at that position, or null if empty
     */
    public Piece getPieceAt(Position pos) {
        if (isValidPosition(pos)) {
            return grid[pos.x][pos.y];
        }
        return null;
    }

    /**
     * Validate grid coordinates
     * @param x the x index
     * @param y the y index
     * @return true if coordinates are within bounds
     */
    private boolean isValidPosition(Position position) {
        return position.x >= 0 && position.x < ROWS && position.y >= 0 && position.y < COLS;
    }

    /**
     * Check if a grid position is valid and contains a piece
     * @param x the x index
     * @param y the y index
     * @return true if position is valid and contains a piece
     */
    public boolean hasPiece(Position pos) {
        return isValidPosition(pos) && grid[pos.x][pos.y] != null;
    }

    /**
     * Get all pieces in the hand as a list
     * @return list of all non-null pieces
     */
    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (Piece[] row : grid) {
            for (Piece piece : row) {  
                if (piece == null) {
                    continue;
                }
                pieces.add(piece);
            }
        }
        return pieces;
    }

    /**
     * Get pieces in hand via stream and also filter for side
     * @param side 
     * @return
     */
    public Collection<Piece> getPiecesOfSide(Sides side) {
        return getAllPieces().stream()
                    .filter(p -> p.getSide() == side)
                    .collect(Collectors.toList());
    }

    /**
     * Check if a piece is in the hand
     * @param piece the piece to check
     * @return true if piece is in hand
     */
    public boolean contains(Piece piece) {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (grid[x][y] == piece) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Clear all pieces from the grid
     */
    public void clear() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                grid[x][y] = null;
            }
        }
    }

    /**
     * Check if the grid is empty
     * @return true if no pieces in grid
     */
    public boolean isEmpty() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (grid[x][y] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Create a deep copy of this hand grid
     * @return a new HandGrid with cloned pieces
     */
    public HandGrid copy(Board board) {
        HandGrid copy = new HandGrid();
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (grid[x][y] != null) {
                    copy.grid[x][y] = grid[x][y].cloneForBoard(board);
                }
            }
        }
        return copy;
    }

    /**
     * Find and remove a piece matching the class and side of the given piece.
     * Used for AI to select a piece to drop from hand.
     * @param templatePiece a piece to match class and side
     * @return the first matching piece found, or null if none found
     */
    public Piece findAndRemoveMatching(Piece templatePiece) {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                Piece candidate = grid[x][y];
                if (candidate != null && 
                    candidate.getClass() == templatePiece.getClass() && 
                    candidate.getSide() == templatePiece.getSide()) {
                    grid[x][y] = null;
                    return candidate;
                }
            }
        }
        return null;
    }
}
