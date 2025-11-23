package com.beanshogi.game;

import com.beanshogi.model.Piece;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

import java.util.List;

/**
 * Manages the selection state during gameplay, tracking selected pieces
 * from both the board and hand panels.
 */
public class SelectionState {
    private Piece selectedBoardPiece = null;
    private Piece selectedHandPiece = null;
    private Sides selectedHandSide = null;
    private List<Position> selectedLegalMoves = null;

    /**
     * Gets the currently selected piece from the board.
     * @return the selected board piece, or null if none selected
     */
    public Piece getSelectedBoardPiece() {
        return selectedBoardPiece;
    }

    /**
     * Sets the selected piece from the board.
     * @param piece the piece to select
     */
    public void setSelectedBoardPiece(Piece piece) {
        this.selectedBoardPiece = piece;
    }

    /**
     * Gets the currently selected piece from a hand panel.
     * @return the selected hand piece, or null if none selected
     */
    public Piece getSelectedHandPiece() {
        return selectedHandPiece;
    }

    /**
     * Sets the selected piece from a hand panel.
     * @param piece the piece to select
     */
    public void setSelectedHandPiece(Piece piece) {
        this.selectedHandPiece = piece;
    }

    /**
     * Gets the side of the hand from which a piece is selected.
     * @return the side of the selected hand, or null if no hand piece selected
     */
    public Sides getSelectedHandSide() {
        return selectedHandSide;
    }

    /**
     * Sets the side of the hand from which a piece is selected.
     * @param side the side to set
     */
    public void setSelectedHandSide(Sides side) {
        this.selectedHandSide = side;
    }

    /**
     * Gets the list of legal move destinations for the selected piece.
     * @return list of legal positions, or null if no piece selected
     */
    public List<Position> getSelectedLegalMoves() {
        return selectedLegalMoves;
    }

    /**
     * Sets the list of legal move destinations.
     * @param moves the list of legal positions
     */
    public void setSelectedLegalMoves(List<Position> moves) {
        this.selectedLegalMoves = moves;
    }

    /**
     * Checks if a board piece is currently selected.
     * @return true if a board piece is selected, false otherwise
     */
    public boolean hasBoardPieceSelected() {
        return selectedBoardPiece != null;
    }

    /**
     * Checks if a hand piece is currently selected.
     * @return true if a hand piece is selected, false otherwise
     */
    public boolean hasHandPieceSelected() {
        return selectedHandPiece != null && selectedHandSide != null;
    }

    /**
     * Checks if any piece is currently selected.
     * @return true if any piece is selected, false otherwise
     */
    public boolean hasAnySelection() {
        return hasBoardPieceSelected() || hasHandPieceSelected();
    }

    /**
     * Selects a board piece with its legal moves.
     * @param piece the piece to select
     * @param legalMoves the legal moves for this piece
     */
    public void selectBoardPiece(Piece piece, List<Position> legalMoves) {
        clear();
        this.selectedBoardPiece = piece;
        this.selectedLegalMoves = legalMoves;
    }

    /**
     * Selects a hand piece with its legal drop points.
     * @param piece the piece to select
     * @param side the side of the hand
     * @param legalDrops the legal drop positions
     */
    public void selectHandPiece(Piece piece, Sides side, List<Position> legalDrops) {
        clear();
        this.selectedHandPiece = piece;
        this.selectedHandSide = side;
        this.selectedLegalMoves = legalDrops;
    }

    /**
     * Clears all selection state.
     */
    public void clear() {
        selectedBoardPiece = null;
        selectedHandPiece = null;
        selectedHandSide = null;
        selectedLegalMoves = null;
    }
}
