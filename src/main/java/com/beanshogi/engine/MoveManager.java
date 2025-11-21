package com.beanshogi.engine;

import java.util.Stack;

import com.beanshogi.game.Player;
import com.beanshogi.model.Board;
import com.beanshogi.model.Move;
import com.beanshogi.model.Piece;

/**
 * Keep track of and manage moves
 * @param board the board on which the move is made
 * @param undoStack a stack using which a move can be undone
 * @param redoStack a stack using which an undone move can be redone
 */
public class MoveManager {
    private transient Board board;  // Mark as transient to prevent circular reference in JSON
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

    public Stack<Move> getUndoStack() {
        return undoStack;
    }

    public Stack<Move> getRedoStack() {
        return redoStack;
    }

    public void applyMove(Move move) {
        Player movePlayer = move.getPlayer();
        
        if (move.isDrop()) {
            // Dropping a piece from hand onto the board
            Piece droppedPiece = move.getMovedPiece();
            movePlayer.getHandGrid().removePiece(droppedPiece);
            board.setPiece(move.getTo(), droppedPiece);
            
        } else {
            // Normal board move
            Piece movedPiece = board.getPiece(move.getFrom());
            Piece capturedPiece = board.getPiece(move.getTo());
            
            // Remove piece from starting position
            board.removePiece(move.getFrom());
            
            // Handle capture
            if (capturedPiece != null) {
                // In Shogi: change side, demote if promoted, add to hand
                capturedPiece.changeSide();
                if (isPiecePromoted(capturedPiece)) {
                    capturedPiece = capturedPiece.demote();
                }
                movePlayer.getHandGrid().addPiece(capturedPiece);
                
                // Store the captured piece in the move for undo
                move.setCapturedPiece(capturedPiece);
            }
            
            // Handle promotion
            if (move.isPromotion()) {
                movedPiece = movedPiece.promote();
            }
            
            // Move the piece to destination (this will overwrite any captured piece)
            board.setPiece(move.getTo(), movedPiece);
        }
        
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
        redoStack.push(lastMove);

        Player movePlayer = lastMove.getPlayer();
        Piece movedPiece = lastMove.getMovedPiece();
        Piece capturedPiece = lastMove.getCapturedPiece();
        
        if (lastMove.isDrop()) {
            // This was a drop: remove from board, return to hand
            board.removePiece(lastMove.getTo());
            movePlayer.getHandGrid().addPiece(movedPiece);
        } else {
            // Remove piece from destination
            Piece pieceAtDestination = board.getPiece(lastMove.getTo());
            board.removePiece(lastMove.getTo());
            
            // If there was a captured piece, restore it to the board
            if (capturedPiece != null) {
                // Change side back to original, re-promote if needed
                capturedPiece.changeSide();
                // Note: You might need to track if it was originally promoted
                board.setPiece(lastMove.getTo(), capturedPiece);
                
                // Remove from capturing player's hand
                movePlayer.getHandGrid().removePiece(capturedPiece);
            }
            
            // Return moved piece to original position
            // If it was promoted during this move, use the unpromoted version
            if (lastMove.isPromotion() && isPiecePromoted(pieceAtDestination)) {
                movedPiece = pieceAtDestination.demote(); // Demote back
            } else {
                movedPiece = pieceAtDestination;
            }
            board.setPiece(lastMove.getFrom(), movedPiece);
        }
    }

    public void redoMove() {
        if (redoStack.empty()) {
            return;
        }

        Move redoMove = redoStack.pop();
        undoStack.push(redoMove);

        Player movePlayer = redoMove.getPlayer();
        Piece movedPiece = redoMove.getMovedPiece();
        Piece capturedPiece = redoMove.getCapturedPiece();

        if (redoMove.isDrop()) {
            // Drop from hand to board
            movePlayer.getHandGrid().removePiece(movedPiece);
            board.setPiece(redoMove.getTo(), movedPiece);
        } else {
            // Normal board move
            // Handle capture first
            if (capturedPiece != null) {
                board.removePiece(redoMove.getTo());
                capturedPiece.changeSide();
                if (isPiecePromoted(capturedPiece)) {
                    capturedPiece = capturedPiece.demote();
                }
                movePlayer.getHandGrid().addPiece(capturedPiece);
            }
            
            // Get the piece from its original position
            Piece pieceToMove = board.getPiece(redoMove.getFrom());
            board.removePiece(redoMove.getFrom());
            
            // Promote if needed
            if (redoMove.isPromotion()) {
                pieceToMove = pieceToMove.promote();
            }
            
            // Place at destination
            board.setPiece(redoMove.getTo(), pieceToMove);
        }
    }

    /**
     * Helper to check promotion status via reflection so code compiles even if Piece
     * doesn't declare isPromoted(); returns false if the method is absent or invocation fails.
     */
    private boolean isPiecePromoted(Piece p) {
        if (p == null) return false;
        try {
            java.lang.reflect.Method m = p.getClass().getMethod("isPromoted");
            Object res = m.invoke(p);
            if (res instanceof Boolean) {
                return (Boolean) res;
            }
        } catch (Exception e) {
            // No method or invocation failed, assume not promoted
        }
        return false;
    }
}