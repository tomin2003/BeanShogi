package com.beanshogi.model;

import java.util.*;
import com.beanshogi.pieces.normal.King;
import com.beanshogi.util.*;

/**
 * Represents a Piece base class, holds the properties that are shared between every piece.
 * @param side determines which player owns the piece and where the piece is aligned (mutable for captures)
 * @param position the position of the piece on the board currently
 * @param board the board the piece belongs to
 */
public abstract class Piece {
    protected Sides side;
    protected Position position;
    protected Board board;

    // Non promotable pieces handle promotions like so
    public Piece promote() { return this; }
    public Piece demote() { return this; }

    // --Constructors-- //
    public Piece(Sides side, Position position, Board board) {
        this.side = side;
        this.position = position;                                               
        this.board = board;
    }                   

    // --Methods-- // 
    public Sides getSide() {
        return side;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void changeSide() {
        side = side.getOpposite();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position pos) {
        this.position = pos;
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

    private boolean selfCollide(Piece piece) {
        // If the board isn't empty, check if the side of the piece at a given position matches -> collision
        return piece != null && (piece.getSide() == this.getSide());
    }

    private boolean opponentCollide(Piece piece) {
        // If the board isn't empty, check if the side of the piece at a given position differs -> collision
        return piece != null && (piece.getSide() != this.getSide());
    }

    protected List<Position> getLegalMovesSlider(int[][] dirs, boolean isKingInclude) {
        List<Position> legalMoves = new ArrayList<>();
        for (int[] dir : dirs) {
            int nx = position.x;
            int ny = position.y;
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

    protected List<Position> getLegalMovesNormal(int[][] offsets, boolean isKingInclude) {
        int x = position.x;
        int y = position.y;
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

    public boolean canPromote() {
        return this.promote() != this;
    }

    /**
     * Simulate a piece's state after desired movement and determine if it must promote (when it's out of moves)
     * @param from Starting position
     * @param to Desired position for evaluation
     * @return Force promote or not
     */
    public boolean shouldPromote(Position from, Position to) {
        boolean shouldPromote = false;
        setPosition(to);
        shouldPromote = getLegalMoves().isEmpty();
        setPosition(from);
        return shouldPromote;
    }   

    /**
     * Public legal move interface, does not include king in evaluation
     * @return
     */
    public abstract List<Position> getLegalMoves();

    /**
     * Public attack move interface, may only contain moves that attack the king
     * @return 
     */
    public abstract List<Position> getAttackMoves();

    /**
     * Individual values for pieces - used for AI
     * @return
     */
    public abstract int value();
 
    /**
     * Clones individual pieces so that the copy of board is a deep copy
     * @param board
     * @return
     */
    public abstract Piece cloneForBoard(Board board);
}