package com.beanshogi.engine;

import java.util.*;
import java.util.stream.Collectors;

import com.beanshogi.model.*;
import com.beanshogi.pieces.normal.King;
import com.beanshogi.util.Position;

/**
 * Class defining board evaluation methods
 * @param board the board on which evaluation is done
 */
public class Evals {
    private Board board;
    private MoveManager moveManager;

    public Evals(Board board) {
        this.board = board;
        moveManager = new MoveManager(board);
    }

    public boolean isKingInCheck() {
        if (moveManager.undoStack.empty()) {
            return false;
        }
        // The top of the undoStack currently must be the enemy's latest move
        Piece lastEnemy = moveManager.lastPiece();
        King king = board.getKing(lastEnemy.getSide().getOpposite());
        // If the latest move's legal moves contain the king's position, it's in check by definition
        if (lastEnemy.getLegalMoves().contains(king.getPosition())) {
            return true;
        }
        return false;
    }

    private boolean kingCanEscape(Piece king, Collection<Piece> enemyPieces) {
        for (Position to : king.getLegalMoves()) {
            boolean safe = true;
            for (Piece enemyPiece : enemyPieces) {
                // If the enemy piece has legal moves where the king can flee, the king is not safe
                if (enemyPiece.getLegalMoves().contains(to)) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                return true;
            }
        }
        return false;
    }

    private boolean canBlockMove(Piece king, Collection<Piece> friendlyPieces, List<Piece> enemyPieces) {
        // Get the attacking enemy pieces and their attacking line
        Map<Piece, HashSet<Position>> attackingPieces = enemyPieces.stream()
        .filter(attacker -> attacker.getLegalMoves().contains(king.getPosition()))
        .collect(Collectors.toMap(
            attacker -> attacker,
            attacker -> new HashSet<>(attacker.getLegalMoves().stream()
                .filter(move -> move.equals(king.getPosition()))
                .collect(Collectors.toCollection(HashSet::new)))
        ));

        // Put the legal moves of friendly pieces into a HashSet for quick lookups
        HashSet<Position> friendlyMoves = friendlyPieces.stream()
            .flatMap(piece -> piece.getLegalMoves().stream())
            .collect(Collectors.toCollection(HashSet::new)
        );

        // Check if any friendly move lines up with any attack line
        for (HashSet<Position> attackLines : attackingPieces.values()) {
            if (!Collections.disjoint(friendlyMoves, attackLines)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCheckMate() {
        // Get the last enemy move
        Piece lastEnemy = moveManager.lastPiece();
        King king = board.getKing(lastEnemy.getSide().getOpposite());
        
        // Get all the enemy pieces on board
        Collection<Piece> enemyPieces = board.getPiecesOfSide(lastEnemy.getSide());
            
        // Check if the king has an escape path
        if (kingCanEscape(king, enemyPieces)) {
            return false; // If it has, then checkmate is avoided
        }

        // Get the attacking enemy pieces
        List<Piece> attackingPieces = enemyPieces.stream()
            .filter(e -> e.getLegalMoves().contains(king.getPosition()))
            .toList();

        // See if any friendly pieces can block the attack
        Collection<Piece> friendlyPieces = board.getPiecesOfSide(king.getSide());

        // Return with the evaluation of a friendly piece can block the attacking piece
        return canBlockMove(king, friendlyPieces, attackingPieces);
    }
}
