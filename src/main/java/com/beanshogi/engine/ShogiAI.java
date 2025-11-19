package com.beanshogi.engine;

import java.util.ArrayList;
import java.util.List;

import com.beanshogi.game.Player;
import com.beanshogi.model.*;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

public class ShogiAI {

    private static class MoveScore {
        final Move move;
        final int orderingScore;
        MoveScore(Move m, int ordering) {
            move = m;
            orderingScore = ordering;
        }
    }

    private final int MAX_DEPTH = 3;
    private final Board board;
    private long totalNodes = 0;
    private long totalPruned = 0;

    public ShogiAI(Board board) {
        this.board = board;
    }

    /**
     * Returns the best move (normal or drop) for the given side.
     */
    public Move getBestMove(Sides sideToMove) {
        long startTime = System.nanoTime();
        totalNodes = 0;
        totalPruned = 0;
        
        System.out.println("\n=== AI Move Search Started ===");
        System.out.println("Side: " + sideToMove);
        System.out.println("Max Depth: " + MAX_DEPTH);
        
        // Work on an isolated copy so search can apply/undo moves without touching the live board
        Board searchBoard = board.copy();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        // Generate all possible moves from the copied board so we can apply/undo safely
        long moveGenStart = System.nanoTime();
        List<MoveScore> candidateMoves = generateAllMoves(searchBoard, sideToMove);
        long moveGenTime = System.nanoTime() - moveGenStart;
        
        System.out.println("Candidate moves generated: " + candidateMoves.size());
        System.out.printf("Move generation time: %.2f ms\n", moveGenTime / 1_000_000.0);
        
        // Evaluate moves sequentially with alpha-beta
        long evalStart = System.nanoTime();

        for (MoveScore ms : candidateMoves) {

            searchBoard.moveManager.applyMove(ms.move);
            if (searchBoard.evals.isKingInCheck(sideToMove)) {
                // Illegal move (king left in check)
                searchBoard.moveManager.undoMove();
                searchBoard.moveManager.getRedoStack().clear();
                continue;
            }

            int score = minimax(searchBoard, MAX_DEPTH - 1, sideToMove.getOpposite(),
                                sideToMove, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (bestMove == null || score > bestScore) {
                bestScore = score;
                bestMove = ms.move;
            }

            searchBoard.moveManager.undoMove();
            searchBoard.moveManager.getRedoStack().clear();
        }
        
        if (bestMove == null) {
            System.out.println("No legal moves found. Likely stalemate or checkmate.");
            return null;
        }

        long evalTime = System.nanoTime() - evalStart;
        long totalTime = System.nanoTime() - startTime;
        
        System.out.println("\n=== AI Move Search Complete ===");
        System.out.printf("Total nodes evaluated: %d\n", totalNodes);
        System.out.printf("Nodes pruned by alpha-beta: %d (%.1f%%)\n", 
            totalPruned, totalPruned * 100.0 / Math.max(1, totalNodes));
        System.out.printf("Evaluation time: %.2f ms\n", evalTime / 1_000_000.0);
        System.out.printf("Total time: %.2f ms\n", totalTime / 1_000_000.0);
        System.out.printf("Best score: %d\n", bestScore);
        System.out.println("================================\n");
        
        return bestMove;
    }
    
    /**
     * Generate pseudo-legal moves for the given board/side, ordered by simple heuristics.
     */
    private List<MoveScore> generateAllMoves(Board targetBoard, Sides sideToMove) {
        List<MoveScore> moves = new ArrayList<>();
        Player player = targetBoard.getPlayer(sideToMove);

        for (Piece piece : targetBoard.getPiecesOfSide(sideToMove)) {
            Position from = piece.getBoardPosition();
            if (from == null) {
                continue;
            }

            // Generate both promotion and non-promotion variants up-front so move ordering can pick the best quickly
            for (Position to : piece.getLegalMoves()) {
                Piece captured = targetBoard.getPiece(to);
                boolean canPromote = piece.canPromote() &&
                    (from.inPromotionZone(sideToMove) || to.inPromotionZone(sideToMove));
                boolean mustPromote = canPromote && piece.shouldPromote(from, to);

                int priority = (captured != null ? Math.abs(captured.value()) + 100 : 0);

                if (mustPromote) {
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, true, false),
                                             priority + 50));
                } else if (canPromote) {
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, true, false),
                                             priority + 50));
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, false, false),
                                             priority));
                } else {
                    moves.add(new MoveScore(new Move(player, from, to, piece, captured, false, false),
                                             priority));
                }
            }
        }

        for (Piece handPiece : player.getHand()) {
            List<Position> dropPoints = targetBoard.getPieceDropPoints(handPiece.getClass(), sideToMove);
            for (Position dropPos : dropPoints) {
                Move dropMove = new Move(
                    player,
                    handPiece.getHandPosition(),
                    dropPos,
                    handPiece,
                    null,
                    false,
                    true
                );
                moves.add(new MoveScore(dropMove, 10));
            }
        }

        moves.sort((a, b) -> Integer.compare(b.orderingScore, a.orderingScore));
        return moves;
    }

    /**
     * Minimax with alpha-beta pruning
     * @param currentBoard
     * @param depth
     * @param sideToMove
     * @param alpha
     * @param beta
     * @return evaluation score
     */
    private int minimax(Board currentBoard, int depth, Sides sideToMove, Sides maximizingSide,
                        int alpha, int beta) {
        totalNodes++;

        if (depth == 0) {
            return evaluate(currentBoard, maximizingSide);
        }

        List<MoveScore> moves = generateAllMoves(currentBoard, sideToMove);
        boolean maximizing = sideToMove == maximizingSide;
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        boolean exploredLegalMove = false;

        if (moves.isEmpty()) {
            return currentBoard.evals.isKingInCheck(sideToMove) ? (maximizing ? Integer.MIN_VALUE + depth : Integer.MAX_VALUE - depth) : 0;
        }

        for (MoveScore ms : moves) {
            // Apply the move directly on the shared board, evaluate, then undo to reuse state
            currentBoard.moveManager.applyMove(ms.move);
            if (currentBoard.evals.isKingInCheck(sideToMove)) {
                currentBoard.moveManager.undoMove();
                currentBoard.moveManager.getRedoStack().clear();
                continue;
            }

            exploredLegalMove = true;
            int score = minimax(currentBoard, depth - 1, sideToMove.getOpposite(), maximizingSide, alpha, beta);

            currentBoard.moveManager.undoMove();
            currentBoard.moveManager.getRedoStack().clear();

            if (maximizing) {
                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, score);
                beta = Math.min(beta, best);
            }

            if (beta <= alpha) {
                totalPruned++;
                break;
            }
        }

        if (!exploredLegalMove) {
            return currentBoard.evals.isKingInCheck(sideToMove)
                    ? (maximizing ? Integer.MIN_VALUE + depth : Integer.MAX_VALUE - depth)
                    : 0;
        }

        return best;
    }

    /**
     * Evaluate score
     * @param board
     * @param maximizingSide
     * @return evaluation score
     */
    private int evaluate(Board board, Sides maximizingSide) {
        int score = 0;

        // Only run expensive mate checks when a king is already in check, otherwise rely on material heuristics
        boolean maxInCheck = board.evals.isKingInCheck(maximizingSide);
        boolean oppInCheck = board.evals.isKingInCheck(maximizingSide.getOpposite());

        if (oppInCheck && board.evals.isCheckMate(maximizingSide.getOpposite())) {
            return Integer.MAX_VALUE - 1;
        }
        if (maxInCheck && board.evals.isCheckMate(maximizingSide)) {
            return Integer.MIN_VALUE + 1;
        }

        // Apply check bonuses/penalties
        if (maxInCheck) {
            score -= 200;
        }
        if (oppInCheck) {
            score += 300;
        }

        // Material evaluation - optimized to iterate once
        for (Piece piece : board.getAllPieces()) {
            int mobilityBonus = piece.getLegalMoves().size() * 3;
            int positionBonus = evaluatePiecePosition(piece, maximizingSide);
            int promotionBonus = 0;
            
            // Check promotion potential
            if (piece.canPromote() && piece.getBoardPosition().inPromotionZone(piece.getSide())) {
                promotionBonus = 100;
            }
            
            int pieceScore = piece.value() + mobilityBonus + positionBonus + promotionBonus;
            score += piece.getSide() == maximizingSide ? pieceScore : -pieceScore;
        }

        return score;
    }

    private int evaluatePiecePosition(Piece piece, Sides maximizingSide) {
        int score = 0;
        int x = piece.getBoardPosition().x;
        int y = piece.getBoardPosition().y;

        if (x >= 3 && x <= 5 && y >= 3 && y <= 5) {
            score += 20;
        }
        if (piece.canPromote() && piece.getBoardPosition().inPromotionZone(maximizingSide)) {
            score += 30;
        }

        return score;
    }
}
