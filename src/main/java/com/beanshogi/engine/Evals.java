package com.beanshogi.engine;

import java.util.*;

import com.beanshogi.game.Player;
import com.beanshogi.model.*;
import com.beanshogi.pieces.normal.King;
import com.beanshogi.pieces.normal.Pawn;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

/**
 * Class defining board evaluation methods
 * @param board the board on which evaluation is done
 */
public class Evals {
    private transient Board board;  // Mark as transient to prevent circular reference in JSON

    public Evals(Board board) {
        this.board = board;
    }

    /**
     * Get the list of attackers checking the king for the given side.
     * @param side the side whose king we want to evaluate
     * @return the list of pieces checking that king
     */
    public List<CheckEvent> kingChecks(Sides side) {
        List<CheckEvent> checks = new ArrayList<>();
        King king = board.getKing(side);
        if (king == null) {
            return checks;
        }
        for (Piece piece : board.getPiecesOfSide(side.getOpposite())) {
            if (piece.getAttackMoves().contains(king.getBoardPosition())) {
                checks.add(new CheckEvent(piece, king));
            }
        }
        return checks;
    }
    
    /**
     * Get the list of pieces checking king
     * @return the list of pieces universally checking kings
     */
    public List<CheckEvent> kingChecks() {
        List<CheckEvent> allChecks = new ArrayList<>();
        for (Sides side : Sides.values()) {
            allChecks.addAll(kingChecks(side));
        }
        return allChecks;
    }

    /**
     * Checks whether any pieces check the king
     * @return king in check or not
     */
    public boolean isKingInCheck() {
        return !kingChecks().isEmpty();
    }

    /**
     * Checks whether the specified king is currently in check.
     * @param side the side whose king should be tested
     * @return king in check or not
     */
    public boolean isKingInCheck(Sides side) {
        return !kingChecks(side).isEmpty();
    }

    /**
     * Produce legal destination squares for a piece, excluding those that leave own king in check.
     * If the side is currently in check, only include moves that resolve the check.
     * @param piece the piece to get legal moves for
     * @return list of legal destination positions that don't leave the king in check
     */
    public List<Position> getFilteredLegalMoves(Piece piece) {
        Sides side = piece.getSide();
        boolean inCheck = isKingInCheck(side);
        
        // If the king is not in check, return all legal moves
        if (!inCheck) {
            return piece.getLegalMoves();
        }
        List<Position> filtered = new ArrayList<>();

        Position from = piece.getBoardPosition();
        for (Position to : piece.getLegalMoves()) {
            // Simulate without promotion
            boolean canPromote = piece.canPromote() && (from.inPromotionZone(side) || to.inPromotionZone(side));
            boolean mustPromote = canPromote && piece.shouldPromote(from, to);

            boolean resolves = false;
            // Try unpromoted variant first (if allowed)
            if (!mustPromote) {
                if (isMoveLegal(piece, from, to, false)) {
                    resolves = true;
                }
            }
            // Try promoted variant if promotion is possible and not yet resolved
            if (canPromote && !resolves) {
                if (isMoveLegal(piece, from, to, true)) {
                    resolves = true;
                }
            }
            if (resolves) {
                filtered.add(to);
            }
        }
        return filtered;
    }

    /**
     * Helper to test a move on a simulated board to verify it doesn't leave the king in check.
     * The real board position remains untouched.
     * @param piece the piece to move
     * @param from the starting position
     * @param to the destination position
     * @param promote whether the piece should be promoted during this move
     * @return true if the move is legal (doesn't leave own king in check), false otherwise
     */
    public boolean isMoveLegal(Piece piece, Position from, Position to, boolean promote) {
        if (piece == null || from == null || to == null) {
            return false;
        }

        MoveManager moveManager = board.moveManager;
        Stack<Move> redoBackup = copyStack(moveManager.getRedoStack());
        Piece captured = board.getPiece(to);
        Move move = new Move(board.getPlayer(piece.getSide()), from, to, piece, captured, promote, false);

        boolean applied = false;
        boolean kingSafe = false;
        try {
            moveManager.applyMove(move);
            applied = true;
            kingSafe = !isKingInCheck(piece.getSide());
        } finally {
            if (applied) {
                moveManager.undoMove();
            }
            restoreStack(moveManager.getRedoStack(), redoBackup);
        }
        return kingSafe;
    }

