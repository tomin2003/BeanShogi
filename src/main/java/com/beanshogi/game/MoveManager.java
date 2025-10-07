package com.beanshogi.game;

import java.util.Stack;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

public class MoveManager {
    Board board;
    private Stack<Move> undoStack = new Stack<>();
    private Stack<Move> redoStack = new Stack<>();

    public MoveManager(Board board) {
        this.board = board;
    }

    public void applyMove(Position from, Position to, boolean isPromotion, Player currentPlayer) {
        Piece movedPiece = board.getPiece(from);
        Piece capturedPiece = board.getPiece(to);
        board.removePiece(from);
        if (capturedPiece != null) {
            board.removePiece(to);
            currentPlayer.addToHand(capturedPiece);
        }
        if (isPromotion && movedPiece instanceof Promotable) {
            movedPiece.promote();
        }
        board.setPiece(to, movedPiece);
        // Add the current move data to undoStack
        undoStack.push(new Move(from, to, movedPiece, capturedPiece, isPromotion));
        // Clear the redoStack (no moves to be redone)
        redoStack.clear();
    }
}


