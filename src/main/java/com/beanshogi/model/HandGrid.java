package com.beanshogi.model;

import java.util.*;

/**
 * Represents a 4x5 grid for storing captured pieces (hand pieces).
 * Provides both a flat list interface (for external use) and grid-based access (for rendering).
 */
public class HandGrid {
    private static final int ROWS = 4;
    private static final int COLS = 5;
    
    private final Piece[][] grid = new Piece[ROWS][COLS];

    /**
     * Add a piece to the next available position in the grid
     * @param piece the piece to add
     * @return true if piece was added, false if grid is full
     */
    public boolean addPiece(Piece piece) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == null) {
                    grid[row][col] = piece;
                    return true;
                }
            }
        }
        return false; // Grid is full
    }

    /**
     * Remove a piece from the hand (from any position in the grid)
     * @param piece the piece to remove
     * @return true if piece was found and removed, false otherwise
     */
    public boolean removePiece(Piece piece) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == piece) {
                    grid[row][col] = null;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove a piece from a specific grid position (for rendering/UI purposes)
     * @param row the row index
     * @param col the column index
     * @return the removed piece, or null if position was empty
     */
    public Piece removePieceAt(int row, int col) {
        if (isValidPosition(row, col)) {
            Piece piece = grid[row][col];
            grid[row][col] = null;
            return piece;
        }
        return null;
    }

    /**
     * Get a piece at a specific grid position (for rendering/UI purposes)
     * @param row the row index
     * @param col the column index
     * @return the piece at that position, or null if empty
     */
    public Piece getPieceAt(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        return null;
    }

    /**
     * Check if a grid position is valid and contains a piece
     * @param row the row index
     * @param col the column index
     * @return true if position is valid and contains a piece
     */
    public boolean hasPiece(int row, int col) {
        return isValidPosition(row, col) && grid[row][col] != null;
    }

    /**
     * Validate grid coordinates
     * @param row the row index
     * @param col the column index
     * @return true if coordinates are within bounds
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    /**
     * Get all pieces in the hand as a flat list (external interface)
     * @return unmodifiable list of all non-null pieces
     */
    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != null) {
                    pieces.add(grid[row][col]);
                }
            }
        }
        return Collections.unmodifiableList(pieces);
    }

    /**
     * Check if a piece is in the hand
     * @param piece the piece to check
     * @return true if piece is in hand
     */
    public boolean contains(Piece piece) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == piece) {
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
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = null;
            }
        }
    }

    /**
     * Check if the grid is empty
     * @return true if no pieces in grid
     */
    public boolean isEmpty() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get grid dimensions (for rendering purposes)
     * @return array [rows, cols]
     */
    public int[] getDimensions() {
        return new int[]{ROWS, COLS};
    }

    /**
     * Create a deep copy of this hand grid
     * @return a new HandGrid with cloned pieces
     */
    public HandGrid copy(Board board) {
        HandGrid copy = new HandGrid();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != null) {
                    copy.grid[row][col] = grid[row][col].cloneForBoard(board);
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
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Piece candidate = grid[row][col];
                if (candidate != null && 
                    candidate.getClass() == templatePiece.getClass() && 
                    candidate.getSide() == templatePiece.getSide()) {
                    grid[row][col] = null;
                    return candidate;
                }
            }
        }
        return null;
    }
}
