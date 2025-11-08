package com.beanshogi.model;

import java.util.*;

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

    private boolean selfCollide(Position pos) {
        // If the board isn't empty, check if the side of the piece at a given position matches -> collision
        return !board.isEmptyAt(pos) && board.getPiece(pos).getSide() == this.getSide();
    }

    private boolean opponentCollide(Position pos) {
        // If the board isn't empty, check if the side of the piece at a given differs -> collision
        return !board.isEmptyAt(pos) && board.getPiece(pos).getSide() != this.getSide();
    }

    public Set<Position> getLegalMovesSlider(int[][] dirs) {
        Set<Position> legalMoves = new HashSet<>();
        for (int[] dir : dirs) {
            int nx = position.getX();
            int ny = position.getY();
            while (true) {
                nx += dir[0];
                ny += dir[1] * side.getAlignFactor();
                Position nPos = new Position(nx, ny);

                if (!nPos.inBounds()) {
                    break;     // Stop if out of board bounds
                }
                if (selfCollide(nPos)) {
                    break;    // Stop if own piece
                }

                legalMoves.add(nPos);

                if (opponentCollide(nPos)) {
                    break; // Stop if opponent piece
                }
            }
        }
        return legalMoves;
    }

    public Set<Position> getLegalMovesNormal(int[][] offsets) {
        int x = position.getX();
        int y = position.getY();
        Set<Position> legalMoves = new HashSet<>();
        for (int[] offset : offsets) {
            Position nPos = new Position(x + offset[0], y + offset[1] * side.getAlignFactor());
            if (nPos.inBounds()) {
                if (!selfCollide(nPos)) {
                    legalMoves.add(nPos);
                }
            }
        }
        return legalMoves;
    }

    private boolean isInPromotionZone() {
        // For sente, the upper three rows, for gote, the lower three rows
        return side == Sides.SENTE ? getPosition().getY() <= 2 : getPosition().getY() >= 6;
    }

    public boolean canPromote() {
        return this.promote() != this && isInPromotionZone();
    }

    public abstract Set<Position> getLegalMoves();

    public abstract int value();

    public abstract Piece cloneForBoard(Board board);
}