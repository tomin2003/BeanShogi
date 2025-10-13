package com.beanshogi.model;

import java.util.*;
import com.beanshogi.util.*;

public abstract class Piece {
    // The color of a piece is mutable, so each player can capture pieces;
    protected Colors color;
    // The board index of a piece.
    protected Position position;
    // The board the piece belongs to.
    protected Board board;


    // Non promotable pieces handle promotions like so
    public Piece promote() { return this; }
    public Piece demote() { return this; }

    // --Constructors-- //
    public Piece(Colors color, Position position, Board board) {
        this.color = color;
        this.position = position;                                               
        this.board = board;
    }                   

    // --Methods-- // 
    public Colors getColor() {
        return color;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void changeColor() {
        if (color == Colors.BLACK) {
            color = Colors.WHITE;
        } else {
            color = Colors.BLACK;
        }
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
        return this.getClass() == piece.getClass() && this.color == piece.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), color);
    }

    private boolean selfCollide(Position pos) {
        // If the board isn't empty, check if the color of the piece at a given position matches -> collision
        return !board.isEmpty(pos) && board.getPiece(pos).getColor() == this.getColor();
    }

    private boolean opponentCollide(Position pos) {
        // If the board isn't empty, check if the color of the piece at a given differs -> collision
        return !board.isEmpty(pos) && board.getPiece(pos).getColor() != this.getColor();
    }

    public List<Position> getLegalMovesSlider(int[][] dirs) {
        List<Position> legalMoves = new ArrayList<>();
        for (int[] dir : dirs) {
            int nx = position.getX();
            int ny = position.getY();
            while (true) {
                nx += dir[0];
                ny += dir[1] * color.getAlignFactor();
                Position nPos = new Position(nx, ny);

                if (!nPos.inBounds()) break;     // Stop if out of board bounds
                if (selfCollide(nPos)) break;    // Stop if own piece

                legalMoves.add(nPos);

                if (opponentCollide(nPos)) break; // Stop if opponent piece
            }
        }
        return legalMoves;
    }

    public List<Position> getLegalMovesNormal(int[][] offsets) {
        int x = position.getX();
        int y = position.getY();
        List<Position> legalMoves = new ArrayList<>();
        for (int[] offset : offsets) {
            Position nPos = new Position(x + offset[0], y + offset[1] * color.getAlignFactor());
            if (nPos.inBounds()) {
                if (!selfCollide(nPos)) {
                    legalMoves.add(nPos);
                }
            }
        }
        return legalMoves;
    }
    public abstract List<Position> getLegalMoves();
}