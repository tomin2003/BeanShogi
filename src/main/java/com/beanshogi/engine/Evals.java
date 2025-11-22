package com.beanshogi.engine;

import java.util.*;

import com.beanshogi.game.Player;
import com.beanshogi.model.*;
import com.beanshogi.pieces.normal.King;
import com.beanshogi.pieces.normal.Pawn;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

// TODO: possibly implement logic where a check has to be ended before making other moves

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
    public List<CheckEvent> kingChecks(Sides kingSide) {
        List<CheckEvent> checks = new ArrayList<>();
        King king = board.getKing(kingSide);
        if (king == null) {
            return checks;
        }
        for (Piece piece : board.getPiecesOfSide(kingSide.getOpposite())) {
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
     * Checks wheteher any pieces check the king
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

    // TODO: Review checkmate logic for correctness

    /**
     * Produce legal destination squares for a piece, excluding those that leave own king in check.
     * If the side is currently in check, only include moves that resolve the check.
     */
    public List<Position> getFilteredLegalMoves(Piece piece) {
        List<Position> filtered = new ArrayList<>();
        if (piece == null || piece.getBoardPosition() == null) {
            return filtered;
        }
        Sides side = piece.getSide();
        boolean mustResolveCheck = isKingInCheck(side);
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
            if (resolves && (!mustResolveCheck || (mustResolveCheck && resolves))) {
                filtered.add(to);
            }
        }
        return filtered;
    }

    /**
     * Helper to test a move on a copied board so the real position is untouched.
     */
    public boolean isMoveLegal(Piece piece, Position from, Position to, boolean promote) {
        if (piece == null || from == null || to == null) {
            return false;
        }

        Board sim = board.copy();
        Piece simPiece = sim.getPiece(from);
        if (simPiece == null) {
            return false;
        }

        Player simPlayer = sim.getPlayer(simPiece.getSide());
        Piece captured = sim.getPiece(to);
        Move move = new Move(simPlayer, from, to, simPiece, captured, promote, false);
        sim.moveManager.applyMove(move);
        return !sim.evals.isKingInCheck(simPiece.getSide());
    }

    private boolean simulateDropCheck(Board simBoard, Class<? extends Piece> pieceClass, Sides side, Position dropPos) {
        // Find a piece of the given class in the player's hand
        Player player = simBoard.getPlayer(side);
        Piece handPiece = null;
        for (Piece p : player.getHandPieces()) {
            if (pieceClass.isInstance(p)) {
                handPiece = p;
                break;
            }
        }
        if (handPiece == null) return false;

        Move dropMove = new Move(
            player,
            handPiece.getHandPosition(),
            dropPos,
            handPiece,
            null,
            false,
            true
        );
        simBoard.moveManager.applyMove(dropMove);
        boolean kingSafe = !simBoard.evals.isKingInCheck(side);
        simBoard.moveManager.undoMove();
        simBoard.moveManager.getRedoStack().clear();

        return kingSafe;
    }

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
                if (simulateDropCheck(board.copy(), handPiece.getClass(), side, dropPos)) { // use copy to avoid state pollution
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
        Board simulated = board.copy();
        Player dropPlayer = simulated.getPlayer(dropSide);

        Piece dropPiece = null;
        for (Piece handPiece : dropPlayer.getHandPieces()) {
            if (handPiece instanceof Pawn) {
                dropPiece = handPiece;
                break;
            }
        }

        if (dropPiece == null) {
            dropPiece = new Pawn(dropSide, null, null, simulated);
            dropPlayer.getHandGrid().addPiece(dropPiece);
        }

        Move dropMove = new Move(
            dropPlayer,
            dropPiece.getHandPosition(),
            dropPos,
            dropPiece,
            null,
            false,
            true
        );

        simulated.moveManager.applyMove(dropMove);
        Sides opponent = dropSide.getOpposite();
        return simulated.evals.isKingInCheck(opponent) && simulated.evals.isCheckMate(opponent);
    }
}
