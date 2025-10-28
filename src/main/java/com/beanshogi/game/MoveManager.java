package com.beanshogi.game;

import java.util.Stack;

import com.beanshogi.model.*;
import com.beanshogi.util.*;

public class MoveManager {
    static private Board board;
    static protected Stack<Move> undoStack = new Stack<>();
    static protected Stack<Move> redoStack = new Stack<>();

    public MoveManager(Board board) {
        MoveManager.board = board;
    }

    public static void applyMove(Player currentPlayer, Position from, Position to, boolean isPromotion) {
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

    public static void undoMove() {
        if (undoStack.empty()) {
            return;
        }

        Move lastMove = undoStack.pop();

        // Stash away the latest move to redo
        redoStack.push(lastMove);

        board.removePiece(lastMove.getTo());

        Piece capturedPiece = lastMove.getCapturedPiece();
        if (capturedPiece != null) {
            capturedPiece.changeColor();
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

    public static void redoMove() {
        if (redoStack.empty()) {
            return;
        }
        Move redoMove = redoStack.pop();
        
        // Stash away the invoked move
        undoStack.push(redoMove);

        board.removePiece(redoMove.getFrom());

        Piece capturedPiece = redoMove.getCapturedPiece();
        if (capturedPiece != null) {
            board.removePiece(redoMove.getTo());
            redoMove.getPlayer().addToHand(capturedPiece);
        }

        Piece movedPiece = redoMove.getMovedPiece();
        if (redoMove.isPromotion()) {
            // Promote, if applicable
            movedPiece = movedPiece.promote();
        }
        board.setPiece(redoMove.getTo(), movedPiece);
    }
}


