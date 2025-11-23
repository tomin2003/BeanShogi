package com.beanshogi.core.pieces;

import java.util.*;

import com.beanshogi.core.board.Board;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.pieces.normal.King;
import com.beanshogi.core.util.*;

/**
 * Represents a Piece base class, holds the properties that are shared between every piece.
 * @param side determines which player owns the piece and where the piece is aligned (mutable for captures)
 * @param boardPosition the position of the piece on the board (null if piece in hand).
 * @param handPosition the position of the piece in the hand (null if piece on board).
 * @param board the board the piece belongs to
 */
public abstract class Piece {
    protected Sides side;
    protected Position boardPosition;
    protected Position handPosition;
    protected transient Board board;  // Marked as transient to prevent circular reference in JSON

    // Non promotable pieces fall back to returning themselves.
    public Piece promote() { return this; }
    public Piece demote() { return this; }

    public Piece(Sides side, Position boardPosition, Position handPosition, Board board) {
        this.side = side;
        this.boardPosition = boardPosition;         
        this.handPosition = handPosition;                                           
        this.board = board;
    }                   

    public Sides getSide() {
        return side;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void changeSide() {
        side = side.getOpposite();
    }

    public Position getBoardPosition() {
        return boardPosition;
    }

    public void setBoardPosition(Position pos) {
        this.boardPosition = pos;
    }

    public Position getHandPosition() {
        return handPosition;
    }

    public void setHandPosition(Position pos) {
        this.handPosition = pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Piece)) {
            return false;
        }
        Piece piece = (Piece) o;
        return this.getClass() == piece.getClass() && this.side == piece.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), side);
    }

    /**
     * Checks if piece may collide with a piece of same side.
     * @param piece the piece that the caller piece get's checked against.
     * @return true if collision occurs, false otherwise.
     */
    private boolean selfCollide(Piece piece) {
        // If the board isn't empty, check if the side of the piece at a given position matches -> collision
        return piece != null && (piece.getSide() == this.getSide());
    }

    /**
     * Checks if piece may collide with an opponent piece.
     * @param piece the piece that the caller piece get's checked against.
     * @return true if collision occurs, false otherwise.
     */
    private boolean opponentCollide(Piece piece) {
        // If the board isn't empty, check if the side of the piece at a given position differs -> collision
        return piece != null && (piece.getSide() != this.getSide());
    }

    /**
     * Calculate all pseudo-legal moves for sliding pieces.
     * @param dirs Transformation matrix for defining move directionality.
     * @param isKingInclude Flag for including king - used for defining attack lines
     * @return List of pseudo-legal moves
     */
    protected List<Position> getLegalMovesSlider(int[][] dirs, boolean isKingInclude) {
        List<Position> legalMoves = new ArrayList<>();
        for (int[] dir : dirs) {
            int nx = boardPosition.x;
            int ny = boardPosition.y;
            while (true) {
                nx += dir[0];
                ny += dir[1] * side.getAlignFactor();
                Position nPos = new Position(nx, ny);
                if (!nPos.inBounds()) {
                    break;     // Stop if out of board bounds
                }
                // Get the piece at target for comparing
                Piece target = board.getPiece(nPos);

                if (isKingInclude && target != null && target instanceof King) {
                    legalMoves.add(nPos);
                    return legalMoves; // early exit, king found
                }

                if (!isKingInclude && target != null && target instanceof King) {
                    break; // stop path for normal moves
                }

                if (selfCollide(target)) {
                    break;    // Stop if own piece
                }
                legalMoves.add(nPos);

                // The first opponent piece in line can be added as a legal move, qualifies as a capture
                if (opponentCollide(target)) {
                    break; // Stop if opponent piece
                }
            }
        }
        return legalMoves;
    }

    /**
     * Calculate all pseudo-legal moves for normal, non sliding pieces.
     * @param offsets Transformation matrix for defining move offset radius.
     * @param isKingInclude Flag for including king - used for defining attack lines
     * @return List of pseudo-legal moves
     */
    protected List<Position> getLegalMovesNormal(int[][] offsets, boolean isKingInclude) {
        int x = boardPosition.x;
        int y = boardPosition.y;
        List<Position> legalMoves = new ArrayList<>();
        for (int[] offset : offsets) {
            Position nPos = new Position(x + offset[0], y + offset[1] * side.getAlignFactor());
            if (!nPos.inBounds()) {
                continue;
            }
            // Get target piece for comparison
            Piece target = board.getPiece(nPos);
            if (selfCollide(target)) {
                continue;
            }
            if (isKingInclude && target != null && target instanceof King) {
                legalMoves.add(nPos);
                return legalMoves; // early exit, king found
            }

            if (!isKingInclude && target != null && target instanceof King) {
                continue; // Continue on the other offsets
            }

            legalMoves.add(nPos);
            // opponentCollide check not needed, every normal piece can move into opponent's square to capture
        }
        return legalMoves;
    }

    /**
     * Determine if a piece will change when promoted
     * @return can or cannot promote
     */
    public boolean canPromote() {
        return !this.promote().equals(this);
    }

    /**
     * Simulate a piece's state after desired movement and determine if it must promote (when it's out of moves)
     * @param from Starting position
     * @param to Desired position for evaluation
     * @return Force promote or not
     */
    public boolean shouldPromote(Position from, Position to) {
        boolean shouldPromote = false;
        setBoardPosition(to);
        shouldPromote = getLegalMoves().isEmpty();
        setBoardPosition(from);
        return shouldPromote;
    }   

    public Piece clone() {
        return cloneForBoard(board);
    }

    /* Abstract functions */

    /**
     * Public legal move interface, does not include king in evaluation.
     * @return list of legal moves
     */
    public abstract List<Position> getLegalMoves();

    /**
     * Public attack move interface, may only contain move lines that attack the king (due to early return).
     * @return list of attack moves
     */
    public abstract List<Position> getAttackMoves();

    /**
     * Individual values for pieces - used for AI
     * @return value of piece
     */
    public abstract int value();
 
    /**
     * Clones individual pieces so that the copy of board is a deep copy
     * @param board board for the piece is cloned
     * @return cloned piece
     */
    public abstract Piece cloneForBoard(Board board);
}