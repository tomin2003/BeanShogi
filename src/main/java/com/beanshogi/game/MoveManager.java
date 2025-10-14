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

    public void applyMove(Player currentPlayer, Position from, Position to, boolean isPromotion) {
        Piece movedPiece = board.getPiece(from);
        Piece capturedPiece = board.getPiece(to);
        board.removePiece(from);
        if (capturedPiece != null) {
            board.removePiece(to);
            currentPlayer.addToHand(capturedPiece);
        }
        if (isPromotion) {
            // Promote, if applicable
            movedPiece = movedPiece.promote();
        }
        board.setPiece(to, movedPiece);
        // Add the current move data to undoStack
        undoStack.push(new Move(currentPlayer, from, to, movedPiece, capturedPiece, isPromotion));
        // Clear the redoStack, as there are no new moves to be redone
        redoStack.clear();
    }

    public void undoMove() {
        // Get previous move
        Move lastMove = undoStack.pop();
        
        // Add the undid move to be able to redo
        redoStack.push(lastMove);

        board.removePiece(lastMove.getTo());

        Piece capturedPiece = lastMove.getCapturedPiece();
        if (capturedPiece != null) {
            board.setPiece(lastMove.getTo(), capturedPiece);
            lastMove.getPlayer().removeFromHand(capturedPiece);
        }

        Piece movedPiece = lastMove.getMovedPiece();
        if (lastMove.isPromotion()) {
            // Demote, if applicable
            movedPiece = movedPiece.demote();
        }
        board.setPiece(lastMove.getFrom(), movedPiece);
    }

    public void redoMove() {
        
    }
}


