package com.beanshogi.engine;

import java.util.Stack;

import com.beanshogi.game.Player;
import com.beanshogi.model.Board;
import com.beanshogi.model.Move;
import com.beanshogi.model.Piece;
import com.beanshogi.util.*;

/**
 * Keep track of and manage moves
 * @param board the board on which the move is made
 * @param undoStack a stack using which a move can be undone
 * @param redoStack a stack using which an undone move can be redone
 */
public class MoveManager {
    private Board board;
    protected Stack<Move> undoStack = new Stack<>();
    protected Stack<Move> redoStack = new Stack<>();

    public MoveManager(Board board) {
        this.board = board;
    }

    public Player lastPlayer() {
        return undoStack.peek().getPlayer();
    }

    public Piece lastPiece() {
        return undoStack.peek().getMovedPiece();
    }

    public boolean isUndoStackEmpty() {
        return undoStack.isEmpty();
    }

    public boolean isRedoStackEmpty() {
        return redoStack.isEmpty();
    }

    public int getNoOfMoves() {
        return undoStack.size();
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

    public void applyMove(Move move) {
        Piece movedPiece = board.getPiece(move.getFrom());
        Piece capturedPiece = board.getPiece(move.getTo());
        board.removePiece(move.getFrom());
        if (capturedPiece != null) {
            board.removePiece(move.getTo());
            move.getPlayer().addToHand(capturedPiece);
        }
        if (move.isPromotion()) {
            // Promote, if applicable
            movedPiece = movedPiece.promote();
        }
        board.setPiece(move.getTo(), movedPiece);
        // Add the current move data to undoStack
        undoStack.push(move);
        // Clear the redoStack, as there are no new moves to be redone
        redoStack.clear();
    }

    public void undoMove() {
        if (undoStack.empty()) {
            return;
        }

        Move lastMove = undoStack.pop();

        // Stash away the latest move to redo
        redoStack.push(lastMove);

        board.removePiece(lastMove.getTo());

        Piece capturedPiece = lastMove.getCapturedPiece();
        if (capturedPiece != null) {
            capturedPiece.changeSide();
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


