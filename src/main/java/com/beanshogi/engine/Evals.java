package com.beanshogi.engine;

import java.util.*;

import com.beanshogi.game.Player;
import com.beanshogi.model.*;
import com.beanshogi.pieces.normal.King;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

/**
 * Class defining board evaluation methods
 * @param board the board on which evaluation is done
 * @param moveManager the moveManager linked to board
 */
public class Evals {
    private Board board;

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
     * Simulate if a move on a board might result in check
     * @param simBoard board cloned for simulation
     * @param piecePos position of the piece to move (on simBoard)
     * @param to position the piece is moved to
     * @param promote promotion state of piece
     * @return king is safe or not
     */
    private boolean simulateMoveCheck(Board simBoard, Position piecePos, Position to, boolean promote) {
        Piece piece = simBoard.getPiece(piecePos);
        if (piece == null) return false;
        
        Player player = simBoard.getPlayer(piece.getSide());
        simBoard.moveManager.applyMove(new Move(
            player, 
            piecePos,
            to,
            piece,
            simBoard.getPiece(to),
            promote,
            false
        ));
        boolean kingSafe = !simBoard.evals.isKingInCheck(piece.getSide());
        simBoard.moveManager.undoMove(); // Step back to previous position
        simBoard.moveManager.getRedoStack().clear();
        return kingSafe;
    }

    private boolean simulateDropCheck(Board simBoard, Class<? extends Piece> pieceClass, Sides side, Position dropPos) {
        // Find a piece of the given class in the player's hand
        Player player = simBoard.getPlayer(side);
        Piece handPiece = null;
        for (Piece p : player.getHand()) {
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
        King king = board.getKing(side);
        if (king == null || !isKingInCheck(side)) return false;

        Board simBoard = board.copy(); // copy for isolated simulation

        // 1. King moves
        King simKing = simBoard.getKing(side);
        if (simKing != null) {
            for (Position move : simKing.getLegalMoves()) {
                if (simulateMoveCheck(simBoard, simKing.getBoardPosition(), move, false)) {
                    return false;
                }
            }
        }

        // 2. Other piece moves (blocking or capturing)
        for (Piece piece : simBoard.getPiecesOfSide(side)) {
            if (piece instanceof King) continue;
            for (Position move : piece.getLegalMoves()) {
                boolean promote = move.inPromotionZone(side) && piece.canPromote();
                if (simulateMoveCheck(simBoard, piece.getBoardPosition(), move, promote)) {
                    return false;
                }
            }
        }

        // 3. Hand drops (blocking)
        Player player = simBoard.getPlayer(side);
        for (Piece handPiece : player.getHand()) {
            for (Position dropPos : simBoard.getPieceDropPoints(handPiece.getClass(), side)) {
                if (simulateDropCheck(simBoard, handPiece.getClass(), side, dropPos)) {
                    return false;
                }
            }
        }
        return true; // no move prevents check → checkmate
    }
}
