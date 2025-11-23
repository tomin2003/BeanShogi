package com.beanshogi.engine;

import java.util.Stack;

import com.beanshogi.game.Player;
import com.beanshogi.model.Board;
import com.beanshogi.model.Move;
import com.beanshogi.model.Piece;
import com.beanshogi.pieces.PromotedPiece;

/**
 * Keep track of and manage moves
 * @param board the board on which the move is made
 * @param undoStack a stack using which a move can be undone
 * @param redoStack a stack using which an undone move can be redone
 * @param archivedMovesMade starting point for tracking moves made after deserialization.
 */
public class MoveManager {
    private transient Board board;  // Marked as transient to prevent circular reference in JSON
    protected Stack<Move> undoStack = new Stack<>();
    protected Stack<Move> redoStack = new Stack<>();
    private int archivedMovesMade = 0;

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

    public void setArchivedMovesMade(int noOfMoves) {
        this.archivedMovesMade = noOfMoves;
    }

    public int getNoOfMoves() {
        return archivedMovesMade + undoStack.size();
    }

    public Stack<Move> getUndoStack() {
        return undoStack;
    }

    public Stack<Move> getRedoStack() {
        return redoStack;
    }

    /**
     * Applies a move to the board, updating piece positions and handling captures, drops, and promotions.
     * Pushes the move onto the undo stack and clears the redo stack.
     * @param move the move to apply
     */
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
                boolean wasPromoted = isPiecePromoted(capturedPiece);
                move.setCapturedWasPromoted(wasPromoted);
                // In Shogi: change side, demote if promoted, add to hand
                capturedPiece.changeSide();
                if (wasPromoted) {
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

    /**
     * Undoes the last move, restoring the board to its previous state.
     * Moves the undone move from the undo stack to the redo stack.
     * Handles both normal moves and drops, restoring captured pieces and promotion states.
     */
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
                // Remove from capturing player's hand, restore side and promotion state
                movePlayer.getHandGrid().removePiece(capturedPiece);
                capturedPiece.changeSide();
                Piece restoredPiece = capturedPiece;
                if (lastMove.wasCapturedPromoted()) {
                    restoredPiece = capturedPiece.promote();
                }
                board.setPiece(lastMove.getTo(), restoredPiece);
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

    /**
     * Redoes a previously undone move, reapplying it to the board.
     * Moves the redone move from the redo stack back to the undo stack.
     * Reapplies captures, drops, and promotions as they were originally.
     */
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
                if (redoMove.wasCapturedPromoted()) {
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
     * Checks if a piece is promoted.
     * @param p the piece to check
     * @return true if the piece is an instance of PromotedPiece
     */
    private boolean isPiecePromoted(Piece p) {
        return p instanceof PromotedPiece;
    }
}