    /**
     * Simulates a piece drop to check if it would leave the side's king safe.
     * @param pieceClass the class of the piece to drop
     * @param side the side performing the drop
     * @param dropPos the position where the piece would be dropped
     * @return true if the drop would leave the king safe, false otherwise
     */
    private boolean simulateDropCheck(Class<? extends Piece> pieceClass, Sides side, Position dropPos) {
        Player player = board.getPlayer(side);
        Piece handPiece = null;
        for (Piece p : player.getHandPieces()) {
            if (pieceClass.isInstance(p)) {
                handPiece = p;
                break;
            }
        }
        if (handPiece == null) {
            return false;
        }

        MoveManager moveManager = board.moveManager;
        Stack<Move> redoBackup = copyStack(moveManager.getRedoStack());
        Move dropMove = new Move(player, handPiece.getHandPosition(), dropPos, handPiece, null, false, true);

        boolean applied = false;
        boolean kingSafe = false;
        try {
            moveManager.applyMove(dropMove);
            applied = true;
            kingSafe = !isKingInCheck(side);
        } finally {
            if (applied) {
                moveManager.undoMove();
            }
            restoreStack(moveManager.getRedoStack(), redoBackup);
        }

        return kingSafe;
    }

    /**
     * Determines if the specified side is in checkmate.
     * @param side the side to check for checkmate
     * @return true if the side is in checkmate, false otherwise
     */
    public boolean isCheckMate(Sides side) {
        if (!isKingInCheck(side)) {
            return false; // not in check -> cannot be checkmate
        }
        // Any legal move that leaves king safe ends checkmate test early.
        for (Piece piece : board.getPiecesOfSide(side)) {
            Position from = piece.getBoardPosition();
            if (from == null) continue;
            for (Position to : piece.getLegalMoves()) {
                boolean canPromote = piece.canPromote() && (from.inPromotionZone(side) || to.inPromotionZone(side));
                boolean mustPromote = canPromote && piece.shouldPromote(from, to);
                // Unpromoted
                if (!mustPromote && isMoveLegal(piece, from, to, false)) {
                    return false;
                }
                // Promoted variant
                if (canPromote && isMoveLegal(piece, from, to, true)) {
                    return false;
                }
            }
        }
        // Drops
        Player player = board.getPlayer(side);
        for (Piece handPiece : player.getHandPieces()) {
            for (Position dropPos : board.getPieceDropPoints(handPiece.getClass(), side)) {
                if (simulateDropCheck(handPiece.getClass(), side, dropPos)) {
                    return false;
                }
            }
        }
        return true; // no legal resolving moves
    }

    /**
     * Function to evaluate if a drop is uchifuzume - a pawn drop that would deliver a checkmate. If so, it's illegal.
     * @param dropSide The side of evaluation
     * @param dropPos The target position of drop
     * @return true if illegal drop, false otherwise.
     */
    public boolean violatesUchifuzume(Sides dropSide, Position dropPos) {
        Player dropPlayer = board.getPlayer(dropSide);

        Piece dropPiece = null;
        for (Piece handPiece : dropPlayer.getHandPieces()) {
            if (handPiece instanceof Pawn) {
                dropPiece = handPiece;
                break;
            }
        }

        boolean createdTempPawn = false;
        if (dropPiece == null) {
            dropPiece = new Pawn(dropSide, null, null, board);
            dropPlayer.getHandGrid().addPiece(dropPiece);
            createdTempPawn = true;
        }

        MoveManager moveManager = board.moveManager;
        Stack<Move> redoBackup = copyStack(moveManager.getRedoStack());
        Move dropMove = new Move(dropPlayer, dropPiece.getHandPosition(), dropPos, dropPiece, null, false, true);

        boolean applied = false;
        boolean isMate = false;
        try {
            moveManager.applyMove(dropMove);
            applied = true;
            Sides opponent = dropSide.getOpposite();
            isMate = isKingInCheck(opponent) && isCheckMate(opponent);
        } finally {
            if (applied) {
                moveManager.undoMove();
            }
            restoreStack(moveManager.getRedoStack(), redoBackup);
            if (createdTempPawn) {
                dropPlayer.getHandGrid().removePiece(dropPiece);
            }
        }
        return isMate;
    }

    /**
     * Creates a shallow copy of a move stack.
     * @param original the stack to copy
     * @return a new stack containing the same elements
     */
    private Stack<Move> copyStack(Stack<Move> original) {
        Stack<Move> copy = new Stack<>();
        copy.addAll(original);
        return copy;
    }

    /**
     * Restores a stack to a previous state by clearing it and copying elements from a snapshot.
     * @param target the stack to restore
     * @param snapshot the snapshot to restore from
     */
    private void restoreStack(Stack<Move> target, Stack<Move> snapshot) {
        target.clear();
        target.addAll(snapshot);
    }
}
